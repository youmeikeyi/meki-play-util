/*
 * Copyright (C) 2019 Baidu, Inc. All Rights Reserved.
 */
package com.meki.play.netty;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.Iterator;

/**
 * Created by xujinchao on 18/8/17.
 * 服务端,会先发欢迎语,后续会将客户端发来的消息转成大写后返回
 */
public class NioServer {

    private InetAddress hostAddress;

    private int port;

    private Selector selector;

    private ServerSocketChannel serverSocketChannel;

    public NioServer(InetAddress hostAddress, int port) throws IOException {
        this.hostAddress = hostAddress;
        this.port = port;

        // init selector, bind server socket listener, set interest event and handler
        initSelector();
    }

    /**
     * 初始化selector,绑定服务端监听套接字、感兴趣事件及对应的handler
     * @return
     * @throws IOException
     */
    private void initSelector() throws IOException {
        // create a selector
        selector = SelectorProvider.provider().openSelector();
        // create and open serverSocketChannel
        serverSocketChannel = ServerSocketChannel.open();
        // set non-blocking
        serverSocketChannel.configureBlocking(false);
        // bind port
        serverSocketChannel.socket().bind(new InetSocketAddress(hostAddress, port));
        // use selector register socket, return key and set interest set
        SelectionKey selectionKey = serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        // bind handler
        selectionKey.attach(new Acceptor(serverSocketChannel, selector));
    }

    public void start() throws IOException {
        while (true) {
            /**
             * choose ready event's key, this is blocked
             * 只有当至少存在selectionKey,或者wakeup方法被调用,或者当前线程被中断,才会返回.
             */
            try {
                selector.select();
            } catch (IOException e) {
                e.printStackTrace();
            }

            // process every event in loop
            Iterator<SelectionKey> items = selector.selectedKeys().iterator();
            while (items.hasNext()) {
                SelectionKey key = items.next();
                items.remove();
                if (!key.isValid()) {
                    continue;
                }
                // process event dispatch
                dispatch(key);
            }

        }
    }

    /**
     * 事件处理分发
     * @param selectionKey 已经ready的selectionKey
     */
    private void dispatch(SelectionKey selectionKey) {
        Handler handler = (Handler) selectionKey.attachment();

        try {
            if (handler != null) {
                handler.handle(selectionKey);
            }
        } catch (IOException e) {
            e.printStackTrace();

            selectionKey.channel();
            try {
                if (selectionKey.channel() != null) {
                    selectionKey.channel().close();
                }
            } catch (IOException el) {
                el.printStackTrace();
            }
        }
    }



    public static void main(String[] args) {
        try {
            // 启动服务器
            new NioServer(null, 9090).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
