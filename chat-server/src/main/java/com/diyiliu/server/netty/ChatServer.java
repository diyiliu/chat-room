package com.diyiliu.server.netty;

import com.diyiliu.server.netty.handler.ServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Description: ChatServer
 * Author: DIYILIU
 * Update: 2018-03-01 13:07
 */

public class ChatServer extends Thread{
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    // 端口号
    private int port;

    private  ChannelFuture future;

    public void init(){

        this.start();
    }

    @Override
    public void run() {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap b = new ServerBootstrap();

            b.group(bossGroup, workGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .childHandler(new ChannelInitializer<SocketChannel>() {

                        @Override
                        protected void initChannel(SocketChannel ch)  {

                            ch.pipeline().addLast(new LineBasedFrameDecoder(1024))
                                    .addLast(new StringDecoder())
                                    .addLast(new ServerHandler());
                        }
                    });

            future = b.bind(port).sync();

            logger.info("服务器启动...");
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            logger.info("服务器已停止!");
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }

    public void setPort(int port) {
        this.port = port;
    }

    /**
     * 关闭连接
     */
    public void shutdown(){
        Channel channel = future.channel();
        if (channel.isActive()){
            channel.close();
        }
    }
}
