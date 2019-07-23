/*
 * Copyright (C) 2019 Baidu, Inc. All Rights Reserved.
 */
package com.meki.play.util;

import com.google.common.collect.Lists;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Excel工具类 Created by xujinchao on 2017/6/19.
 */
public class ExcelUtil {
    public static final String OFFICE_EXCEL_2003_POSTFIX = "xls";
    public static final String OFFICE_EXCEL_2010_POSTFIX = "xlsx";
    public static final String EMPTY = "";
    public static final String POINT = ".";
    public static final String PROCESSING = "Processing...";
    private static final Logger LOGGER = LoggerFactory.getLogger(ExcelUtil.class);

    /**
     * get postfix of the path 从文件路径取得后缀名
     */
    public static String getPostfix(String path) {
        if (path == null || EMPTY.equals(path.trim())) {
            throw new RuntimeException("The input path is empty");
        }
        if (path.contains(POINT)) {
            return path.substring(path.lastIndexOf(POINT) + 1, path.length());
        }
        return EMPTY;
    }

    /**
     * 将ExcelSheetBean写入到指定文件位置 注意:xls有行数限制,所以一般写入采用xlsx
     */
    public static void write2Xlsx(String filePath, List<ExcelSheetBean> excelSheetBeanList) {
        if (StringUtils.isBlank(filePath) || CollectionUtils.isEmpty(excelSheetBeanList)) {
            LOGGER.error("filePath or excelSheetBeanList is not valid!");
            return;
        }

        XSSFWorkbook xssfWorkbook = null;
        File file = new File(filePath);

        try {
            if (file.exists()) {
                xssfWorkbook = new XSSFWorkbook(new FileInputStream(file));
            } else {
                xssfWorkbook = new XSSFWorkbook();
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return;
        }

        int sheetNum = excelSheetBeanList.size();

        // write the Sheet
        for (int curSheet = 0; curSheet < sheetNum; curSheet++) {
            writeXssfSheetBySheetNumber(xssfWorkbook, excelSheetBeanList.get(curSheet));
        }

        try {
            FileOutputStream fout = new FileOutputStream(filePath);
            xssfWorkbook.write(fout);
            fout.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void writeXssfSheetBySheetNumber(XSSFWorkbook xssfWorkbook, ExcelSheetBean sheetBean) {
        if (xssfWorkbook == null || sheetBean == null) {
            LOGGER.error("params is not valid!");
            return;
        }
        List<String> columnNames = sheetBean.getColumnKeys();

        int sheetNum = xssfWorkbook.getNumberOfSheets() + 1;
        String sheetName = "col";
        if (!StringUtils.isBlank(sheetBean.getSheetName())) {
            sheetName = sheetBean.getSheetName();
        }
        XSSFSheet xssfSheet = xssfWorkbook.createSheet(sheetName + sheetNum);
        Map<Integer, Map<String, String>> sheetMap = sheetBean.getSheetMap();

        Map<String, String> tmpRowMap = null;
        XSSFRow xssfRow = null;
        XSSFCell cell = null;
        for (int rowNum = 0; rowNum < sheetMap.keySet().size(); rowNum++) {
            tmpRowMap = sheetMap.get(rowNum);
            if (tmpRowMap != null) {
                xssfRow = xssfSheet.createRow(rowNum);
                for (int columnIndex = 0; columnIndex < tmpRowMap.keySet().size(); columnIndex++) {
                    cell = xssfRow.createCell(columnIndex);
                    cell.setCellValue(tmpRowMap.get(columnNames.get(columnIndex)));
                }
            }
        }

    }

    /**
     * read the Excel file
     *
     * @param path the path of the Excel file
     */
    public static List<ExcelSheetBean> readExcel(String path) {
        // 检查路径合法性
        String postfix = getPostfix(path);
        if (!EMPTY.equals(postfix)) {
            try {
                if (OFFICE_EXCEL_2003_POSTFIX.equals(postfix)) {
                    return readXls(path);
                } else if (OFFICE_EXCEL_2010_POSTFIX.equals(postfix)) {
                    return readXlsx(path);
                }
            } catch (IOException ioe) {
                LOGGER.error(ioe.getMessage(), ioe);
            }

        }
        LOGGER.info(path + " is not Excel format!");
        return new ArrayList<ExcelSheetBean>();
    }

    /**
     * Read the Excel 2010
     *
     * @param path the path of the excel file
     */
    private static List<ExcelSheetBean> readXlsx(String path) throws IOException {
        LOGGER.info(PROCESSING + path);
        InputStream inputStream = new FileInputStream(path);
        XSSFWorkbook xssfWorkbook = new XSSFWorkbook(inputStream);

        List<ExcelSheetBean> sheetList = new ArrayList<ExcelSheetBean>();
        int sheetNum = xssfWorkbook.getNumberOfSheets();
        // Read the Sheet
        for (int curSheet = 0; curSheet < sheetNum; curSheet++) {
            XSSFSheet xssfSheet = xssfWorkbook.getSheetAt(curSheet);
            if (xssfSheet == null) {
                continue;
            }
            sheetList.add(getXssfSheetByNumber(xssfWorkbook, curSheet));
        }
        return sheetList;
    }

    /**
     * Read the Excel 2003-2007
     *
     * @param path the path of the Excel
     */
    private static List<ExcelSheetBean> readXls(String path) throws IOException {
        LOGGER.info(PROCESSING + path);
        InputStream inputStream = new FileInputStream(path);
        HSSFWorkbook hssfWorkbook = new HSSFWorkbook(inputStream);

        List<ExcelSheetBean> excelSheetBeanList = new ArrayList<ExcelSheetBean>();
        // Read the Sheet
        int sheetNum = hssfWorkbook.getNumberOfSheets();
        for (int curSheet = 0; curSheet < sheetNum; curSheet++) {
            HSSFSheet hssfSheet = hssfWorkbook.getSheetAt(curSheet);
            if (hssfSheet == null) {
                continue;
            }
            excelSheetBeanList.add(getSheetByNumber(hssfWorkbook, curSheet));
        }
        return excelSheetBeanList;
    }

    /**
     * 获取某sheet的标题列表（第一行）
     *
     * @param hssfWorkbook 所在的workbook
     * @param sheetNum     选择sheet
     */
    private static List<String> getTitleListOfSheet(HSSFWorkbook hssfWorkbook, int sheetNum) {
        HSSFSheet hssfSheet = hssfWorkbook.getSheetAt(sheetNum);
        if (hssfSheet == null) {
            return new ArrayList<String>();
        }
        HSSFRow hssfRow = hssfSheet.getRow(hssfSheet.getFirstRowNum());
        return getDefaultTitleList(hssfRow.getFirstCellNum(), hssfRow.getLastCellNum());
    }

    /**
     * 获取标题栏 默认start为0，end为总列数
     *
     * @param firstRow 标题行
     * @param start    开始索引
     * @param end      结束索引
     */
    private static List<String> getTitleList(HSSFRow firstRow, int start, int end) {
        List<String> titles = new ArrayList<String>(end);
        HSSFCell cell = null;
        for (int titleIndex = start; titleIndex < end; titleIndex++) {
            cell = firstRow.getCell(titleIndex);
            titles.add(cell.getRichStringCellValue().toString());
        }
        return titles;
    }

    /**
     * 获取某sheet的标题列表（第一行）
     */
    private static List<String> getTitleListInXssfSheet(XSSFWorkbook hssfWorkbook, int sheetNum) {
        XSSFSheet hssfSheet = hssfWorkbook.getSheetAt(sheetNum);
        if (hssfSheet == null) {
            return new ArrayList<String>();
        }
        XSSFRow hssfRow = hssfSheet.getRow(hssfSheet.getFirstRowNum());
        return getDefaultTitleList(hssfRow.getFirstCellNum(), hssfRow.getLastCellNum());
    }

    /**
     * 获取标题栏 默认start为0，end为总列数
     *
     * @param firstRow 标题行
     * @param start    开始索引
     * @param end      结束索引
     */
    private static List<String> getXssfTitleList(XSSFRow firstRow, int start, int end) {
        List<String> titles = new ArrayList<String>(end);
        XSSFCell cell = null;
        for (int cellIndex = start; cellIndex < end; cellIndex++) {
            cell = firstRow.getCell(cellIndex);
            if (cell == null || StringUtils.isBlank(getValue(cell))) {
                titles.add(ExcelSheetBean.DEFAULT_COLUMN_NAME + cellIndex);
            } else {
                titles.add(getValue(cell));
            }
        }
        return titles;
    }

    /**
     * 获取默认标题行
     *
     * @param start 开始索引0
     * @param end   结束索引
     */
    private static List<String> getDefaultTitleList(int start, int end) {
        List<String> titles = new ArrayList<String>(end);
        for (int cellIndex = start; cellIndex < end; cellIndex++) {
            titles.add(ExcelSheetBean.DEFAULT_COLUMN_NAME + cellIndex);
        }
        return titles;
    }

    /**
     * 根据sheet编号获取一个hssfSheet
     */
    public static ExcelSheetBean getSheetByNumber(HSSFWorkbook hssfWorkbook, int sheetNum) {
        ExcelSheetBean sheetBean = new ExcelSheetBean();
        HSSFSheet hssfSheet = hssfWorkbook.getSheetAt(sheetNum);
        if (hssfSheet == null) {
            return new ExcelSheetBean();
        }

        // int rowNumber = hssfSheet.getPhysicalNumberOfRows();
        sheetBean.setSheetName(hssfSheet.getSheetName());

        List<String> columnNames = getTitleListOfSheet(hssfWorkbook, hssfSheet.getFirstRowNum());
        if (!columnNames.isEmpty()) {
            sheetBean.setColumnKeys(columnNames);
        }
        // int start = hssfRow.getFirstCellNum();
        // // 注意 end从1计算 相当于length
        // int end = hssfRow.getLastCellNum();
        HSSFRow hssfRow = null;
        HSSFCell cell = null;

        Map<String, String> rowMap = null;
        for (int rowNum = 1; rowNum <= hssfSheet.getLastRowNum(); rowNum++) {
            hssfRow = hssfSheet.getRow(rowNum);
            rowMap = new HashMap<String, String>();
            if (hssfRow != null) {
                for (int cellNum = hssfRow.getFirstCellNum(); cellNum < hssfRow.getLastCellNum(); cellNum++) {
                    cell = hssfRow.getCell(cellNum);
                    if (sheetBean.getColumnKeys().get(cellNum) != null && cell != null) {
                        rowMap.put(sheetBean.getColumnKeys().get(cellNum), getValue(cell));
                    }
                }
                sheetBean.getSheetMap().put(rowNum, rowMap);
            }
        }

        return sheetBean;
    }

    /**
     * 根据sheet编号获取一个XssfSheet
     */
    public static ExcelSheetBean getXssfSheetByNumber(XSSFWorkbook xssfWorkbook, int sheetNum) {
        ExcelSheetBean excelSheetBean = new ExcelSheetBean();

        XSSFSheet xssfSheet = xssfWorkbook.getSheetAt(sheetNum);
        if (xssfSheet == null) {
            return excelSheetBean;
        }
        // int rowNumber = xssfSheet.getPhysicalNumberOfRows();

        excelSheetBean.setSheetName(xssfSheet.getSheetName());
        List<String> columnNames = getTitleListInXssfSheet(xssfWorkbook, xssfSheet.getFirstRowNum());
        if (CollectionUtils.isNotEmpty(columnNames)) {
            excelSheetBean.setColumnKeys(columnNames);
        }

        int columnNumber = excelSheetBean.getColumnKeys().size();
        XSSFRow xssfRow = null;
        XSSFCell cell = null;
        // Read each row and store in rowMap
        Map<String, String> rowMap = null;
        for (int rowNum = 0; rowNum <= xssfSheet.getLastRowNum(); rowNum++) {
            xssfRow = xssfSheet.getRow(rowNum);
            rowMap = new HashMap<String, String>();
            if (xssfRow != null) {
                for (int cellNum = xssfRow.getFirstCellNum(); cellNum < columnNumber; cellNum++) {
                    cell = xssfRow.getCell(cellNum);
                    if (excelSheetBean.getColumnKeys().size() >= cellNum
                            && excelSheetBean.getColumnKeys().get(cellNum) != null && cell != null) {
                        rowMap.put(excelSheetBean.getColumnKeys().get(cellNum), getValue(cell));
                    }
                }
                excelSheetBean.getSheetMap().put(rowNum, rowMap);
            }
        }
        return excelSheetBean;
    }

    /**
     * 获取XSSFCell的单元格值
     */
    @SuppressWarnings("static-access")
    private static String getValue(XSSFCell xssfRow) {
        if (xssfRow.getCellType() == xssfRow.CELL_TYPE_BOOLEAN) {
            return String.valueOf(xssfRow.getBooleanCellValue());
        } else if (xssfRow.getCellType() == xssfRow.CELL_TYPE_NUMERIC) {
            return String.valueOf(xssfRow.getNumericCellValue());
        } else {
            return String.valueOf(xssfRow.getStringCellValue());
        }
    }

    /**
     * 获取HSSFCell的单元格值
     */
    @SuppressWarnings("static-access")
    private static String getValue(HSSFCell hssfCell) {
        if (hssfCell.getCellType() == hssfCell.CELL_TYPE_BOOLEAN) {
            return String.valueOf(hssfCell.getBooleanCellValue());
        } else if (hssfCell.getCellType() == hssfCell.CELL_TYPE_NUMERIC) {
            return convertScienceToNormal(hssfCell);
        } else {
            return String.valueOf(hssfCell.getStringCellValue());
        }
    }

    public static String convertScienceToNormal(HSSFCell hssfCell) {
        // 使用DecimalFormat类对科学计数法格式的数字进行格式化
        DecimalFormat df = new DecimalFormat("0");
        return String.valueOf(df.format(hssfCell.getNumericCellValue()));
    }

    public static void main(String[] args) {
        String filePath = "/Users/xujinchao/Documents/cmp_parse/自抓取公司名匹配头条公司结果.xlsx";
        // 含有标题行的测试
        String filterColumnKey = "A";
        String targetColumnKey = "B";
        // 指定关键字,用于过滤得到指定行
        List<String> filterNames = Lists.newArrayList();
        try {
            List<ExcelSheetBean> sheetBeanList = ExcelUtil.readExcel(filePath);
            List<String> bugList = new ArrayList<String>();

            Map<String, String> tempMap = new HashMap<String, String>();
            for (ExcelSheetBean sheetBean : sheetBeanList) {
                tempMap.clear();
                // key其实就是rowID
                for (Integer key : sheetBean.getSheetMap().keySet()) {
                    tempMap = sheetBean.getSheetMap().get(key);
                    for (Map.Entry<String, String> entry : tempMap.entrySet()) {
                        if (entry.getKey().contains(filterColumnKey)
                                && filterNames.contains(entry.getValue())) {
                            bugList.add(tempMap.get(targetColumnKey));
                        }
                    }

                }
            }

            StringBuilder builder = new StringBuilder();
            for (String bug : bugList) {
                bug = bug.replaceFirst(" ", "");
                builder.append(bug);
                builder.append("|");
            }
            LOGGER.info(builder.substring(0, builder.length() - 1));
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }
}