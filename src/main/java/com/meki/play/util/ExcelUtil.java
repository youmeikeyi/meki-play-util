package com.meki.play.util;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.*;

/**
 * Excel工具类
 * Created by xujinchao on 2015/6/9.
 */
public class ExcelUtil {
    public static final String DEFAULT_DIR = "E:\\mywork\\temp\\";

    public static final String TARGET_FILE = "0917资料片更新内容.xlsx";

    //有新的服务器  加上
    public static final List<String> filterNames = Arrays.asList("吴攀亮", "李洁", "蔡佳何","许金超" ,
            "康超", "张汝彬", "张宇");
    public static String filterKey = "程序";
    public static String targetKey = "BUG列表";
    public static final String OFFICE_EXCEL_2003_POSTFIX = "xls";
    public static final String OFFICE_EXCEL_2010_POSTFIX = "xlsx";

    public static final String EMPTY = "";
    public static final String POINT = ".";

    public static final String PROCESSING = "Processing...";

    /**
     * get postfix of the path
     * 从文件路径取得后缀名
     *
     * @param path
     * @return
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
     * read the Excel file
     *
     * @param path the path of the Excel file
     * @return
     * @throws java.io.IOException
     */
    public static List<ExcelSheetBean> readExcel(String path) throws IOException {
        //检查路径合法性
        String postfix = getPostfix(path);
        if (!EMPTY.equals(postfix)) {
            if (OFFICE_EXCEL_2003_POSTFIX.equals(postfix)) {
                return readXls(path);
            } else if (OFFICE_EXCEL_2010_POSTFIX.equals(postfix)) {
                return readXlsx(path);
            }
        }
        System.out.println(path + " is not Excel format!");
        return null;
    }

    /**
     * Read the Excel 2010
     *
     * @param path the path of the excel file
     * @return
     * @throws IOException
     */
    public static List<ExcelSheetBean> readXlsx(String path) throws IOException {
        System.out.println(PROCESSING + path);
        InputStream is = new FileInputStream(path);
        XSSFWorkbook xssfWorkbook = new XSSFWorkbook(is);

        ExcelSheetBean sheetBean = null;
        List<ExcelSheetBean> sheetList = new ArrayList<ExcelSheetBean>();
        // Read the Sheet
//        for (int numSheet = 0; numSheet < xssfWorkbook.getNumberOfSheets(); numSheet++) {
            XSSFSheet xssfSheet = xssfWorkbook.getSheetAt(0);
            if (xssfSheet == null) {
                return sheetList;
            }
            sheetBean = getXssfSheetByNumber(xssfWorkbook, 0);
            sheetList.add(sheetBean);

            System.out.println();
//            System.out.println(sheetList.size());
//        }
        return sheetList;
    }

    /**
     * Read the Excel 2003-2007
     *
     * @param path the path of the Excel
     * @return
     * @throws IOException
     */
    public static List<ExcelSheetBean> readXls(String path) throws IOException {
        System.out.println(PROCESSING + path);
        InputStream is = new FileInputStream(path);
        HSSFWorkbook hssfWorkbook = new HSSFWorkbook(is);
        ExcelSheetBean sheetBean = null;
        List<ExcelSheetBean> sheetList = new ArrayList<ExcelSheetBean>();
        // Read the Sheet
        for (int numSheet = 0; numSheet < hssfWorkbook.getNumberOfSheets(); numSheet++) {
            HSSFSheet hssfSheet = hssfWorkbook.getSheetAt(numSheet);
            if (hssfSheet == null) {
                continue;
            }
            sheetBean = getSheetByNumber(hssfWorkbook, numSheet);
            sheetList.add(sheetBean);

            System.out.println();
            System.out.println(sheetList);
            System.out.println(sheetList.size());
        }
        return sheetList;
    }

    /**
     * 获取某sheet的标题列表（第一行）
     * @param hssfWorkbook
     * @param sheetNum
     * @return
     */
    private static List<String> getTitleListInSheet(HSSFWorkbook hssfWorkbook, int sheetNum){
        HSSFSheet hssfSheet = hssfWorkbook.getSheetAt(sheetNum);
        if (hssfSheet == null) {
            return new ArrayList<String>();
        }
        HSSFRow hssfRow = hssfSheet.getRow(hssfSheet.getFirstRowNum());
        return getTitleList(hssfRow, hssfRow.getFirstCellNum(), hssfRow.getLastCellNum());
    }


    /**
     * 获取标题栏
     * 默认start为0，end为总列数
     * @param firstRow
     * @param start
     * @param end
     * @return
     */
    private static List<String> getTitleList(HSSFRow firstRow, int start, int end) {
        List<String> titles = new ArrayList<String>(end);
        HSSFCell cell = null;
        for (int i = start; i < end; i ++) {
            cell = firstRow.getCell(i);
            titles.add(cell.getRichStringCellValue().toString());
            System.out.println("titles add :" + cell.getRichStringCellValue().toString());
        }
        return titles;
    }

    /**
     * 获取某sheet的标题列表（第一行）
     * @param hssfWorkbook
     * @param sheetNum
     * @return
     */
    private static List<String> getTitleListInXssfSheet(XSSFWorkbook hssfWorkbook, int sheetNum){
        XSSFSheet hssfSheet = hssfWorkbook.getSheetAt(sheetNum);
        if (hssfSheet == null) {
            return new ArrayList<String>();
        }
        XSSFRow hssfRow = hssfSheet.getRow(hssfSheet.getFirstRowNum());
        return getXssfTitleList(hssfRow, hssfRow.getFirstCellNum(), hssfRow.getLastCellNum());
    }


    /**
     * 获取标题栏
     * 默认start为0，end为总列数
     * @param firstRow
     * @param start
     * @param end
     * @return
     */
    private static List<String> getXssfTitleList(XSSFRow firstRow, int start, int end) {
        List<String> titles = new ArrayList<String>(end);
        XSSFCell cell = null;
        for (int i = start; i < end; i ++) {
            cell = firstRow.getCell(i);
            titles.add(cell.getRichStringCellValue().toString());
            System.out.println("titles add :" + cell.getRichStringCellValue().toString());
        }
        return titles;
    }

    /**
     * 根据sheet编号获取一个Sheet
     * @param hssfWorkbook
     * @param sheetNum
     * @return
     */
    public static ExcelSheetBean getSheetByNumber(HSSFWorkbook hssfWorkbook, int sheetNum){
        ExcelSheetBean sheetBean = new ExcelSheetBean();
        HSSFSheet hssfSheet = hssfWorkbook.getSheetAt(sheetNum);
        if (hssfSheet == null) {
            return new ExcelSheetBean();
        }

        int rowNumber = hssfSheet.getPhysicalNumberOfRows();
        sheetBean.setNumber(rowNumber);
        sheetBean.setSheetName(hssfSheet.getSheetName());
        List<String> columnNames = getTitleListInSheet(hssfWorkbook, hssfSheet.getFirstRowNum());
        if (!columnNames.isEmpty()) {
            sheetBean.setColumnKeys(columnNames);
        }
//            int start = hssfRow.getFirstCellNum();
//            //注意 end从1计算 相当于length
//            int end = hssfRow.getLastCellNum();
        HSSFRow hssfRow = null;
        HSSFCell cell = null;
        // Read the Row
        Map<String, String> rowMap = null;
        for (int rowNum = 1; rowNum <= hssfSheet.getLastRowNum(); rowNum++) {
            hssfRow = hssfSheet.getRow(rowNum);
            rowMap = new HashMap<String, String>();
            if (hssfRow != null) {
                for (int i = hssfRow.getFirstCellNum(); i < hssfRow.getLastCellNum(); i++) {
                    cell = hssfRow.getCell(i);
                    if (sheetBean.getColumnKeys().get(i) != null && cell != null) {
                        rowMap.put(sheetBean.getColumnKeys().get(i), getValue(cell));
                    }
                }
                sheetBean.getColumnMap().put(rowNum, rowMap);
                System.out.println(sheetBean);
            }
        }

        return sheetBean;
    }

    /**
     * 根据sheet编号获取一个Sheet
     * @param xssfWorkbook
     * @param sheetNum
     * @return
     */
    public static ExcelSheetBean getXssfSheetByNumber(XSSFWorkbook xssfWorkbook, int sheetNum){
        ExcelSheetBean sheetBean = new ExcelSheetBean();

        XSSFSheet xssfSheet = xssfWorkbook.getSheetAt(sheetNum);
        if (xssfSheet == null) {
            return new ExcelSheetBean();
        }

        int rowNumber = xssfSheet.getPhysicalNumberOfRows();
        sheetBean.setNumber(rowNumber);
//        System.out.println("xssfSheet.getPhysicalNumberOfRows():" + xssfSheet.getPhysicalNumberOfRows());
//        System.out.println("xssfSheet.getLastRowNum():" + xssfSheet.getLastRowNum());
        sheetBean.setSheetName(xssfSheet.getSheetName());
        List<String> columnNames = getTitleListInXssfSheet(xssfWorkbook, xssfSheet.getFirstRowNum());
        if (!columnNames.isEmpty()) {
//            System.out.println(columnNames.size());
//            System.out.println("lastRowNum:" +xssfSheet.getLastRowNum());
            sheetBean.setColumnKeys(columnNames);
        }

        XSSFRow xssfRow = null;
        XSSFCell cell = null;
        // Read the Row
        Map<String, String> rowMap = null;
        for (int rowNum = 1; rowNum <= xssfSheet.getLastRowNum(); rowNum++) {
            xssfRow = xssfSheet.getRow(rowNum);
            rowMap = new HashMap<String, String>();
            if (xssfRow != null) {
                for (int i = xssfRow.getFirstCellNum(); i < sheetBean.getColumnKeys().size(); i++) {
                    cell = xssfRow.getCell(i);
                    if (sheetBean.getColumnKeys().size() >= i
                            &&sheetBean.getColumnKeys().get(i) != null && cell !=null) {
                        rowMap.put(sheetBean.getColumnKeys().get(i), getValue(cell));
                    }
                }
                sheetBean.getColumnMap().put(rowNum, rowMap);
//                System.out.println(sheetBean);
            }
        }
        return sheetBean;
    }

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

    public static String convertScienceToNormal(HSSFCell hssfCell){
        DecimalFormat df = new DecimalFormat("0");//使用DecimalFormat类对科学计数法格式的数字进行格式化
        return String.valueOf(df.format(hssfCell.getNumericCellValue()));
    }

    public static void main(String[] args) {
        try {
            List<ExcelSheetBean> sheetBeanList = ExcelUtil.readExcel(DEFAULT_DIR + TARGET_FILE);
            List<String> bugList = new ArrayList<String>();

            for (ExcelSheetBean sheetBean : sheetBeanList) {
                Map<String, String> tempMap = new HashMap<String, String>();
                for (Integer key : sheetBean.getColumnMap().keySet()) {//key其实就是rowID
                    tempMap = sheetBean.getColumnMap().get(key);
                    for (Map.Entry<String, String> entry : tempMap.entrySet()) {
                        if (entry.getKey().contains(filterKey)
                                && filterNames.contains(entry.getValue())) {
                            bugList.add(tempMap.get(targetKey));
                        }
                    }

                }
            }

            StringBuilder builder = new StringBuilder();
            for (String bug : bugList) {
                bug = bug.replaceFirst(" ", "");
                builder.append(bug.substring(0, bug.indexOf("【")));
                builder.append("|");
            }
            System.out.println(builder.substring(0, builder.length() - 1));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
