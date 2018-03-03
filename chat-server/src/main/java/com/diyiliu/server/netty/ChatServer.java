package com.diyiliu.server.netty;

import com.diyiliu.common.thread.ChannelThread;
import com.diyiliu.server.netty.handler.ServerHandler;
import com.diyiliu.server.support.ui.ServerUI;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.nio.charset.Charset;

/**
 * Description: ChatServer
 * Author: DIYILIU
 * Update: 2018-03-01 13:07
 */

public class ChatServer extends ChannelThread {
    // 端口号
    private int port;

    private ServerUI serverUI;

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
                        protected void initChannel(SocketChannel ch) {

                            ch.pipeline().addLast(new StringEncoder(Charset.forName("UTF-8")))
                                    .addLast(new LineBasedFrameDecoder(1024))
                                    .addLast(new StringDecoder(Charset.forName("UTF-8")))
                                    .addLast(new ServerHandler(serverUI));
                        }
                    });

            future = b.bind(port).sync();

            logger.info("服务器启动[{}]...", port);
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

    public void setServerUI(ServerUI serverUI) {
        this.serverUI = serverUI;
    }
}
