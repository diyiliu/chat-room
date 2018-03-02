package com.diyiliu.server.netty.handler;

import com.diyiliu.common.cache.ICache;
import com.diyiliu.common.util.JacksonUtil;
import com.diyiliu.common.util.SpringUtil;
import com.diyiliu.server.support.model.ClientPipeline;
import com.diyiliu.server.support.ui.ServerUI;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Description: ServerHandler
 * Author: DIYILIU
 * Update: 2018-03-01 13:10
 */

public class ServerHandler extends ChannelInboundHandlerAdapter {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private ServerUI serverUI;

    private ICache onlineCacheProvider;

    public ServerHandler(ServerUI serverUI) {
        this.serverUI = serverUI;

        onlineCacheProvider = SpringUtil.getBean("onlineCacheProvider");
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        String host = ctx.channel().remoteAddress().toString().trim().replaceFirst("/", "");
        logger.info("[{}]建立连接...", host);

        onlineCacheProvider.put(host, new ClientPipeline(ctx));

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
        String host = ctx.channel().remoteAddress().toString().trim().replaceFirst("/", "");
        String content = (String) msg;

        // 客户端注册
        if (content.startsWith("[user]^") && content.endsWith("$")) {
            // 加入用户列表
            join(host, washMsg(content));
            return;
        }

        // 客户端消息
        if (content.startsWith("[message]^") && content.endsWith("$")) {
            ClientPipeline pipeline = (ClientPipeline) onlineCacheProvider.get(host);
            String user = pipeline.getUser();

            content = user + "~" + washMsg(content);
            broadcast(content, 1);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.error("服务器异常[{}]!", cause.getMessage());
        cause.printStackTrace();
    }

    /**
     * 清洗数据格式
     *
     * @param str
     * @return
     */
    public String washMsg(String str) {
        String[] strArr = str.split("\\^");

        return strArr[1].replace("$", "");
    }

    private void refreshUserList() {
        Set userSet = onlineCacheProvider.getKeys();
        List<ClientPipeline> list = new ArrayList();
        userSet.forEach(e -> {
            ClientPipeline pipeline = (ClientPipeline) onlineCacheProvider.get(e);
            if (pipeline.isOnline()) {
                list.add(pipeline);
            }
        });

        List<String> userList = list.stream()
                .sorted(Comparator.comparing(ClientPipeline::getDatetime))
                .map(ClientPipeline::getUser).collect(Collectors.toList());

        String[] strings = userList.toArray(new String[userList.size()]);
        serverUI.getLtUser().setModel(new javax.swing.AbstractListModel<String>() {
            public int getSize() {
                return strings.length;
            }

            public String getElementAt(int i) {
                return strings[i];
            }
        });

        String jsonList = JacksonUtil.toJson(userList);
        broadcast(jsonList, 0);
    }

    public void broadcast(String content, int type) {
        Set userSet = onlineCacheProvider.getKeys();

        String message = "";
        switch (type) {
            case 0:

                message = "[list]^" + content;
                break;
            case 1:

                message = "[message]^" + content;
                break;
            default:
        }

        String msg = message + "$" + System.lineSeparator();
        userSet.forEach(e -> {
            ClientPipeline pipeline = (ClientPipeline) onlineCacheProvider.get(e);
            if (pipeline.isOnline()) {

                ByteBuf byteBuf = Unpooled.copiedBuffer(msg.getBytes());
                pipeline.getContext().writeAndFlush(byteBuf);

                logger.info("广播消息[{}:{}]", e, content);
            }else {
                logger.warn("[{}]未注册!", e);
            }
        });

    }

    /**
     * 注册在线
     *
     * @param host
     * @param user
     */
    public void join(String host, String user) {
        synchronized (onlineCacheProvider) {
            ClientPipeline pipeline = (ClientPipeline) onlineCacheProvider.get(host);
            pipeline.setUser(user);
            pipeline.setOnline(true);
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
        }
    }
}
