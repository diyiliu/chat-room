package com.diyiliu.client.netty;

import com.diyiliu.client.netty.handler.ClientHandler;
import com.diyiliu.client.support.ui.ClientUI;
import com.diyiliu.client.support.ui.LoginUI;
import com.diyiliu.common.thread.ChannelThread;
import com.diyiliu.common.util.SpringUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.timeout.IdleStateHandler;

/**
 * Description: ChatClient
 * Author: DIYILIU
 * Update: 2018-03-01 13:24
 */
public class ChatClient extends ChannelThread {
    private String host;
    private int port;

    private String account;

    private ClientUI clientUI;
    private LoginUI loginUI;

    private ClientHandler clientHandler;

    // 重连次数
    private int reconnect = 0;
    // 最大重连次数
    private final static int MAX_REC_TIME = 3;



    public ChatClient() {
        loginUI = SpringUtil.getBean("loginUI");
        clientUI = SpringUtil.getBean("clientUI");
    }

    @Override
    public void run() {
        connectServer(host, port);
    }

    public void connectServer(String host, int port) {
        EventLoopGroup group = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();

        clientHandler = new ClientHandler(account, clientUI);
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                //.option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        ch.pipeline().addLast(new LineBasedFrameDecoder(1024))
                                .addLast(new StringDecoder())
                                .addLast(new IdleStateHandler(0, 40, 0))
                                .addLast(clientHandler);
                    }
                });

        try {
            future = bootstrap.connect(host, port).sync();
            refreshUI();
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
            if (reconnect < MAX_REC_TIME ) {
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                logger.info("客户端, 尝试第{}次重连...", ++reconnect);
                connectServer(host, port);
                return;
            }
            logger.warn("客户端重连失败！");

            if (clientUI.isShowing()){
                clientUI.dispose();
            }

            if (!loginUI.isShowing()){
                loginUI.setVisible(true);
            }
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

    /**
     * 连接服务成功
     */
    public void refreshUI(){
        logger.info("客户端启动...");
        reconnect = 0;

        clientUI.getLbAccount().setText(account);
        clientUI.setVisible(true);
        clientUI.setContext(clientHandler.getContext());

        if (loginUI.isShowing()){
            loginUI.setVisible(false);
        }
    }
}
