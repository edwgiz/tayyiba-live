package com.github.edwgiz.tayyib.adapter.in.springMvc;

import com.github.edwgiz.tayyib.domain.model.CalendarDayPair;
import com.github.edwgiz.tayyib.domain.model.CalendarDayPair.CalendarEventReasonGroup;
import com.github.edwgiz.tayyib.domain.model.I18n;
import com.github.edwgiz.tayyib.domain.usecase.calendar.GetCalendarDaysUsecase;
import com.github.edwgiz.tayyib.domain.usecase.i18n.GetBundleUsecase;
import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.*;
import net.fortuna.ical4j.model.property.immutable.ImmutableCalScale;
import net.fortuna.ical4j.model.property.immutable.ImmutableVersion;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static java.util.Locale.ROOT;
import static org.springframework.http.HttpStatus.OK;


@Component
public class CalendarFeedView {

    private static final Logger log = LoggerFactory.getLogger(CalendarFeedView.class);

    private static final MediaType CONTENT_TYPE = new MediaType("text", "calendar");

    private final GetBundleUsecase getBundleUsecase;

    public CalendarFeedView(GetBundleUsecase getBundleUsecase) {
        this.getBundleUsecase = getBundleUsecase;
    }


    ResponseEntity<StreamingResponseBody> render(
            final GetCalendarDaysUsecase.Result.Ok resultModel,
            final Locale locale,
            final ZoneId timezone) {

        final var i18n = getBundleUsecase.apply(locale);

        final var icsCalendar = new Calendar();
        icsCalendar.add(new ProdId("-//Tayyiba//Fasting Calendar//" + locale.getLanguage().toUpperCase(ROOT)));
        icsCalendar.add(ImmutableVersion.VERSION_2_0);
        icsCalendar.add(ImmutableCalScale.GREGORIAN);
        icsCalendar.add(new Method(Method.VALUE_PUBLISH));
        icsCalendar.add(new XProperty("X-WR-CALNAME", i18n.fasting().head().title()));
        icsCalendar.add(new XProperty("X-WR-CALDESC", i18n.fasting().head().description()));

        final var fastingReasonGroupsI18n = i18n.fasting().reasonGroups();
        for (final var dayModel : resultModel.calendarDayPairs()) {
            DAY_EVENTS:
            for (final var eventModel : dayModel.events()) {
                for (final var reasonModel : eventModel.reasons()) {
                    switch (reasonModel.group()) {
                        case OBLIGATORY_FASTING, VOLUNTARY_FASTING, PROHIBITING_FASTING -> {
                            icsCalendar.add(renderEvent(dayModel, eventModel, timezone, fastingReasonGroupsI18n));
                            break DAY_EVENTS;
                        }
                        default -> {
                        }
                    }
                }
            }
        }
        return ResponseEntity
                .status(OK)
                .contentType(CONTENT_TYPE)
                .body(out -> new CalendarOutputter(false).output(icsCalendar, out));
    }


    private static VEvent renderEvent(
            final CalendarDayPair dayModel,
            final CalendarDayPair.CalendarEvent eventModel,
            final ZoneId timezone,
            final I18n.Fasting.FastingReasonGroups fastingReasonGroupsI18n
    ) {
        final var icsEventModel = getSummaryAndDescription(eventModel.reasons(), fastingReasonGroupsI18n);
        final var summary = icsEventModel != null
                ? icsEventModel.summary()
                : fastingReasonGroupsI18n.generic().icsTitle();
        final var startOfDay = dayModel.gregorian().atStartOfDay(timezone);
        final VEvent icsEvent;
        if (eventModel.startMinute() != null && eventModel.endMinute() != null) {
            final var eventStart = startOfDay.plusMinutes(eventModel.startMinute());
            final var eventEnd = startOfDay.plusMinutes(eventModel.endMinute());
            icsEvent = new VEvent(eventStart, eventEnd, summary);
        } else {
            icsEvent = new VEvent(dayModel.gregorian(), summary);
        }
        icsEvent.withProperty(new Uid("fasting-" + dayModel.gregorian() + "@tayyiba.live"));
        if (icsEventModel != null) {
            icsEvent.withProperty(new Description(icsEventModel.description()));
        }
        return icsEvent;
    }


    private static @Nullable IcsEventModel getSummaryAndDescription(
            final List<CalendarDayPair.CalendarEventReason> reasonModels,
            final I18n.Fasting.FastingReasonGroups fastingReasonGroupsI18n
    ) {
        final var description = new StringBuilder();
        final var reasonGroups = new HashMap<CalendarEventReasonGroup, I18n.Fasting.FastingReasonGroup>();
        for (final var reasonModel : reasonModels) {
            final var reasonGroupI18n = switch (reasonModel.group()) {
                case OBLIGATORY_FASTING -> fastingReasonGroupsI18n.obligatory();
                case VOLUNTARY_FASTING -> fastingReasonGroupsI18n.voluntary();
                case PROHIBITING_FASTING -> fastingReasonGroupsI18n.prohibiting();
            };
            reasonGroups.putIfAbsent(reasonModel.group(), reasonGroupI18n);
            for (var reasonI18n : reasonGroupI18n.reasons()) {
                if (reasonModel.id().equals(reasonI18n.id())) {
                    if (reasonI18n.headlines() != null) {
                        final var headlineId = reasonModel.headlineId();
                        if (headlineId != null) {
                            final var headlineI18n = reasonI18n.headlines().get(headlineId);
                            if (headlineI18n != null) {
                                description.append(headlineI18n).append('\n');
                                continue;
                            }
                        }
                    }
                    description.append(reasonI18n.title()).append('\n');
                }
            }
        }
        if (reasonGroups.isEmpty()) {
            log.warn("Can't determine reason group");
            return null;
        } else if (reasonGroups.size() > 1) {
            log.warn("Multiple fasting reason groups found in one event");
        }
        final var summary = reasonGroups.entrySet().iterator().next().getValue().icsTitle();
        return new IcsEventModel(summary, description.toString());
    }

    private record IcsEventModel(
            String summary,
            String description) {
    }

}
