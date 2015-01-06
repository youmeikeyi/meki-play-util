package com.meki.play.util;

import com.meki.play.util.pattern.RegexBox;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: jinchao.xu
 * Date: 2014/8/14
 * Time: 9:56
 */
public class StringBox {

    private static String chinese = "[\u0391-\uFFE5]+";
    private static Pattern chinesePattern = Pattern.compile(chinese);

    /**
     * 获取字符串的中文长度，向下取整
     *
     * @param value 指定的字符串
     * @return 字符串的长度
     */
    public static int chineseLength(String value) {
        if (StringUtils.isBlank(value))
            return 0;
        int valueLength = 0;

        /* 获取字段值的长度，如果含中文字符，则每个中文字符长度为2，否则为1 */
        for (int i = 0; i < value.length(); i++) {
            /* 获取一个字符 */
            String temp = value.substring(i, i + 1);
            /* 判断是否为中文字符 */
            if (chinesePattern.matcher(temp).matches()) {
                /* 中文字符长度为2 */
                valueLength += 2;
            } else {
                /* 其他字符长度为1 */
                valueLength += 1;
            }
        }
        return valueLength / 2;
    }

    /**
     * 空格过滤，包含全角和半角空格
     *
     * @param s 字符串
     * @return 过滤空格（包含全角和半角）后的字符串
     */
    public static String trim(String s) {
        if (s == null) {
            return s;
        }
        return s.replaceAll("[ |　]+", "");
    }

    /**
     * 判断字符串是否为空
     *
     * @param s 字符串
     * @return true：null，""， 只包含空格（包括全角空格）
     */
    public static boolean isEmpty(String s) {
        return s == null || trim(s).length() == 0;
    }

    /**
     * 判断字符串是否非空
     *
     * @param s 字符串
     * @return false：null，""， 只包含空格（包括全角空格）
     */
    public static boolean isNotEmpty(String s) {
        return !isEmpty(s);
    }

    /**
     * 判断是否是数字串
     * 没有判断.
     *
     * @param s
     * @return
     */
    public static boolean isNumeric(String s) {
        if (s == null || s.equals("")) {
            return false;
        }
        for (int i = s.length(); --i >= 0; ) {
            if (!Character.isDigit(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断字符串是否是整数
     */
    public static boolean isInteger(String value) {
        try {
            Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    /**
     * 判断字符串是否是浮点数
     */
    public static boolean isDouble(String value) {
        try {
            Double.parseDouble(value);
            if (value.contains(".")) {
                return true;
            }
        } catch (NumberFormatException e) {
            return false;
        }
        return false;
    }

    /**
     * 判断字符串是否是数字
     */
    public static boolean isNumber(String value) {
        return isInteger(value) || isDouble(value);
    }

    /**
     * 判断字符串是否为手机号码
     *
     * @param phone phone
     * @return true: ^1[0-9]{10}$
     */
    public static boolean isTelephone(String phone) {
        if (StringBox.isEmpty(phone)) {
            return false;
        }
        Pattern p = Pattern.compile(RegexBox.TELEPHONE);
        Matcher m = p.matcher(phone.toLowerCase());
        return m.matches();
    }

    /**
     * 按指定分隔符分割字符串
     * @param digitString 需要分割的string
     * @param separator   分隔符
     * @return List<Integer> 数字列表,任何错误结果都返回null
     * @Title string2Int
     * @Description 分割string，返回List
     */
    public static List<Integer> string2Int(String digitString, String separator) {
        if (isEmpty(digitString) || isEmpty(separator)) {
            return null;
        }
        try {
            String[] array = digitString.split(separator);
            List<Integer> intList = new ArrayList<Integer>();
            for (String item : array) {
                intList.add(Integer.parseInt(item));
            }
            return intList;
        } catch (NumberFormatException e) {
            return null;
        }

    }
    /**
     * 默认的字符串分隔符
     */
    public static final String DEFAULT_SEPARATOR = ",";

    public static String str2String(List<String> target) {
        if (CollectionUtils.isEmpty(target)) {
            return null;
        }
        try {
            StringBuilder result = new StringBuilder();
            for (String str : target) {
                result.append(str);
                result.append(DEFAULT_SEPARATOR);
            }
            return result.substring(0, result.length() - 1);
        } catch (Exception e) {
            return null;
        }
    }



}
