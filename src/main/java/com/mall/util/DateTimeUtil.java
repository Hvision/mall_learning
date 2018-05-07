package com.mall.util;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Date;

/**
 * Created by wxhong on 2018/5/3.
 */
public class DateTimeUtil {

    private static final String STANDARD_FORMAT = "yyyy-MM-dd HH:mm:ss";
    //joda-time
    public static Date strToDate(String dateTimeStr){
        DateTimeFormatter dateTimeFormat = DateTimeFormat.forPattern(STANDARD_FORMAT);
        DateTime dateTime = dateTimeFormat.parseDateTime(dateTimeStr);
        return dateTime.toDate();
    }

    public static String dateToStr(Date date){
        if(date == null){
             return StringUtils.EMPTY;
        }
        DateTime dateTime = new DateTime(date);

        return dateTime.toString(STANDARD_FORMAT);
    }
}
