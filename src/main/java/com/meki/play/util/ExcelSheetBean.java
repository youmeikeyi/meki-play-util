/*
 * Copyright (C) 2019 Baidu, Inc. All Rights Reserved.
 */
package com.meki.play.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Excel Sheet存储对象 Created by xujinchao on 2017/6/19.
 */
public class ExcelSheetBean {

    public static final String DEFAULT_COLUMN_NAME = "col";
    private static final String DEFAULT_SHEET_NAME = "Sheet";
    private static int DEFAULT_COLUMN_NUMBER = 7;
    private String sheetName;
    /**
     * 列的个数
     */
    private int columnNumber;

    /**
     * 列标题列表
     */
    private List<String> columnKeys;

    /**
     * 行数据组装成map结构 外层Map的key为rowNumber,value为对应column列的单元格数据
     */
    private Map<Integer, Map<String, String>> sheetMap = new HashMap<Integer, Map<String, String>>();

    public ExcelSheetBean() {
        this.columnNumber = DEFAULT_COLUMN_NUMBER;
        this.columnKeys = Arrays.asList(getDefaultKeys(DEFAULT_COLUMN_NUMBER));
        this.sheetName = DEFAULT_SHEET_NAME;
        init();
    }

    public ExcelSheetBean(String sheetName, List<String> columnKeys) {
        this.sheetName = sheetName;
        this.columnKeys = columnKeys;
        this.columnNumber = columnKeys.size();
        init();
    }

    public static String[] getDefaultKeys(int number) {
        if (number <= 0) {
            number = DEFAULT_COLUMN_NUMBER;
        }
        String[] arrays = new String[number];
        for (int i = 0; i < number; i++) {
            arrays[i] = DEFAULT_COLUMN_NAME + (i + 1);
        }
        return arrays;
    }

    private void init() {
        // 标题行
        int titleRow = 0;
        Map<String, String> titleMap = new HashMap<String, String>();

        for (int index = 0; index < columnKeys.size(); index++) {
            titleMap.put(columnKeys.get(index), columnKeys.get(index));
        }
        sheetMap.put(titleRow, titleMap);
    }

    public String getSheetName() {
        return sheetName;
    }

    public void setSheetName(String sheetName) {
        this.sheetName = sheetName;
    }

    public int getColumnNumber() {
        if (columnKeys.isEmpty()) {
            return DEFAULT_COLUMN_NUMBER;
        }
        return columnKeys.size();
    }

    public List<String> getColumnKeys() {
        return columnKeys;
    }

    public void setColumnKeys(List<String> columnKeys) {
        this.columnKeys = columnKeys;
        this.columnNumber = columnKeys.size();
    }

    public Map<Integer, Map<String, String>> getSheetMap() {
        return sheetMap;
    }

    public void setSheetMap(Map<Integer, Map<String, String>> sheetMap) {
        this.sheetMap = sheetMap;
    }

    @Override
    public String toString() {
        return "ExcelBean{ sheetName=" + sheetName + ", columnNumber=" + columnNumber + ", sheetMap size="
                + sheetMap.size() + ", columnKeys=" + columnKeys + ", sheetMap=\n" + sheetMap + "}";
    }
}
