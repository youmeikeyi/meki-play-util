/*
 * Copyright (C) 2019 Baidu, Inc. All Rights Reserved.
 */
package com.meki.play.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * MD5工具类
 *
 * @author Administrator
 */
public class MD5Utils {

    private static final Logger LOG = LoggerFactory.getLogger(MD5Utils.class);

    private static final int BUFFER_SIZE = 256 * 1024;

    public static final boolean md5check(byte[] data, String md5) {
        String sum = md5sum(data);
        return sum.equalsIgnoreCase(md5);
    }

    /**
     * 使用md5的算法进行加密
     */
    public static String md5(String plainText) {
        byte[] secretBytes = null;
        try {
            secretBytes = MessageDigest.getInstance("md5").digest(
                    plainText.getBytes());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("没有md5这个算法！");
        }
        String md5code = new BigInteger(1, secretBytes).toString(16); // 16进制数字

        // 如果生成数字未满32位，需要前面补0
        for (int i = 0; i < 32 - md5code.length(); i++) {
            md5code = "0" + md5code;
        }
        return md5code;
    }

    public static final String md5sum(byte[] data) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(data);
            return byteToBase16(md.digest());
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException("MD5 not supported.", e);
        }
    }

    public static final String byteToBase16(byte[] bytes) {
        if (bytes == null) {
            throw new IllegalArgumentException("The parameter should not be null!");
        }
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            sb.append(Integer.toHexString((b & 0xF0) >> 4));
            sb.append(Integer.toHexString(b & 0xF));
        }
        return sb.toString();
    }

    public static String fileMD5(String inputFile) throws IOException {

        String md5Code = StringUtils.EMPTY;
        FileInputStream fileInputStream = null;
        DigestInputStream digestInputStream = null;

        try {

            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            fileInputStream = new FileInputStream(inputFile);
            digestInputStream = new DigestInputStream(fileInputStream, messageDigest);
            byte[] buffer = new byte[BUFFER_SIZE];
            int readBuffer = digestInputStream.read(buffer);
            while (readBuffer > 0) {
                readBuffer = digestInputStream.read(buffer);
            }
            messageDigest = digestInputStream.getMessageDigest();
            byte[] resultByteArray = messageDigest.digest();
            md5Code = byteToBase16(resultByteArray);
        } catch (Exception e) {
            throw new IOException(e);
        } finally {
            if (fileInputStream != null) {
                fileInputStream.close();
            }
            if (digestInputStream != null) {
                digestInputStream.close();
            }
        }

        return md5Code;
    }

    public static String appKeyOfiLead(String md5Str) {
        String result = "";

        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");

            messageDigest.update((md5Str).getBytes("UTF-8"));
            byte[] bytes = messageDigest.digest();

            int i;
            StringBuffer buf = new StringBuffer("");

            for (int offset = 0; offset < bytes.length; offset++) {
                i = bytes[offset];
                if (i < 0) {
                    i += 256;
                }
                if (i < 16) {
                    buf.append("0");
                }
                buf.append(Integer.toHexString(i));
            }

            result = buf.toString();
        } catch (Exception e) {
            throw new RuntimeException("Error get md5 ...");
        }
        return result;
    }

    /**
     * 使用md5的算法进行加密 生成广告标识
     *
     * @param advertisementEncryptionBean
     *
     * @return 广告标识
     */
    public static String md5ArticleMaterialForAdFlag(AdvertisementEncryptionBean advertisementEncryptionBean) {
        StringBuilder strBuilder = new StringBuilder();

        String adTitle = String.valueOf(advertisementEncryptionBean.getTitle());
        strBuilder.append(adTitle);

        String adDisplayMethod = String.valueOf(advertisementEncryptionBean.getAdDisplayMethod());
        strBuilder.append(adDisplayMethod);

        String adSrc = String.valueOf(advertisementEncryptionBean.getAdSrc());
        strBuilder.append(adSrc);
        strBuilder.append(String.valueOf(advertisementEncryptionBean.getPublishTime()));
        // todo  url处理
        String originUrl = String.valueOf(advertisementEncryptionBean.getUrl());
        strBuilder.append(originUrl);

        return md5(strBuilder.toString());

    }

}
