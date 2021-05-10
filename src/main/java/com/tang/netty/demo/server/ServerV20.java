package com.tang.netty.demo.server;

import com.tang.netty.demo.server.codec.OrderFrameDecoder;
import com.tang.netty.demo.server.codec.OrderFrameEncoder;
import com.tang.netty.demo.server.codec.OrderProtocolDecoder;
import com.tang.netty.demo.server.codec.OrderProtocolEncoder;
import com.tang.netty.demo.server.handler.AuthHandler;
import com.tang.netty.demo.server.handler.MetricsHandler;
import com.tang.netty.demo.server.handler.OrderServerProcessHandler;
import com.tang.netty.demo.server.handler.ServerIdleCheckHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.DefaultEventLoop;
import io.netty.channel.nio.NioEventLoop;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioChannelOption;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.flush.FlushConsolidationHandler;
import io.netty.handler.ipfilter.IpFilterRuleType;
import io.netty.handler.ipfilter.IpSubnetFilterRule;
import io.netty.handler.ipfilter.RuleBasedIpFilter;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import io.netty.handler.traffic.GlobalTrafficShapingHandler;
import io.netty.util.concurrent.DefaultThreadFactory;
import io.netty.util.concurrent.UnorderedThreadPoolEventExecutor;

import javax.net.ssl.SSLException;
import java.security.cert.CertificateException;
import java.util.concurrent.ExecutionException;


/**
 * 简单服务端
 * <p>
 * Netty 系统参数调优
 */
public class ServerV20 {

    public static void main(String[] args) throws InterruptedException, ExecutionException, CertificateException, SSLException {

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
        UnorderedThreadPoolEventExecutor business = new UnorderedThreadPoolEventExecutor(10, new DefaultThreadFactory("business"));
        // 不使用NioEventLoopGroup的方式，不能发挥多线程的优势，只能使用一个线程
        NioEventLoopGroup business1 = new NioEventLoopGroup(0, new DefaultThreadFactory("business"));

        GlobalTrafficShapingHandler globalTrafficShapingHandler = new GlobalTrafficShapingHandler(new NioEventLoopGroup(), 100 * 1024 * 1024, 100 * 1024 * 1024);

        // 127.0.0.1  8
        IpSubnetFilterRule ipSubnetFilterRule = new IpSubnetFilterRule("127.1.0.1", 16, IpFilterRuleType.REJECT);
        RuleBasedIpFilter ruleBasedIpFilter = new RuleBasedIpFilter(ipSubnetFilterRule);

        AuthHandler authHandler = new AuthHandler();

        // SSL 单向认证客户端
        SelfSignedCertificate selfSignedCertificate = new SelfSignedCertificate();
        System.out.println("证书位置：" + selfSignedCertificate.certificate());
        SslContext sslContext = SslContextBuilder.forServer(selfSignedCertificate.certificate(), selfSignedCertificate.privateKey()).build();

        serverBootstrap.childHandler(new ChannelInitializer<NioSocketChannel>() {
            @Override
            protected void initChannel(NioSocketChannel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();
                // 放在这里，可以打印出原始数据
//                pipeline.addLast(new LoggingHandler(LogLevel.DEBUG));

                // 黑白名单
                pipeline.addLast("IPfilter", ruleBasedIpFilter);

                // 流量整形
                pipeline.addLast("TShandle", globalTrafficShapingHandler);

                // 空闲监测
                pipeline.addLast("idleCheck ", new ServerIdleCheckHandler());

                // SSL
                SslHandler sslHandler = sslContext.newHandler(ch.alloc());
                pipeline.addLast("SSL", sslHandler);

                // 完善”Handler“名称
                pipeline.addLast("FrameDecoder", new OrderFrameDecoder());
                pipeline.addLast(new OrderFrameEncoder());

                pipeline.addLast(new OrderProtocolEncoder());
                pipeline.addLast(new OrderProtocolDecoder());

                // 可视化
                pipeline.addLast("metricsHandler", metricsHandler);

                // 授权,必须在OrderProtocolDecoder之后
                pipeline.addLast(authHandler);

                // 解析后的日志数据
                pipeline.addLast(new LoggingHandler(LogLevel.INFO));


                pipeline.addLast("fulshEnhance",
                        // 5 次数据flush一次
                        // 打开异步增强
                        new FlushConsolidationHandler(5, true));

//                pipeline.addLast(new OrderServerProcessHandler());
                // 业务处理使用线程池
                pipeline.addLast(business, new OrderServerProcessHandler());
            }
        });

        ChannelFuture channelFuture = serverBootstrap.bind(8090).sync();

        channelFuture.channel().closeFuture().get();
    }

}
