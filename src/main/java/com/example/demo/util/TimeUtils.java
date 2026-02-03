package com.example.demo.util;

import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
public final class TimeUtils {
    public static LocalDateTime calculateStartDate(String period) {
        LocalDateTime now = LocalDateTime.now();

        if (period == null || period.trim().isEmpty()) {
            return now.minusMonths(1); // default 1 month
        }

        period = period.toLowerCase().trim();

        try {
            if (period.endsWith("d")) {
                int days = Integer.parseInt(period.substring(0, period.length() - 1));
                return now.minusDays(days);
            } else if (period.endsWith("m")) {
                int months = Integer.parseInt(period.substring(0, period.length() - 1));
                return now.minusMonths(months);
            } else if (period.endsWith("y")) {
                int years = Integer.parseInt(period.substring(0, period.length() - 1));
                return now.minusYears(years);
            }
        } catch (NumberFormatException e) {
            // invalid period format -> fallback
        }

        return now.minusMonths(1); // fallback default
    }
}
