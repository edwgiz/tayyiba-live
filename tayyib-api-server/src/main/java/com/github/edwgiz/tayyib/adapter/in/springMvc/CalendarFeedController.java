package com.github.edwgiz.tayyib.adapter.in.springMvc;

import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.ComponentFactory;
import net.fortuna.ical4j.model.component.CalendarComponent;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.component.VTimeZone;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.Uid;
import net.fortuna.ical4j.model.property.immutable.ImmutableCalScale;
import net.fortuna.ical4j.model.property.immutable.ImmutableVersion;
import net.fortuna.ical4j.validate.ValidationException;
import net.fortuna.ical4j.validate.ValidationResult;
import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.time.Instant;
import java.util.Locale;
import java.util.UUID;

import static org.springframework.http.HttpStatus.OK;


@RestController
public class CalendarFeedController {

    private static final MediaType CONTENT_TYPE = new MediaType("text", "calendar");


    @GetMapping("/calendar/fasting.ics")
    public ResponseEntity<StreamingResponseBody> get(
            @RequestParam @Nullable String latitude,
            @RequestParam @Nullable String longitude,
            @RequestParam @Nullable String timezone,
            @RequestParam @Nullable String prayerTimeMethod,
            @RequestParam @Nullable String locale,
            Locale defaultLocale) {

        final var calendar = new Calendar();
        calendar.add(new ProdId("-//Tayyiba//Fasting Calendar//" + defaultLocale));
        calendar.add(ImmutableVersion.VERSION_2_0);
        calendar.add(ImmutableCalScale.GREGORIAN);

        final var hello = new VEvent(Instant.parse("2026-08-10T18:00:00Z"), "Hello");
        hello.add(new Uid(UUID.randomUUID().toString()));
        calendar.add(hello);

        return ResponseEntity
                .status(OK)
                .contentType(CONTENT_TYPE)
                .body(outputStream -> new CalendarOutputter().output(calendar, outputStream));
    }


}
