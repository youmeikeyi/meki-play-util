package com.meki.play.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 功能：部分处理字符串和日期的工具类
 * 
 * @author
 * @since 2012-9-17 下午13:42:07
 * 
 */

public class AnalysisUtil {

	private static Log logger = LogFactory.getLog(AnalysisUtil.class);
	
	
	/** 分割符 */
	private static final String COMMA = ",";

	/** 日期类型:yyyy-MM-dd */
	private static final String YEAR_MONTH_DAY = "yyyy-MM-dd";

	/** 日期类型:yyyy-MM */
	private static final String YEAR_MONTH = "yyyy-MM";

	/** 日期常数 */
	private static final int DATE_CONSTANT = 86400000;

	/** 字符：-1 */
	private static final String MINUS_ONE = "-1";

	/**
	 * 获取两个月份之间的差
	 * 
	 * @param monthFrom
	 * @param monthEnd
	 * @return 两个月之间所差的月数
	 */
	public int getMonthInterval(String monthFrom, String monthEnd) {
		if(logger.isDebugEnabled()) {
			logger.debug("getMonthInterval.params:monthFrom:"+monthFrom+"|monthEnd:"+monthEnd);
		}

		int result = 0;
		
		// 输入格式：2012-08
		SimpleDateFormat ft = new SimpleDateFormat(YEAR_MONTH);
		Calendar cal = Calendar.getInstance();
		try {
			cal.setTime(ft.parse(monthFrom));
			int yearfrom = cal.get(Calendar.YEAR);
			int monthfrom = cal.get(Calendar.MONTH) + 1;
			cal.setTime(ft.parse(monthEnd));
			int yearend = cal.get(Calendar.YEAR);
			int monthend = cal.get(Calendar.MONTH) + 1;
			if (yearend > yearfrom) {
				result =  (yearend - yearfrom) * 12 + monthend - monthfrom;
			} else {
				result =  monthend - monthfrom;
			}
		} catch (ParseException e) {
			logger.error(e.getMessage(), e);
		}
		
		if(logger.isDebugEnabled()){
			logger.debug("result:"+result);
		}
		
		return result;
	}

	/**
	 * 返回两个时间之间的天数
	 * 
	 * @param dateFrom
	 * @param dateTo
	 * @return 两个日期之间所差的天数
	 */
	public int getDayInterval(String dateFrom, String dateTo) {
		// 日期格式：2012-08-15
		SimpleDateFormat ft = new SimpleDateFormat(YEAR_MONTH_DAY);
		Date df, dt;
		try {
			df = ft.parse(dateFrom);
			dt = ft.parse(dateTo);
			GregorianCalendar pFormer = new GregorianCalendar();
			GregorianCalendar pLatter = new GregorianCalendar();
			pFormer.setTime(df);
			pLatter.setTime(dt);
			GregorianCalendar vFormer = pFormer, vLatter = pLatter;
			boolean vPositive = true;
			if (pFormer.before(pLatter)) {
				vFormer = pFormer;
				vLatter = pLatter;
			} else {
				vFormer = pLatter;
				vLatter = pFormer;
				vPositive = false;
			}
			vFormer.set(Calendar.MILLISECOND, 0);
			vFormer.set(Calendar.SECOND, 0);
			vFormer.set(Calendar.MINUTE, 0);
			vFormer.set(Calendar.HOUR_OF_DAY, 0);
			vLatter.set(Calendar.MILLISECOND, 0);
			vLatter.set(Calendar.SECOND, 0);
			vLatter.set(Calendar.MINUTE, 0);
			vLatter.set(Calendar.HOUR_OF_DAY, 0);
			int vCounter = 0;
			while (vFormer.before(vLatter)) {
				vFormer.add(Calendar.DATE, 1);
				vCounter++;
			}
			if (vPositive)
				return vCounter;
			else
				return -vCounter;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * 将String字符串分割去重后存入String[]中
	 * 
	 * @param str
	 *            待分割字符串
	 * @return 字符串分割后的数组
	 */
	public String[] strToArr(String str) {
		HashSet<String> resultHS = new HashSet<String>();
		String[] middleStr = str.split(COMMA);
		int count = 0;
		while (count < middleStr.length) {
			resultHS.add(middleStr[count]);
			count++;
		}
		String[] finialStr = new String[resultHS.size()];
		count = 0;
		Iterator<String> iterator = resultHS.iterator();
		while (iterator.hasNext()) {
			finialStr[count] = iterator.next();
			count++;
		}
		return finialStr;
	}

	/**
	 * 返回一个月有多少天
	 * 
	 * @param date
	 * @return 返回当前输入日期所在月的天数
	 */
	public int getHowManyDay(Date date) {
		@SuppressWarnings("unused")
		DateFormat format = new SimpleDateFormat(YEAR_MONTH_DAY);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		// 设置日期为本月最大日期
		calendar.set(Calendar.DATE, calendar.getActualMinimum(Calendar.DATE));
		Date first = calendar.getTime();
		calendar.set(Calendar.DATE, calendar.getActualMaximum(Calendar.DATE));
		Date last = calendar.getTime();
		int num = (int) ((last.getTime() - first.getTime()) / DATE_CONSTANT + 1);
		return num;
	}

	/**
	 * 返回输入月份的第一天和最后一天的日期
	 * 
	 * @param month
	 *            输入年月 格式为:2012-08
	 * @return 输入月的第一天和最后一天
	 */
	public Date[] firstAndLastDayOfMonth(String month) {
		// 例:输入：2012-8 输出：firstAndLastDayOfMonth[0]:2012-08-01
		// firstAndLastDayOfMonth[1]:2012-08-31
		DateFormat format = new SimpleDateFormat(YEAR_MONTH_DAY);
		Calendar calendar = Calendar.getInstance();
		Date[] maxAndminDate = new Date[2];
		month += MINUS_ONE;
		try {
			Date date = format.parse(month);
			calendar.setTime(date);
			calendar.set(Calendar.DATE,
					calendar.getActualMinimum(Calendar.DATE));
			maxAndminDate[0] = calendar.getTime();
			calendar.set(Calendar.DATE,
					calendar.getActualMaximum(Calendar.DATE));
			maxAndminDate[1] = calendar.getTime();
			return maxAndminDate;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 将String[]转换成List
	 * 
	 * @param str
	 *            []:待转string
	 * @return 输出list
	 */
	public List<String> strToList(String str[]) {
		List<String> list = new ArrayList<String>();
		int count = 0;
		while (count < str.length) {
			list.add(str[count]);
			count++;
		}
		return list;
	}

	/**
	 * 判断给定的字符串是否符合 Java 标识符的规范 *
	 * 
	 * @param test
	 * @return 符合返回true否则返回false
	 */
	public boolean isJavaIdentifier(String test) {
		if (test == null || test.length() == 0) {
			return false;
		}
		if (!Character.isJavaIdentifierStart(test.charAt(0))
				&& test.charAt(0) != '_') {
			return false;
		}
		for (int i = 1; i < test.length(); i++) {
			if (!Character.isJavaIdentifierPart(test.charAt(i))
					&& test.charAt(i) != '_') {
				return false;
			}
		}
		return true;
	}

}