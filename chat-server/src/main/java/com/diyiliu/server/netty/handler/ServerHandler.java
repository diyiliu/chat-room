package com.diyiliu.server.netty.handler;

import com.diyiliu.common.cache.ICache;
import com.diyiliu.common.util.SpringUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Description: ServerHandler
 * Author: DIYILIU
 * Update: 2018-03-01 13:10
 */

public class ServerHandler extends ChannelInboundHandlerAdapter {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private ICache onlineCacheProvider;

    private String host;

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        host = ctx.channel().remoteAddress().toString().trim().replaceFirst("/", "");
        logger.info("[{}]建立连接...", host);

        onlineCacheProvider = SpringUtil.getBean("onlineCacheProvider");

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
        if (content.startsWith("[user]:") && content.endsWith("$")){

            String[] strArr = content.split(":");
            String user = strArr[1].replace("$", "");

            onlineCacheProvider.put(host, user);

            String response = "1" + System.lineSeparator();
            ByteBuf byteBuf = Unpooled.copiedBuffer(response.getBytes());
            ctx.writeAndFlush(byteBuf);

            return;
        }

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.error("服务器异常[{}]!", cause.getMessage());
        cause.printStackTrace();
    }
}
