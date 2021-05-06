package com.tang.netty.demo.client;

import com.tang.netty.demo.client.codec.OrderFrameDecoder;
import com.tang.netty.demo.client.codec.OrderFrameEncoder;
import com.tang.netty.demo.client.codec.OrderProtocolDecoder;
import com.tang.netty.demo.client.codec.OrderProtocolEncoder;
import com.tang.netty.demo.common.RequestMessage;
import com.tang.netty.demo.common.auth.AuthOperation;
import com.tang.netty.demo.common.order.OrderOperation;
import com.tang.netty.demo.server.handler.OrderServerProcessHandler;
import com.tang.netty.demo.util.IdUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.util.concurrent.ExecutionException;


/**
 * 简单的客户端程序
 */
public class ClientV10 {

    public static void main(String[] args) throws InterruptedException, ExecutionException {

        Bootstrap bootstrap = new Bootstrap();
        bootstrap.channel(NioSocketChannel.class);

        bootstrap.group(new NioEventLoopGroup());

        bootstrap.handler(new ChannelInitializer<NioSocketChannel>() {
            @Override
            protected void initChannel(NioSocketChannel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();

                pipeline.addLast(new OrderFrameDecoder());
                pipeline.addLast(new OrderFrameEncoder());

                pipeline.addLast(new OrderProtocolEncoder());
                pipeline.addLast(new OrderProtocolDecoder());

                pipeline.addLast(new LoggingHandler(LogLevel.INFO));

            }
        });

        ChannelFuture channelFuture = bootstrap.connect("127.0.0.1", 8090);
        channelFuture.sync();

        // 发送请求
        RequestMessage requestMessage = new RequestMessage(IdUtil.nextId(), new OrderOperation(10010, "tudou10010"));
        channelFuture.channel().writeAndFlush(requestMessage);

        channelFuture.channel().closeFuture().get();

    }

}
