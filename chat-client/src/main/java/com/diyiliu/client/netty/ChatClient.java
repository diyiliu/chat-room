package com.diyiliu.client.netty;

import com.diyiliu.client.netty.handler.ClientHandler;
import com.diyiliu.client.support.ui.ClientUI;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Description: ChatClient
 * Author: DIYILIU
 * Update: 2018-03-01 13:24
 */
public class ChatClient extends Thread {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private String host;
    private int port;

    private String account;
    private ClientUI clientUI;


    // 重连次数
    private int reconnect;

    private boolean isRunning = false;

    @Override
    public void run() {
        connectServer(host, port);
    }

    public void connectServer(String host, int port) {
        EventLoopGroup group = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();

        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {

                        ch.pipeline().addLast(new LineBasedFrameDecoder(1024))
                                .addLast(new StringDecoder())
                                .addLast(new IdleStateHandler(0, 40, 0))
                                .addLast(new ClientHandler(account, clientUI));
                    }
                });

        try {
            ChannelFuture future = bootstrap.connect(host, port).sync();
            logger.info("客户端启动...");
            isRunning = true;
            reconnect = 0;
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
            isRunning = false;
            if (reconnect > 2) {
                logger.warn("客户端重连失败！");
                if (clientUI.isActive()){
                    clientUI.dispose();
                }
                return;
            }

            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            logger.info("客户端, 尝试第{}次重连...", ++reconnect);
            connectServer(host, port);
        }
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public void setClientUI(ClientUI clientUI) {
        this.clientUI = clientUI;
    }

    /**
     * 是否启动成功
     *
     * @return
     */
    public boolean isRunning() {
        return isRunning;
    }
}
