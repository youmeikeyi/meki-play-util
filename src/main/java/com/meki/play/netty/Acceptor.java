/*
 * Copyright (C) 2019 Baidu, Inc. All Rights Reserved.
 */
package com.meki.play.netty;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

/**
 * Created by xujinchao on 18/8/17.
 */
public class Acceptor implements Handler {

    static final Charset charset = Charset.forName("UTF-8");

    private ServerSocketChannel serverSocketChannel;

    private Selector selector;

    public Acceptor(ServerSocketChannel serverSocketChannel, Selector selector) {
        this.selector = selector;
        this.serverSocketChannel = serverSocketChannel;
    }

    @Override
    public void handle(SelectionKey selectionKey) throws IOException {
        System.out.println("[Event] connect");
        // setup connection
        SocketChannel socketChannel = serverSocketChannel.accept();
        System.out.println("[new client connected] client:" + socketChannel.getRemoteAddress());
        // 设置为非阻塞
        socketChannel.configureBlocking(false);
        // 创建Handler,专门处理该连接后续发生的OP_READ和OP_WRITE事件
        new EventHandler(socketChannel, this.selector);
        // 发送欢迎语
        socketChannel.write(charset.encode("welcome my client."));
    }
}
