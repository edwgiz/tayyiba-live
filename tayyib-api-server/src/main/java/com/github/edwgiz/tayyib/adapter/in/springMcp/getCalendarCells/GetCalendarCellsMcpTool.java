package com.github.edwgiz.tayyib.adapter.in.springMcp.getCalendarCells;

import com.github.edwgiz.tayyib.adapter.in.springMcp.getCalendarCells.dto.HijriMethod;
import com.github.edwgiz.tayyib.adapter.in.springMcp.getCalendarCells.dto.PrayerTimeMethod;
import com.github.edwgiz.tayyib.adapter.in.springMcp.getCalendarCells.dto.Response;
import com.github.edwgiz.tayyib.adapter.in.springMcp.getCalendarCells.dto.Response.CalendarDayPair.CalendarDay;
import com.github.edwgiz.tayyib.adapter.in.springMcp.getCalendarCells.dto.Response.CalendarDayPair.CalendarEvent;
import com.github.edwgiz.tayyib.adapter.in.springMcp.getCalendarCells.dto.Response.CalendarDayPair.CalendarEventReason;
import com.github.edwgiz.tayyib.domain.model.CalendarDayPair;
import com.github.edwgiz.tayyib.domain.model.I18n;
import com.github.edwgiz.tayyib.domain.usecase.calendar.GetCalendarDaysUsecase;
import com.github.edwgiz.tayyib.domain.usecase.calendar.GetCalendarDaysUsecase.FastingTimeCalculationArgs;
import com.github.edwgiz.tayyib.domain.usecase.i18n.GetBundleUsecase;
import io.modelcontextprotocol.spec.McpError;
import org.jspecify.annotations.Nullable;
import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import static com.github.edwgiz.tayyib.adapter.in.commons.rendering.I18nRenderingUtils.renderHadithHref;
import static com.github.edwgiz.tayyib.adapter.in.springMcp.getCalendarCells.dto.Response.CalendarEventReasonGroup.OBLIGATORY_FASTING;
import static com.github.edwgiz.tayyib.adapter.in.springMcp.getCalendarCells.dto.Response.CalendarEventReasonGroup.PROHIBITING_FASTING;
import static com.github.edwgiz.tayyib.adapter.in.springMcp.getCalendarCells.dto.Response.CalendarEventReasonGroup.VOLUNTARY_FASTING;
import static io.modelcontextprotocol.spec.McpSchema.ErrorCodes.INTERNAL_ERROR;
import static io.modelcontextprotocol.spec.McpSchema.ErrorCodes.INVALID_PARAMS;
import static java.util.Objects.requireNonNull;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;


@Component
class GetCalendarCellsMcpTool {

    private final GetCalendarDaysUsecase getCalendarDaysUsecase;
    private final GetBundleUsecase getBundleUsecase;

    GetCalendarCellsMcpTool(final GetCalendarDaysUsecase getCalendarDaysUsecase,
                            final GetBundleUsecase getBundleUsecase) {
        this.getCalendarDaysUsecase = getCalendarDaysUsecase;
        this.getBundleUsecase = getBundleUsecase;
    }


    @McpTool(
            name = "get-islamic-fasting-calendar-cells",
            description = """
                    I will return fasting schedule for requested days by given user's Hijri method based on the Quran and the strong Hadiths.\
                    Also specific bound times will be calculated by given timezone, latitude/longitude and prayer time method prayer.
                    Also references to reliable religious sources will be provided.""",
            generateOutputSchema = true,
            annotations = @McpTool.McpAnnotations(
                    destructiveHint = false,
                    openWorldHint = false,
                    readOnlyHint = true,
                    title = "Islamic Fasting Calendar"
            )

    )
    public Response apply(
            final @McpToolParam LocalDate start,
            final @McpToolParam LocalDate end,
            final @McpToolParam(description = "Must be IANA Time Zone ID") String timezone,
            final @McpToolParam(description = "Must be in BCP 47") String locale,
            final @McpToolParam(description = """
                    HJCoSA: High Judicial Council of Saudi Arabia,
                    UAQ: Umm al-Qura University,
                    Diyanet: Presidency of Religious Affairs of Türkiye (Diyanet İşleri Başkanlığı)""") HijriMethod hijriMethod,
            final @McpToolParam(required = false, description = "Must be in WGS 84") @Nullable Double latitude,
            final @McpToolParam(required = false, description = "Must be in WGS 84") @Nullable Double longitude,
            final @McpToolParam(description = """
                    AUTO_BY_LOCATION: Automatic by given latitude/longitude,
                    MWL: Muslim World League,
                    ISNA: Islamic Society of North America (ISNA),
                    EGAS: Egyptian General Authority of Survey,
                    UMM_AL_QURA: Umm al-Qura University, Makkah,
                    KARACHI: University of Islamic Sciences, Karachi,
                    TEHRAN: Institute of Geophysics, University of Tehran,
                    SHIA_QUM: Shia Ithna Ashari, Qum,
                    GULF: Gulf Region,
                    KUWAIT: Kuwait,
                    QATAR: Qatar,
                    MUIS: Majlis Ugama Islam Singapura (MUIS),
                    UOIF: Union of Islamic Organizations of France (UOIF),
                    DIYANET: Diyanet (Turkey),
                    SAMR: Spiritual Administration of Muslims, Russia,
                    MSC: Moon Sighting Committee,
                    DUBAI: Dubai, UAE,
                    JAKIM: JAKIM, Malaysia,
                    TUNISIA: Tunisia,
                    ALGERIA: Algeria,
                    KEMENAG: Ministry of Religious Affairs, Indonesia,
                    MOROCCO: Morocco,
                    CIL: Islamic Community of Lisbon,
                    """, required = false) @Nullable PrayerTimeMethod prayerTimeMethod
    ) {
        final ZoneId zoneId;
        try {
            zoneId = ZoneId.of(timezone);
        } catch (RuntimeException ex) {
            throw McpError.builder(INVALID_PARAMS).message("timezone must be IANA Time Zone ID").build();
        }

        final Locale locale_;
        try {
            locale_ = Locale.forLanguageTag(locale);
        } catch (final RuntimeException ex) {
            throw McpError.builder(INVALID_PARAMS).message("locale must be in BCP 47").build();
        }


        return switch (getCalendarDaysUsecase.apply(
                start,
                end,
                switch (hijriMethod) {
                    case HJCoSA -> com.github.edwgiz.tayyib.domain.model.HijriMethod.HJCoSA;
                    case UAQ -> com.github.edwgiz.tayyib.domain.model.HijriMethod.UAQ;
                    case Diyanet -> com.github.edwgiz.tayyib.domain.model.HijriMethod.Diyanet;
                },
                zoneId,
                createFastingTimeCalculationArgs(latitude, longitude, locale_, prayerTimeMethod))) {
            case GetCalendarDaysUsecase.Result.Ok ok -> {
                final var i18n = getBundleUsecase.apply(locale_);
                final var reasonDtos = new ArrayList<Response.Reason>();
                final var reasonIds = ok.calendarDayPairs().stream()
                        .flatMap(day -> day.events().stream())
                        .flatMap(event -> event.reasons().stream())
                        .map(CalendarDayPair.CalendarEventReason::id)
                        .collect(Collectors.toSet());
                addToReasonDtos(i18n.fasting().reasonGroups().obligatory(), OBLIGATORY_FASTING, reasonIds, reasonDtos);
                addToReasonDtos(i18n.fasting().reasonGroups().voluntary(), VOLUNTARY_FASTING, reasonIds, reasonDtos);
                addToReasonDtos(i18n.fasting().reasonGroups().prohibiting(), PROHIBITING_FASTING, reasonIds, reasonDtos);

                yield toOkResponseDto(ok, reasonDtos);
            }
            case GetCalendarDaysUsecase.Result.ServiceUnavailable _ ->
                    throw McpError.builder(INTERNAL_ERROR).message("Service Unavailable").build();
        };
    }


    @Nullable FastingTimeCalculationArgs createFastingTimeCalculationArgs(
            final @Nullable Double latitude,
            final @Nullable Double longitude,
            final Locale locale,
            final @Nullable PrayerTimeMethod prayerTimeMethod) {

        if (latitude == null || longitude == null || prayerTimeMethod == null) return null;

        if (latitude < -90.0 || 90.0 < latitude) {
            throw McpError.builder(INVALID_PARAMS).message("latitude out of bound -90..90").build();
        }
        if (longitude < -180.0 || 180.0 < longitude) {
            throw McpError.builder(INVALID_PARAMS).message("longitude out of bound -180..180").build();
        }

        return new FastingTimeCalculationArgs(
                latitude,
                longitude,
                locale.getLanguage(),
                switch (prayerTimeMethod) {
                    case AUTO_BY_LOCATION -> null;
                    case MWL -> com.github.edwgiz.tayyib.domain.model.PrayerTimeMethod.MWL;
                    case ISNA -> com.github.edwgiz.tayyib.domain.model.PrayerTimeMethod.ISNA;
                    case EGAS -> com.github.edwgiz.tayyib.domain.model.PrayerTimeMethod.EGAS;
                    case UMM_AL_QURA -> com.github.edwgiz.tayyib.domain.model.PrayerTimeMethod.UMM_AL_QURA;
                    case KARACHI -> com.github.edwgiz.tayyib.domain.model.PrayerTimeMethod.KARACHI;
                    case TEHRAN -> com.github.edwgiz.tayyib.domain.model.PrayerTimeMethod.TEHRAN;
                    case SHIA_QUM -> com.github.edwgiz.tayyib.domain.model.PrayerTimeMethod.SHIA_QUM;
                    case GULF -> com.github.edwgiz.tayyib.domain.model.PrayerTimeMethod.GULF;
                    case KUWAIT -> com.github.edwgiz.tayyib.domain.model.PrayerTimeMethod.KUWAIT;
                    case QATAR -> com.github.edwgiz.tayyib.domain.model.PrayerTimeMethod.QATAR;
                    case MUIS -> com.github.edwgiz.tayyib.domain.model.PrayerTimeMethod.MUIS;
                    case UOIF -> com.github.edwgiz.tayyib.domain.model.PrayerTimeMethod.UOIF;
                    case DIYANET -> com.github.edwgiz.tayyib.domain.model.PrayerTimeMethod.DIYANET;
                    case SAMR -> com.github.edwgiz.tayyib.domain.model.PrayerTimeMethod.SAMR;
                    case MSC -> com.github.edwgiz.tayyib.domain.model.PrayerTimeMethod.MSC;
                    case DUBAI -> com.github.edwgiz.tayyib.domain.model.PrayerTimeMethod.DUBAI;
                    case JAKIM -> com.github.edwgiz.tayyib.domain.model.PrayerTimeMethod.JAKIM;
                    case TUNISIA -> com.github.edwgiz.tayyib.domain.model.PrayerTimeMethod.TUNISIA;
                    case ALGERIA -> com.github.edwgiz.tayyib.domain.model.PrayerTimeMethod.ALGERIA;
                    case KEMENAG -> com.github.edwgiz.tayyib.domain.model.PrayerTimeMethod.KEMENAG;
                    case MOROCCO -> com.github.edwgiz.tayyib.domain.model.PrayerTimeMethod.MOROCCO;
                    case CIL -> com.github.edwgiz.tayyib.domain.model.PrayerTimeMethod.CIL;
                });
    }


    private Response toOkResponseDto(
            final GetCalendarDaysUsecase.Result.Ok ok,
            final ArrayList<Response.Reason> reasonDtos) {
        final var calendarDayPairs = ok.calendarDayPairs().stream()
                .filter(calendarDayPair -> isNotEmpty(calendarDayPair.events()))
                .map(calendarDayPair -> new Response.CalendarDayPair(
                        calendarDayPair.gregorian(),
                        new CalendarDay(
                                calendarDayPair.hijri().year(),
                                calendarDayPair.hijri().month(),
                                calendarDayPair.hijri().day()),
                        toCalendarEventListDto(calendarDayPair.events()))).toList();

        return new Response(
                calendarDayPairs,
                ok.locationDisplayName(),
                toPrayerTimeMethodDto(ok.prayerTimeMethod()),
                reasonDtos
        );
    }


    List<CalendarEvent> toCalendarEventListDto(final List<CalendarDayPair.CalendarEvent> events) {
        return events.stream().map(calendarEvent -> new CalendarEvent(
                calendarEvent.reasons().stream().map(reason -> new CalendarEventReason(
                        reason.id(),
                        reason.headlineId(),
                        switch (reason.group()) {
                            case OBLIGATORY_FASTING -> OBLIGATORY_FASTING;
                            case VOLUNTARY_FASTING -> VOLUNTARY_FASTING;
                            case PROHIBITING_FASTING -> PROHIBITING_FASTING;
                        }
                )).toList(),
                calendarEvent.startMinute(),
                calendarEvent.endMinute())
        ).toList();
    }


    private @Nullable PrayerTimeMethod toPrayerTimeMethodDto(com.github.edwgiz.tayyib.domain.model.@Nullable PrayerTimeMethod prayerTimeMethod) {
        return switch (prayerTimeMethod) {
            case MWL -> PrayerTimeMethod.MWL;
            case ISNA -> PrayerTimeMethod.ISNA;
            case EGAS -> PrayerTimeMethod.EGAS;
            case UMM_AL_QURA -> PrayerTimeMethod.UMM_AL_QURA;
            case KARACHI -> PrayerTimeMethod.KARACHI;
            case TEHRAN -> PrayerTimeMethod.TEHRAN;
            case SHIA_QUM -> PrayerTimeMethod.SHIA_QUM;
            case GULF -> PrayerTimeMethod.GULF;
            case KUWAIT -> PrayerTimeMethod.KUWAIT;
            case QATAR -> PrayerTimeMethod.QATAR;
            case MUIS -> PrayerTimeMethod.MUIS;
            case UOIF -> PrayerTimeMethod.UOIF;
            case DIYANET -> PrayerTimeMethod.DIYANET;
            case SAMR -> PrayerTimeMethod.SAMR;
            case MSC -> PrayerTimeMethod.MSC;
            case DUBAI -> PrayerTimeMethod.DUBAI;
            case JAKIM -> PrayerTimeMethod.JAKIM;
            case TUNISIA -> PrayerTimeMethod.TUNISIA;
            case ALGERIA -> PrayerTimeMethod.ALGERIA;
            case KEMENAG -> PrayerTimeMethod.KEMENAG;
            case MOROCCO -> PrayerTimeMethod.MOROCCO;
            case CIL -> PrayerTimeMethod.CIL;
            case null -> null;
        };
    }


    private void addToReasonDtos(
            final I18n.Fasting.FastingReasonGroup reasonGroupI18n,
            final Response.CalendarEventReasonGroup reasonGroupDto,
            final Set<String> reasonIds,
            final List<Response.Reason> reasonDtos) {
        for (final var reasonI18n : reasonGroupI18n.reasons()) {
            if (reasonIds.contains(reasonI18n.id())) {
                reasonDtos.add(toReasonDto(reasonI18n, reasonGroupDto));
            }
        }
    }


    private Response.Reason toReasonDto(
            final I18n.Fasting.Reason reasonI18n,
            final Response.CalendarEventReasonGroup reasonGroupDto
    ) {
        final var citeDtos = new Response.Reason.Cite[reasonI18n.cites().length];
        final var citeI18ns = reasonI18n.cites();
        for (int i = 0; i < citeI18ns.length; i++) {
            var citeI18n = citeI18ns[i];
            final var hadithReferenceI18ns = citeI18n.references();
            final var hadithReferenceUrls = new String[hadithReferenceI18ns.length];
            for (int j = 0; j < hadithReferenceUrls.length; j++) {
                hadithReferenceUrls[j] = renderHadithHref(hadithReferenceI18ns[j]);
            }
            citeDtos[i] = new Response.Reason.Cite(citeI18n.intro(), citeI18n.quote(), hadithReferenceUrls);
        }
        return new Response.Reason(
                requireNonNull(reasonI18n.id()),
                reasonGroupDto,
                requireNonNull(reasonI18n.title()),
                citeDtos,
                reasonI18n.headlines());
    }

}
