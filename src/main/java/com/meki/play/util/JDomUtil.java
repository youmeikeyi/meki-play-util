/*
 * Copyright (C) 2019 Baidu, Inc. All Rights Reserved.
 */
package com.meki.play.util;

import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;

/**
 * Created by xujinchao on 17/8/18.
 */
public class JDomUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(JDomUtil.class);

    public static String document2String(Document document) {
        Format format = Format.getPrettyFormat();
        format.setEncoding("UTF-8");
        XMLOutputter xmlOutputter = new XMLOutputter(format);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try {
            xmlOutputter.output(document, outputStream);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return outputStream.toString();
    }

    public static Document string2Document(String xmlStr) {
        Document document = null;
        if (StringUtils.isEmpty(xmlStr)) {
            return document;
        }

        StringReader stringReader = new StringReader(xmlStr);
        InputSource inputSource = new InputSource(stringReader);
        try {
            document = (new SAXBuilder()).build(inputSource);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }

        return document;
    }

    public static String parseLtpServerResultInXmlStr(String content) {
        StringBuilder builder = new StringBuilder();

        try {
            Document document = string2Document(content);
            if (document == null) {
                return builder.toString();
            }
            Element root = document.getRootElement();
            Element sent = root.getChild("doc").getChild("para").getChild("sent");
            List wordList = sent.getChildren("word");
            Element word = null;
            for (int index = 0; index < wordList.size(); index++) {
                word = (Element) wordList.get(index);
                if (word.getAttributeValue("ne").endsWith("Ni")) {
                    builder.append(word.getAttributeValue("cont"));
                }
                if (word.getAttributeValue("ne").endsWith("E-Ni")) {
                    builder.append("|");
                }
            }

        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        String result = builder.toString();
        if (result.endsWith("|")) {
            result = result.substring(0, result.length() - 1);
        }
        return result;
    }
}
