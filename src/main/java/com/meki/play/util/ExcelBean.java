package com.meki.play.util;

import java.util.*;

/**
 * Excel通用bean
 * Created by xujinchao on 2015/6/9.
 */
public class ExcelBean {

    private final static String DEFAULT_COLUMN_NAME = "col";

    private List<String> defaultKeys = Arrays.asList(getDefaultKeys(DEFAULT_NUMBER));

    private static int DEFAULT_NUMBER = 10;

    private int number = DEFAULT_NUMBER;//行的个数
    private List<String> columnKeys = defaultKeys;//key的list
    private Map<Integer, Map<String, String>> columnMap = new HashMap<Integer, Map<String, String>>();   //key-value的map

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public List<String> getColumnKeys() {
        return columnKeys;
    }

    public void setColumnKeys(List<String> columnKeys) {
        this.columnKeys = columnKeys;
    }

    public Map getColumnMap() {
        return columnMap;
    }

    public void setColumnMap(Map columnMap) {
        this.columnMap = columnMap;
    }

    public static String[] getDefaultKeys(int number) {
        if (number <= 0 ) {
            number = DEFAULT_NUMBER;
        }
        String[] arrays = new String[number];
        for (int i = 0; i < number; i ++) {
            arrays[i] = DEFAULT_COLUMN_NAME + (i + 1);
        }
        return arrays;
    }

    @Override
    public String toString() {
        return "ExcelBean{" +
                "number=" + number +
                ", columnMap=" + columnMap +
//                ", columnKeys=" + columnKeys +
                ", columnMap size=" + columnMap.size() +
                '}';
    }

    class ExcelRowBean{

    }
}
