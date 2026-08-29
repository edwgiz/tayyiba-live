package com.github.edwgiz.tayyib.adapter.in.springMcp.getCalendarCells.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import org.jspecify.annotations.Nullable;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.NOT_REQUIRED;


public record Response(
        @Schema($comment = "Days without any events are omitted") List<CalendarDayPair> calendarDays,
        @JsonInclude(NON_NULL) @Schema(requiredMode = NOT_REQUIRED) @Nullable String reverseGeocodingLocationName,
        @JsonInclude(NON_NULL) @Schema(requiredMode = NOT_REQUIRED) @Nullable PrayerTimeMethod prayerTimeMethod,
        List<Reason> reasons
) {
    public enum CalendarEventReasonGroup {
        OBLIGATORY_FASTING,
        VOLUNTARY_FASTING,
        PROHIBITING_FASTING
    }

    public record CalendarDayPair(
            @Schema($comment = "Day in Gregorian calendar") LocalDate gregorian,
            @Schema($comment = "Day in Hijri calendar") CalendarDay hijri,
            List<CalendarEvent> events
    ) {
        public record CalendarDay(
                int year,
                int month,
                int day
        ) {
        }

        public record CalendarEvent(
                List<CalendarEventReason> reasons,
                @JsonInclude(NON_NULL) @Schema(requiredMode = NOT_REQUIRED, $comment = "Starts with Fajr, day offset in minutes, negative value points to previous local day") @Nullable Integer startMinute,
                @JsonInclude(NON_NULL) @Schema(requiredMode = NOT_REQUIRED, $comment = "Ends with sunset, day offset in minutes, may point to next local day") @Nullable Integer endMinute
        ) {
        }

        public record CalendarEventReason(
                @Schema($comment = "Reference to reason in i18n resource") String id,
                @JsonInclude(NON_NULL) @Schema(requiredMode = NOT_REQUIRED) @Nullable String headlineId,
                Response.CalendarEventReasonGroup group) {
        }
    }

    public record Reason(
            String id,
            CalendarEventReasonGroup group,
            String title,
            Cite[] cites,
            @JsonInclude(NON_NULL) @Schema(requiredMode = NOT_REQUIRED) @Nullable Map<String, String> headlines
    ) {
        public record Cite(
                @JsonInclude(NON_NULL) @Schema(requiredMode = NOT_REQUIRED) @Nullable String intro,
                String quote,
                String[] hadithReferenceUrls
        ) {
        }
    }
}
