package com.github.edwgiz.tayyib.adapter.in.springMvc;

import com.github.edwgiz.tayyib.domain.model.CalendarDay;
import com.github.edwgiz.tayyib.domain.model.CalendarDayPair;
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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.OutputStreamWriter;
import java.time.ZoneId;
import java.util.Locale;
import java.util.Random;

import static com.github.edwgiz.tayyib.adapter.in.commons.rendering.I18nRenderingUtils.renderHadithHref;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Locale.ROOT;
import static org.apache.commons.lang3.ArrayUtils.isNotEmpty;
import static org.springframework.http.HttpStatus.OK;


@Component
public class CalendarFeedView {

    private static final MediaType CONTENT_TYPE = new MediaType("text", "calendar", UTF_8);

    private final GetBundleUsecase getBundleUsecase;
    private final Random random;

    CalendarFeedView(final GetBundleUsecase getBundleUsecase) {
        this.getBundleUsecase = getBundleUsecase;
        this.random = new Random();
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
        icsCalendar.add(new XProperty("X-WR-CALNAME", i18n.fasting().ics().calendar().name()));
        icsCalendar.add(new XProperty("X-WR-CALDESC", i18n.fasting().head().description()));

        final var textBuff = new StringBuilder();
        for (final var dayModel : resultModel.calendarDayPairs()) {
            DAY_EVENTS:
            for (final var eventModel : dayModel.events()) {
                for (final var reasonModel : eventModel.reasons()) {
                    switch (reasonModel.group()) {
                        case OBLIGATORY_FASTING, VOLUNTARY_FASTING, PROHIBITING_FASTING -> {
                            icsCalendar.add(renderEvent(dayModel, eventModel, timezone, i18n, textBuff));
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
                .body(out -> new CalendarOutputter(false).output(icsCalendar, new OutputStreamWriter(out, UTF_8)));
    }


    private VEvent renderEvent(
            final CalendarDayPair dayModel,
            final CalendarDayPair.CalendarEvent eventModel,
            final ZoneId timezone,
            final I18n i18n,
            final StringBuilder textBuff) {
        final var summary = i18n.fasting().ics().calendar().event().summary();
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
        renderDescriptionTo(dayModel, eventModel, i18n, textBuff);
        icsEvent.withProperty(new Description(textBuff.toString()));
        textBuff.setLength(0);
        return icsEvent;
    }


    private void renderDescriptionTo(
            final CalendarDayPair dayModel,
            final CalendarDayPair.CalendarEvent eventModel,
            final I18n i18n, StringBuilder textBuff) {
        final var fastingReasonGroupsI18n = i18n.fasting().reasonGroups();
        textBuff.append(i18n.fasting().ics().calendar().event().hijriDateLabel()).append(": ");
        renderHijriDateTo(dayModel.hijri(), i18n.calendar().monthNames(), textBuff);
        for (final var reasonModel : eventModel.reasons()) {
            final var reasonGroupI18n = switch (reasonModel.group()) {
                case OBLIGATORY_FASTING -> fastingReasonGroupsI18n.obligatory();
                case VOLUNTARY_FASTING -> fastingReasonGroupsI18n.voluntary();
                case PROHIBITING_FASTING -> fastingReasonGroupsI18n.prohibiting();
            };
            textBuff.append("\n\n").append(reasonGroupI18n.icsTitle()).append(": ");
            for (var reasonI18n : reasonGroupI18n.reasons()) {
                if (reasonModel.id().equals(reasonI18n.id())) {
                    final var reasonTitle = getReasonTitle(reasonModel, reasonI18n);
                    if (reasonTitle != null) {
                        textBuff.append(reasonTitle);
                        final var citesI18n = reasonI18n.cites();
                        if (isNotEmpty(citesI18n)) {
                            textBuff.append('\n');
                            final var i = random.nextInt(citesI18n.length);
                            final var citeI18n = citesI18n[i];
                            if (citeI18n.intro() != null) {
                                textBuff.append(citeI18n.intro()).append('\n');
                            }
                            textBuff.append(citeI18n.quote());
                            if (isNotEmpty(citeI18n.references())) {
                                for (final var hadithReferenceI18n : citeI18n.references()) {
                                    textBuff.append('\n').append(renderHadithHref(hadithReferenceI18n));
                                }
                            }
                        }
                    }
                    break;
                }
            }
        }
    }


    private static @Nullable String getReasonTitle(
            final CalendarDayPair.CalendarEventReason reasonModel,
            final I18n.Fasting.Reason reasonI18n) {
        if (reasonI18n.headlines() != null) {
            final var headlineId = reasonModel.headlineId();
            if (headlineId != null) {
                final var headlineI18n = reasonI18n.headlines().get(headlineId);
                if (headlineI18n != null) {
                    return headlineI18n;
                }
            }
        }
        return reasonI18n.title();
    }


    static void renderHijriDateTo(final CalendarDay day, String[] monthNames, final StringBuilder buff) {
        buff.append(day.day()).append(' ').append(monthNames[day.month()]).append(' ').append(day.year());
    }
}
