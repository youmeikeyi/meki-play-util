/*
 * Copyright (C) 2019 Baidu, Inc. All Rights Reserved.
 */
package com.meki.play.util;

import java.util.zip.CRC32;

/**
 * CRC32工具类
 *
 * @author Administrator
 */
public class CRC32Utils {

    public static final Long encode(final String value) {
        CRC32 crc32 = new CRC32();
        crc32.update(value.getBytes());
        return crc32.getValue();
    }

}
