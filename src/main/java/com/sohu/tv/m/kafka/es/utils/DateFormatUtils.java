package com.sohu.tv.m.kafka.es.utils;

import org.apache.log4j.Logger;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DateFormatUtils {

	/** log **/
	private static final Logger LOG = Logger.getLogger(DateFormatUtils.class);

	/** 服务器时间类型 */
	private static final String origin = "dd/MMM/yyyy:HH:mm:ss Z";

	/** 小时粒度整型时间 */
	private static final String createdOnTimeId = "yyyyMMddHH";

	/** 秒级整型时间 */
	private static final String createdOnTime = "yyyyMMddHHmmss";
	
	/** 空字符串 */
	private static final String BLANK_STR = "";

	/**
	 * 服务器时间字符串转化为Date
	 * @param timeStr
	 * 		服务器时间字符串，如： 07/Mar/2014:11:20:53 +0800
	 * @return
	 */
	public static Date parseFromOrigin(String timeStr) {
		try {
			DateFormat sdf = new SimpleDateFormat(origin, Locale.ENGLISH);
			return sdf.parse(timeStr);
		} catch (Exception e) {
			LOG.error("parseDateError-" + timeStr + "\n"
					+ "will return now date.", e);
			return new Date();
		}
	}

	/**
	 * 转化成秒级时间，如20140307111600
	 * @param date
	 * 		Date类型时间
	 * @return
	 * 		秒级时间
	 */
	public static String parseToCreateTime(Date date) {
		if(null == date){
			return BLANK_STR;
		}
		DateFormat sdf = new SimpleDateFormat(createdOnTime);
		return sdf.format(date);
	}
	
	/**
	 * 转化为小时级时间，如2014030711
	 * @param date
	 * 		Date类型时间
	 * @return
	 * 		小时级时间
	 */
	public static String parseToCreateTimeId(Date date) {
		if(null == date){
			return BLANK_STR;
		}
		DateFormat sdf = new SimpleDateFormat(createdOnTimeId);
		return sdf.format(date);
	}
	
	public static List<String> timetransfer(String time){
		List<String> times = new ArrayList<String>();
		Date date = parseFromOrigin(time);
		times.add(parseToCreateTime(date));
		times.add(parseToCreateTimeId(date));
		return times;
		
	}
}
