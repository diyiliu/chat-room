package udp;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.DatagramPacket;

import java.net.InetSocketAddress;
import java.util.*;

/**
 * Description: UDPHandler
 * Author: DIYILIU
 * Update: 2018-06-08 15:58
 */

public class UDPHandler extends ChannelInboundHandlerAdapter {

    private Set online = new HashSet();

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        DatagramPacket packet = (DatagramPacket) msg;

        InetSocketAddress socketAddress =  packet.sender();
        ByteBuf buf = packet.content();

        byte[] bytes = new byte[buf.readableBytes()];
        buf.readBytes(bytes);

        System.out.println("接收数据: " + new String(bytes));
        online.add(socketAddress);

        for (Iterator iterator = online.iterator(); iterator.hasNext(); ) {
            InetSocketAddress sender = (InetSocketAddress) iterator.next();
            ctx.writeAndFlush(new DatagramPacket(Unpooled.copiedBuffer("come on!".getBytes()), sender));
        }
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        System.out.println("error");
    }
}
