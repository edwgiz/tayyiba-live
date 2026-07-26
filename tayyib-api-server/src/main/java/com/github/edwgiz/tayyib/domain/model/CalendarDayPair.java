package com.github.edwgiz.tayyib.domain.model;

import org.jspecify.annotations.Nullable;

import java.time.LocalDate;
import java.util.List;

public record CalendarDayPair(
        LocalDate gregorian,
        CalendarDay hijri,
        List<CalendarEvent> events) {

    public record CalendarEvent(
            List<CalendarEventReason> reasons,
            @Nullable Integer startMinute,
            @Nullable Integer endMinute
    ) {
    }

    public record CalendarEventReason(String id, @Nullable String headlineId, CalendarEventReasonGroup group) {
    }

    public enum CalendarEventReasonGroup {
        OBLIGATORY_FASTING,
        VOLUNTARY_FASTING,
        PROHIBITING_FASTING
    }
}
