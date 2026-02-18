package com.example.smptimer.utils;

import net.md_5.bungee.api.ChatColor;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageUtils {
    
    private static final Pattern HEX_PATTERN = Pattern.compile("&#([A-Fa-f0-9]{6})");
    
    public static String colorize(String message) {
        if (message == null) return "";
        
        // Convert hex colors for 1.16+
        Matcher matcher = HEX_PATTERN.matcher(message);
        StringBuffer buffer = new StringBuffer();
        
        while (matcher.find()) {
            String color = matcher.group(1);
            StringBuilder replacement = new StringBuilder("§x");
            for (char c : color.toCharArray()) {
                replacement.append('§').append(c);
            }
            matcher.appendReplacement(buffer, replacement.toString());
        }
        matcher.appendTail(buffer);
        
        // Convert & color codes
        return ChatColor.translateAlternateColorCodes('&', buffer.toString());
    }
    
    public static String formatTime(int seconds) {
        int hours = seconds / 3600;
        int minutes = (seconds % 3600) / 60;
        int secs = seconds % 60;
        
        if (hours > 0) {
            return String.format("%02d:%02d:%02d", hours, minutes, secs);
        } else {
            return String.format("%02d:%02d", minutes, secs);
        }
    }
    
    public static String getProgressBar(int current, int max, int totalBars, String symbol) {
        float percent = (float) current / max;
        int progressBars = (int) (totalBars * percent);
        
        StringBuilder bar = new StringBuilder();
        bar.append("&a");
        for (int i = 0; i < totalBars; i++) {
            if (i < progressBars) {
                bar.append(symbol);
            } else {
                bar.append("&7" + symbol);
            }
        }
        
        return colorize(bar.toString());
    }
}
