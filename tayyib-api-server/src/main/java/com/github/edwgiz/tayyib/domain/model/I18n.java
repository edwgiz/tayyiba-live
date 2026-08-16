package com.github.edwgiz.tayyib.domain.model;

import org.jspecify.annotations.Nullable;

import java.util.Map;


public record I18n(
        Commons commons,
        HadithReferencing hadithReferencing,
        Fasting fasting,
        Map<String, PrayerTimeMethodGroup> prayerTimeMethodGroups,
        Settings settings,
        Calendar calendar,
        Footer footer,
        Errors errors
) {
    public record Commons(
            String cancel,
            String ok,
            String or
    ) {
    }

    public record HadithReferencing(
            Map<String, String> collections
    ) {
    }

    public record Fasting(
            Head head,
            Ics ics,
            String reasonsTitle,
            FastingReasonGroups reasonGroups
    ) {

        public record Head(
                String title,
                String description
        ) {
        }

        public record Ics(
                String label,
                String linkLabel,
                String copyLinkLabel,
                IcsCalendar calendar
        ) {
            public record IcsCalendar(
                    String name,
                    IcsEvent event
            ) {
                public record IcsEvent(
                        String summary,
                        String hijriDateLabel
                ) {}
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
                String icsTitle,
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
            Location location,
            String timeZone,
            String prayerTimeMethod
    ) {
        public record Location(
                String label,
                String pickOnMap,
                String forgetCustomLocation,
                Permission permission
        ) {

            public record Permission(
                    String changeInBrowser,
                    String grantReason,
                    String revokeBlocking
            ) {
            }
        }
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


    public record PrayerTimeMethodGroup(
            String name,
            Map<String, String> methodNames
    ) {
    }

    public record Errors(
            String locationAccess
    ) {
    }
}
