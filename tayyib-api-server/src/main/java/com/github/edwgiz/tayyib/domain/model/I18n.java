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
            FastingReasons reasons
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

        public record FastingReasons(
                Reason[] generic,
                Reason[] obligatory,
                Reason[] voluntary,
                Reason[] forbidding
        ){

        }

        public record Reason(
                String id,
                String title,
                Citation[] cites,
                Map<String, ReasonRef> refs
        ) {
            public record ReasonRef(
                    String tooltip
            ) {
            }
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
            String change,
            String timeZone,
            String changeLocationWindowTitle,
            String ok,
            String defaultValue
    ) {
    }

    public record Calendar(
            String currentMonth,
            String sunrise,
            String sunset,
            String[] monthNames
    ) {
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
