package de.rusticprism.kreiscraftbot.utils;

import java.util.regex.Pattern;

public class FormatUtil {
    private static final Pattern URL_PATTERN = Pattern.compile("\\s*(https?|attachment)://\\S+\\s*", Pattern.CASE_INSENSITIVE);
    public static boolean isUrl(String link) {
        return URL_PATTERN.matcher(link).matches();
    }
    public static String formatTime(long duration){
        if(duration == Long.MAX_VALUE)
            return "LIVE";
        long seconds = Math.round(duration/1000.0);
        long hours = seconds/(60*60);
        seconds %= 60*60;
        long minutes = seconds/60;
        seconds %= 60;
        return (hours>0 ? hours+":" : "") + (minutes<10 ? "0"+minutes : minutes) + ":" + (seconds<10 ? "0"+seconds : seconds);
    }
    public static String shorten(String str, int length) {
        return str.substring(0, length) + "...";
    }
}
