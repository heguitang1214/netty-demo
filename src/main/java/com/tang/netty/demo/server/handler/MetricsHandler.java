package com.tang.netty.demo.server.handler;

import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.Gauge;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.jmx.JmxReporter;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 可视化
 * ChannelDuplexHandler：支持输入、输出
 * ChannelHandler.Sharable：可共享
 *
 * @author heguitang
 */
@ChannelHandler.Sharable
public class MetricsHandler extends ChannelDuplexHandler {

    /**
     * 当前连接数
     */
    private AtomicLong totalConnectionNumber = new AtomicLong();

    {
        MetricRegistry metricRegistry = new MetricRegistry();

        metricRegistry.register("totalConnectionNumber", new Gauge<Long>() {
            @Override
            public Long getValue() {
                return totalConnectionNumber.longValue();
            }
        });

        // Console 方式展示
        ConsoleReporter consoleReporter = ConsoleReporter.forRegistry(metricRegistry).build();
        consoleReporter.start(10, TimeUnit.SECONDS);

        // jmx 方式展示
        JmxReporter jmxReporter = JmxReporter.forRegistry(metricRegistry).build();
        jmxReporter.start();
    }

    /**
     * 创建连接
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        totalConnectionNumber.incrementAndGet();
        super.channelActive(ctx);
    }

    /**
     * 释放连接
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        totalConnectionNumber.decrementAndGet();
        super.channelInactive(ctx);
    }
}
