/*
 * Copyright (C) 2019 Baidu, Inc. All Rights Reserved.
 */
package com.meki.play;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

/**
 * 抽象Json返回类
 *
 * @author wuliang07
 */

public class AbstractJsonActionController extends MultiActionController {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractJsonActionController.class);

    /**
     * 将json结果直接输出到前台
     *
     * @param response http响应
     * @param json     json串
     */
    public static void outJson(HttpServletResponse response, String json) {
        response.setContentType("application/json;charset=UTF-8");
        response.setHeader("Cache-Control", "no-store, max-age=0, no-cache, must-revalidate");
        response.addHeader("Cache-Control", "post-check=0, pre-check=0");
        response.setHeader("Pragma", "no-cache");
        try {
            PrintWriter out = response.getWriter();
            out.write(json.toCharArray());
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 将指定文件输出到浏览器下载
     *
     * @param file 文件
     */
    public static void outFile(HttpServletRequest request, HttpServletResponse response, File file) {
        String fileName = file.getName();

        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            inputStream = new BufferedInputStream(new FileInputStream(file));
            byte[] buffer = new byte[inputStream.available()];
            inputStream.read(buffer);

            // 清空response
            response.reset();
            String userAgent = request.getHeader("User-Agent");
            if (userAgent.contains("MSIE") || userAgent.contains("Trident")) {
                response.setHeader("Content-Disposition", "attachment;filename=\""
                        + URLEncoder.encode(fileName, "utf-8") + "\"");
            } else {
                // 下载的文件名显示编码处理
                fileName = new String(fileName.getBytes("utf-8"), "ISO-8859-1");
                response.setHeader("Content-Disposition", "attachment;filename=\"" + fileName + "\"");
            }
            response.setContentType("application/x-msdownload;charset=UTF-8");
            // 设置response的Header
            response.addHeader("Content-Length", "" + file.length());
            outputStream = new BufferedOutputStream(response.getOutputStream());
            outputStream.write(buffer);

            outputStream.flush();
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (Exception e) {
                LOG.error(e.getMessage(), e);
            }

        }
    }

    /**
     * 生成指定文件的字节数组
     *
     * @param filePath 文件路径
     *
     * @return
     */
    private static byte[] getBytes(String filePath) {
        byte[] buffer = null;
        try {
            File file = new File(filePath);
            FileInputStream fis = new FileInputStream(file);
            ByteArrayOutputStream bos = new ByteArrayOutputStream(1000);
            byte[] b = new byte[1000];
            int n;
            while ((n = fis.read(b)) != -1) {
                bos.write(b, 0, n);
            }
            fis.close();
            bos.close();
            buffer = bos.toByteArray();
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return buffer;
    }
}
