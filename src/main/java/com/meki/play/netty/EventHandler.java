/*
 * Copyright (C) 2019 Baidu, Inc. All Rights Reserved.
 */
package com.meki.play.netty;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

/**
 * Created by xujinchao on 18/8/17.
 */
public class EventHandler implements Handler {

    SocketChannel socketChannel;
    SelectionKey selectionKey;

    static final int MAXIN = 256*1024;
    static final int MAXOUT = 256*1024;
    static final Charset charset = Charset.forName("UTF-8");

    ByteBuffer readBuffer = ByteBuffer.allocate(MAXIN);
    ByteBuffer outBuffer = ByteBuffer.allocate(MAXOUT);

    EventHandler(SocketChannel socketChannel, Selector selector) throws IOException {
        this.socketChannel = socketChannel;
        // register socket key, return selectionKey, and set read event on the key's interest set
        this.selectionKey = socketChannel.register(selector, SelectionKey.OP_READ);
        // bind the handler
        this.selectionKey.attach(this);
    }

    @Override
    public void handle(SelectionKey selectionKey) throws IOException {
        if (selectionKey.isReadable()) {
            System.out.println("[Event]read");
            read();
        } else if (selectionKey.isWritable()) {
            System.out.println("[Event]write");
            write();
        }

    }

    /**
     * 处理read事件
     * @throws IOException
     */
    private void read() throws IOException {
        // read data
        readBuffer.clear();
        StringBuilder content = new StringBuilder();
        int readNum = socketChannel.read(readBuffer);
        if (readNum == 0) {
            return;
        } else if (readNum < 0) {
            throw new IOException("Exception.");
        } else {
            readBuffer.flip();
            // decode
            content.append(charset.decode(readBuffer));
        }

        // process data
        process(content.toString());
        selectionKey.interestOps(SelectionKey.OP_WRITE);

    }


    /**
     * 处理客户端请求数据
     * @param content
     */
    private void process(String content) throws IOException{
        System.out.println("[receive from client] -> client:" + socketChannel.getRemoteAddress() + ", content: " + content);
        outBuffer = ByteBuffer.wrap(content.toUpperCase().getBytes());
    }

    /**
     * 处理write事件
     * @throws IOException
     */
    private void write() throws IOException {
        // 写数据
        socketChannel.write(outBuffer);
        if (outBuffer.remaining() > 0) {
            return;
        }
        //
        selectionKey.interestOps(SelectionKey.OP_READ);
    }
}
