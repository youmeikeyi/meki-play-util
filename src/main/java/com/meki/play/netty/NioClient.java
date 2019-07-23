/*
 * Copyright (C) 2019 Baidu, Inc. All Rights Reserved.
 */
package com.meki.play.netty;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Scanner;

/**
 * 手动输入客户端
 * Created by xujinchao on 18/8/17.
 */
public class NioClient {

    private InetAddress hostAddress;

    private int port;

    private Selector selector;
    private SocketChannel socketChannel;

    private ByteBuffer readBuffer = ByteBuffer.allocate(8192);

    static final Charset charset = Charset.forName("UTF-8");

    public NioClient(InetAddress hostAddress, int port) throws IOException {
        this.hostAddress = hostAddress;
        this.port = port;

        // init selector
        initSelector();
    }

    private void initSelector() throws IOException {
        // create a selector
        selector = SelectorProvider.provider().openSelector();
        // open socketChannel
        socketChannel = SocketChannel.open();
        socketChannel.configureBlocking(false);
        // connection point out ip and port address
        socketChannel.connect(new InetSocketAddress(this.hostAddress, this.port));

        // use selector register socket, return key ,and set interest set
        socketChannel.register(selector, SelectionKey.OP_CONNECT);

        // open new thread execute
        new Thread(new ClientThread()).start();

        // 在主线程中 从键盘读取数据输入到服务器端
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if ("".equals(line)) {
                // no permission to send empty message
                continue;
            }
            // scanner can write and read
            socketChannel.write(charset.encode(line));
        }
    }

    private class ClientThread implements Runnable {
        @Override
        public void run() {
            while (true) {
                try {
                    selector.select();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Iterator<SelectionKey> selectedKeys = selector.selectedKeys().iterator();
                while (selectedKeys.hasNext()) {
                    SelectionKey key = (SelectionKey) selectedKeys.next();
                    selectedKeys.remove();
                    if (!key.isValid()) {
                        continue;
                    }
                    dispatch(key);
                }
            }
        }

        /**
         * 事件处理分发
         *
         * @param key 已经ready的selectionKey
         */
        private void dispatch(SelectionKey key) {
            try {
                if (key.isConnectable()) {
                    System.out.println("[event]connect.");
                    finishConnection(key);
                } else if (key.isReadable()) {
                    System.out.println("[event]read");
                    read(key);
                }
            } catch (IOException e) {
                e.printStackTrace();
                key.channel();
                try {
                    if (key.channel() != null) {
                        key.channel().close();
                    }
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    /**
     * 完成与服务端连接
     *
     * @param key
     *
     * @throws IOException
     */
    private void finishConnection(SelectionKey key) throws IOException {
        SocketChannel socketChannel = (SocketChannel) key.channel();
        // 判断连接是否建立成功，不成功会抛异常
        socketChannel.finishConnect();
        // 设置Key的interest set为OP_WRITE事件
        key.interestOps(SelectionKey.OP_READ);
    }

    /**
     * 处理read
     *
     * @param key
     *
     * @throws IOException
     */
    private void read(SelectionKey key) throws IOException {
        // 读取数据
        SocketChannel socketChannel = (SocketChannel) key.channel();
        readBuffer.clear();
        StringBuilder content = new StringBuilder();
        int readNum = socketChannel.read(readBuffer);
        if (readNum == 0) {
            return;
        } else if (readNum < 0) {
            throw new IOException("exception.");
        } else {
            readBuffer.flip();
            content.append(charset.decode(readBuffer)); //decode
        }
        while (socketChannel.read(readBuffer) > 0) {
            readBuffer.flip();
            content.append(charset.decode(readBuffer));
        }
        // 处理数据
        process(content.toString(), key);
        // 设置Key的interest set为OP_READ事件
        //        key.interestOps(SelectionKey.OP_READ);
    }

    /**
     * 处理服务端响应数据
     *
     * @param content
     */
    private void process(String content, SelectionKey key) {
        System.out.println("[Client receive from server] -> content: " + content);
    }

    public static void main(String[] args) {
        try {
            new NioClient(InetAddress.getByName("localhost"), 9090);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
