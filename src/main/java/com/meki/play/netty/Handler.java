/*
 * Copyright (C) 2019 Baidu, Inc. All Rights Reserved.
 */
package com.meki.play.netty;

import java.io.IOException;
import java.nio.channels.SelectionKey;

/**
 * nio single thread reactor mode
 * Created by xujinchao on 18/8/17.
 */
public interface Handler {
    void handle(SelectionKey selectionKey) throws IOException;
}
