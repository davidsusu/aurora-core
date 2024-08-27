package hu.webarticum.aurora.core.model.time;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import hu.webarticum.aurora.core.text.CoreText;

public class Time implements Comparable<Time>, Serializable {

    private static final long serialVersionUID = 1L;

    
    public enum PART {
        SECONDS, MINUTES, HOURS, DAYS, WEEKS,
        FULLSECONDS, FULLMINUTES, FULLHOURS, FULLDAYS,
        SECONDS_OF_HOUR, SECONDS_OF_DAY, SECONDS_OF_WEEK,
        MINUTES_OF_DAY, MINUTES_OF_WEEK,
        HOURS_OF_WEEK,
    }

    
    public static final long SECOND = 1L;
    public static final long MINUTE = 60L;
    public static final long HOUR = 3600L;
    public static final long DAY = 24 * HOUR;
    public static final long WEEK = 7 * DAY;

    public static final long AM = 0L;
    public static final long PM = 12 * HOUR;
    
    public static final long WEEK_1 = 0L;
    public static final long WEEK_2 = WEEK;
    public static final long WEEK_3 = 2 * WEEK;
    public static final long WEEK_4 = 3 * WEEK;
    public static final long MONDAY = 0L;
    public static final long TUESDAY = 1 * DAY;
    public static final long WEDNESDAY = 2 * DAY;
    public static final long THURSDAY = 3 * DAY;
    public static final long FRIDAY = 4 * DAY;
    public static final long SATURDAY = 5 * DAY;
    public static final long SUNDAY = 6 * DAY;
    
    private static final Pattern TIME_STRING_PATTERN =
        Pattern.compile("^(((((W\\.(\\-?\\d+) )?D\\.(\\d+) )?(\\d+):)?(\\d+):)?)?(\\d+)$")
    ;

    
    protected final long seconds;

    
    public Time() {
        this(0);
    }

    public Time(String timeString) {
        this.seconds = parseTimeString(timeString);
    }

    public Time(long seconds) {
        this.seconds = seconds;
    }

    
    public long getSeconds() {
        return seconds;
    }
    
    public long getPart(PART part) {
        switch (part) {
            case SECONDS: return mod(seconds, MINUTE);
            case MINUTES: return mod(seconds, HOUR) / MINUTE;
            case HOURS: return mod(seconds, DAY) / HOUR;
            case DAYS: return mod(seconds, WEEK) / DAY;
            case WEEKS: return div(seconds, WEEK);

            case FULLSECONDS: return seconds;
            case FULLMINUTES: return div(seconds, MINUTE);
            case FULLHOURS: return div(seconds, HOUR);
            case FULLDAYS: return div(seconds, DAY);

            case SECONDS_OF_HOUR: return mod(seconds, HOUR);
            case SECONDS_OF_DAY: return mod(seconds, DAY);
            case SECONDS_OF_WEEK: return mod(seconds, WEEK);
            
            case MINUTES_OF_DAY: return mod(seconds, DAY) / MINUTE;
            case MINUTES_OF_WEEK: return mod(seconds, WEEK) / MINUTE;
            
            case HOURS_OF_WEEK: return mod(seconds, DAY) / HOUR;
            
            default: return 0L;
        }
    }

    private long mod(long number, long base) {
        long mod = number % base;
        return (mod < 0 ? mod + base : mod);
    }

    private long div(long number, long divider) {
        if (number < 0) {
            return (number - mod(number, divider)) / divider;
        } else {
            return number / divider;
        }
    }
    
    public Time getDateOnly() {
        return new Time(seconds - getPart(PART.SECONDS_OF_DAY));
    }
    
    public Time getTimeOnly() {
        return new Time(getPart(PART.SECONDS_OF_DAY));
    }
    
    public Time getMoved(long seconds) {
        return new Time(this.seconds + seconds);
    }
    
    @Override
    public int compareTo(Time other) {
        if (this.seconds < other.seconds) {
            return -1;
        } else if (this.seconds > other.seconds) {
            return 1;
        } else {
            return 0;
        }
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Time)) {
            return false;
        }
        return (this.seconds == ((Time)other).seconds);
    }
    
    @Override
    public int hashCode() {
        return Long.valueOf(seconds).hashCode();
    }
    
    @Override
    public String toString() {
        long w = getPart(PART.WEEKS);
        long d = getPart(PART.DAYS);
        long h = getPart(PART.HOURS);
        long m = getPart(PART.MINUTES);
        long s = getPart(PART.SECONDS);
        DecimalFormat f = new DecimalFormat("00");
        return "W." + w + " D." + d + " " + f.format(h) + ":" + f.format(m) + ":" + f.format(s);
    }

    public String toReadableString() {
        long w = getPart(PART.WEEKS);
        long d = getPart(PART.DAYS);
        
        StringBuilder resultBuilder = new StringBuilder();
        
        if (w != 0) {
            long readableWeek = w;
            if (readableWeek >= 0) {
                readableWeek++;
            }
            
            resultBuilder.append(
                String.format(CoreText.get("week.nth", "Week %d"), readableWeek) + " "
            );
        }
        
        resultBuilder.append(getDayName((int)d) + " ");
        
        resultBuilder.append(toTimeString());
        
        return resultBuilder.toString();
    }
    
    public String toTimeString() {
        long h = getPart(PART.HOURS);
        long m = getPart(PART.MINUTES);
        long s = getPart(PART.SECONDS);
        
        StringBuilder resultBuilder = new StringBuilder();
        DecimalFormat f = new DecimalFormat("00");
        resultBuilder.append(f.format(h) + ":" + f.format(m));
        
        if (s > 0) {
            resultBuilder.append(":" + f.format(s));
        }
        
        return resultBuilder.toString();
    }
    
    public static long parseTimeString(String timeString) {
        long result = 0;
        Matcher matcher = TIME_STRING_PATTERN.matcher(timeString);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid time string: '" + timeString + "'");
        }
        String weeksString = matcher.group(6);
        String daysString = matcher.group(7);
        String hoursString = matcher.group(8);
        String minutesString = matcher.group(9);
        String secondsString = matcher.group(10);
        try {
            if (weeksString != null) {
                result += Integer.parseInt(weeksString) * Time.WEEK;
            }
            if (daysString != null) {
                result += Integer.parseInt(daysString) * Time.DAY;
            }
            if (hoursString != null) {
                result += Integer.parseInt(hoursString) * Time.HOUR;
            }
            if (minutesString != null) {
                result += Integer.parseInt(minutesString) * Time.MINUTE;
            }
            if (secondsString != null) {
                result += Integer.parseInt(secondsString);
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid time string: '" + timeString + "'", e);
        }
        return result;
    }
    
    public static String getDayName(int day) {
        switch (day) {
            case 0:
                return CoreText.get("day.monday", "Monday");
            case 1:
                return CoreText.get("day.tuesday", "Tuesday");
            case 2:
                return CoreText.get("day.wednesday", "Wednesday");
            case 3:
                return CoreText.get("day.thursday", "Thursday");
            case 4:
                return CoreText.get("day.friday", "Friday");
            case 5:
                return CoreText.get("day.saturday", "Saturday");
            case 6:
                return CoreText.get("day.sunday", "Sunday");
            default:
                return "?";
        }
    }

    public static String formatSeconds(long seconds) {
        Time wrapperTime = new Time(seconds);
        long secondsPart = wrapperTime.getPart(Time.PART.SECONDS);
        long minutesPart = wrapperTime.getPart(Time.PART.MINUTES);
        long hoursPart = wrapperTime.getPart(Time.PART.FULLHOURS);
        String result = minutesPart + ((secondsPart < 10) ? ":0" : ":") + secondsPart;
        if (hoursPart > 0) {
            if (minutesPart < 10) {
                result = "0" + result;
            }
            result = hoursPart + ":" + result;
        }
        return result;
    }

    public static long parseSeconds(String secondsString) {
        long result = 0;
        String[] tokens = secondsString.split(":");
        long multiplier = 1;
        for (int i = tokens.length - 1; i >= 0; i--) {
            String token = tokens[i];
            long digit = 0;
            try {
                digit = Integer.parseInt(token);
            } catch (Exception e) {
                // fallback is zero
            }
            long value = digit * multiplier;
            result += value;
            multiplier *= 60;
        }
        return result;
    }
    
}
