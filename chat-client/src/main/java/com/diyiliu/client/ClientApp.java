package com.diyiliu.client;

import com.diyiliu.client.netty.ChatClient;

/**
 * Description: ClientApp
 * Author: DIYILIU
 * Update: 2018-03-01 13:23
 */
public class ClientApp {

    public static void main(String[] args) {
        ChatClient client = new ChatClient();
        client.setHost("192.168.1.132");
        client.setPort(8888);

        client.start();
    }
}
