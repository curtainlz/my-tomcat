package com.curtainlz.cataline.server;

import com.curtainlz.cataline.handler.CustomServerHandler;
import com.curtainlz.cataline.servlets.ImplementServlet;
import com.curtainlz.cataline.servlets.ServletMapping;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author curtainlz
 * @title SimpleTomcat
 * @description 用netty开发简易的tomcat
 */
public class SimpleTomcat {

    private static final Properties webProperties = new Properties();

    public void init() {
        try {

            String path = this.getClass().getClassLoader().getResource("web.properties").getPath();
            InputStream inputStream = new FileInputStream(path);
            webProperties.load(inputStream);

            for (Object item : webProperties.keySet()) {
                String key = (String) item;
                if (key.endsWith(".url")) {
                    String servletKey = key.replaceAll("\\.url", "\\.className");
                    String servletName = webProperties.getProperty(servletKey);

                    ImplementServlet servlet = (ImplementServlet) Class.forName(servletName).newInstance();
                    ServletMapping.getUrlServletMapping().put(webProperties.getProperty(key), servlet);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void start(int port) {
        init();
        // Boos 线程
        EventLoopGroup boosGroup = new NioEventLoopGroup();
        // work 线程
        EventLoopGroup workGroup = new NioEventLoopGroup();
        try {
            // Netty 服务
            ServerBootstrap server = new ServerBootstrap();
            server.group(boosGroup, workGroup)
                    // NIO服务端通道,主线程处理类
                    .channel(NioServerSocketChannel.class)
                    // 工作线程处理,类似NIO例子中的handle方法
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        //客户端连接后的处理
                        @Override
                        protected void initChannel(SocketChannel channel) {
                            // netty对http的封装，对顺序有要求
                            // 责任链模式，双向链表 InBound、OutBound
                            // InBound 从上至下执行
                            // OutBound 由下至上执行
                            // http编码器
                            channel.pipeline().addLast(new HttpResponseEncoder());
                            // http解码器
                            channel.pipeline().addLast(new HttpRequestDecoder());
                            // Http 对象聚合器
                            channel.pipeline().addLast(new HttpObjectAggregator(65535));
                            // 业务逻辑处理
                            channel.pipeline().addLast(new CustomServerHandler());
                        }
                    })
                    // 配置信息
                    .option(ChannelOption.SO_BACKLOG, 150) //初始化服务器连接队列大小 这里分配最大队列128
                    .childOption(ChannelOption.SO_KEEPALIVE, true);  //工作线程配置，保持长连接

            //启动服务器(并绑定端口)，bind是异步操作，sync方法是等待异步操作执行完毕，通过isDone()等方法可以判断异步事件的执行情况
            ChannelFuture channelFuture = server.bind(port).sync();
            boolean done = channelFuture.isDone();
            System.out.printf("SimpleTomcat is started  %s...  ,port:%s%n", done, port);
            //通过sync方法同步等待通道关闭处理完毕，这里会阻塞等待通道关闭完成
            channelFuture.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            boosGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        try {
            new SimpleTomcat().start(8000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
