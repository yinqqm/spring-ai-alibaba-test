package com.ai.demo.tools;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.context.i18n.LocaleContextHolder;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 快速开始例子
 */
public class DateTimeTools {

    @Tool(description = "Get the current data, time, and week in the user's timezone")
    public String getCurrentDataTime() {
        System.out.println("-------------call getCurrentDataTime-----------------");
        //return LocalDateTime.now().atZone(LocaleContextHolder.getTimeZone().toZoneId()).toString();
        var zonedDateTime = LocalDateTime.now().atZone(LocaleContextHolder.getTimeZone().toZoneId());
        String dayOfWeek = zonedDateTime.getDayOfWeek()
                .getDisplayName(java.time.format.TextStyle.FULL, java.util.Locale.CHINESE);
        return zonedDateTime + ", " + dayOfWeek;
    }

    @Tool(description = "Set a user alarm for the given time, provided in ISO-8601 format")
    public void setAlarm(@ToolParam(required = true, description = "String format") String time) {
        LocalDateTime alarmTime = LocalDateTime.parse(time, DateTimeFormatter.ISO_DATE_TIME);
        System.out.println("Alarm set for " + alarmTime);
    }

}
