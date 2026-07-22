package com.github.edwgiz.tayyib.adapter.out.httpClient;

import com.github.edwgiz.tayyib.domain.model.CalendarDay;
import com.github.edwgiz.tayyib.domain.model.HijriMethod;
import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.LocalDate;


@Component
public class AladhanIslamicCalendarClient {

    private final RestClient restClient;


    AladhanIslamicCalendarClient() {
        restClient = RestClient.create();
    }

    public @Nullable GregorianToHijriCalendar gregorianToHijriCalendar(
            final int gregorianMonth,
            final int gregorianYear,
            final HijriMethod method) {
        return restClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("https")
                        .host("api.aladhan.com")
                        .path("/v1/gToHCalendar/")
                        .path(Integer.toString(gregorianMonth))
                        .path("/")
                        .path(Integer.toString(gregorianYear))
                        .queryParam("calendarMethod", method)
                        .build())
                .exchange((_, clientResponse) -> {
                    if (clientResponse.getStatusCode() != HttpStatus.OK) {
                        return null;
                    }
                    final var responseDto = clientResponse.bodyTo(GregorianToHijriCalendarResponse.class);
                    if (responseDto == null || responseDto.code != 200) {
                        return null;
                    }

                    final var gregorian = new LocalDate[responseDto.data().length];
                    final var hijri = new CalendarDay[responseDto.data().length];
                    final var datePairs = responseDto.data();
                    for (int i = 0; i < datePairs.length; i++) {
                        var datePair = datePairs[i];
                        gregorian[i] = toLocalDate(datePair.gregorian());
                        hijri[i] = toCalendarDay(datePair.hijri());
                    }
                    return new GregorianToHijriCalendar(gregorian, hijri);
                });
    }

    private static LocalDate toLocalDate(final GregorianToHijriCalendarResponse.GregorianHijriPair.Date dateDto) {
        return LocalDate.of(
                Integer.parseInt(dateDto.year()),
                dateDto.month().number(),
                Integer.parseInt(dateDto.day()));
    }

    private static CalendarDay toCalendarDay(final GregorianToHijriCalendarResponse.GregorianHijriPair.Date dateDto) {
        return new CalendarDay(
                Integer.parseInt(dateDto.year()),
                dateDto.month().number(),
                Integer.parseInt(dateDto.day()));
    }

    public record GregorianToHijriCalendarResponse(
            int code,
            GregorianHijriPair[] data) {
        public record GregorianHijriPair(
                Date hijri,
                Date gregorian) {
            public record Date(
                    String year,
                    Month month,
                    String day) {
                public record Month(
                        int number) {
                }
            }
        }
    }

    public record GregorianToHijriCalendar(
            LocalDate[] gregorian,
            CalendarDay[] hijri) {
    }
}
