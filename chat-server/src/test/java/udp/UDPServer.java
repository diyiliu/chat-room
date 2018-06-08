package udp;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Description: UDPServer
 * Author: DIYILIU
 * Update: 2018-06-08 15:55
 */

public class UDPServer extends Thread{
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private int port = 8888;

    public void init() {
        this.start();
    }

    @Override
    public void run() {
        logger.info("服务器启动, 端口[{}] ...", port);

        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();

            b.group(group)
                    .channel(NioDatagramChannel.class)
                    .option(ChannelOption.SO_BROADCAST, true)
                    .handler(new UDPHandler());

            // 绑定端口，同步等待成功
            b.bind(port).sync().channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        UDPServer server = new UDPServer();
        server.init();
    }
}
