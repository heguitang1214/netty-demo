package com.tang.netty.demo.client;

import com.tang.netty.demo.client.codec.OrderFrameDecoder;
import com.tang.netty.demo.client.codec.OrderFrameEncoder;
import com.tang.netty.demo.client.codec.OrderProtocolDecoder;
import com.tang.netty.demo.client.codec.OrderProtocolEncoder;
import com.tang.netty.demo.client.handler.ClientIdleCheckHandler;
import com.tang.netty.demo.client.handler.KeepaliveHandler;
import com.tang.netty.demo.common.RequestMessage;
import com.tang.netty.demo.common.auth.AuthOperation;
import com.tang.netty.demo.common.order.OrderOperation;
import com.tang.netty.demo.util.IdUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioChannelOption;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;

import javax.net.ssl.SSLException;
import java.util.concurrent.ExecutionException;

public class ClientV20 {

    public static void main(String[] args) throws InterruptedException, ExecutionException, SSLException {

        Bootstrap bootstrap = new Bootstrap();
        bootstrap.channel(NioSocketChannel.class);

        bootstrap.option(NioChannelOption.CONNECT_TIMEOUT_MILLIS, 10 * 1000);

        NioEventLoopGroup group = new NioEventLoopGroup();
        try {

            bootstrap.group(group);

            KeepaliveHandler keepaliveHandler = new KeepaliveHandler();
            LoggingHandler loggingHandler = new LoggingHandler(LogLevel.INFO);

            SslContextBuilder sslContextBuilder = SslContextBuilder.forClient();

            //下面这行，先直接信任自签证书，以避免没有看到ssl那节课程的同学运行不了；
            //学完ssl那节后，可以去掉下面这行代码，安装证书，安装方法参考课程，执行命令参考resources/ssl.txt里面
            sslContextBuilder.trustManager(InsecureTrustManagerFactory.INSTANCE);

            SslContext sslContext = sslContextBuilder.build();

            // 客户端只有 handler
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

                    pipeline.addLast(loggingHandler);

                    pipeline.addLast(keepaliveHandler);

                }
            });

            ChannelFuture channelFuture = bootstrap.connect("127.0.0.1", 8090);
            // 先连接上
            channelFuture.sync();

            AuthOperation authOperation = new AuthOperation("admin", "password");

            channelFuture.channel().writeAndFlush(new RequestMessage(IdUtil.nextId(), authOperation));


            RequestMessage requestMessage = new RequestMessage(IdUtil.nextId(), new OrderOperation(1001, "tudou"));

            channelFuture.channel().writeAndFlush(requestMessage);

            // 两者区别不大，内部都是wait来等，区别在于一个有返回结果，一个无返回结果；
            // 作用：当thread通过下面类似的方式设置成守护线程时：
            // EventLoopGroup group = new NioEventLoopGroup(0, new DefaultThreadFactory("boss", true));
            // 加这样一句可以阻止客户端直接退出了。同时配合finally（执行关闭）可以实现优美的被动关闭。
//            channelFuture.channel().closeFuture().get();
            channelFuture.channel().closeFuture().sync();

        } finally {
            group.shutdownGracefully();
        }

    }

}
