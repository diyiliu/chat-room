package com.diyiliu.server.support.model;

import io.netty.channel.ChannelHandlerContext;

/**
 * Description: ClientPipeline
 * Author: DIYILIU
 * Update: 2018-03-02 10:05
 */
public class ClientPipeline {

    private String user;

    private boolean online = false;

    private long datetime;

    private ChannelHandlerContext context;

    public ClientPipeline() {

    }

    public ClientPipeline(ChannelHandlerContext context) {
        this.datetime = System.currentTimeMillis();
        this.context = context;
    }

    public ClientPipeline(String user, ChannelHandlerContext context) {
        this.user = user;
        this.context = context;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public ChannelHandlerContext getContext() {
        return context;
    }

    public void setContext(ChannelHandlerContext context) {
        this.context = context;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public long getDatetime() {
        return datetime;
    }

    public void setDatetime(long datetime) {
        this.datetime = datetime;
    }
}
