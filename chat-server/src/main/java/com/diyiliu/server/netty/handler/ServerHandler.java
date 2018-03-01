package com.diyiliu.server.netty.handler;

import com.diyiliu.common.cache.ICache;
import com.diyiliu.common.util.JacksonUtil;
import com.diyiliu.common.util.SpringUtil;
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

    private ICache onlineCacheProvider;

    private ICache clientCacheProvider;

    // 不能共享
    private List userList = new ArrayList();

    public ServerHandler() {
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
                    if (future.isDone()){
                        logger.info("[{}]断开连接...", host);
                    }
                }
        );
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        String content = (String) msg;

        // 客户端注册
        if (content.startsWith("[user]^") && content.endsWith("$")){
            String[] strArr = content.split("\\^");
            String user = strArr[1].replace("$", "");

            String host = ctx.channel().remoteAddress().toString().trim().replaceFirst("/", "");

            userList.add(user);
            onlineCacheProvider.put(host, user);
            clientCacheProvider.put(user, ctx);

            refreshUserList();
            return;
        }

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.error("服务器异常[{}]!", cause.getMessage());
        cause.printStackTrace();
    }

    private void refreshUserList(){
       Set set =  clientCacheProvider.getKeys();

       String jsonList = JacksonUtil.toJson(userList);
       String msg = "[list]^" + jsonList + "$" + System.lineSeparator();

       ByteBuf byteBuf = Unpooled.copiedBuffer(msg.getBytes());
       set.forEach(e ->{
           ChannelHandlerContext ctx = (ChannelHandlerContext) clientCacheProvider.get(e);
           ctx.writeAndFlush(byteBuf);
       });
    }
}
