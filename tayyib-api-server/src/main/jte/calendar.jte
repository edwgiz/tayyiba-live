@import com.github.edwgiz.tayyib.domain.model.I18n

@param Object $
@param I18n i18n

<!DOCTYPE html>
<html lang="en">
<head>
    <link rel="icon" type="image/svg+xml" href="icon.svg">
    <link rel="canonical" href="https://calendar.tayyiba.live/"/>
    <meta charset="UTF-8"/>
    <meta property="og:title" content="Islamic Fasting Calendar"/>
    <meta property="og:description" content="Track Ramadan, White Days, Monday & Thursday fasting, Ashura and more."/>
    <meta property="og:type" content="website"/>
    <meta property="og:url" content="https://calendar.tayyiba.live/"/>
    <meta property="og:image" content="https://calendar.tayyiba.live/logo.svg"/>
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
    <script src="https://unpkg.com/suncalc@1.9.0/suncalc.js"></script>

    <title>${i18n.fasting().head().title()}</title>

    <style>
        body {
            font-family: sans-serif;
            background-color: var(--smart-base-background-color-dark);
            color: var(--smart-primary-color);
            font-size: larger;
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

        h3.fasting-reason-group {
            margin-block-start: 2em;
            color: var(--smart-base-100);
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
                            width: 0.5em;
                            height: 0.5em;
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
                    background-image: url("hand-drawn-circle.svg");
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
            >ul {
                margin: 0;
                padding-left: 0;
                list-style-type: none;
                list-style-position: inside;
                >li {
                    margin: 0.4em;
                    >a {
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

        smart-window#change-location-window {
            border-color: var(--smart-base-100) !important;
            left: 0;
            top: 0;
            width: 100%;
            height: 100%;
        }

        div.smart-header-section {
            background-color: var(--smart-base-100) !important;

            div.smart-header {
                font-size: larger;
            }
        }

        div#change-location-windowContent {
            overflow: hidden;
        }

        smart-button#change-location-save-button,
        smart-button#change-location-default-button {
            margin-right: 1em;
            padding: 0.2em 1em;
            --smart-window-header-button-size: max-content !important;
            width: max-content !important;
        }

        smart-button#change-location-default-button {
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
    </style>
</head>
<body theme="dark">
<header>
    <h1>
        <img alt="Khatim icon" style="height: 2ch; vertical-align: center" src="logo.svg">
        <span>${i18n.fasting().head().title()}</span>
    </h1>
    @template.calendar.fastingReasons(
    reasonGroup = i18n.fasting().reasonGroups().generic(),
    hadithCollectionReferencing = i18n.hadithReferencing().collections())
</header>
<main>
    <section id="months">
        <smart-button id="prev-month-button"></smart-button>
        <span id="current-month-label">Loading... <img alt="" style="height: 2ch" src="spinner.svg"/></span>
        <smart-button id="next-month-button"></smart-button>
    </section>
    <section id="calendar-grid-cells">
    </section>
    <script>
        function graphql(query, variables = {}) {
            return fetch("/graphql", {
                method: "POST",
                headers: {
                    "content-type": "application/json"
                },
                body: JSON.stringify({
                    query,
                    variables
                })
            });
        }

    </script>
    <section id="settings">
        <div class="settings-label">
            <i18n-text key="settings.location"></i18n-text>
            :
        </div>
        <div><span id="location-value"></span>
            <smart-button id="change-location-open-button">
                <i18n-text key="settings.change"></i18n-text>
            </smart-button>
        </div>
        <div class="settings-label">
            <i18n-text key="settings.timeZone"></i18n-text>
            :
        </div>
        <div>
            <smart-drop-down-list
                    id='change-tz-dropdown'
                    filterable
                    selection-mode="one"
                    disabled="disabled"
            ></smart-drop-down-list>
        </div>
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
        id='change-location-window'
        header-buttons='["close"]'
        modal
></smart-window>

<script type="module">
    const defaultDatetimeOptions = Intl.DateTimeFormat().resolvedOptions();
    let locale = defaultDatetimeOptions.locale;

    const d = document;

    const geoPositionRepository = {
        getAsString() {
            return localStorage.getItem('geoPosition');
        },
        get() {
            return JSON.parse(this.getAsString());
        },
        setAsString(str) {
            localStorage.setItem('geoPosition', str);
        },
    };
    const timezoneRepository = {
        getOrDefault(defaultValue) {
            const value = localStorage.getItem('timezone');
            return value ? value : defaultValue;
        },
        set(str) {
            localStorage.setItem('timezone', str);
        },
    };


    const today = new Date();
    let month = new Date(today);
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
            yearNumber: new Intl.DateTimeFormat(locale, {
                timeZone: tzValue,
                year: 'numeric',
            }),
            monthNumber: new Intl.DateTimeFormat(locale, {
                timeZone: tzValue,
                month: '2-digit',
            }),
            date: new Intl.DateTimeFormat(locale, {
                timeZone: tzValue,
                day: '2-digit',
            }),
            utcWeekday: new Intl.DateTimeFormat(locale, {
                timeZone: 'UTC',
                weekday: 'short',
            }),
            weekday: new Intl.DateTimeFormat(locale, {
                timeZone: tzValue,
                weekday: 'short',
            }),
            hijriDate: new Intl.DateTimeFormat(locale, {
                calendar: "islamic",
                timeZone: tzValue,
                numberingSystem: "latn",
                day: '2-digit',
            }),
            hijriMonth: new Intl.DateTimeFormat('ar-SA', {
                calendar: "islamic",
                timeZone: tzValue,
                month: "long",
            }),
            hijriMonthNumber: new Intl.DateTimeFormat(locale, {
                calendar: "islamic",
                timeZone: tzValue,
                numberingSystem: "latn",
                month: "2-digit",
            }),
            time: new Intl.DateTimeFormat(locale, {
                timeZone: tzValue,
                hour: '2-digit',
                hour12: false,
                minute: '2-digit',
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


    graphql(`query
            GetCalendarCells(
                $millis: Long!,
                $timezoneOffset: Int!,
                $method: HijriMethod!
            ) {
                getCalendarCells(
                    millis: $millis,
                    timezoneOffset: $timezoneOffset,
                    method: $method
                ) {
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
                    todayIndex
                }
            }`,
        {
            millis: Date.now(),
            timezoneOffset: -today.getTimezoneOffset(),
            method: "UAQ"
        })
        .then(httpResp => httpResp.json())
        .then(jsonResp => {
            const dto = jsonResp.data.getCalendarCells;
            updateCalendarHeader(dto.dayOfWeekNames);
            updateCalendarDates(dto);
        });

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

    prevMonthEl.addEventListener('click', _ => {
        month.setUTCMonth(month.getUTCMonth() - 1);
        update();
    });
    nextMonthEl.addEventListener('click', _ => {
        month.setUTCMonth(month.getUTCMonth() + 1);
        update();
    });


    function getCalendarEventReasonHeadline(elId) {
        const fastingReasonEl = d.getElementById(elId);
        return fastingReasonEl.firstElementChild.textContent;
    }


    function updateCalendarDates(dto) {
        for (let i = gridEl.children.length - 1; i >= 7; i--) {
            gridEl.children.item(i).remove();
        }

        const geoPosition = geoPositionRepository.get();
//        const todayDateId = getDateId(today);
//        const activeMonthNumber = datetimeFormats.monthNumber.format(month);

//        const weekdayIndex = 0;//getWeekdayIndex(month);
//        let date = new Date(month);
//        date.setDate(month.getDate() - weekdayIndex + firstDayOfWeek);

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
            if (day.events && day.events.length > 0) {
                const fastingEl = d.createElement('div');
                if (false) { // geoPosition
  ///                  fastingEl.classList.add('calendar-grid-cell-fasting')
//                    fastingEl.textContent = timeFormat.format(times.sunrise) + ' - ' + timeFormat.format(times.sunset);
                } else {
                    fastingEl.classList.add('calendar-grid-cell-fasting-undefined')
                    fastingEl.appendChild(d.createTextNode('Fajr '));
                    const spinnerEl = d.createElement("img");
                    spinnerEl.classList.add('calendar-grid-cell-fasting-undefined-spinner')
                    spinnerEl.src = 'spinner.svg';
                    fastingEl.appendChild(spinnerEl);
                    fastingEl.appendChild(d.createTextNode(' Sunset'));
                }
                cellEl.appendChild(fastingEl);
                const tooltip = d.createElement('smart-tooltip');
                tooltip.selector = cellElId;
                tooltip.position = 'bottom';
                tooltip.arrow = true;
                const tooltipContent = d.createElement('ul');
                for (const calendarEvent of day.events) {
                    for(const reasonView of calendarEvent.reasonViews) {
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
                tooltip.appendChild(tooltipContent);
                cellEl.appendChild(tooltip);
            }
            gridEl.appendChild(cellEl);
        }
    }


    function update() {
        updateMonthLabels();
        updateCalendarDates();
    }

    updateMonthLabels();

    function updateLocationDescription() {
        const el = d.getElementById('location-value');
        const position = geoPositionRepository.get();
        if (position) {
            const url = new URL("https://nominatim.openstreetmap.org/reverse");
            url.searchParams.set("format", "jsonv2");
            url.searchParams.set("lat", position.latitude);
            url.searchParams.set("lon", position.longitude);
            url.searchParams.set("accept-language", locale);
            fetch(url).then(response => {
                if (response.ok) {
                    response.json().then(data => {
                        el.textContent = data.display_name + '.';
                    });
                }
            });
        } else {
            const spinner = d.createElement('img');
            spinner.style.height = '2em';
            spinner.src = 'spinner.svg';
            el.append(spinner);
        }
    }

    function setLocation(latitude, longitude) {
        const currentPositionStr = JSON.stringify({
            latitude: latitude,
            longitude: longitude,
        });
        const prevPositionStr = geoPositionRepository.getAsString();
        if (currentPositionStr !== prevPositionStr) {
            geoPositionRepository.setAsString(currentPositionStr);
            updateCalendarDates();
            updateLocationDescription();
        }
    }

    function updateLocationByDevice() {
        navigator.geolocation.getCurrentPosition(
            position => {
                setLocation(position.coords.latitude, position.coords.longitude);
            },
            error => {
                console.error('Error getting location:', error);
            },
            {
                enableHighAccuracy: false,
                maximumAge: 3600000 // 1 hour
            }
        );
    }

    requestIdleCallback(() => {
        updateLocationDescription();
        updateLocationByDevice();
    });

    let changeLocationWindowEl = null;
    let changeLocationMarker = null;
    let changeLocationMapView = null;

    d.getElementById('change-location-open-button').addEventListener('click', async _ => {
        let geoPosition = geoPositionRepository.get();
        if (!geoPosition) {
            // default Kaaba
            geoPosition = {
                latitude: 21.4225,
                longitude: 39.8262,
            };
        }
        if (changeLocationWindowEl == null) {
            changeLocationWindowEl = d.getElementById('change-location-window');
            changeLocationWindowEl.label = '';//await i18n('settings.changeLocationWindowTitle');
            await import('https://unpkg.com/leaflet/dist/leaflet.js');
            changeLocationMapView = L.map('change-location-windowContent').setView([geoPosition.latitude, geoPosition.longitude], 8);
            L.tileLayer('https://tile.openstreetmap.org/{z}/{x}/{y}.png', {
                maxZoom: 19,
                attribution: '&copy; <a href="http://www.openstreetmap.org/copyright">OpenStreetMap</a>'
            }).addTo(changeLocationMapView);
            changeLocationMarker = L.marker([geoPosition.latitude, geoPosition.longitude], {
                draggable: true
            }).addTo(changeLocationMapView);
            const smartButtonsEl = changeLocationWindowEl.querySelector('.smart-buttons-container');
            {
                const saveButtonEl = document.createElement('smart-button');
                saveButtonEl.id = 'change-location-save-button';
                saveButtonEl.textContent = '';//await i18n('settings.save');
                saveButtonEl.addEventListener('click', _ => {
                    const markerPosition = changeLocationMarker.getLatLng();
                    setLocation(markerPosition.lat, markerPosition.lng);
                    changeLocationWindowEl.close();
                });
                smartButtonsEl.prepend(saveButtonEl);
            }
            {
                const defaultButtonEl = document.createElement('smart-button');
                defaultButtonEl.id = 'change-location-default-button';
                defaultButtonEl.textContent = '';//await i18n('settings.default');
                defaultButtonEl.addEventListener('click', _ => {
                    updateLocationByDevice();
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

    requestIdleCallback(() => {
        const changeTzDropdown = d.getElementById('change-tz-dropdown');
        changeTzDropdown.disabled = false;
        changeTzDropdown.filterable = true;
        changeTzDropdown.filterMode = 'contains';
        changeTzDropdown.horizontalScrollBarVisibility = 'hidden';
        changeTzDropdown.virtualized = true;
        changeTzDropdown.dataSource = Intl.supportedValuesOf('timeZone').map(v => ({'l': v, 'v': v}));
        changeTzDropdown.displayMember = 'l';
        changeTzDropdown.valueMember = 'v';
        const selectedValue = getTimezoneUsecase.apply();
        changeTzDropdown.selectedValues = [selectedValue];
        const selectedItem = changeTzDropdown.getItem(selectedValue);
        if (selectedItem) {
            changeTzDropdown.ensureVisible(selectedItem);
        }
        changeTzDropdown.addEventListener('change', (event) => {
            timezoneRepository.set(event.detail.value);
            updateDatetimeFormats();
            update();
        });
    });
</script>
</body>
</html>
