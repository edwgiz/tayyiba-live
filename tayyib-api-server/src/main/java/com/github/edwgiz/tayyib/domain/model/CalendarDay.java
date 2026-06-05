package com.github.edwgiz.tayyib.domain.model;

import org.jspecify.annotations.Nullable;

import java.time.LocalDate;
import java.util.List;

public record CalendarDay(
        LocalDate gregorian,
        LocalDate hijri,
        List<CalendarEvent> events) {

    public record CalendarEvent(String reasonId, @Nullable String tooltipId, CalendarEventType reasonType) {
    }

    public enum CalendarEventType {
        OBLIGATORY_FASTING,
        VOLUNTARY_FASTING,
        PROHIBITING_FASTING
    }
}
