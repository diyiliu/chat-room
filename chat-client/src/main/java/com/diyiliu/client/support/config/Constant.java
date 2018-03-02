package com.diyiliu.client.support.config;

import com.diyiliu.client.support.model.ClientMsg;

import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * Description: Constant
 * Author: DIYILIU
 * Update: 2018-03-02 13:23
 */
public class Constant {

    public final static ConcurrentLinkedDeque<ClientMsg> MSG_LINKED_DEQUE = new ConcurrentLinkedDeque();
}
