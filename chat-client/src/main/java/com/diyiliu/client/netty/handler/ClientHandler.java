package com.diyiliu.client.netty.handler;

import com.diyiliu.client.support.config.Constant;
import com.diyiliu.client.support.model.ClientMsg;
import com.diyiliu.client.support.ui.ClientUI;
import com.diyiliu.common.util.DateUtil;
import com.diyiliu.common.util.JacksonUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import java.awt.*;
import java.util.Date;
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

    private ChannelHandlerContext context;

    public ClientHandler(String account, ClientUI clientUI) {
        this.account = account;
        this.clientUI = clientUI;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        logger.info("已连接服务器...");
        this.context = ctx;

        String msg = "[user]^" + account + "$" + System.lineSeparator();
        ByteBuf byteBuf = Unpooled.copiedBuffer(msg.getBytes());
        ctx.writeAndFlush(byteBuf);
    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        String content = (String) msg;

        // 客户端列表
        if (content.startsWith("[list]^") && content.endsWith("$")) {
            String jsonList = washMsg(content);

            List<String> userList = JacksonUtil.toList(jsonList, String.class);
            refreshUserList(userList);
            return;
        }

        // 客户端消息
        if (content.startsWith("[message]^") && content.endsWith("$")) {
            String message = washMsg(content);

            String[] strArr = message.split("~");

            String user = strArr[0];
            String info = strArr[1];

            ClientMsg clientMsg = new ClientMsg(user, info, System.currentTimeMillis());
            refreshHistoryMsg(clientMsg);
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
    private void refreshUserList(List<String> list) {
        String[] strings = list.toArray(new String[]{});

        logger.info("用户列表:" + JacksonUtil.toJson(strings));
        clientUI.getLtUser().setModel(new javax.swing.AbstractListModel<String>() {
            public int getSize() {
                return strings.length;
            }

            public String getElementAt(int i) {
                return strings[i];
            }
        });
    }

    /**
     * 刷新消息内容
     *
     * @param msg
     */
    private void refreshHistoryMsg(ClientMsg msg) {
        ClientMsg lastMsg = Constant.MSG_LINKED_DEQUE.peek();

        Constant.MSG_LINKED_DEQUE.add(msg);
        if (Constant.MSG_LINKED_DEQUE.size() > 100) {
            Constant.MSG_LINKED_DEQUE.poll();
        }

        JTextPane textPane = clientUI.getTpContent();
        textPane.setContentType("text/html");

        try {
            if (lastMsg != null) {
                if (msg.getDatetime() - lastMsg.getDatetime() > 60 * 1000) {
                    Date date = new Date(msg.getDatetime());
                    showContent(textPane, DateUtil.dateToString(date, "%1$tH:%1$tM:%1$tS"),
                            Color.GRAY, 14, StyleConstants.ALIGN_CENTER, 0);
                }
            }

            if (msg.getUser().equals(account)) {
                showContent(textPane, msg.getUser(), Color.GRAY, 14, StyleConstants.ALIGN_RIGHT, 0);
                showContent(textPane, msg.getContent(), Color.ORANGE, 16, StyleConstants.ALIGN_RIGHT, 20);
            } else {
                showContent(textPane, msg.getUser(), Color.GRAY, 14, StyleConstants.ALIGN_LEFT, 0);
                showContent(textPane, msg.getContent(), Color.DARK_GRAY, 16, StyleConstants.ALIGN_LEFT, 20);
            }

            JScrollBar scrollBar = clientUI.getSclPnContent().getVerticalScrollBar();
            scrollBar.setValue(scrollBar.getMaximum());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showContent(JTextPane textPane, String msg, Color color,
                             int fontSize, int align, float indent) throws BadLocationException {
        SimpleAttributeSet attributeSet = new SimpleAttributeSet();

        StyleConstants.setForeground(attributeSet, color);
        StyleConstants.setFontSize(attributeSet, fontSize);
        StyleConstants.setAlignment(attributeSet, align);

        if (indent > 0) {
            if (align == StyleConstants.ALIGN_RIGHT) {
                StyleConstants.setRightIndent(attributeSet, indent);

            } else if (align == StyleConstants.ALIGN_LEFT) {
                StyleConstants.setLeftIndent(attributeSet, indent);
            }
        }

        Document document = textPane.getDocument();
        document.insertString(document.getLength(), msg + System.lineSeparator(), attributeSet);
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

    public ChannelHandlerContext getContext() {
        return context;
    }
}
