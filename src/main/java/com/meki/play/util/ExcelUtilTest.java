package com.meki.play.util;

import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * 读取Excel的工具测试类
 * Use Apache poi
 * User: jinchao.xu
 * Date: 14-8-28
 * Time: 下午4:59
 */
public class ExcelUtilTest {
    public static final String DEFAULT_DIR = "E:\\mywork\\temp\\";

    public static final String TARGET_FILE = "0917资料片更新内容.xlsx";
    public static final String OFFICE_EXCEL_2003_POSTFIX = "xls";
    public static final String OFFICE_EXCEL_2010_POSTFIX = "xlsx";

    public static final String EMPTY = "";
    public static final String POINT = ".";
    //    public static final String LIB_PATH = "lib";
    public static final String STUDENT_INFO_XLS_PATH = DEFAULT_DIR + "/input" + POINT + OFFICE_EXCEL_2003_POSTFIX;
    //    public static final String STUDENT_INFO_XLSX_PATH = DEFAULT_DIR + "/input" + POINT + OFFICE_EXCEL_2010_POSTFIX;
    public static final String NOT_EXCEL_FILE = " : Not the Excel file!";
    public static final String PROCESSING = "Processing...";

    /**
     * get postfix of the path
     *
     * @param path
     * @return
     */
    public static String getPostfix(String path) {
        if (path == null || EMPTY.equals(path.trim())) {
            System.out.println("the path is empty");
            return EMPTY;
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
     * @throws IOException
     */
    public static List<Student> readExcel(String path) throws IOException {
        if (path == null || EMPTY.equals(path)) {
            return null;
        } else {
            String postfix = getPostfix(path);
            if (!EMPTY.equals(postfix)) {
                if (OFFICE_EXCEL_2003_POSTFIX.equals(postfix)) {
                    return readXls(path);
                } else if (OFFICE_EXCEL_2010_POSTFIX.equals(postfix)) {
                    return readXlsx(path);
                }
            } else {
                System.out.println(path + NOT_EXCEL_FILE);
            }
        }
        return null;
    }

    /**
     * Read the Excel 2010
     *
     * @param path the path of the excel file
     * @return
     * @throws IOException
     */
    public static List<Student> readXlsx(String path) throws IOException {
        System.out.println(PROCESSING + path);
        InputStream is = new FileInputStream(path);
        XSSFWorkbook xssfWorkbook = new XSSFWorkbook(is);
        Student student = null;
        List<Student> list = new ArrayList<Student>();
        // Read the Sheet
        for (int numSheet = 0; numSheet < xssfWorkbook.getNumberOfSheets(); numSheet++) {
            XSSFSheet xssfSheet = xssfWorkbook.getSheetAt(numSheet);
            if (xssfSheet == null) {
                continue;
            }
            // Read the Row
            for (int rowNum = 1; rowNum <= xssfSheet.getLastRowNum(); rowNum++) {
                XSSFRow xssfRow = xssfSheet.getRow(rowNum);
                if (xssfRow != null) {
                    student = new Student();
                    XSSFCell no = xssfRow.getCell(0);
                    XSSFCell name = xssfRow.getCell(1);
                    XSSFCell age = xssfRow.getCell(2);
                    XSSFCell score = xssfRow.getCell(3);
                    student.setNo(getValue(no));
                    student.setName(getValue(name));
                    student.setAge(getValue(age));
                    student.setScore(Float.valueOf(getValue(score)));
                    list.add(student);
                }
            }
        }
        return list;
    }

    /**
     * Read the Excel 2003-2007
     *
     * @param path the path of the Excel
     * @return
     * @throws IOException
     */
    public static List<Student> readXls(String path) throws IOException {
        System.out.println(PROCESSING + path);
        InputStream is = new FileInputStream(path);
        HSSFWorkbook hssfWorkbook = new HSSFWorkbook(is);
        Student student = null;
        List<Student> list = new ArrayList<Student>();
        // Read the Sheet
        for (int numSheet = 0; numSheet < hssfWorkbook.getNumberOfSheets(); numSheet++) {
            HSSFSheet hssfSheet = hssfWorkbook.getSheetAt(numSheet);
            if (hssfSheet == null) {
                continue;
            }
            // Read the Row
            for (int rowNum = 1; rowNum <= hssfSheet.getLastRowNum(); rowNum++) {
                HSSFRow hssfRow = hssfSheet.getRow(rowNum);

                if (hssfRow != null) {

                    int columnNum = hssfRow.getLastCellNum();
                    student = new Student();
                    HSSFCell no = hssfRow.getCell(0);
                    HSSFCell name = hssfRow.getCell(1);
                    HSSFCell age = hssfRow.getCell(2);
                    HSSFCell score = hssfRow.getCell(3);
                    student.setNo(getValue(no));
                    student.setName(getValue(name));
                    student.setAge(getValue(age));
                    student.setScore(Float.valueOf(getValue(score)));
                    System.out.println(student);
                    list.add(student);
                }
            }
        }
        return list;
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

    /**
     * Excel 2003-2007
     * 获取值
     * @param hssfCell
     * @return
     */
    @SuppressWarnings("static-access")
    private static String getValue(HSSFCell hssfCell) {
        if (hssfCell.getCellType() == hssfCell.CELL_TYPE_BOOLEAN) {
            return String.valueOf(hssfCell.getBooleanCellValue());
        } else if (hssfCell.getCellType() == hssfCell.CELL_TYPE_NUMERIC) {
//            System.out.println(Double.toString(hssfCell.getNumericCellValue()));
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
//            testCreate();
//            testRead();
            List<Student> list = readExcel(DEFAULT_DIR + TARGET_FILE);
            System.out.println(list);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void testCreate() throws IOException {
        File resultFile = null;
        HSSFWorkbook wb = null;
        FileOutputStream fos = null;
        HSSFRow row = null;
        HSSFCell cell = null;
        HSSFCellStyle cellType = null;
        // result file
        resultFile = new File("E:\\mywork\\temp\\result.xls");
        // create a workbook
        wb = new HSSFWorkbook();
        // create a cell style
        cellType = wb.createCellStyle();
//        cellType.setBorderTop(HSSFCellStyle.BORDER_THIN);
//        cellType.setBorderBottom(HSSFCellStyle.BORDER_THIN);
//        cellType.setBorderLeft(HSSFCellStyle.BORDER_THIN);
//        cellType.setBorderRight(HSSFCellStyle.BORDER_THIN);
        cellType.setAlignment(HSSFCellStyle.ALIGN_CENTER);
//        cellType.setFillForegroundColor(HSSFColor.GOLD.index);
        cellType.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        // create a sheet named "sheet1"
        HSSFSheet sheet = wb.createSheet("sheet1");
        // create a row
        row = sheet.createRow(1);
        // create a cell
        cell = row.createCell(1);
        // set style of cell
        cell.setCellStyle(cellType);
        // set value of cell
        cell.setCellValue("hello world!");
        fos = new FileOutputStream(resultFile);
        // save workbook
        wb.write(fos);
        fos.flush();
        fos.close();
    }

    private static void testRead() throws IOException {

        File outputFile = new File(DEFAULT_DIR + "output.xls");
        InputStream input = new FileInputStream(new File(DEFAULT_DIR + "input.xls"));
        POIFSFileSystem fs = new POIFSFileSystem(input);
        HSSFWorkbook workbook = new HSSFWorkbook(fs);
        // cell style
        HSSFCellStyle TABLE_TITLE_STYLE = workbook.createCellStyle();
//        TABLE_TITLE_STYLE.setBorderTop(HSSFCellStyle.BORDER_THIN);
//        TABLE_TITLE_STYLE.setBorderBottom(HSSFCellStyle.BORDER_THIN);
//        TABLE_TITLE_STYLE.setBorderLeft(HSSFCellStyle.BORDER_THIN);
//        TABLE_TITLE_STYLE.setBorderRight(HSSFCellStyle.BORDER_THIN);
        TABLE_TITLE_STYLE.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        TABLE_TITLE_STYLE.setFillForegroundColor(HSSFColor.LIGHT_ORANGE.index);
        TABLE_TITLE_STYLE.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        // get first sheet of workbook
        HSSFSheet sheet = workbook.getSheetAt(0);
        // get first row
        HSSFRow row = sheet.getRow(0);
        // get first col
        HSSFCell cell = row.getCell(0);
        // set value of cell
        cell.setCellValue("ni hao a!");
        TABLE_TITLE_STYLE.setFillForegroundColor(HSSFColor.LIGHT_GREEN.index);
        cell.setCellStyle(TABLE_TITLE_STYLE);

        FileOutputStream fos = new FileOutputStream(outputFile);
        workbook.write(fos);
        fos.flush();
        fos.close();
        input.close();
    }

}
