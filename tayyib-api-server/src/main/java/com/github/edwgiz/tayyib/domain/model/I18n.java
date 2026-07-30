package com.github.edwgiz.tayyib.domain.model;

import org.jspecify.annotations.Nullable;

import java.util.Map;


public record I18n(
        HadithReferencing hadithReferencing,
        Fasting fasting,
        Settings settings,
        Calendar calendar,
        Footer footer,
        Errors errors
) {
    public record HadithReferencing(
            Map<String, String> collections
    ) {
    }

    public record Fasting(
            Head head,
            FastingReasonGroups reasonGroups
    ) {

        public record Head(
                String title,
                String description,
                Meta meta
        ) {

            public record Meta(
                    String keywords
            ) {
            }
        }

        public record FastingReasonGroups(
                FastingReasonGroup generic,
                FastingReasonGroup obligatory,
                FastingReasonGroup voluntary,
                FastingReasonGroup prohibiting
        ) {
        }

        public record FastingReasonGroup(
                @Nullable String title,
                Reason[] reasons
        ) {
        }

        public record Reason(
                @Nullable String id,
                @Nullable String title,
                Citation[] cites,
                @Nullable Map<String, String> headlines
        ) {
        }

        public record Citation(
                @Nullable String intro,
                String quote,
                HadithReference[] references
        ) {
        }

        public record HadithReference(
                String collection,
                String hadith
        ) {
        }
    }

    public record Settings(
            String location,
            String grantLocationPermission,
            String change,
            String timeZone,
            String changeLocationWindowTitle,
            String ok,
            String defaultValue
    ) {
    }


    public record Calendar(
            String currentMonth,
            CalendarEvent event,
            String[] monthNames
    ) {
        public record CalendarEvent(
                String fajr,
                String sunset
        ) {
        }
    }


    public record Footer(
            Contacts contacts,
            Attributions attributions
    ) {

        public record Contacts(
                String label,
                String githubRefDescription
        ) {
        }

        public record Attributions(
                String label
        ) {
        }
    }

    public record Errors(
            String locationAccess
    ) {
    }
}
