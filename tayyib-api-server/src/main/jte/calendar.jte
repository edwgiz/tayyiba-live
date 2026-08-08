@import com.github.edwgiz.tayyib.adapter.commons.dgs.dto.PrayerTimeMethod
@import com.github.edwgiz.tayyib.domain.model.I18n

@param Object $
@param I18n i18n

<!DOCTYPE html>
<html lang="en">
<head>
    <link rel="icon" type="image/svg+xml" href="/img/icon.svg">
    <link rel="canonical" href="https://calendar.tayyiba.live/"/>
    <meta charset="UTF-8"/>
    <meta property="og:title" content="Islamic Fasting Calendar"/>
    <meta property="og:description" content="Track Ramadan, White Days, Monday & Thursday fasting, Ashura and more."/>
    <meta property="og:type" content="website"/>
    <meta property="og:url" content="https://calendar.tayyiba.live/"/>
    <meta property="og:image" content="https://calendar.tayyiba.live/img/logo.svg"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <meta name="description"
          content="Interactive Islamic fasting calendar with Ramadan, White Days, Monday & Thursday fasting, Ashura, Arafah,
    Hijri dates, and sunrise/sunset times based on your location."/>
    <script type="application/ld+json">
        {
            "@context": "https://schema.org",
            "@type": "WebApplication",
            "name": "Islamic Fasting Calendar",
            "applicationCategory": "LifestyleApplication",
            "operatingSystem": "Web Browser",
            "description": "Interactive Islamic fasting calendar with Hijri dates and fasting reminders.",
            "url": "https://calendar.tayyiba.live/"
        }
    </script>
    <link rel="stylesheet" href="https://unpkg.com/smart-webcomponents@26.0.0/source/styles/smart.default.css"
          type="text/css"/>
    <link rel="stylesheet" href="https://unpkg.com/smart-webcomponents@26.0.0/source/styles/smart.dark-green.css"
          type="text/css"/>
    <link rel="stylesheet" href="https://unpkg.com/smart-webcomponents@26.0.0/source/styles/smart.ui.default.css"
          type="text/css"/>
    <link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css" type="text/css"/>
    <script type="module">
        import 'https://unpkg.com/smart-webcomponents@26.0.0/source/modules/smart.button.js';
        import 'https://unpkg.com/smart-webcomponents@26.0.0/source/modules/smart.tooltip.js';
        import 'https://unpkg.com/smart-webcomponents@26.0.0/source/modules/smart.window.js';
        import 'https://unpkg.com/smart-webcomponents@26.0.0/source/modules/smart.dropdownlist.js';
    </script>

    <title>${i18n.fasting().head().title()}</title>

    <style>
        body {
            font-family: sans-serif;
            background-color: var(--smart-base-background-color-dark);
            color: var(--smart-primary-color);
            font-size: larger;
        }

        body > * {
            background: none;
        }

        a {
            color: var(--smart-base-100);
        }

        a:visited {
            color: #c58af9;
        }

        a:hover {
            color: #ffffff;
        }

        a:focus-visible {
            outline: 2px solid rgba(138, 180, 248, 0.6);
        }

        hr {
            color: var(--smart-border);
            border-color: var(--smart-border);
        }

        header > h1 {
            text-align: center;
            display: flex;
            flex-direction: row;
            @media (max-width: 50em) {
                flex-direction: column;
            }
            align-items: center;
            justify-content: center;

            img {
                margin-right: 0.4em;
            }
        }

        section.fasting-reason > h4 {
            margin: 0;
        }

        section.fasting-reason > ul > li {
            margin-top: 0.75em;
        }

        blockquote {
            display: block;
            padding: 0;
            margin: 0;
            font-style: italic;
        }

        blockquote::before {
            content: "- ";
        }

        li:has(>q) {
            color: var(--smart-base-100);
        }

        li > q {
            display: block;
            padding: 0;
            margin: 0;
            color: var(--smart-primary-color);
        }

        geolocation {
            --smart-button-flat-color: var(--smart-background-color) !important;
            text-align: center !important;
            vertical-align: middle !important;
            cursor: pointer !important;
            text-transform: var(--smart-button-text-transform) !important;
            letter-spacing: .08929em !important;
            overflow: hidden !important;
            border-color: var(--smart-border) !important;
            border-radius: var(--smart-border-top-left-radius) var(--smart-border-top-right-radius) var(--smart-border-bottom-right-radius) var(--smart-border-bottom-left-radius) !important;
            border-width: var(--smart-border-width) !important;
            border-style: solid !important;

            box-sizing: border-box !important;
            font-family: var(--smart-font-family), sans-serif !important;
            font-size: var(--smart-font-size) !important;
            font-weight: var(--smart-font-weight) !important;
            -webkit-tap-highlight-color: transparent !important;
            background: var(--smart-background) !important;
            color: var(--smart-background-color) !important;
        }

        geolocation:hover {
            color: var(--smart-ui-state-color-hover);
            border-color: var(--smart-ui-state-border-hover);
            background-color: var(--smart-ui-state-hover);
            transition: background-color .1s linear, box-shadow 280ms ease-in-out;
            opacity: var(--smart-button-opacity-hover)
        }

        div#page-loading-spinner {
            position: fixed;
            inset: 0;
            z-index: 9999;
            backdrop-filter: blur(1px);
            -webkit-backdrop-filter: blur(1px);
            background-image: url('/img/spinner-smart-base-800.svg');
            background-size: max(2rem, min(25vw, 25vh));
            background-repeat: no-repeat;
            background-position: center center;
            opacity: 0.0;
            transition: opacity 500ms ease;
        }

        div#page-loading-spinner.shown {
            opacity: 1;
        }

        div#page-loading-spinner.hidden {
            display: none;
        }


        main {
            display: flex;
            flex-direction: column;
            align-items: center;
            margin-top: 2em;
            margin-bottom: 1em;

            section#months {
                display: flex;
                flex-direction: row;
                justify-content: center;
                align-items: center;
                margin-bottom: 1em;
                gap: 0.5em;
                @media (max-width: 50em) {
                    flex-direction: column;
                }
            }

            section#calendar-grid-cells {
                display: grid;
                grid-template-columns: repeat(7, 6em);
                @media (max-width: 50em) {
                    grid-template-columns: repeat(7, 14vw);
                }

                div {
                    padding: 0.3ch;
                    text-align: center;
                }

                div.calendar-grid-header {
                    background-color: var(--smart-border);
                }

                div.calendar-grid-cell {
                    border-top-style: solid;
                    border-top-color: var(--smart-border);
                    border-top-width: 1px;
                    padding-top: 0.2em;
                    padding-bottom: 0.2em;

                    div {
                        padding: 0;
                    }

                    div.calendar-grid-cell-hijri-month {
                        color: #00b300;
                        font-size: small;
                        @media (max-width: 50em) {
                            font-size: x-small;
                        }
                        overflow-wrap: break-word;
                    }

                    div.calendar-grid-cell-hijri-date {
                        color: #00ff00;
                    }

                    div.calendar-grid-cell-fasting-undefined {
                        color: #b3b300;
                        font-size: small;
                        @media (max-width: 50em) {
                            font-size: x-small;
                        }

                        img.calendar-grid-cell-fasting-undefined-spinner {
                            width: auto;
                            height: 1lh;
                            vertical-align: middle;
                        }
                    }

                    div.calendar-grid-cell-fasting {
                        color: #ffff00;
                        font-size: small;
                        @media (max-width: 50em) {
                            font-size: x-small;
                        }
                    }
                }

                div.calendar-grid-cell-today {
                    background-image: url("/img/hand-drawn-circle.svg");
                    background-size: auto 100%;
                    @media (max-width: 35em) {
                        background-size: 100% 100%;
                    }
                    background-position: center;
                    background-repeat: no-repeat;
                }

                div.calendar-grid-cell-adjacent-month {
                    color: var(--smart-base-100)
                }
            }

            section#settings {
                display: grid;
                grid-template-columns: min-content fit-content(80vw);
                justify-content: center;
                align-items: center;
                gap: 2em;
                font-size: var(--smart-font-size);
                margin-top: 3em;
                margin-bottom: 3em;

                .settings-label {
                    white-space: nowrap;
                }

                smart-drop-down-list#change-prayer-time-method-dropdown {
                    div.smart-drop-down-container {
                        width: 50ch !important;
                    }
                }

                > div > table {
                    margin: 0;
                    border-style: none;
                    border-collapse: collapse;

                    > tr > td {
                        padding: 0;
                        vertical-align: middle;
                    }
                }

                .settings-location-dependencies {
                    display: none;
                }
            }

            section#fasting-reasons,
            section#prohibiting-fasting-reasons {
                font-size: small;
                width: 100%;

                section > h4 {
                    margin-top: 3ch;
                    margin-bottom: 0;
                }
            }
        }

        #prev-month-button, #next-month-button {
            width: 13em;
            align-items: center;
            --smart-ui-state-border-active: var(--smart-base-200) !important;
            --smart-ui-state-active: var(--smart-base-200) !important;

            button {
                vertical-align: top;
            }
        }

        #current-month-label {
            display: inline-block;
            width: 13em;
            text-align: center;
            vertical-align: center;
            white-space: nowrap;
        }

        smart-tooltip div.smart-tooltip-content {
            background-color: var(--smart-secondary-lighter) !important;
            border-color: var(--smart-secondary-lighter) !important;

            &::after {
                --smart-tooltip-arrow-color: var(--smart-secondary-lighter);
            }

            > ul {
                margin: 0;
                padding-left: 0;
                list-style-type: none;
                list-style-position: inside;

                > li {
                    margin: 0.4em;

                    > a {
                        color: var(--smart-base-50);
                        text-decoration: none;

                        &:hover {
                            border-bottom-color: var(--smart-base-50);
                            border-bottom-width: 1px;
                            border-bottom-style: dashed;
                        }

                        &:visited {
                            color: var(--smart-base-50);
                        }
                    }
                }
            }
        }

        span.smart-list-items-group-label {
            font-size: small !important;
            text-align: right !important;
            display: inline-block;
            color: var(--smart-base-100) !important;
            margin: 0 !important;
            padding: 0 !important;
            cursor: default;
            height: min-content;
        }

        smart-window#pick-location-on-map-window {
            position: fixed;
            inset: 0;
            width: 100%;
            height: 100%;
            border-color: var(--smart-base-100) !important;
        }

        div.smart-header-section {
            background-color: var(--smart-base-100) !important;

            div.smart-header {
                font-size: larger;
            }
        }

        div#pick-location-on-map-windowContent {
            overflow: hidden;
        }

        smart-button#pick-location-on-map-save-button,
        smart-button#pick-location-on-map-default-button {
            margin-right: 1em;
            padding: 0.2em 1em;
            --smart-window-header-button-size: max-content !important;
            width: max-content !important;
        }

        smart-button#pick-location-on-map-default-button {
            background-color: var(--smart-base-200)
        }

        .leaflet-container {
            height: 400px;
            width: 600px;
            max-width: 100%;
            max-height: 100%;
        }

        .leaflet-layer,
        .leaflet-control-zoom-in,
        .leaflet-control-zoom-out,
        .leaflet-control-attribution {
            filter: invert(100%) hue-rotate(180deg) brightness(95%) contrast(90%);
            background: var(--smart-base-background-color-dark);
        }

        footer {
            color: var(--smart-base-100);
            font-size: small;
        }

        span.missed-permission {
            color: var(--smart-base-100);
        }
    </style>
</head>
<body theme="dark">
<header>
    <h1>
        <img alt="Khatim icon" style="height: 2ch; vertical-align: center" src="/img/logo.svg">
        <span>${i18n.fasting().head().title()}</span>
    </h1>
    @template.calendar.fastingReasons(
    reasonGroup = i18n.fasting().reasonGroups().generic(),
    hadithCollectionReferencing = i18n.hadithReferencing().collections())
</header>
<main>
    <section id="months">
        <smart-button id="prev-month-button"></smart-button>
        <span id="current-month-label"></span>
        <smart-button id="next-month-button"></smart-button>
    </section>
    <section id="calendar-grid-cells"></section>
    <script>
        /**
         * @param {string} query
         * @param {string} requestId
         * @param {object} variables
         */
        function graphql(query, requestId, variables = {}) {
            return fetch("/graphql", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    "X-Request-ID": requestId
                },
                body: JSON.stringify({
                    query,
                    variables
                })
            });
        }

    </script>
    <section id="settings">
        <div class="settings-label">${i18n.settings().location().label()}:</div>
        <div><span id='location-value'></span>
            <img src='/img/spinner-smart-base-800.svg' height='2em'/>
            <smart-button id="pick-location-on-map-button">${i18n.settings().location().pickOnMap()}</smart-button>
        </div>
        <div class="settings-label settings-location-dependencies">${i18n.settings().timeZone()}:</div>
        <div class="settings-location-dependencies">
            <smart-drop-down-list id='change-tz-dropdown'></smart-drop-down-list>
        </div>
        <div class="settings-label settings-location-dependencies">${i18n.settings().prayerTimeMethod()}:</div>
        <table class="settings-location-dependencies">
            <tr>
                <td>
                    <smart-drop-down-list
                            id='change-prayer-time-method-dropdown'
                            filterable
                            width='auto'
                            sorted='true'
                            filter-mode='containsIgnoreCase'
                            selected-values='["${PrayerTimeMethod.AUTO_BY_LOCATION}"]'
                            horizontal-scroll-bar-visibility='hidden'
                            selection-mode='one'>
                        @for(var prayerTimeMethodGroup : i18n.prayerTimeMethodGroups().values())
                            <smart-list-items-group label='${prayerTimeMethodGroup.name()}'>
                                @for(var e : prayerTimeMethodGroup.methodNames().entrySet())
                                    <smart-list-item value='${e.getKey()}'>${e.getValue()}</smart-list-item>
                                @endfor
                            </smart-list-items-group>
                        @endfor
                    </smart-drop-down-list>
                </td>
                <td id='settings-prayer-time-method-label'></td>
            </tr>
        </table>
    </section>
    <section id="fasting-reasons">
        @template.calendar.fastingReasons(
        reasonGroup = i18n.fasting().reasonGroups().obligatory(),
        hadithCollectionReferencing = i18n.hadithReferencing().collections())
        @template.calendar.fastingReasons(
        reasonGroup = i18n.fasting().reasonGroups().voluntary(),
        hadithCollectionReferencing = i18n.hadithReferencing().collections())
    </section>
    <section id="prohibiting-fasting-reasons">
        @template.calendar.fastingReasons(
        reasonGroup = i18n.fasting().reasonGroups().prohibiting(),
        hadithCollectionReferencing = i18n.hadithReferencing().collections())
    </section>
</main>
<hr/>
<footer>
    <ul>
        <li>
            ${i18n.footer().contacts().label()}
            :
            <a
                    href="https://github.com/edwgiz/tayyib-fasting-calendar"
                    target="_blank"
                    rel="noopener noreferrer"
            >${i18n.footer().contacts().githubRefDescription()}</a>
        </li>
        <li>
            ${i18n.footer().attributions().label()}
            :
            <a href="https://iconscout.com/contributors/rizsign-icon" target="_blank">Rizsign icon</a>
        </li>
    </ul>
</footer>

<smart-window
        id='pick-location-on-map-window'
        header-buttons='["close"]'
        modal
></smart-window>

<div id="page-loading-spinner" class='hidden'></div>

<script type="module">
    const defaultDatetimeOptions = Intl.DateTimeFormat().resolvedOptions();
    let locale = defaultDatetimeOptions.locale;

    const d = document;

    const prayerTimeMethodRepository = {
        /**
         * @param {string|null} value
         */
        set(value) {
            return localStorage.setItem('prayerTimeMethod', value);
        },

        /**
         * @return {string|null}
         */
        get() {
            return localStorage.getItem('prayerTimeMethod');
        },
    }


    class GeoPositionModel {
        /**
         * @param {number} latitude
         * @param {number} longitude
         */
        constructor(latitude, longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }
    }


    /**
     * @typedef {'CUSTOM' | 'DEVICE'} GeoPositionSourceModel
     */

    class GetPositionUtils {
        /**
         * @param {GeoPositionModel|null} o1
         * @param {GeoPositionModel|null} o2
         * @return {boolean}
         */
        static equals(o1, o2) {
            if (o1 === o2) {
                return true;
            }
            if (o1 !== null && o2 !== null) {
                return o1.latitude === o2.latitude && o1.longitude === o2.longitude
            }
            return false;
        }
    }

    const geoPositionRepository = {

        /**
         * @param {GeoPositionSourceModel} source
         * @return {GeoPositionModel|null}
         */
        get(source) {
            const str = localStorage.getItem('geoPosition' + source);
            return JSON.parse(str);
        },

        /**
         * @param {GeoPositionModel} model
         * @param {GeoPositionSourceModel} source
         */
        set(model, source) {
            const str = JSON.stringify(model);
            localStorage.setItem('geoPosition' + source, str);
        },
    };

    const getGeoPositionUsecase = {
        /**
         * @return {GeoPositionModel|null}
         */
        apply() {
            const custom = geoPositionRepository.get('CUSTOM')
            if (custom != null) {
                return custom;
            }
            const device = geoPositionRepository.get('DEVICE')
            if (device != null) {
                return device;
            }
            return null;
        }
    }

    const setGeoPositionUsecase = {
        /**
         * @param {number|null} latitude
         * @param {number|null} longitude
         * @param {GeoPositionSourceModel} source
         */
        apply(latitude, longitude, source) {
            const clean = latitude === null || longitude === null;
            const oldModel = geoPositionRepository.get(source);
            const newModel = clean ? null : new GeoPositionModel(latitude, longitude);
            if (!GetPositionUtils.equals(oldModel, newModel)) {
                geoPositionRepository.set(newModel, source);
                const actualModel = getGeoPositionUsecase.apply();
                if (!GetPositionUtils.equals(oldModel, actualModel)) {
                    reloadCalendar();
                }
            }
        }
    }

    const timezoneRepository = {
        getOrDefault(defaultValue) {
            const value = localStorage.getItem('timezone');
            return value ? value : defaultValue;
        },
        set(str) {
            localStorage.setItem('timezone', str);
        },
    };


    let month = new Date();
    month.setDate(2);

    const getTimezoneUsecase = {
        apply() {
            return timezoneRepository.getOrDefault(defaultDatetimeOptions.timeZone);
        }
    }

    let datetimeFormats = null;

    function updateDatetimeFormats() {
        const tzValue = getTimezoneUsecase.apply();
        datetimeFormats = {
            monthLabel: new Intl.DateTimeFormat(locale, {
                timeZone: tzValue,
                month: 'long',
                year: 'numeric'
            }),
        }
    }

    updateDatetimeFormats();

    const gridEl = d.getElementById('calendar-grid-cells');

    function updateCalendarHeader(dayOfWeekNames) {
        gridEl.innerHTML = '';
        for (const dayOfWeekName of dayOfWeekNames) {
            const cellEl = d.createElement('div');
            cellEl.classList.add("calendar-grid-header");
            cellEl.textContent = dayOfWeekName;
            gridEl.appendChild(cellEl);
        }
    }


    function getCalendarEventReasonHeadline(elId) {
        const fastingReasonEl = d.getElementById(elId);
        return fastingReasonEl.firstElementChild.textContent;
    }


    function getFastingEvents(dto, day) {
        const result = [];
        if (day.events && day.events.length > 0) {
            for (const calendarEvent of day.events) {
                REASONS:
                    for (const reasonView of calendarEvent.reasonViews) {
                        for (const reason of dto.calendarEventReasons) {
                            if (reason.id === reasonView.id) {
                                if (reason.reasonGroup === 'VOLUNTARY_FASTING'
                                    || reason.reasonGroup === 'OBLIGATORY_FASTING') {
                                    result.push(calendarEvent);
                                    break REASONS;
                                }
                            }
                        }
                    }
            }
        }
        return result;
    }

    /**
     * @param minutes {Number}
     * @param defaultResult {String}
     * @return {string}
     */
    function formatMinutes(minutes, defaultResult) {
        if (!minutes) {
            return defaultResult;
        }
        if (0 <= minutes && minutes <= 1440) {
            const minutesOnly = minutes % 60;
            return ((minutes - minutesOnly) / 60).toString().padStart(2, '0')
                + ':'
                + minutesOnly.toString().padStart(2, '0');
        } else {
            return "";
        }
    }


    /**
     * @param {object} dto
     * @param {boolean} hasLocation
     */
    function updateCalendarDates(dto, hasLocation) {
        for (let i = gridEl.children.length - 1; i >= 7; i--) {
            gridEl.children.item(i).remove();
        }
        for (let i = 0; i < dto.days.length; i++) {
            const day = dto.days[i];
            const cellElId = 'calendar-grid-cell-' + i;
            const cellEl = d.createElement('div');
            cellEl.id = cellElId;
            cellEl.classList.add('calendar-grid-cell');
            if (i === dto.todayIndex) {
                cellEl.classList.add('calendar-grid-cell-today')
            }
            if (day.gregorian.isAdjacentMonth) {
                cellEl.classList.add('calendar-grid-cell-adjacent-month');
            }
            cellEl.appendChild(d.createTextNode(day.gregorian.dayOfMonth));

            {
                const hijriMonthEl = d.createElement('div');
                hijriMonthEl.classList.add('calendar-grid-cell-hijri-month');
                const hijriMonthKey = day.hijri.month.toString();
                hijriMonthEl.textContent = dto.hijriMonthNames.find(e => e.key === hijriMonthKey).value;
                cellEl.appendChild(hijriMonthEl);
            }
            {
                const hijriDateEl = d.createElement('div');
                hijriDateEl.classList.add('calendar-grid-cell-hijri-date')
                hijriDateEl.textContent = day.hijri.dayOfMonth;
                cellEl.appendChild(hijriDateEl);
            }
            const fastingEvents = getFastingEvents(dto, day);
            for (const fastingEvent of fastingEvents) {
                const fastingEl = d.createElement('div');
                if (fastingEvent.startMinute || fastingEvent.endMinute) {
                    fastingEl.classList.add('calendar-grid-cell-fasting');
                    fastingEl.textContent = formatMinutes(fastingEvent.startMinute, '${i18n.calendar().event().fajr()}')
                        + '…'
                        + formatMinutes(fastingEvent.endMinute, '${i18n.calendar().event().sunset()}');
                } else {
                    fastingEl.classList.add('calendar-grid-cell-fasting-undefined')
                    fastingEl.appendChild(d.createTextNode('${i18n.calendar().event().fajr()}'));
                    if (!hasLocation) {
                        fastingEl.appendChild(d.createTextNode('…'));
                    } else {
                        const spinnerEl = d.createElement("img");
                        spinnerEl.classList.add('calendar-grid-cell-fasting-undefined-spinner')
                        spinnerEl.src = '/img/spinner.svg';
                        fastingEl.appendChild(spinnerEl);
                    }
                    fastingEl.appendChild(d.createTextNode('${i18n.calendar().event().sunset()}'));
                }
                cellEl.appendChild(fastingEl);
                const tooltip = d.createElement('smart-tooltip');
                tooltip.selector = cellElId;
                tooltip.position = 'bottom';
                tooltip.arrow = true;
                const tooltipContent = d.createElement('ul');
                const reasonIds = new Set();
                for (const calendarEvent of day.events) {
                    for (const reasonView of calendarEvent.reasonViews) {
                        if (!reasonIds.has(reasonView.id)) {
                            reasonIds.add(reasonView.id)
                            const calendarEventReasonEl = d.createElement('li');
                            const anchorEl = d.createElement('a');
                            anchorEl.href = '#' + reasonView.id;
                            anchorEl.textContent = reasonView.headline
                                ? reasonView.headline
                                : getCalendarEventReasonHeadline(reasonView.id);
                            calendarEventReasonEl.appendChild(anchorEl);
                            tooltipContent.appendChild(calendarEventReasonEl);
                        }
                    }
                }
                tooltip.appendChild(tooltipContent);
                cellEl.appendChild(tooltip);
            }
            gridEl.appendChild(cellEl);
        }
    }


    const locationValueEl = d.getElementById('location-value');


    function addLocationSpinner() {
        const spinner = d.createElement('img');
        spinner.style.height = '2em';
        spinner.src = '/img/spinner-smart-base-800.svg';
        locationValueEl.replaceChildren(spinner);
    }


    addLocationSpinner();


    const locationLabelAdapter = {

        /**
         * @param {PermissionStatus} permission
         */
        updateByPermission(permission) {
            if (permission.state === 'granted') {
                addLocationSpinner();
            } else {
                const grantI18n = '${i18n.settings().location().permission().grant()}';
                const orI18n = ' ${i18n.commons().or()} ';
                const revokeBlockingI18n = ' ${i18n.settings().location().permission().revokeBlocking()} ';
                const missingPermissionDescriptionEl = d.createElement('span');
                missingPermissionDescriptionEl.classList.add('missed-permission');
                missingPermissionDescriptionEl.textContent = grantI18n;
                if (typeof HTMLGeolocationElement === 'function') {
                    locationValueEl.replaceChildren(missingPermissionDescriptionEl, ' ', d.createElement('geolocation'), orI18n);
                } else {
                    if (permission.state === 'denied') {
                        missingPermissionDescriptionEl.appendChild(d.createTextNode(', ' + revokeBlockingI18n + orI18n));
                        locationValueEl.replaceChildren(missingPermissionDescriptionEl);
                    } else {
                        const geolocationEl = d.createElement('smart-button');
                        geolocationEl.id = 'grant-geolocation-permission';
                        geolocationEl.textContent = grantI18n;
                        geolocationEl.addEventListener('click', updateLocationByDevice);
                        locationValueEl.replaceChildren(' ', geolocationEl, orI18n);
                    }
                }
            }
        }
    }

    const updateLocationDependenciesUsecase = {
        wgs84Formatter: new Intl.NumberFormat(undefined, {
            minimumFractionDigits: 0,
            maximumFractionDigits: 5,
        }),
        settingsLocationDependantEls: Array.from(document.getElementsByClassName('settings-location-dependencies')),


        /**
         * @param {number} latitude
         * @param {number} longitude
         * @return {string}
         */
        formatWgs84(latitude, longitude) {
            return this.wgs84Formatter.format(latitude) + ',' + this.wgs84Formatter.format(longitude);
        },

        /**
         * @param {number|null} latitude
         * @param {number|null} longitude
         * @param {string|null} displayName
         */
        apply(latitude, longitude, displayName) {
            const hasLocation = latitude != null;
            if (hasLocation) {
                locationValueEl.textContent = displayName != null && displayName.trim().length > 0 ? displayName : this.formatWgs84(latitude, longitude);
            } else {
                navigator.permissions.query({name: "geolocation"}).then(locationLabelAdapter.updateByPermission);
            }

            for (const el of this.settingsLocationDependantEls) {
                if (el instanceof HTMLElement) {
                    el.style.display = hasLocation ? 'block' : 'none';
                }
            }
        }
    }


    const prevMonthEl = d.getElementById('prev-month-button');
    const currentMonthEl = d.getElementById('current-month-label');
    const nextMonthEl = d.getElementById('next-month-button');

    function updateMonthLabels() {
        const prevMonth = new Date(month);
        prevMonth.setUTCMonth(month.getUTCMonth() - 1);
        const nextMonth = new Date(month);
        nextMonth.setUTCMonth(month.getUTCMonth() + 1);
        prevMonthEl.innerHTML = datetimeFormats.monthLabel.format(prevMonth) + '&nbsp; ◂◂';
        currentMonthEl.textContent = datetimeFormats.monthLabel.format(month);
        nextMonthEl.innerHTML = '▸▸ &nbsp;' + datetimeFormats.monthLabel.format(nextMonth);
    }

    const pageLoadingSpinner = {
        el: d.getElementById('page-loading-spinner'),
        timeoutId: null,

        hideTransitionHandler() {
            pageLoadingSpinner.el.classList.add('hidden');
            pageLoadingSpinner.timeoutId = null;
        },

        hide() {
            const el = this.el;
            el.classList.remove('shown');
            this.timeoutId = setTimeout(this.hideTransitionHandler, 500);
        },

        show() {
            if(this.timeoutId != null) {
                clearTimeout(this.timeoutId);
                this.timeoutId = null;
            }
            const el = this.el;
            el.classList.remove('hidden');
            requestAnimationFrame(_ => el.classList.add('shown'));
        },
    }

    function ensureSelectedItemVisible(smartDropDownListEl) {
        smartDropDownListEl.addEventListener('open', _ => {
            const selectedValue = smartDropDownListEl.selectedValues[0];
            const selectedItem = smartDropDownListEl.getItem(selectedValue);
            smartDropDownListEl.ensureVisible(selectedItem);
        }, {once: true});
    }

    const changePrayerTimeMethodDropdownEl = d.getElementById('change-prayer-time-method-dropdown')

    const updateSettingsPrayerTimeMethodLabelUsecase = {
        labelEl: document.getElementById('settings-prayer-time-method-label'),
        /**
         * @param {string|null} usedPrayerTimeMethod
         */
        apply(usedPrayerTimeMethod) {
            const chosenPrayerTimeMethod = prayerTimeMethodRepository.get();
            this.labelEl.replaceChildren(usedPrayerTimeMethod !== chosenPrayerTimeMethod
                ? this.getPrayerTimeMethodLabel(usedPrayerTimeMethod)
                : []);
        },
        /**
         * @param {string|null} key
         * @return {string|null}
         */
        getPrayerTimeMethodLabel(key) {
            const item = changePrayerTimeMethodDropdownEl.items.find(item => item.value === key);
            if (item) {
                return ': \u00A0 \u00A0 ' + item.label;
            } else {
                return '';
            }
        }
    }

    let reloadCalendarDatesRequestId = null;

    function reloadCalendar() {
        reloadCalendarDatesRequestId = crypto.randomUUID();

        pageLoadingSpinner.show()

        const today = new Date();
        const isTodayMonth = month.getFullYear() === today.getFullYear() && month.getMonth() === today.getMonth();

        /**
         * @return {null|{latitude: number, longitude: number, prayerTimeMethod}}
         */
        function fastingTimeCalculationInput() {
            const getGeoPosition = getGeoPositionUsecase.apply();
            return !getGeoPosition ? null : {
                latitude: getGeoPosition.latitude,
                longitude: getGeoPosition.longitude,
                prayerTimeMethod: prayerTimeMethodRepository.get() ?? 'AUTO_BY_LOCATION'
            };
        }

        const args = {
            input: {
                millis: isTodayMonth ? today.getTime() : month.getTime(),
                timezone: timezoneRepository.getOrDefault(Intl.DateTimeFormat().resolvedOptions().timeZone),
                hijriMethod: "UAQ",
                fastingTimeCalculationInput: fastingTimeCalculationInput()
            }
        };

        graphql(`query
        GetCalendarCells($input: GetCalendarCellsInput!) {
            getCalendarCells(input: $input) {
                hijriMonthNames {
                    key
                    value
                }
                calendarEventReasons {
                    id
                    reasonGroup
                }
                currentMonthLabel
                dayOfWeekNames
                days {
                    gregorian {
                        isAdjacentMonth
                        dayOfMonth
                    }
                    hijri {
                        dayOfMonth
                        month
                    }
                    events {
                        reasonViews {
                            id,
                            headline
                        }
                        startMinute
                        endMinute
                    }
                }
                todayIndex,
                locationDisplayName,
                prayerTimeMethod
            }
        }`, reloadCalendarDatesRequestId, args)
            .then(httpResp => {
                const requestId = httpResp.headers.get('X-Request-ID');
                if (requestId === reloadCalendarDatesRequestId) {
                    pageLoadingSpinner.hide();
                    updateMonthLabels();
                }
                return httpResp.json();
            })
            .then(jsonResp => {
                const dto = jsonResp.data.getCalendarCells;
                if (!isTodayMonth) {
                    dto.todayIndex = -1;
                }
                const pageScrollTop = window.scrollY;
                updateCalendarHeader(dto.dayOfWeekNames);
                const locationInput = args.input.fastingTimeCalculationInput;
                const hasLocation = locationInput != null;
                updateCalendarDates(dto, hasLocation);
                updateLocationDependenciesUsecase.apply(
                    locationInput?.latitude,
                    locationInput?.longitude,
                    dto.locationDisplayName);
                updateSettingsPrayerTimeMethodLabelUsecase.apply(dto.prayerTimeMethod);
                window.scrollTo({top: pageScrollTop, behavior: 'smooth'});
            });
    }


    reloadCalendar();


    prevMonthEl.addEventListener('click', _ => {
        month.setUTCMonth(month.getUTCMonth() - 1);
        reloadCalendar();
    });
    nextMonthEl.addEventListener('click', _ => {
        month.setUTCMonth(month.getUTCMonth() + 1);
        reloadCalendar();
    });


    {// init prayerTimeMethod
        const el = changePrayerTimeMethodDropdownEl;

        let oldVal = prayerTimeMethodRepository.get();
        if (!oldVal) {
            oldVal = '${PrayerTimeMethod.AUTO_BY_LOCATION}';
            prayerTimeMethodRepository.set(oldVal)
        }
        el.selectedValues = [oldVal];

        el.addEventListener('change', _ => {
            prayerTimeMethodRepository.set(el.selectedValues[0])
            reloadCalendar();
        });
        ensureSelectedItemVisible(el);
    }


    function updateLocationByDevice() {
        navigator.geolocation.getCurrentPosition(
            position => {
                setGeoPositionUsecase.apply(position.coords.latitude, position.coords.longitude, 'DEVICE');
            },
            error => {
                if (error.code === error.PERMISSION_DENIED) {
                    setGeoPositionUsecase.apply(null, null, 'DEVICE');
                } else if (error.code === error.POSITION_UNAVAILABLE) {
                    console.warn('Error getting location: position unavailable');
                } else if (error.code === error.TIMEOUT) {
                    console.warn('Error getting location: timeout');
                } else {
                    console.error('Error getting location:', error);
                }
            },
            {
                enableHighAccuracy: false,
            }
        );
    }

    navigator.permissions.query({
        name: 'geolocation'
    }).then(permission => {
        permission.addEventListener('change', () => {
            locationLabelAdapter.updateByPermission(permission);
            if (permission.state !== 'granted') {
                setGeoPositionUsecase.apply(null, null, 'DEVICE');
            } else {
                updateLocationByDevice();
            }
        });
    });

    requestIdleCallback(() => {
        reloadCalendar();
        if (!getGeoPositionUsecase.apply()) {
            updateLocationByDevice();
        }
    });

    let changeLocationWindowEl = null;
    let changeLocationMarker = null;
    let changeLocationMapView = null;

    d.getElementById('pick-location-on-map-button').addEventListener('click', async _ => {
        let geoPosition = getGeoPositionUsecase.apply();
        if (!geoPosition) {
            geoPosition = { // default Kaaba
                latitude: 21.4225,
                longitude: 39.8262,
            };
        }
        if (changeLocationWindowEl == null) {
            changeLocationWindowEl = d.getElementById('pick-location-on-map-window');
            changeLocationWindowEl.label = '${i18n.settings().location().pickOnMap()}';
            await import('https://unpkg.com/leaflet/dist/leaflet.js');
            changeLocationMapView = L.map('pick-location-on-map-windowContent').setView([geoPosition.latitude, geoPosition.longitude], 8);
            L.tileLayer('https://tile.openstreetmap.org/{z}/{x}/{y}.png', {
                maxZoom: 19,
                attribution: '&copy; <a href="http://www.openstreetmap.org/copyright">OpenStreetMap</a>'
            }).addTo(changeLocationMapView);
            changeLocationMarker = L.marker([geoPosition.latitude, geoPosition.longitude], {
                draggable: true
            }).addTo(changeLocationMapView);
            const smartButtonsEl = changeLocationWindowEl.querySelector('.smart-buttons-container');
            {
                const saveButtonEl = d.createElement('smart-button');
                saveButtonEl.id = 'pick-location-on-map-save-button';
                saveButtonEl.textContent = '${i18n.commons().ok()}';
                saveButtonEl.addEventListener('click', _ => {
                    const markerPosition = changeLocationMarker.getLatLng();
                    setGeoPositionUsecase.apply(markerPosition.lat, markerPosition.lng, 'CUSTOM');
                    changeLocationWindowEl.close();
                });
                smartButtonsEl.prepend(saveButtonEl);
            }
            {
                const defaultButtonEl = document.createElement('smart-button');
                defaultButtonEl.id = 'pick-location-on-map-default-button';
                defaultButtonEl.textContent = '${i18n.settings().byDevice()}';
                defaultButtonEl.addEventListener('click', _ => {
                    setGeoPositionUsecase.apply(null, null, 'CUSTOM');
                    changeLocationWindowEl.close();
                });
                smartButtonsEl.prepend(defaultButtonEl);
            }
        } else {
            changeLocationMarker.setLatLng([geoPosition.latitude, geoPosition.longitude]);
            changeLocationMapView.panTo([geoPosition.latitude, geoPosition.longitude]);
        }
        changeLocationWindowEl.open();
    });


    {
        const dataSource = Intl.supportedValuesOf('timeZone').map(v => ({l: v, v: v}));
        const selectedValue = getTimezoneUsecase.apply();
        const changeTzDropdown = d.getElementById('change-tz-dropdown');
        changeTzDropdown.selectionMode = 'one';
        changeTzDropdown.filterable = true;
        changeTzDropdown.filterMode = 'containsIgnoreCase';
        changeTzDropdown.horizontalScrollBarVisibility = 'hidden';
        changeTzDropdown.virtualization = true;
        changeTzDropdown.displayMember = 'l';
        changeTzDropdown.valueMember = 'v';
        changeTzDropdown.dataSource = dataSource;
        changeTzDropdown.selectedValues = [selectedValue];
        changeTzDropdown.addEventListener('change', (event) => {
            timezoneRepository.set(event.detail.value);
            updateDatetimeFormats();
            reloadCalendar();
        });
        ensureSelectedItemVisible(changeTzDropdown);
        changeTzDropdown.disabled = false;
    }
</script>
</body>
</html>
