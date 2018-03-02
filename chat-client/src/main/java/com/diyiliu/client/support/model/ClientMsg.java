package com.diyiliu.client.support.model;

/**
 * Description: ClientMsg
 * Author: DIYILIU
 * Update: 2018-03-02 11:31
 */
public class ClientMsg {

    private String user;

    private String content;

    private Long datetime;

    public ClientMsg() {

    }

    public ClientMsg(String user, String content, Long datetime) {
        this.user = user;
        this.content = content;
        this.datetime = datetime;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getDatetime() {
        return datetime;
    }

    public void setDatetime(Long datetime) {
        this.datetime = datetime;
    }
}
