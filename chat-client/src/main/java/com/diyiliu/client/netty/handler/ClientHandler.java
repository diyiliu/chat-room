package com.diyiliu.client.netty.handler;

import com.diyiliu.client.support.ui.ClientUI;
import com.diyiliu.common.util.JacksonUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Description: ClientHandler
 * Author: DIYILIU
 * Update: 2018-03-01 13:24
 */
public class ClientHandler extends ChannelInboundHandlerAdapter {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private String account;
    private ClientUI clientUI;


    public ClientHandler(String account, ClientUI clientUI) {
        this.account = account;
        this.clientUI = clientUI;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        logger.info("已连接服务器...");
        String msg = "[user]^" + account + "$" + System.lineSeparator();

        ByteBuf byteBuf = Unpooled.copiedBuffer(msg.getBytes());
        ctx.writeAndFlush(byteBuf);
    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception{
        String content = (String) msg;

        // 客户端列表
        if (content.startsWith("[list]^") && content.endsWith("$")){
            String[] strArr = content.split("\\^");
            String jsonList = strArr[1].replace("$", "");

            List<String> userList = JacksonUtil.toList(jsonList, String.class);
            refreshUserList(userList);
            return;
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.error("客户端异常[{}]!", cause.getMessage());
        cause.printStackTrace();
    }

    /**
     * 刷新侧边栏用户列表
     *
     * @param list
     */
    private void refreshUserList(List<String> list){
        String[] strings = list.toArray(new String[]{});

        logger.info("用户列表:" + JacksonUtil.toJson(strings));
        clientUI.getLtUser().setModel(new javax.swing.AbstractListModel<String>() {
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
    }
}
