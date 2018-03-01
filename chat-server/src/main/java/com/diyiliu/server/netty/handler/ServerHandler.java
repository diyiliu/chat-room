package com.diyiliu.server.netty.handler;

import com.diyiliu.common.cache.ICache;
import com.diyiliu.common.util.JacksonUtil;
import com.diyiliu.common.util.SpringUtil;
import com.diyiliu.server.support.ui.ServerUI;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Description: ServerHandler
 * Author: DIYILIU
 * Update: 2018-03-01 13:10
 */

public class ServerHandler extends ChannelInboundHandlerAdapter {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private ServerUI serverUI;

    private ICache onlineCacheProvider;

    private ICache clientCacheProvider;

    public ServerHandler(ServerUI serverUI) {
        this.serverUI = serverUI;

        onlineCacheProvider = SpringUtil.getBean("onlineCacheProvider");
        clientCacheProvider = SpringUtil.getBean("clientCacheProvider");
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        String host = ctx.channel().remoteAddress().toString().trim().replaceFirst("/", "");
        logger.info("[{}]建立连接...", host);

        // 断开连接
        ctx.channel().closeFuture().addListener(
                (ChannelFuture future) -> {
                    if (future.isDone()) {
                        logger.info("[{}]断开连接...", host);

                        remove(host);
                        refreshUserList();
                    }
                }
        );
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        String content = (String) msg;

        // 客户端注册
        if (content.startsWith("[user]^") && content.endsWith("$")) {
            String[] strArr = content.split("\\^");
            String user = strArr[1].replace("$", "");

            String host = ctx.channel().remoteAddress().toString().trim().replaceFirst("/", "");
            join(host, user, ctx);

            return;
        }

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.error("服务器异常[{}]!", cause.getMessage());
        cause.printStackTrace();
    }

    private void refreshUserList() {
        Set userSet = onlineCacheProvider.getKeys();

        List<String> list = new ArrayList();
        userSet.forEach(e -> {
            String user = (String) onlineCacheProvider.get(e);
            list.add(user);
        });

        String[] strings = list.toArray(new String[list.size()]);

        serverUI.getLtUser().setModel(new javax.swing.AbstractListModel<String>() {
            public int getSize() {
                return strings.length;
            }

            public String getElementAt(int i) {
                return strings[i];
            }
        });

        Set set = clientCacheProvider.getKeys();
        String jsonList = JacksonUtil.toJson(list);
        String msg = "[list]^" + jsonList + "$" + System.lineSeparator();

        set.forEach(e -> {
            ChannelHandlerContext ctx = (ChannelHandlerContext) clientCacheProvider.get(e);

            System.out.println(e + ":" + msg);

            ByteBuf byteBuf = Unpooled.copiedBuffer(msg.getBytes());
            ctx.writeAndFlush(byteBuf);
        });
    }

    /**
     * 加入缓存
     *
     * @param host
     * @param user
     * @param ctx
     */
    public void join(String host, String user, ChannelHandlerContext ctx) {
        synchronized (onlineCacheProvider) {
            onlineCacheProvider.put(host, user);
            clientCacheProvider.put(host, ctx);

            refreshUserList();
        }
    }

    /**
     * 清除缓存
     *
     * @param host
     */
    public void remove(String host) {
        synchronized (onlineCacheProvider) {
            onlineCacheProvider.remove(host);
            clientCacheProvider.remove(host);
        }
    }
}
