package com.tang.netty.demo.client;

import com.sun.corba.se.spi.protocol.RequestDispatcherDefault;
import com.tang.netty.demo.client.codec.*;
import com.tang.netty.demo.client.handler.ClientIdleCheckHandler;
import com.tang.netty.demo.client.handler.KeepaliveHandler;
import com.tang.netty.demo.client.handler.dispatcher.OperationResultFuture;
import com.tang.netty.demo.client.handler.dispatcher.RequestPendingCenter;
import com.tang.netty.demo.client.handler.dispatcher.ResponseDispatcherHandler;
import com.tang.netty.demo.common.OperationResult;
import com.tang.netty.demo.common.RequestMessage;
import com.tang.netty.demo.common.auth.AuthOperation;
import com.tang.netty.demo.common.order.OrderOperation;
import com.tang.netty.demo.util.IdUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;

import javax.net.ssl.SSLException;
import java.util.concurrent.ExecutionException;


/**
 * 简单的客户端程序
 * 接收响应结果
 */
public class ClientV12 {

    public static void main(String[] args) throws InterruptedException, ExecutionException, SSLException {

        Bootstrap bootstrap = new Bootstrap();
        bootstrap.channel(NioSocketChannel.class);

        bootstrap.group(new NioEventLoopGroup());

        RequestPendingCenter requestPendingCenter = new RequestPendingCenter();
        KeepaliveHandler keepaliveHandler = new KeepaliveHandler();

        // SSL
        SslContextBuilder sslContextBuilder = SslContextBuilder.forClient();
        SslContext sslContext = sslContextBuilder.build();

        bootstrap.handler(new ChannelInitializer<NioSocketChannel>() {
            @Override
            protected void initChannel(NioSocketChannel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();

                pipeline.addLast(new ClientIdleCheckHandler());

                pipeline.addLast(sslContext.newHandler(ch.alloc()));

                pipeline.addLast(new OrderFrameDecoder());
                pipeline.addLast(new OrderFrameEncoder());
                pipeline.addLast(new OrderProtocolEncoder());
                pipeline.addLast(new OrderProtocolDecoder());

                pipeline.addLast(keepaliveHandler);

                pipeline.addLast(new ResponseDispatcherHandler(requestPendingCenter));
                pipeline.addLast(new OperationToRequestMessageEncoder());
                pipeline.addLast(new LoggingHandler(LogLevel.INFO));

            }
        });

        ChannelFuture channelFuture = bootstrap.connect("127.0.0.1", 8090);
        channelFuture.sync();

        // 发送请求
        long streamId = IdUtil.nextId();

        OperationResultFuture operationResultFuture = new OperationResultFuture();
        requestPendingCenter.add(streamId, operationResultFuture);

        // 授权
        AuthOperation authOperation = new AuthOperation("admin", "123456");
        channelFuture.channel().writeAndFlush(new RequestMessage(IdUtil.nextId(), authOperation));

        RequestMessage requestMessage = new RequestMessage(streamId, new OrderOperation(10012, "tudou12"));
        channelFuture.channel().writeAndFlush(requestMessage);

        // 获取响应
        OperationResult operationResult = operationResultFuture.get();
        System.out.println("获取的响应结果为：" + operationResult);

        channelFuture.channel().closeFuture().get();
    }

}
