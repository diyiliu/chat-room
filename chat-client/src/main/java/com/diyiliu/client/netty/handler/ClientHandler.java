package com.diyiliu.client.netty.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Description: ClientHandler
 * Author: DIYILIU
 * Update: 2018-03-01 13:24
 */
public class ClientHandler extends ChannelInboundHandlerAdapter {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private String user;

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        logger.info("已连接服务器...");

        user = "diyiliu";
        String msg = "[user]:" + user + "$" + System.lineSeparator();

        ByteBuf byteBuf = Unpooled.copiedBuffer(msg.getBytes());
        ctx.writeAndFlush(byteBuf);
    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        String content = (String) msg;

        System.out.println(content);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {


    }
}
