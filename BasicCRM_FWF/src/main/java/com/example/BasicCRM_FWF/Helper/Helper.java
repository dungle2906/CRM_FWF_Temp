package com.example.BasicCRM_FWF.Helper;

import com.example.BasicCRM_FWF.Model.ServiceType;
import com.example.BasicCRM_FWF.Repository.ServiceTypeRepository;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class Helper {

    public static class DateRange {
        private final LocalDateTime start;
        private final LocalDateTime end;

        public DateRange(LocalDateTime start, LocalDateTime end) {
            this.start = start;
            this.end = end;
        }

        public LocalDateTime getStart() {
            return start;
        }

        public LocalDateTime getEnd() {
            return end;
        }
    }

    public enum CompareMode {
        DAY_RANGE,
        MONTH_OVER_MONTH
    }

    public static DateRange getPreviousPeriod(LocalDateTime currentStart, LocalDateTime currentEnd, CompareMode mode) {
        switch (mode) {
            case MONTH_OVER_MONTH -> {
                LocalDate prevStartDate = currentStart.toLocalDate().minusMonths(1);
                LocalDate prevEndDate = currentEnd.toLocalDate().minusMonths(1);

                prevStartDate = prevStartDate.withDayOfMonth(Math.min(currentStart.getDayOfMonth(), prevStartDate.lengthOfMonth()));
                prevEndDate = prevEndDate.withDayOfMonth(Math.min(currentEnd.getDayOfMonth(), prevEndDate.lengthOfMonth()));

                LocalDateTime prevStart = prevStartDate.atStartOfDay();
                LocalDateTime prevEnd = prevEndDate.atTime(LocalTime.MAX);
                return new DateRange(prevStart, prevEnd);
            }

            case DAY_RANGE -> {
                long days = Duration.between(currentStart, currentEnd).toDays() + 1;
                LocalDateTime prevStart = currentStart.minusDays(days);
                LocalDateTime prevEnd = currentStart.minusSeconds(1);
                return new DateRange(prevStart, prevEnd);
            }

            default -> throw new IllegalArgumentException("Unknown compare mode: " + mode);
        }
    }

}
