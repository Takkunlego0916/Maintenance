package io.github.takkunlego0916.maintenance.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class DurationUtil {

    private static final Pattern PATTERN = Pattern.compile("^(\\d{1,6})([smhd])$", Pattern.CASE_INSENSITIVE);

    private DurationUtil() {
    }

    public static long parseSeconds(String input) {

        if (input == null) {
            return -1;
        }

        Matcher matcher = PATTERN.matcher(input.trim().toLowerCase());

        if (!matcher.matches()) {
            return -1;
        }

        long amount = Long.parseLong(matcher.group(1));
        char unit = matcher.group(2).charAt(0);

        return switch (unit) {
            case 's' -> amount;
            case 'm' -> amount * 60L;
            case 'h' -> amount * 3600L;
            case 'd' -> amount * 86400L;
            default -> -1;
        };
    }

    public static String format(long totalSeconds) {

        if (totalSeconds < 0) {
            totalSeconds = 0;
        }

        long days = totalSeconds / 86400;
        long hours = (totalSeconds % 86400) / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;

        StringBuilder builder = new StringBuilder();

        if (days > 0) {
            builder.append(days).append("d ");
        }

        if (hours > 0) {
            builder.append(hours).append("h ");
        }

        if (minutes > 0) {
            builder.append(minutes).append("m ");
        }

        if (seconds > 0 || builder.isEmpty()) {
            builder.append(seconds).append("s");
        }

        return builder.toString().trim();
    }
}
