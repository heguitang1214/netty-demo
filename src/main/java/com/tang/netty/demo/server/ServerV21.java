package com.tang.netty.demo.server;

import com.tang.netty.demo.server.codec.OrderFrameDecoder;
import com.tang.netty.demo.server.codec.OrderFrameEncoder;
import com.tang.netty.demo.server.codec.OrderProtocolDecoder;
import com.tang.netty.demo.server.codec.OrderProtocolEncoder;
import com.tang.netty.demo.server.handler.MetricsHandler;
import com.tang.netty.demo.server.handler.OrderServerProcessHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.DefaultEventLoop;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioChannelOption;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.DefaultThreadFactory;

import java.util.concurrent.ExecutionException;


/**
 * 简单服务端
 * <p>
 * Netty 系统参数调优
 */
public class ServerV21 {

    public static void main(String[] args) throws InterruptedException, ExecutionException {

        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.channel(NioServerSocketChannel.class);

        serverBootstrap.handler(new LoggingHandler(LogLevel.INFO));

        // Netty 系统参数设置
        serverBootstrap.childOption(NioChannelOption.TCP_NODELAY, true);
        serverBootstrap.option(NioChannelOption.SO_BACKLOG, 1024);

        // 线程名设置
        NioEventLoopGroup boss = new NioEventLoopGroup(0, new DefaultThreadFactory("boss"));
        NioEventLoopGroup worker = new NioEventLoopGroup(0, new DefaultThreadFactory("worker"));

        serverBootstrap.group(boss);

        // Netty 系统参数设置
        serverBootstrap.childOption(NioChannelOption.TCP_NODELAY, true);
        serverBootstrap.option(NioChannelOption.SO_BACKLOG, 1024);

        MetricsHandler metricsHandler = new MetricsHandler();

        serverBootstrap.childHandler(new ChannelInitializer<NioSocketChannel>() {
            @Override
            protected void initChannel(NioSocketChannel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();
                // 放在这里，可以打印出原始数据
//                pipeline.addLast(new LoggingHandler(LogLevel.DEBUG));

                // 完善”Handler“名称
                pipeline.addLast("FrameDecoder", new OrderFrameDecoder());
                pipeline.addLast(new OrderFrameEncoder());

                pipeline.addLast(new OrderProtocolEncoder());
                pipeline.addLast(new OrderProtocolDecoder());

                // 可视化 
                pipeline.addLast("metricsHandler", metricsHandler);

                // 解析后的日志数据
                pipeline.addLast(new LoggingHandler(LogLevel.INFO));
                pipeline.addLast(new OrderServerProcessHandler());
            }
        });

        ChannelFuture channelFuture = serverBootstrap.bind(8090).sync();

        channelFuture.channel().closeFuture().get();
    }

}
