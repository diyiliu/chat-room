package com.diyiliu.common.thread;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Description: ChannelThread
 * Author: DIYILIU
 * Update: 2018-03-01 19:28
 */
public class ChannelThread  extends Thread{
    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    protected ChannelFuture future;

    public void init(){

        this.start();
    }

    @Override
    public void run() {


    }

    /**
     * 关闭连接
     */
    public void shutdown(){
        Channel channel = future.channel();
        if (channel.isActive()){
            channel.close();
        }
    }
}
