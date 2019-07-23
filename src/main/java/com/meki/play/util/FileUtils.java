/*
 * Copyright (C) 2019 Baidu, Inc. All Rights Reserved.
 */
package com.meki.play.util;

import com.google.common.collect.Lists;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

/**
 * 文件工具类
 *
 * @author Administrator
 */
public class FileUtils {

    public static String encodeFileName(String fileName, String agent) throws IOException {
        String codedfilename = "";
        if (null != agent && -1 != agent.indexOf("MSIE")) {
            codedfilename = URLEncoder.encode(fileName, "UTF8");
        } else if (null != agent && -1 != agent.indexOf("Mozilla")) {
            codedfilename = "=?UTF-8?B?"
                    + (new String(
                    org.apache.commons.codec.binary.Base64.encodeBase64(fileName.getBytes("UTF-8"))))
                    + "?=";
        } else {
            codedfilename = fileName;
        }
        return codedfilename;
    }

    /**
     * 将文件中每一行转换为List
     * @param filePath
     * @return
     */
    public static List<String> getContentLine(String filePath) {
        List<String> lines = Lists.newArrayList();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(filePath)), "utf-8"));
            String nextLine = null;
            while ((nextLine = reader.readLine()) != null) {
                //                String[] splitArray = nextLine.split("\t");
                if (StringUtils.isNotEmpty(nextLine)) {
                    lines.add(nextLine);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
        return lines;
    }
}
