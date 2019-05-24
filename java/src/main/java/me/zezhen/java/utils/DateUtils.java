package me.zezhen.java.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.SimpleTimeZone;
import java.util.TimeZone;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DateUtils {
    
    private static Logger LOG = LoggerFactory.getLogger(DateUtils.class);    
    
    public static final long MILLIS_PER_SECOND          = 1000;
    public static final long SECONDS_PER_MINUTE         = 60;
    public static final long MINUTES_PER_HOUR           = 60;
    public static final long HOURS_PER_DAY              = 24;
    public static final long MILLIS_PER_MINUTE          = MILLIS_PER_SECOND * SECONDS_PER_MINUTE;
    public static final long MILLIS_PER_HOUR            = MILLIS_PER_MINUTE * MINUTES_PER_HOUR;
    public static final long MILLIS_PER_DAY             = MILLIS_PER_HOUR * HOURS_PER_DAY;
    public static final long SECONDS_PER_HOUR           = SECONDS_PER_MINUTE * MINUTES_PER_HOUR;
    public static final long SECONDS_PER_DAY            = SECONDS_PER_HOUR * HOURS_PER_DAY;
    public static final long MINUTES_PER_DAY            = MINUTES_PER_HOUR * HOURS_PER_DAY;
    
    public static final String STD_MILLI_FMT            = "yyyy-MM-dd HH:mm:ss.sss";
    public static final String STD_SECOND_FMT           = "yyyy-MM-dd HH:mm:ss";
    public static final String STD_MINUTE_FMT           = "yyyy-MM-dd HH:mm";
    public static final String CPT_MILLI_FMT            = "yyyyMMddHHmmsssss";
    public static final String CPT_SECOND_FMT           = "yyyyMMddHHmmss";
    public static final String CPT_MINUTE_FMT           = "yyyyMMddHHmm";
    public static final String DEFAULT_FORMAT           = CPT_MINUTE_FMT;
    
    public static final String DAY_START                = "0000";
    public static final String MONTH_START              = "010000";
    public static final String UTC_TIMEZONE             = "UTC";
    public static final String HOUR_FORMAT              = "HHmm";
    public static final String MONTH_FORMAT             = "ddHHmm";
    

    /**
     * Converts given time in milliseconds to seconds
     * 
     * @param time
     *            the time to be converted
     * @return time represented in seconds
     */
    public static long ms2s(long time) {
        return time / MILLIS_PER_SECOND;
    }

    /**
     * Converts given time in milliseconds to minutes
     * 
     * @param time
     *            the time to be converted
     * @return time represented in minutes
     */
    public static long ms2m(long time) {
        return time / MILLIS_PER_MINUTE;
    }

    /**
     * Converts given time in milliseconds to hours
     * 
     * @param time
     *            the time to be converted
     * @return time represented in hours
     */
    public static long ms2h(long time) {
        return time / MILLIS_PER_HOUR;
    }

    /**
     * Converts given time in seconds to milliseconds
     * 
     * @param time
     *            the time to be converted
     * @return time represented in milliseconds
     */ 
    public static long s2ms(long time) {
        return time * MILLIS_PER_SECOND;
    }

    /**
     * Converts given time in seconds to minutes
     * 
     * @param time
     *            the time to be converted
     * @return time represented in minutes
     */
    public static long s2m(long time) {
        return time / SECONDS_PER_MINUTE;
    }

    /**
     * Converts given time in seconds to hours
     * 
     * @param time
     *            the time to be converted
     * @return time represented in hours
     */
    public static long s2h(long time) {
        return time / SECONDS_PER_HOUR;
    }

    /**
     * Converts given time in minutes to milliseconds
     * 
     * @param time
     *            the time to be converted
     * @return time represented in milliseconds
     */
    public static long m2ms(long time) {
        return time * MILLIS_PER_MINUTE;
    }

    /**
     * Converts given time in minutes to seconds
     * 
     * @param time
     *            the time to be converted
     * @return time represented in seconds
     */
    public static long m2s(long time) {
        return time * SECONDS_PER_MINUTE;
    }

    /**
     * Converts given time in minutes to hours
     * 
     * @param time
     *            the time to be converted
     * @return time represented in hours
     */
    public static long m2h(long time) {
        return time / MINUTES_PER_HOUR;
    }

    /**
     * Converts given time in hours to milliseconds
     * 
     * @param time
     *            the time to be converted
     * @return time represented in milliseconds
     */
    public static long h2ms(long time) {
        return time * MILLIS_PER_HOUR;
    }

    /**
     * Converts given time in hours to seconds
     * 
     * @param time
     *            the time to be converted
     * @return time represented in seconds
     */
    public static long h2s(long time) {
        return time * SECONDS_PER_HOUR;
    }

    /**
     * Converts given time in hours to minutes
     * 
     * @param time
     *            the time to be converted
     * @return time represented in minutes
     */
    public static long h2m(long time) {
        return time * MINUTES_PER_HOUR;
    }

    public static String toString(long time, String format) {
        return DateTimeFormat.forPattern(format).withZone(DateTimeZone.UTC).print(time);
    }
    
    public static String toString(long time) {
        return DateUtils.toString(time, DateUtils.DEFAULT_FORMAT);
    }
    
    public static long timeOf(String time, String format, long defaultValue) {
        try {
            return DateTimeFormat.forPattern(format)
                    .withZone(DateTimeZone.UTC).parseDateTime(time).getMillis();
        } catch (IllegalArgumentException e) {
            LOG.warn("Time string and format string mismatch. Time string: {}, format string: {}.", time, format);
            return defaultValue;
        }
    }
    
    public static long timeOf(String time, String format) {
        return DateUtils.timeOf(time, format, -1L);
    }
    
    public static long timeOf(String time) {
        return DateUtils.timeOf(time, DateUtils.DEFAULT_FORMAT);
    }

    public static long getDateMills(String dateStr, String dateFormat) {
        try {
            DateTimeFormatter df = DateTimeFormat.forPattern(dateFormat);
            DateTime date = df.withZone(DateTimeZone.UTC).parseDateTime(dateStr);
            return date.getMillis();
        } catch (IllegalArgumentException ex) {
            LOG.warn("date format isn't match, dateStr:" + dateStr + ", dateFormat:" + dateFormat);
            return -1;
        }
    }
    
    public static long getDateSeconds(String dateStr, String dateFormat) {
        return DateUtils.getDateMills(dateStr, dateFormat) / 1000;
    }

    public static long getDateSeconds(String dateStr) {
        return DateUtils.getDateSeconds(dateStr, DEFAULT_FORMAT);
    }

    public static String getFormatDate(long timeInMills, String dateFormat) {
        SimpleDateFormat sf = new SimpleDateFormat(dateFormat);
        sf.setTimeZone(new SimpleTimeZone(SimpleTimeZone.UTC_TIME, "UTC"));
        Date date = new Date(timeInMills);
        return sf.format(date);
    }

    public static String getFormatDateFromDate(Date dt, String dateFormat) {
        SimpleDateFormat sf = new SimpleDateFormat(dateFormat);
        sf.setTimeZone(new SimpleTimeZone(SimpleTimeZone.UTC_TIME, "UTC"));
        return sf.format(dt);
    }
    
    public static String getFormatDate(long timeInMills) {
        return DateUtils.getFormatDate(timeInMills, DEFAULT_FORMAT);
    }
    
    public static long getDateSeconds(String dateStr, long defaultValue) {
        if(dateStr != null) {
            long date = getDateSeconds(dateStr);
            if(date != -1) {
                return date;
            }
        }
        return defaultValue;
    }
    
    // this function can not handle the switch between daylight and standard time.
    public static String getTimeZone(long dayBoundarySecondInUTC) {
        long day = DateUtils.MILLIS_PER_DAY;
        
        dayBoundarySecondInUTC %= day;
        // the dayBoundary and offset are symmetrical, e.g. Beijing time's offset is +8
        // it's day boundary in UTC time is 16:00, equal to -8.
        long rawOffset = - dayBoundarySecondInUTC;
        if(rawOffset < - day / 2) {
            rawOffset += day;
        }
        if(rawOffset > day / 2) {
            rawOffset -= day;
        }

        String[] timezones = TimeZone.getAvailableIDs((int)rawOffset);
        return (timezones == null || timezones.length == 0 || timezones[0] == null) ? 
                "America/New_York" : timezones[0];
    }
    
    // below is moved from original TimeUtils in spend_plan_gen
    public static boolean isDayStart(String timezoneId, Long utcTime) {
        Date date = new Date(utcTime);
        DateFormat dateFormat = new SimpleDateFormat(HOUR_FORMAT);
        TimeZone timeZone = TimeZone.getTimeZone(timezoneId);
        dateFormat.setTimeZone(timeZone);
        String localTime = dateFormat.format(date);
        return DAY_START.equals(localTime);
    }

    /**
     * <p>
     * Returns the UTC timestamp of local day start, given local timezone and UTC timestamp of local current time.
     * </p>
     * <p>
     * Note that unlike {@link getDayEnd}, nesting invocation of {@code getDayStart} <b>WILL NOT</b> yield the day
     * before yesterday. One should use following snippet to reach that function
     * </p>
     * 
     * <pre>
     * String tzStr = &quot;America/New_York&quot;;
     * // Deducts one millisecond to mimic the closing day end of previous day.
     * long dayStartOfYesterday = TimeUtils.getDayStart(tzStr, TimeUtils.getDayStart(tzStr, currentTimestamp) - 1);
     * </pre>
     * 
     * @param timezoneString
     *            string representing local timezone Id
     * @param utcTimestamp
     *            corresponding UTC timestamp of local current time in milliseconds
     * @return UTC timestamp of local day start
     * @throws Exception
     *             if timezone Id is malformed or undefined
     */
    public static Long getDayStart(String timezoneString, Long utcTimestamp) throws Exception {

        TimeZone utcTimezone = TimeZone.getTimeZone(UTC_TIMEZONE);
        TimeZone localTimezone = TimeZone.getTimeZone(timezoneString);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(localTimezone);

        Date date = new Date(utcTimestamp);
        calendar.setTime(date);

        int offset = localTimezone.getOffset(utcTimestamp);
        Long localDayStart = ((utcTimestamp + offset) / MILLIS_PER_DAY) * MILLIS_PER_DAY;

        DateFormat dateFormat = new SimpleDateFormat(STD_MILLI_FMT);
        dateFormat.setTimeZone(utcTimezone);
        String localTime = dateFormat.format(localDayStart);

        dateFormat.setTimeZone(localTimezone);
        return dateFormat.parse(localTime).getTime();
    }

    /**
     * <p>
     * Returns the UTC timestamp of local day end, given local timezone and UTC timestamp of local current time.
     * </p>
     * <p>
     * One can use nesting invocation to get the day end of next days.
     * </p>
     * 
     * <pre>
     * String tzStr = &quot;America/New_York&quot;;
     * long dayEndOfTomorrow = TimeUtils.getDayEnd(tzStr, TimeUtils.getDayEnd(tzStr, currentTimestamp));
     * </pre>
     * 
     * @param timezoneString
     *            string representing local timezone Id
     * @param utcTimestamp
     *            corresponding UTC timestamp of local current time in milliseconds
     * @return UTC timestamp of local day end
     * @throws Exception
     *             if timezone Id is malformed or undefined
     */
    public static Long getDayEnd(String timezoneString, Long utcTimestamp) throws Exception {

        TimeZone utcTimezone = TimeZone.getTimeZone(UTC_TIMEZONE);
        TimeZone localTimezone = TimeZone.getTimeZone(timezoneString);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(localTimezone);

        Date date = new Date(utcTimestamp);
        calendar.setTime(date);

        int offset = localTimezone.getOffset(utcTimestamp);
        Long localDayEnd = ((utcTimestamp + offset) / MILLIS_PER_DAY + 1) * MILLIS_PER_DAY;

        DateFormat dateFormat = new SimpleDateFormat(STD_MILLI_FMT);
        dateFormat.setTimeZone(utcTimezone);
        String localTime = dateFormat.format(localDayEnd);

        dateFormat.setTimeZone(localTimezone);
        return dateFormat.parse(localTime).getTime();
    }

    // only support milli-seconds level timestamp
    public static Long getMonthEnd(String timezoneId, Long utcTime) throws Exception {
        TimeZone utc = TimeZone.getTimeZone(UTC_TIMEZONE);
        TimeZone timeZone = TimeZone.getTimeZone(timezoneId);

        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(timeZone);

        Date date = new Date(utcTime);
        cal.setTime(date);

        int currentDayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
        int totalDayOfMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

        int offset = timeZone.getOffset(utcTime);

        Long currentDayStart = (utcTime + offset) / MILLIS_PER_DAY;
        Long currentMonthEnd = (currentDayStart + totalDayOfMonth - currentDayOfMonth + 1) * MILLIS_PER_DAY;

        DateFormat dateFormat = new SimpleDateFormat(STD_MILLI_FMT);
        dateFormat.setTimeZone(utc);
        String localTime = dateFormat.format(currentMonthEnd);

        dateFormat.setTimeZone(timeZone);
        return dateFormat.parse(localTime).getTime();
    }

    // only support milli-seconds level timestamp
    public static boolean isMonthStart(String timezoneId, Long utcTime) {
        Date date = new Date(utcTime);
        DateFormat dateFormat = new SimpleDateFormat(MONTH_FORMAT);
        TimeZone timeZone = TimeZone.getTimeZone(timezoneId);
        dateFormat.setTimeZone(timeZone);
        String localTime = dateFormat.format(date);
        return MONTH_START.equals(localTime);
    }

    public static Long getHourStart(String timezoneId, Long utcTime) {
        TimeZone timeZone = TimeZone.getTimeZone(timezoneId);
        int offset = timeZone.getOffset(utcTime);
        long hourStart = utcTime - (utcTime + offset) % MILLIS_PER_HOUR;
        return hourStart;
    }

    public static Long getHourEnd(String timezoneId, Long utcTime) {
        return getHourStart(timezoneId, utcTime) + MILLIS_PER_HOUR;
    }
    
}
