@import com.github.edwgiz.tayyib.adapter.commons.dgs.dto.HijriMethod
@import com.github.edwgiz.tayyib.adapter.commons.dgs.dto.PrayerTimeMethod
@import com.github.edwgiz.tayyib.domain.model.I18n

@param I18n i18n

<!DOCTYPE html>
<html lang='en'>
<head>
    <link rel='icon' type='image/svg+xml' href='/img/icon.svg'>
    <link rel='canonical' href='https://tayyiba.live/calendar'/>
    <meta charset='UTF-8'/>
    <meta name='viewport' content='width=device-width, initial-scale=1.0'/>
    <meta name='description' content='${i18n.fasting().head().description()}'/>

    <meta property='og:title' content='${i18n.fasting().head().title()}'/>
    <meta property='og:description' content='${i18n.fasting().head().description()}'/>
    <meta property='og:type' content='website'/>
    <meta property='og:url' content='https://tayyiba.live/calendar'/>
    <meta property='og:image' content='https://tayyiba.live/img/logo.svg'/>

    <script type='application/ld+json'>
        {
            "@context": "https://schema.org",
            "@type": "WebApplication",
            "name": "${i18n.fasting().head().title()}",
            "applicationCategory": "LifestyleApplication",
            "operatingSystem": "Web Browser",
            "description": "${i18n.fasting().head().description()}",
            "url": "https://calendar.tayyiba.live/"
        }
    </script>
    <link rel='stylesheet' href='https://unpkg.com/smart-webcomponents@26.0.0/source/styles/smart.default.css'
          type='text/css'/>
    <link rel='stylesheet' href='https://unpkg.com/smart-webcomponents@26.0.0/source/styles/smart.dark-green.css'
          type='text/css'/>
    <link rel='stylesheet' href='https://unpkg.com/smart-webcomponents@26.0.0/source/styles/smart.ui.default.css'
          type='text/css'/>
    <link rel='stylesheet' href='https://unpkg.com/leaflet@1.9.4/dist/leaflet.css' type='text/css'/>
    <script type='module'>
        import 'https://unpkg.com/smart-webcomponents@26.0.0/source/modules/smart.button.js';
        import 'https://unpkg.com/smart-webcomponents@26.0.0/source/modules/smart.tooltip.js';
        import 'https://unpkg.com/smart-webcomponents@26.0.0/source/modules/smart.window.js';
        import 'https://unpkg.com/smart-webcomponents@26.0.0/source/modules/smart.dropdownlist.js';
    </script>

    <title>${i18n.fasting().head().title()}</title>

    <style>
        :root {
            interpolate-size: allow-keywords;
            --smart-font-family: sans-serif !important;
            font-family: var(--smart-font-family);
            --smart-font-weight: 300 !important;
            font-weight: var(--smart-font-weight);
        }

        body {
            background-color: var(--smart-base-background-color-dark);
            color: var(--smart-primary-color);
            font-size: larger;
        }

        body > * {
            background: none;
        }

        a {
            color: var(--smart-base-100);

            &:visited {
                color: #c58af9;
            }

            &:hover {
                color: #ffffff;
            }

            &:focus-visible {
                outline: 2px solid rgba(138, 180, 248, 0.6);
            }
        }

        h1, h2, h3, h4, h5 {
            margin-bottom: 0.2lh;
            padding-bottom: 0;
        }

        hr {
            color: var(--smart-border);
            border-color: var(--smart-border);
        }

        header {

            h1 {
                font-size: clamp(1.38em, 4vw, 2.5rem);
                text-align: center;
                display: flex;
                flex-direction: row;
                flex-wrap: wrap;
                align-items: center;
                justify-content: center;
                padding: 0.5em 0;

                img {
                    margin-right: 0.4em;
                }
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

            &::before {
                content: '- ';
            }
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

            &:hover {
                color: var(--smart-ui-state-color-hover);
                border-color: var(--smart-ui-state-border-hover);
                background-color: var(--smart-ui-state-hover);
                transition: background-color .1s linear, box-shadow 280ms ease-in-out;
                opacity: var(--smart-button-opacity-hover)
            }
        }

        div#page-loading-spinner {
            position: fixed;
            inset: 0;
            z-index: 9999;

            backdrop-filter: blur(1px);
            -webkit-backdrop-filter: blur(1px);

            &::before {
                content: '';
                position: absolute;
                inset: 0;

                background-image: url('/img/spinner-smart-base-800.svg');
                background-size: max(2rem, min(25vw, 25vh));
                background-repeat: no-repeat;
                background-position: center center;
                opacity: 0;
            }

            &.shown::before {
                animation: page-loading-spinner-show 1s ease 1s forwards;
            }

            &.hidden {
                display: none;
            }
        }

        @keyframes page-loading-spinner-show {
            from {
                opacity: 0;
            }

            to {
                opacity: 1;
            }
        }

        main {
            display: flex;
            flex-direction: column;
            align-items: center;
            margin-top: 2em;
            margin-bottom: 1em;

            section#months {
                display: grid;
                grid-template-columns: 1fr min-content 1fr;
                grid-column-gap: 1ch;
                justify-content: center;
                align-items: center;

                margin-bottom: 1lh;

                #prev-month-button, #next-month-button {
                    align-items: center;
                    --smart-ui-state-border-active: var(--smart-base-200) !important;
                    --smart-ui-state-active: var(--smart-base-200) !important;

                    > button {
                        vertical-align: top;

                        > span {
                            display: inline-block;
                            transition: width 2s ease, opacity 1s ease;
                            @media (max-width: 32em) {
                                width: 0 !important;
                                opacity: 0;
                            }
                        }
                    }
                }

                > * {
                    margin: 0;
                    align-self: center;
                    justify-self: center;
                }

                #current-month-label {
                    display: inline-block;
                    white-space: nowrap;
                }
            }

            --max-calendar-column-width: 6em;

            section#calendar-grid-cells {
                display: grid;
                grid-template-columns: repeat(7, min(var(--max-calendar-column-width), 100vw / 7));

                > div {
                    padding: 0.3ch;
                    text-align: center;

                    &.calendar-grid-header {
                        background-image: linear-gradient(
                                to bottom,
                                var(--smart-base-background-color-dark) 0,
                                var(--smart-base-400) 100%
                        );
                    }

                    &.calendar-grid-cell {
                        display: grid;
                        grid-row: span 4;
                        grid-template-rows: subgrid;
                        padding-top: 0.3em;

                        padding-bottom: 0.3em;
                        --hijri-color: #00a000;

                        &:has(>div.calendar-grid-cell-fasting, >div.calendar-grid-cell-fasting-undefined) {
                            background-image: linear-gradient(
                                    to bottom,
                                    transparent 0,
                                    color-mix(in srgb, var(--hijri-color) 15%, transparent) 80%,
                                    color-mix(in srgb, var(--hijri-color) 30%, transparent) 100%
                            );
                        }

                        > div {
                            padding: 0;

                            &.calendar-grid-cell-hijri-month,
                            &.calendar-grid-cell-fasting,
                            &.calendar-grid-cell-fasting-undefined {
                                font-size: min(0.833rem, 2.4vw);
                                overflow: visible;
                            }


                            &.calendar-grid-cell-hijri-month {
                                color: var(--hijri-color);
                                overflow-wrap: break-word;
                                align-self: center;
                            }

                            &.calendar-grid-cell-hijri-date {
                                color: var(--hijri-color);
                            }

                            &.calendar-grid-cell-fasting, &.calendar-grid-cell-fasting-undefined {
                                color: var(--smart-primary-color) !important;
                            }

                            &.calendar-grid-cell-fasting {
                                font-variant-numeric: tabular-nums;
                            }

                            &.calendar-grid-cell-fasting-undefined {
                                > img.calendar-grid-cell-fasting-undefined-spinner {
                                    width: auto;
                                    height: 1lh;
                                    vertical-align: middle;
                                }
                            }
                        }
                    }

                    &.calendar-grid-cell-today {
                        position: relative;
                        isolation: isolate;

                        ::before {
                            content: "";
                            position: absolute;
                            inset: 0;
                            z-index: -1;
                            pointer-events: none;
                            background-image: linear-gradient(
                                    to top,
                                    transparent 0,
                                    color-mix(in srgb, var(--smart-base-200) 15%, transparent) 80%,
                                    color-mix(in srgb, var(--smart-base-200) 30%, transparent) 100%
                            );
                        }

                        :first-child, > div.calendar-grid-cell-hijri-date {
                            font-weight: 1000;
                        }
                    }

                    &.calendar-grid-cell-adjacent-month {
                        color: var(--smart-base-100)
                    }
                }
            }

            section#settings {
                display: grid;
                grid-template-columns: min-content auto;
                grid-column-gap: 0.5em;
                font-size: var(--smart-font-size);
                margin-top: 3em;
                max-width: 50em;

                > div {
                    display: grid;
                    grid-column: 1 / -1;
                    grid-template-columns: subgrid;
                    align-items: center;
                    margin-top: 2em;

                    &:first-child {
                        margin-top: 0;
                    }

                    transition: height 1s ease, margin-top 1s ease, display 1s allow-discrete;
                    @starting-style {
                        height: 0 !important;
                        margin-top: 0 !important;
                    }

                    &.settings-location-hidden-dependencies {
                        overflow: hidden;
                        height: 0 !important;
                        margin-top: 0 !important;
                        display: none;
                    }

                    > div:first-child {
                        color: var(--smart-base-100);
                    }

                    > div {
                        display: flex;
                        flex-direction: row;
                        flex-wrap: wrap;
                        align-items: center;
                        row-gap: 0;
                        column-gap: 1ch;
                    }

                    > div:nth-child(2) {
                        smart-drop-down-list#change-prayer-time-method-dropdown {
                            div.smart-drop-down-container {
                                width: min(100%, 50ch) !important;
                            }
                        }
                    }
                }
            }

            section#ics {
                text-align: center;

                span#ics-actions {
                    font-size: 1rem;

                    a#ics-link {
                        margin-left: 1ch;
                        margin-right: 1ch;
                    }

                    #ics-copy-status {
                        display: inline-block;
                        margin-left: 1ch;
                        color: #00b300;
                        opacity: 0;
                        transition: opacity 300ms ease;

                        &.visible {
                            opacity: 1;
                        }
                    }
                }
            }

            section#fasting-reasons,
            section#prohibiting-fasting-reasons {
                font-size: 0.833rem;
                width: 100%;

                > section > h4 {
                    margin-top: 3ch;
                    margin-bottom: 0;
                }
            }
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

                        &:hover {
                            color: #ffffff !important;
                        }

                        &:visited {
                            color: var(--smart-base-50);
                        }
                    }
                }
            }
        }

        span.smart-list-items-group-label {
            font-size: 0.833rem !important;
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
            font-size: 0.833rem;
        }

        span.missed-permission {
            color: var(--smart-base-100);
        }
    </style>
</head>
<body theme='dark'>
<header>
    <h1>
        <img alt='Khatim icon' style='height: 2ch; vertical-align: center' src='/img/logo.svg'>
        <span>${i18n.fasting().head().title()}</span>
    </h1>
    @template.calendar.fastingReasons(
    reasonGroup = i18n.fasting().reasonGroups().generic(),
    hadithCollectionReferencing = i18n.hadithReferencing().collections())
</header>
<main>
    <section id='months'>
        <smart-button id='prev-month-button'><span></span>◂◂</smart-button>
        <span id='current-month-label'></span>
        <smart-button id='next-month-button'>▸▸<span></span></smart-button>
    </section>
    <section id='calendar-grid-cells'></section>
    <section id='settings'>
        <div>
            <div class='settings-label'>${i18n.settings().location().label()}:</div>
            <div id='location-value'>
                <img src='/img/spinner-smart-base-800.svg' height='2em'/>
                <smart-button id='pick-location-on-map-button'>${i18n.settings().location().pickOnMap()}</smart-button>
            </div>
        </div>
        <div class='settings-location-hidden-dependencies'>
            <div class='settings-label'>${i18n.settings().timeZone()}:</div>
            <div>
                <smart-drop-down-list id='change-tz-dropdown'></smart-drop-down-list>
            </div>
        </div>
        <div class='settings-location-hidden-dependencies'>
            <div class='settings-label'>${i18n.settings().prayerTimeMethod()}:</div>
            <div class='settings-prayer-time-method'>
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
                <span id='settings-prayer-time-method-label'></span>
            </div>
        </div>
    </section>
    <section id='ics'>
        <h2>${i18n.fasting().ics().label()}</h2>
        <span id='ics-actions'>
            <a id='ics-link' target='_blank' rel="noopener noreferrer">${i18n.fasting().ics().linkLabel()}</a>
            ${i18n.commons().or()}
            <smart-button type='button' id='ics-copy-link'>${i18n.fasting().ics().copyLinkLabel()}</smart-button>
            <span id='ics-copy-status' role='status' aria-live='polite'>✓</span>
        </span>
    </section>
    <section>
        <h2>${i18n.fasting().reasonsTitle()}</h2>
        <section id='fasting-reasons'>
            @template.calendar.fastingReasons(
            reasonGroup = i18n.fasting().reasonGroups().obligatory(),
            hadithCollectionReferencing = i18n.hadithReferencing().collections())
            @template.calendar.fastingReasons(
            reasonGroup = i18n.fasting().reasonGroups().voluntary(),
            hadithCollectionReferencing = i18n.hadithReferencing().collections())
        </section>
        <section id='prohibiting-fasting-reasons'>
            @template.calendar.fastingReasons(
            reasonGroup = i18n.fasting().reasonGroups().prohibiting(),
            hadithCollectionReferencing = i18n.hadithReferencing().collections())
        </section>
    </section>
</main>
<hr/>
<footer>
    <ul>
        <li>
            ${i18n.footer().contacts().label()}
            :
            <a
                    href='https://github.com/edwgiz/tayyib-fasting-calendar'
                    target='_blank'
                    rel='noopener noreferrer'
            >${i18n.footer().contacts().githubRefDescription()}</a>
        </li>
        <li>
            ${i18n.footer().attributions().label()}
            :
            <a href='https://iconscout.com/contributors/rizsign-icon' target='_blank'>Rizsign icon</a>
        </li>
    </ul>
</footer>

<smart-window
        id='pick-location-on-map-window'
        header-buttons='["close"]'
        modal
></smart-window>

<div id='page-loading-spinner' class='hidden'></div>

<script type='module'>
    /*
     * App structure
     */
    const app = {
        model: {},
        adapter: {
            dom: {
                commons: {
                    smartComponents: {}
                },
                calendar: {
                    monthFolding: {}
                },
                ics: {},
                settings: {
                    location: {},
                    prayerTimeMethod: {}
                },
            },
            repository: {}
        },
        usecase: {
            calendar: {
                monthFolding: {}
            },
            location: {},
            time: {},
            settings: {
                location: {},
                prayerTimeMethod: {},
            }
        }
    };


    /*
     * Model
     */
    /**
     * @typedef {'CUSTOM' | 'DEVICE'} app.model.GeoPositionSource
     */
    app.model.GeoPosition = class {
        /**
         * @param {Number} latitude
         * @param {Number} longitude
         */
        constructor(latitude, longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }
    }

    /**
     * Classes for utils, usecases and adapters
     */
    const classes = {
        adapter: {
            dom: {
                settings: {
                    LocationDependencies: class {
                        #className = 'settings-location-hidden-dependencies';
                        #settingsLocationDependantEls = Array.from(
                            document.getElementsByClassName(this.#className)
                        );

                        /**
                         * @param {Boolean} show
                         */
                        show(show = true) {
                            for (const el of this.#settingsLocationDependantEls) {
                                if (show) {
                                    el.classList.remove(this.#className);
                                } else {
                                    el.classList.add(this.#className);
                                }
                            }
                        }
                    }
                },
            },
            L10n: class {
                /** @type {String} */
                defaultLocale;
                /** @type {String} */
                defaultTimezone;

                constructor() {
                    const dateTimeFormatOptions = new Intl.DateTimeFormat().resolvedOptions();
                    this.defaultLocale = dateTimeFormatOptions.locale;
                    this.defaultTimezone = dateTimeFormatOptions.timeZone;
                }
            },
            repository: {
                Month: class extends Date {
                    constructor() {
                        super();
                        this.setDate(2);
                    }
                }
            }
        },
        usecase: {
            location: {
                GetPositionUtils: class {
                    /**
                     * @param {app.model.GeoPosition|null} o1
                     * @param {app.model.GeoPosition|null} o2
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
            }
        }
    };

    /*
     * Adapters
     */

    /**
     * @param {String} query
     * @param {String} requestId
     * @param {object} variables
     */
    app.adapter.graphql = function (query, requestId, variables = {}) {
        return fetch('/graphql', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'X-Request-ID': requestId
            },
            body: JSON.stringify({
                query,
                variables
            })
        });
    };

    /**
     * Localization
     */
    app.adapter.l10n = new classes.adapter.L10n();

    app.adapter.repository.timezone = {
        getOrDefault(defaultValue) {
            const value = localStorage.getItem('timezone');
            return value ? value : defaultValue;
        },
        set(str) {
            localStorage.setItem('timezone', str);
        },
    };

    app.adapter.repository.prayerTimeMethod = {
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

    app.adapter.repository.geoPosition = {
        /**
         * @param {app.model.GeoPositionSource} source
         * @return {app.model.GeoPosition|null}
         */
        get(source) {
            const str = localStorage.getItem('geoPosition' + source);
            return JSON.parse(str);
        },

        /**
         * @param {app.model.GeoPosition} model
         * @param {app.model.GeoPositionSource} source
         */
        set(model, source) {
            const str = JSON.stringify(model);
            localStorage.setItem('geoPosition' + source, str);
        }
    };

    /**
     * @type {Date}
     */
    app.adapter.repository.Month = new classes.adapter.repository.Month();


    const d = document;

    app.adapter.dom.pageLoadingSpinner = {
        el: d.getElementById('page-loading-spinner'),
        timeoutId: null,

        hideTransitionHandler() {
            this.el.classList.add('hidden');
            this.timeoutId = null;
        },

        hide() {
            const el = this.el;
            el.classList.remove('shown');
            this.timeoutId = setTimeout(_ => {this.hideTransitionHandler()}, 500);
        },

        show() {
            if (this.timeoutId != null) {
                clearTimeout(this.timeoutId);
                this.timeoutId = null;
            }
            const el = this.el;
            el.classList.remove('hidden');
            requestAnimationFrame(_ => el.classList.add('shown'));
        },
    }

    /**
     * @typedef {HTMLElement & {
     *   selectedValues: string[],
     *   getItem: (value: string) => HTMLElement,
     *   ensureVisible: (item: HTMLElement) => void
     * }} SmartDropdownElement
     */

    app.adapter.dom.commons.smartComponents.dropdownUtils = {
        /**
         * @param {SmartDropdownElement} el
         */
        ensureSelectedItemVisible(el) {
            el.addEventListener('open', _ => {
                const selectedValue = el.selectedValues[0];
                const selectedItem = el.getItem(selectedValue);
                el.ensureVisible(selectedItem);
            }, {once: true});
        }
    }


    app.adapter.dom.settings.prayerTimeMethod.dropdown = {
        /** @type {SmartDropdownElement} */
        el: d.getElementById('change-prayer-time-method-dropdown'),

        /**
         * @param {string|null} key
         * @return {string[]}
         */
        getLabel(key) {
            const item = this.el.items.find(item => item.value === key);
            if (item) {
                return [item.label];
            } else {
                return [];
            }
        },

        /**
         * @param {String} oldVal
         * @param {EventListener} onChange
         */
        init(oldVal, onChange) {
            const el = this.el;
            el.selectedValues = [oldVal];
            el.addEventListener('change', onChange);
            app.adapter.dom.commons.smartComponents.dropdownUtils.ensureSelectedItemVisible(el);
        }
    }

    app.adapter.dom.settings.prayerTimeMethod.label = {
        /** @type {HTMLElement} */
        labelEl: document.getElementById('settings-prayer-time-method-label'),

        /**
         * @param {string[]} label
         */
        show(label) {
            this.labelEl.replaceChildren(label);
            this.labelEl.style.removeProperty('display');
        },

        hide() {
            this.labelEl.style.display = 'none';
        }
    }

    app.adapter.dom.calendarEventReasons = {
        /**
         * @return {Map<String, String>} map  of reason name by id
         */
        getAll() {
            const result = new Map();
            const nodeList = d.getElementsByClassName('fasting-reason');
            for (let i = 0; i < nodeList.length; i++) {
                const node = nodeList.item(i);
                if (node instanceof HTMLElement && node.id) {
                    result.set(node.id, node.firstElementChild.textContent);
                }
            }
            return result;
        }
    }

    app.adapter.dom.calendar.monthFolding = {
        /** @type HTMLElement */
        prevMonthEl: d.getElementById('prev-month-button'),
        /** @type HTMLElement */
        prevMonthLabelEl: d.getElementById('prev-month-button').querySelector('span'),
        /** @type HTMLElement */
        currentMonthLabelEl: d.getElementById('current-month-label'),
        /** @type HTMLElement */
        nextMonthEl: d.getElementById('next-month-button'),
        /** @type HTMLElement */
        nextMonthLabelEl: d.getElementById('next-month-button').querySelector('span'),

        /**
         * @param month {Date}
         * @param format {(date: Date) => String}
         */
        updateMonthLabels(month, format) {
            const prevMonth = new Date(month);
            prevMonth.setUTCMonth(month.getUTCMonth() - 1);
            const nextMonth = new Date(month);
            nextMonth.setUTCMonth(month.getUTCMonth() + 1);
            this.prevMonthLabelEl.textContent = format.apply(prevMonth) + '\u202F';
            this.currentMonthLabelEl.textContent = format.apply(month);
            this.nextMonthLabelEl.textContent = '\u202F' + format.apply(nextMonth);
        }
    };

    app.adapter.dom.calendar.Grid = {
        /** @type HTMLElement */
        el: d.getElementById('calendar-grid-cells'),

        /**
         * @param {string[]} dayOfWeekNames
         */
        renderCalendarHeader(dayOfWeekNames) {
            this.el.replaceChildren();
            for (const dayOfWeekName of dayOfWeekNames) {
                const cellEl = d.createElement('div');
                cellEl.classList.add('calendar-grid-header');
                cellEl.textContent = dayOfWeekName;
                this.el.appendChild(cellEl);
            }
        },

        getFastingEvents(dto, day) {
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
        },

        /**
         * @param minutes {Number}
         * @param defaultResult {String}
         * @return {String}
         */
        formatMinutes(minutes, defaultResult) {
            if (!minutes) {
                return defaultResult;
            }
            if (0 <= minutes && minutes <= 1440) {
                const minutesOnly = minutes % 60;
                return ((minutes - minutesOnly) / 60).toString().padStart(2, '0')
                    + ':'
                    + minutesOnly.toString().padStart(2, '0');
            } else {
                return '';
            }
        },

        /**
         * @param {Object} dto
         * @param {Boolean} hasLocation
         * @param {Map<String, String>} calendarEventReasons
         */
        renderDates(dto, hasLocation, calendarEventReasons) {
            const gridEl = this.el;
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
                {
                    const gregorianDateEl = d.createElement('div');
                    gregorianDateEl.textContent = day.gregorian.dayOfMonth;
                    cellEl.appendChild(gregorianDateEl);
                }
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
                const fastingEvents = this.getFastingEvents(dto, day);
                for (const fastingEvent of fastingEvents) {
                    const fastingEl = d.createElement('div');
                    if (fastingEvent.startMinute || fastingEvent.endMinute) {
                        fastingEl.classList.add('calendar-grid-cell-fasting');
                        fastingEl.textContent = this.formatMinutes(fastingEvent.startMinute, '${i18n.calendar().event().fajr()}')
                            + '…'
                            + this.formatMinutes(fastingEvent.endMinute, '${i18n.calendar().event().sunset()}');
                    } else {
                        fastingEl.classList.add('calendar-grid-cell-fasting-undefined')
                        fastingEl.appendChild(d.createTextNode('${i18n.calendar().event().fajr()}'));
                        if (!hasLocation) {
                            fastingEl.appendChild(d.createTextNode('…'));
                        } else {
                            const spinnerEl = d.createElement('img');
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
                                    : calendarEventReasons.get(reasonView.id);
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
    }

    app.adapter.dom.settings.location.ValueSection = {
        /** @type HTMLElement */
        el: d.getElementById('location-value'),

        addSpinner() {
            const pickLocationButtonEl = this.el.lastElementChild;
            /** @type HTMLImageElement */
            const spinner = d.createElement('img');
            spinner.style.height = '2em';
            spinner.src = '/img/spinner-smart-base-800.svg';
            this.el.replaceChildren(spinner, pickLocationButtonEl);
        },


        /**
         * @param {Boolean} denied
         * @param {EventListener} updateLocationByDeviceCallback
         */
        update(denied, updateLocationByDeviceCallback) {
            const pickLocationButtonEl = this.el.lastElementChild;
            const orI18n = ' ${i18n.commons().or()} ';
            const revokeBlockingI18n = ' ${i18n.settings().location().permission().revokeBlocking()} ';
            const missingPermissionDescriptionEl = d.createElement('span');
            missingPermissionDescriptionEl.classList.add('missed-permission');
            missingPermissionDescriptionEl.textContent = '${i18n.settings().location().permission().grantReason()}';
            /** @typedef HTMLGeolocationElement */
            if (typeof HTMLGeolocationElement === 'function') {
                this.el.replaceChildren(missingPermissionDescriptionEl, ' ', d.createElement('geolocation'), orI18n, pickLocationButtonEl);
            } else {
                if (denied) {
                    missingPermissionDescriptionEl.appendChild(d.createTextNode(', ' + revokeBlockingI18n + orI18n, pickLocationButtonEl));
                    this.el.replaceChildren(missingPermissionDescriptionEl);
                } else {
                    const geolocationEl = d.createElement('smart-button');
                    geolocationEl.id = 'grant-geolocation-permission';
                    geolocationEl.textContent = '${i18n.settings().location().permission().changeInBrowser()}';
                    geolocationEl.addEventListener('click', updateLocationByDeviceCallback);
                    this.el.replaceChildren(missingPermissionDescriptionEl, ' ', geolocationEl, orI18n, pickLocationButtonEl);
                }
            }
        },

        /**
         * @param {String} str
         */
        setTextContent(str) {
            const pickLocationButtonEl = this.el.lastElementChild;
            this.el.replaceChildren(d.createTextNode(str), pickLocationButtonEl)
        }
    }

    app.adapter.dom.settings.locationDependencies = new classes.adapter.dom.settings.LocationDependencies();


    /*
     * Usecases
     */
    app.usecase.getTimezone = {
        apply() {
            return app.adapter.repository.timezone.getOrDefault(app.adapter.l10n.defaultTimezone);
        }
    }

    app.usecase.settings.prayerTimeMethod.UpdateLabel = {
        /**
         * @param {string|null} usedPrayerTimeMethod
         */
        apply(usedPrayerTimeMethod) {
            const chosenPrayerTimeMethod = app.adapter.repository.prayerTimeMethod.get();
            if (usedPrayerTimeMethod !== chosenPrayerTimeMethod) {
                const label = app.adapter.dom.settings.prayerTimeMethod.dropdown.getLabel(usedPrayerTimeMethod);
                app.adapter.dom.settings.prayerTimeMethod.label.show(label);
            } else {
                app.adapter.dom.settings.prayerTimeMethod.label.hide();
            }
        },
    }


    app.usecase.location.GetGeoPosition = {
        /**
         * @return {app.model.GeoPosition|null}
         */
        apply() {
            const custom = app.adapter.repository.geoPosition.get('CUSTOM')
            if (custom != null) {
                return custom;
            }
            const device = app.adapter.repository.geoPosition.get('DEVICE')
            if (device != null) {
                return device;
            }
            return null;
        }
    };

    app.usecase.location.SetGeoPosition = {
        /**
         * @param {Number|null} latitude
         * @param {Number|null} longitude
         * @param {app.model.GeoPositionSource} source
         */
        apply(latitude, longitude, source) {
            const clean = latitude === null || longitude === null;
            const oldModel = app.adapter.repository.geoPosition.get(source);
            const newModel = clean ? null : new app.model.GeoPosition(latitude, longitude);
            if (!classes.usecase.location.GetPositionUtils.equals(oldModel, newModel)) {
                app.adapter.repository.geoPosition.set(newModel, source);
                const actualModel = app.usecase.location.GetGeoPosition.apply();
                if (!classes.usecase.location.GetPositionUtils.equals(oldModel, actualModel)) {
                    reloadCalendar();
                }
            }
        }
    };

    app.usecase.formatMonthLabel = {
        /** @type {Intl.DateTimeFormat|null} */
        datetimeFormat: null,

        init() {
            const tzValue = app.usecase.getTimezone.apply();
            this.datetimeFormat = new Intl.DateTimeFormat(app.adapter.l10n.defaultLocale, {
                timeZone: tzValue,
                month: 'long',
                year: 'numeric'
            })
        },

        /**
         * @param {Date} date
         * @return {String}
         */
        apply(date) {
            if (this.datetimeFormat == null) {
                this.init()
            }
            return this.datetimeFormat.format(date);
        }
    };

    app.usecase.settings.location.UpdateByPermissionState = {
        /**
         * @param {'granted'| 'denied'|'prompt'} permissionState
         */
        apply(permissionState) {
            if (permissionState === 'granted') {
                app.adapter.dom.settings.location.ValueSection.addSpinner();
            } else {
                app.adapter.dom.settings.location.ValueSection.update(
                    permissionState === 'denied',
                    app.usecase.settings.location.updateLocationByDevice);
            }
        },
    }

    app.usecase.settings.updateLocationDependencies = {
        wgs84Formatter: new Intl.NumberFormat(undefined, {
            minimumFractionDigits: 0,
            maximumFractionDigits: 5,
        }),


        /**
         * @param {Number} latitude
         * @param {Number} longitude
         * @return {String}
         */
        formatWgs84(latitude, longitude) {
            return this.wgs84Formatter.format(latitude) + ',' + this.wgs84Formatter.format(longitude);
        },

        /**
         * @param {Number|null} latitude
         * @param {Number|null} longitude
         * @param {string|null} displayName
         */
        apply(latitude, longitude, displayName) {
            const hasLocation = latitude != null;
            if (hasLocation) {
                app.adapter.dom.settings.location.ValueSection.setTextContent(
                    displayName != null && displayName.trim().length > 0 ? displayName : this.formatWgs84(latitude, longitude)
                )
            } else {
                navigator.permissions.query({name: 'geolocation'}).then(p => app.usecase.settings.location.UpdateByPermissionState.apply(p));
            }
            app.adapter.dom.settings.locationDependencies.show(hasLocation);
        }
    }

    app.adapter.dom.ics.link = {
        icsLinkEl: d.getElementById('ics-link'),

        acceptLanguage: navigator.languages
            .map((language, index) => {
                if (index === 0) {
                    return language;
                }

                const q = Math.max(0.1, 1 - index * 0.1).toFixed(1);
                return language + ';q=' + q;
            })
            .join(','),

        /**
         * @param {Date} start
         * @param {String} timezone
         * @param {String} hijriMethod
         * @param {null|{latitude:Number, longitude:Number, prayerTimeMethod:String}} locationInput
         */
        setHref(start, timezone, hijriMethod, locationInput) {
            const url = new URL('/calendar/fasting.ics', window.location.origin);
            const q = url.searchParams;
            q.set('start', start.toISOString().substring(0, 10));
            q.set('timezone', timezone);
            q.set('hijri-method', hijriMethod);
            q.set('locale', this.acceptLanguage);
            if (locationInput) {
                q.set('latitude', locationInput.latitude.toString());
                q.set('longitude', locationInput.longitude.toString());
                q.set('prayer-time-method', locationInput.prayerTimeMethod);
            }
            this.icsLinkEl.href = url.href;
        },

        /**
         * @return {String}
         */
        getHref() {
            return this.icsLinkEl.href;
        }
    }

    {
        let icsCopyStatusTimeout = null;
        d.getElementById('ics-copy-link')?.addEventListener('click', async () => {
            try {
                await navigator.clipboard.writeText(app.adapter.dom.ics.link.getHref());
                clearTimeout(icsCopyStatusTimeout);
                const icsCopyStatusEl = document.getElementById('ics-copy-status');
                icsCopyStatusEl.setAttribute('aria-hidden', 'false');
                icsCopyStatusEl.classList.add('visible');
                icsCopyStatusTimeout = setTimeout(() => {
                    icsCopyStatusEl.classList.remove('visible');
                    setTimeout(() => {
                        if (!icsCopyStatusEl.classList.contains('visible')) {
                            icsCopyStatusEl.setAttribute('aria-hidden', 'true');
                        }
                    }, 300);
                }, 2000);
            } catch (error) {
                console.error('Failed to copy ICS link:', error);
            }
        });
    }

    let reloadCalendarDatesRequestId = null;
    const reasonNameById = app.adapter.dom.calendarEventReasons.getAll();

    function reloadCalendar() {
        reloadCalendarDatesRequestId = crypto.randomUUID();

        app.adapter.dom.pageLoadingSpinner.show()

        const today = new Date();
        const month = app.adapter.repository.Month;
        const isTodayMonth = month.getFullYear() === today.getFullYear() && month.getMonth() === today.getMonth();

        /**
         * @return {null|{latitude:Number, longitude:Number, prayerTimeMethod:String}}
         */
        function fastingTimeCalculationInput() {
            const getGeoPosition = app.usecase.location.GetGeoPosition.apply();
            return !getGeoPosition ? null : {
                latitude: getGeoPosition.latitude,
                longitude: getGeoPosition.longitude,
                prayerTimeMethod: app.adapter.repository.prayerTimeMethod.get() ?? 'AUTO_BY_LOCATION'
            };
        }

        const args = {
            input: {
                millis: isTodayMonth ? today.getTime() : month.getTime(),
                timezone: app.adapter.repository.timezone.getOrDefault(Intl.DateTimeFormat().resolvedOptions().timeZone),
                hijriMethod: '${HijriMethod.UAQ}',
                fastingTimeCalculationInput: fastingTimeCalculationInput()
            }
        };

        app.adapter.graphql(`query
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
                    app.adapter.dom.pageLoadingSpinner.hide();
                    app.adapter.dom.calendar.monthFolding.updateMonthLabels(
                        app.adapter.repository.Month,
                        app.usecase.formatMonthLabel
                    );
                }
                return httpResp.json();
            })
            .then(jsonResp => {
                const dto = jsonResp.data.getCalendarCells;
                if (!isTodayMonth) {
                    dto.todayIndex = -1;
                }
                const pageScrollTop = window.scrollY;
                app.adapter.dom.calendar.Grid.renderCalendarHeader(dto.dayOfWeekNames);
                const locationInput = args.input.fastingTimeCalculationInput;
                const hasLocation = locationInput != null;
                app.adapter.dom.calendar.Grid.renderDates(dto, hasLocation, reasonNameById);
                app.usecase.settings.updateLocationDependencies.apply(
                    locationInput?.latitude,
                    locationInput?.longitude,
                    dto.locationDisplayName);
                app.usecase.settings.prayerTimeMethod.UpdateLabel.apply(dto.prayerTimeMethod);
                app.adapter.dom.ics.link.setHref(today, args.input.timezone, args.input.hijriMethod, hasLocation ? {
                    latitude: locationInput.latitude,
                    longitude: locationInput.longitude,
                    prayerTimeMethod: dto.prayerTimeMethod
                } : null);
                window.scrollTo({top: pageScrollTop, behavior: 'smooth'});
            });
    }

    app.usecase.calendar.monthFolding.decrementMonth = () => {
        const repository = app.adapter.repository.Month;
        repository.setUTCMonth(repository.getUTCMonth() - 1);
        reloadCalendar();
    };

    app.usecase.calendar.monthFolding.incrementMonth = () => {
        const repository = app.adapter.repository.Month;
        repository.setUTCMonth(repository.getUTCMonth() + 1);
        reloadCalendar();
    };

    app.usecase.settings.location.updateLocationByDevice = () => {
        navigator.geolocation.getCurrentPosition(
            position => {
                app.usecase.location.SetGeoPosition.apply(position.coords.latitude, position.coords.longitude, 'DEVICE');
            },
            error => {
                if (error.code === error.PERMISSION_DENIED) {
                    app.usecase.location.SetGeoPosition.apply(null, null, 'DEVICE');
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

    {// init calendar month folding
        app.adapter.dom.calendar.monthFolding.prevMonthEl.addEventListener('click', app.usecase.calendar.monthFolding.decrementMonth)
        app.adapter.dom.calendar.monthFolding.nextMonthEl.addEventListener('click', app.usecase.calendar.monthFolding.incrementMonth);
    }
    {// init prayer-time-method dropdown
        let oldVal = app.adapter.repository.prayerTimeMethod.get();
        if (!oldVal) {
            oldVal = '${PrayerTimeMethod.AUTO_BY_LOCATION}';
            app.adapter.repository.prayerTimeMethod.set(oldVal)
        }
        const onChange = e => {
            app.adapter.repository.prayerTimeMethod.set(e.target.selectedValues[0])
            reloadCalendar();
        };
        app.adapter.dom.settings.prayerTimeMethod.dropdown.init(oldVal, onChange);
    }
    // init calendar location
    navigator.permissions.query({
        name: 'geolocation'
    }).then(permission => {
        permission.addEventListener('change', _ => {
            app.usecase.settings.location.UpdateByPermissionState.apply(permission.state);
            if (permission.state !== 'granted') {
                app.usecase.location.SetGeoPosition.apply(null, null, 'DEVICE');
            } else {
                app.usecase.settings.location.updateLocationByDevice();
            }
        });
    });


    requestIdleCallback(() => {
        reloadCalendar();
    });


    let changeLocationWindowEl = null;
    let changeLocationMarker = null;
    let changeLocationMapView = null;

    d.getElementById('pick-location-on-map-button').addEventListener('click', async _ => {
        let geoPosition = app.usecase.location.GetGeoPosition.apply();
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
                    app.usecase.location.SetGeoPosition.apply(markerPosition.lat, markerPosition.lng, 'CUSTOM');
                    changeLocationWindowEl.close();
                });
                smartButtonsEl.prepend(saveButtonEl);
            }
            {
                const defaultButtonEl = document.createElement('smart-button');
                defaultButtonEl.id = 'pick-location-on-map-default-button';
                defaultButtonEl.textContent = '${i18n.settings().byDevice()}';
                defaultButtonEl.addEventListener('click', _ => {
                    app.usecase.location.SetGeoPosition.apply(null, null, 'CUSTOM');
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
        const selectedValue = app.usecase.getTimezone.apply();
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
            app.adapter.repository.timezone.set(event.detail.value);
            app.usecase.formatMonthLabel.init();
            reloadCalendar();
        });
        app.adapter.dom.commons.smartComponents.dropdownUtils.ensureSelectedItemVisible(changeTzDropdown);
        changeTzDropdown.disabled = false;
    }
</script>
</body>
</html>
