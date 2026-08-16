@import com.github.edwgiz.tayyib.adapter.commons.dgs.dto.HijriMethod
@import com.github.edwgiz.tayyib.adapter.commons.dgs.dto.PrayerTimeMethod
@import com.github.edwgiz.tayyib.domain.model.I18n

@param I18n i18n

<!DOCTYPE html>
<html lang='en' data-fa-kit-code="tayyiba" class="wa-theme-default wa-palette-default wa-dark wa-brand-green">
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
  <link rel="stylesheet" href="https://ka-f.webawesome.com/webawesome@3.11.0/styles/themes/default.css"/>
  <link rel="stylesheet" href="https://ka-f.webawesome.com/webawesome@3.11.0/styles/color/palettes/default.css"/>
  <link rel="stylesheet" href="https://ka-f.webawesome.com/webawesome@3.11.0/styles/color/variants/brand.css"/>
  <script type='module'>
    import 'https://unpkg.com/smart-webcomponents@26.0.0/source/modules/smart.dropdownlist.js';
    import 'https://ka-f.webawesome.com/webawesome@3.11.0/components/button/button.js';
    import 'https://ka-f.webawesome.com/webawesome@3.11.0/components/spinner/spinner.js';
    import 'https://ka-f.webawesome.com/webawesome@3.11.0/components/tooltip/tooltip.js';
  </script>

  <title>${i18n.fasting().head().title()}</title>

  <style>
    :root {
      interpolate-size: allow-keywords;

      --wa-font-weight-light: 200;
      --wa-font-weight-normal: 300;
      --wa-font-weight-semibold: 400;
      --wa-font-weight-bold: 500;

      --wa-font-family-body: sans-serif;
      --wa-font-family-longform: var(--wa-font-family-body);
      --wa-color-text-link: var(--wa-color-text-quiet);

      --wa-form-control-padding-block: 0.4lh;

      --wa-color-brand-border-quiet: var(--wa-color-brand-30);
      --wa-color-brand-border-normal: var(--wa-color-brand-60);
      --wa-color-brand-border-loud: var(--wa-color-brand-80);
      --wa-color-surface-hover: var(--wa-color-brand-30);
    }


    @keyframes page-loading-spinner-show {
      from {
        opacity: 0;
      }

      to {
        opacity: 1;
      }
    }

    @keyframes form-control-hover-animation {
      from {
        background-color: var(--wa-color-surface-hover);
      }

      to {
        background-color: transparent;
      }
    }

    html {
      scrollbar-gutter: stable both-edges;
    }

    body {
      margin: 0;
    }

    wa-button::part(button) {
      color: var(--wa-color-text-normal);
      border-color: var(--wa-color-border-loud);
      background: none;
      padding: 0 1.5ch 0 1.5ch;
    }

    wa-button::part(button):hover, wa-button::part(button):focus, a:hover, a:focus {
      animation: form-control-hover-animation 1s ease;
      @starting-style {
        background-color: var(--wa-color-surface-hover);
      }
    }

    a {
      color: var(--wa-color-text-link);

      &:visited {
        color: color-mix(currentColor 33%, var(--wa-color-pink));
      }

      &:hover {
        color: color-mix(currentColor 33%, var(--wa-color-text-normal));
        background-color: var(--wa-color-surface-hover);

        &:visited {
          color: color-mix(var(--wa-color-text-normal), color-mix(currentColor 33%, var(--wa-color-pink)));
        }
      }

      &:focus-visible {
        outline: 2px solid var(--wa-color-brand-border-loud);
      }
    }

    h1, h2, h3, h4, h5 {
      color: var(--wa-color-text-normal) !important;
      margin-bottom: 0.2lh;
      padding-bottom: 0;
    }

    hr {
      color: var(--wa-color-neutral-border-normal);
      border-color: var(--wa-color-neutral-border-normal);
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
      color: var(--wa-color-text-quiet);
    }

    li > q {
      display: block;
      padding: 0;
      margin: 0;
      color: var(--wa-color-text-normal);
    }

    geolocation {
      text-align: center !important;
      vertical-align: middle !important;
      cursor: pointer !important;
      letter-spacing: inherit !important;
      overflow: hidden !important;
      border-color: var(--wa-color-neutral-border-loud) !important;
      border-radius: var(--wa-form-control-border-radius) !important;
      border-width: var(--wa-form-control-border-width) !important;
      border-style: var(--wa-form-control-border-style) !important;

      box-sizing: border-box !important;
      font-family: var(--wa-font-family-body), sans-serif !important;
      font-size: var(--wa-font-size-m) !important;
      font-weight: var(--wa-font-weight-normal) !important;
      -webkit-tap-highlight-color: transparent !important;
      background: none !important;
      color: var(--wa-color-text-normal) !important;
      height: var(--wa-form-control-height) !important;

      &:hover {
        background: var(--wa-color-surface-hover) !important;
      }
    }

    div#page-loading-spinner {
      position: fixed;
      inset: 0;
      z-index: 9999;
      backdrop-filter: blur(1px);
      -webkit-backdrop-filter: blur(1px);

      display: grid;
      place-items: center;

      wa-spinner {
        --size: min(20vw, 20vh);
        font-size: var(--size);
        --track-width: calc(1lh / 20);
        color: var(--wa-color-brand);
        --track-color: color-mix(var(--wa-color-brand) 20%, transparent);
        opacity: 0;
      }

      &.shown > wa-spinner {
        animation: page-loading-spinner-show 1s ease 1s forwards;
      }

      &.hidden {
        display: none;
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
        grid-column-gap: 2ch;

        margin-bottom: 1lh;

        #prev-month-label, #next-month-label {
          display: inline-block;
          transition: width 1s ease, opacity 1s ease, display 1s allow-discrete;
          @media (max-width: 32em) {
            width: 0 !important;
            opacity: 0;
            display: none;
          }
          @starting-style {
            width: 0 !important;
            opacity: 0;
          }
        }

        #prev-month-button {
          align-self: end;
        }

        #next-month-button {
          align-self: start;
        }

        > * {
          margin: 0;
          align-self: center;
        }

        #current-month-label {
          display: inline-block;
          white-space: nowrap;
        }
      }

      section#calendar-grid-cells {
        display: grid;
        grid-template-columns: repeat(7, min(calc(100vw / 7), 12ch));

        > div {
          padding: 0.3ch;
          text-align: center;

          &.calendar-grid-header {
            background-image: linear-gradient(
              to bottom,
              var(--wa-color-surface-default) 0,
              var(--wa-color-neutral-fill-normal) 100%
            );
          }

          &.calendar-grid-cell {
            display: grid;
            grid-row: span 4;
            grid-template-rows: subgrid;
            padding-top: 0.3em;

            padding-bottom: 0.3em;
            --hijri-color: var(--wa-color-brand);

            &:has(>div.calendar-grid-cell-fasting, >div.calendar-grid-cell-fasting-undefined) {
              background-image: linear-gradient(
                to bottom,
                transparent 0,
                var(--wa-color-brand-20) 80%,
                var(--wa-color-surface-hover) 100%
              ) !important;
            }

            > div {
              padding: 0;

              &.calendar-grid-cell-hijri-month,
              &.calendar-grid-cell-fasting,
              &.calendar-grid-cell-fasting-undefined {
                font-size: min(var(--wa-font-size-s), 2.7vw);
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
                color: var(--wa-color-text-normal);
                font-variant-numeric: tabular-nums;
              }
            }

            > wa-tooltip {
              --wa-tooltip-background-color: var(--wa-color-surface-hover);
              --wa-tooltip-border-color: var(--wa-color-brand-50);

              > ul {
                margin: 0;
                padding-left: 0;
                list-style-type: none;
                list-style-position: inside;

                > li {
                  margin: 0.4em;

                  > a {
                    &:hover {
                      color: #ffffff !important;
                    }

                    &:visited {
                      color: var(--wa-color-text-link);
                    }
                  }
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
                color-mix(var(--wa-color-neutral-fill-normal) 25%, transparent) 80%,
                var(--wa-color-neutral-fill-normal) 100%
              );
            }

            :first-child, > div.calendar-grid-cell-hijri-date {
              font-weight: 1000;
            }
          }

          &.calendar-grid-cell-adjacent-month {
            color: var(--wa-color-text-quiet)
          }
        }
      }

      section#settings {
        display: grid;
        grid-template-columns: min-content auto;
        grid-column-gap: 0.5em;
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
            color: var(--wa-color-text-quiet);
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

            span.missed-permission {
              color: var(--wa-color-text-quiet);
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

    span.smart-list-items-group-label {
      font-size: 0.833rem !important;
      text-align: right !important;
      display: inline-block;
      color: var(--wa-color-text-quiet) !important;
      margin: 0 !important;
      padding: 0 !important;
      cursor: default;
      height: min-content;
    }

    div#pick-location-on-map-view {
      position: fixed;
      inset: 0;
      z-index: 9999;
      padding: 0;
      margin: 0;
      border: none;
      background: var(--wa-color-surface-default) !important;
      display: none;
      width: 100%;
      height: 100%;
      opacity: 0;

      transition: opacity 1s ease, display 1s allow-discrete;

      &.shown {
        opacity: 1;
        display: block;
        @starting-style {
          opacity: 0;
        }
      }

      div#pick-location-on-map-controls {
        display: flex;
        justify-content: flex-end;
        align-items: center;
        font-size: var(--wa-font-size-m);
        gap: 2ch;

        wa-button {
          background-color: var(--wa-color-surface-default) !important;
        }
      }
    }

    .leaflet-container {
      width: 100%;
      height: 100%;
      max-width: 100%;
      max-height: 100%;
    }

    .leaflet-layer,
    .leaflet-control-zoom-in,
    .leaflet-control-zoom-out,
    .leaflet-control-attribution {
      filter: invert(100%) hue-rotate(180deg) brightness(95%) contrast(90%);
      background: var(--wa-color-surface-default);
    }

    footer {
      color: var(--wa-color-text-quiet);
      font-size: 0.833rem;
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
    <wa-button id='prev-month-button'><span id='prev-month-label'></span>◂◂</wa-button>
    <span id='current-month-label'>
            <img src='/img/spinner-smart-base-800.svg'/>
        </span>
    <wa-button id='next-month-button'>▸▸<span id='next-month-label' span></span></wa-button>
  </section>
  <section id='calendar-grid-cells'></section>
  <section id='settings'>
    <div>
      <div class='settings-label'>${i18n.settings().location().label()}:</div>
      <div id='location-value'>
        <img src='/img/spinner-smart-base-800.svg' height='2em'/>
        <wa-button id='pick-location-on-map-button'>${i18n.settings().location().pickOnMap()}</wa-button>
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
            <wa-button type='button' id='ics-copy-link'>${i18n.fasting().ics().copyLinkLabel()}</wa-button>
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

<div id='pick-location-on-map-view'></div>

<div id='page-loading-spinner' class='hidden'>
  <wa-spinner></wa-spinner>
</div>

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
  app.adapter.l10n = new classes.adapter.L10n();

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
   * @type {Date}
   */
  app.adapter.repository.Month = new classes.adapter.repository.Month();

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
      this.timeoutId = setTimeout(_ => {
        this.hideTransitionHandler()
      }, 500);
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


  app.adapter.dom.calendar.monthFolding = {
    /** @type HTMLElement */
    prevMonthEl: d.getElementById('prev-month-button'),
    /** @type HTMLElement */
    prevMonthLabelEl: d.getElementById('prev-month-label'),
    /** @type HTMLElement */
    currentMonthLabelEl: d.getElementById('current-month-label'),
    /** @type HTMLElement */
    nextMonthEl: d.getElementById('next-month-button'),
    /** @type HTMLElement */
    nextMonthLabelEl: d.getElementById('next-month-label'),

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

  app.adapter.dom.calendar.grid = {
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
              fastingEl.appendChild(d.createElement('wa-spinner'));
            }
            fastingEl.appendChild(d.createTextNode('${i18n.calendar().event().sunset()}'));
          }
          cellEl.appendChild(fastingEl);
          const tooltip = d.createElement('wa-tooltip');
          tooltip.for = cellElId;
          tooltip.placement = 'bottom';
          tooltip.distance = 0;
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

  app.adapter.dom.settings.timezoneDropdown = {
    /**
     * @param {String} selectedValue
     */
    init(selectedValue) {
      const dropdownEl = d.getElementById('change-tz-dropdown');
      dropdownEl.disabled = false;
      dropdownEl.selectionMode = 'one';
      dropdownEl.filterable = true;
      dropdownEl.filterMode = 'containsIgnoreCase';
      dropdownEl.horizontalScrollBarVisibility = 'hidden';
      dropdownEl.virtualization = true;
      dropdownEl.displayMember = 'l';
      dropdownEl.valueMember = 'v';
      dropdownEl.dataSource = [{l: selectedValue, v: selectedValue}];
      dropdownEl.selectedValues = [selectedValue];
      dropdownEl.addEventListener('open', () => {
        const selectedValues = dropdownEl.selectedValues;
        dropdownEl.dataSource = Intl.supportedValuesOf('timeZone').map(v => ({l: v, v: v}));
        dropdownEl.selectedValues = selectedValues;
        app.adapter.dom.commons.smartComponents.dropdownUtils.ensureSelectedItemVisible(dropdownEl);
        dropdownEl.addEventListener('change', (event) => {
          app.adapter.repository.timezone.set(event.detail.value);
          app.usecase.formatMonthLabel.init();
          app.usecase.calendar.reload.apply();
        });
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

  app.adapter.dom.ics.link = {
    /**
     * @type HTMLAnchorElement
     */
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

  app.adapter.dom.ics.copyLink = {
    init() {
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
  }

  app.adapter.dom.settings.location.ValueSection = {
    /** @type HTMLElement */
    el: d.getElementById('location-value'),

    addSpinner() {
      const pickLocationButtonEl = this.el.lastElementChild;
      /** @type HTMLImageElement */
      const spinner = d.createElement('wa-spinner');
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
          const geolocationEl = d.createElement('wa-button');
          geolocationEl.id = 'grant-geolocation-permission';
          geolocationEl.textContent = '${i18n.settings().location().permission().changeInBrowser()}';
          geolocationEl.addEventListener('click', updateLocationByDeviceCallback);
          this.el.replaceChildren(missingPermissionDescriptionEl, ' ', geolocationEl, orI18n, pickLocationButtonEl);
        }
      }
    },

    /**
     * @param {String} str
     * @param {() => {} | null} forgetCustomLocationCallback
     */
    setTextContent(str, forgetCustomLocationCallback) {
      const pickLocationButtonEl = this.el.lastElementChild;
      if (forgetCustomLocationCallback != null) {
        const forgetCustomLocationButtonEl = d.createElement('wa-button');
        forgetCustomLocationButtonEl.id = 'grant-geolocation-permission';
        forgetCustomLocationButtonEl.textContent = '${i18n.settings().location().forgetCustomLocation()}';
        forgetCustomLocationButtonEl.addEventListener('click', forgetCustomLocationCallback);
        this.el.replaceChildren(d.createTextNode(str), ' ', forgetCustomLocationButtonEl, '${i18n.commons().or()}', pickLocationButtonEl)
      } else {
        this.el.replaceChildren(d.createTextNode(str), pickLocationButtonEl)
      }
    }
  }

  app.adapter.dom.settings.location.changeLocationWindow = {
    /** @type HTMLElement */
    windowEl: null,
    leafletMarker: null,
    leafletMap: null,

    /**
     * @param {app.model.GeoPosition} geoPosition
     * @param {(latitude: Number|null, longitude: Number|null) => void} setGeoPosition
     */
    async init(geoPosition, setGeoPosition) {
      const windowEl = d.getElementById('pick-location-on-map-view');
      this.windowEl = windowEl;
      await import('https://unpkg.com/leaflet/dist/leaflet.js');
      this.leafletMap = L.map('pick-location-on-map-view').setView([geoPosition.latitude, geoPosition.longitude], 8);
      windowEl.classList.add('shown');
      L.tileLayer('https://tile.openstreetmap.org/{z}/{x}/{y}.png', {
        maxZoom: 19,
        attribution: '&copy; <a href="http://www.openstreetmap.org/copyright">OpenStreetMap</a>'
      }).addTo(this.leafletMap);
      const leafletMarker = L.marker([geoPosition.latitude, geoPosition.longitude], {
        draggable: true
      }).addTo(this.leafletMap);
      this.leafletMarker = leafletMarker;

      class CustomMapWindowControls extends L.Control {
        options = {
          position: 'topright'
        };

        onAdd(_) {
          const container = d.createElement('div');
          container.id = 'pick-location-on-map-controls';
          container.classList.add('leaflet-control');
          const okButton = d.createElement('wa-button');
          okButton.id = 'ok-pick-location-on-map'
          okButton.textContent = '${i18n.commons().ok()}';
          okButton.addEventListener('click', () => {
            const markerPosition = leafletMarker.getLatLng();
            setGeoPosition(markerPosition.lat, markerPosition.lng);
            windowEl.classList.remove('shown');
          });
          container.appendChild(okButton);
          const cancelButton = d.createElement('wa-button');
          cancelButton.id = 'cancel-pick-location-on-map'
          cancelButton.textContent = '${i18n.commons().cancel()}';
          cancelButton.addEventListener('click', () => {
            windowEl.classList.remove('shown');
          });
          container.appendChild(cancelButton);
          container.addEventListener('click', event => event.stopPropagation());
          container.addEventListener('wheel', event => event.stopPropagation());
          return container;
        }
      }

      new CustomMapWindowControls().addTo(this.leafletMap)
      this.leafletMap.invalidateSize({animate: false});
    },

    /**
     * @param {app.model.GeoPosition} geoPosition
     * @param {(latitude: Number|null, longitude: Number|null) => void} setGeoPosition
     */
    async open(geoPosition, setGeoPosition) {
      if (this.windowEl == null) {
        await this.init(geoPosition, setGeoPosition);
      } else {
        this.windowEl.classList.add('shown');
        this.leafletMarker.setLatLng([geoPosition.latitude, geoPosition.longitude]);
        this.leafletMap.panTo([geoPosition.latitude, geoPosition.longitude]);
      }
    }
  }

  app.adapter.dom.settings.locationDependencies = new classes.adapter.dom.settings.LocationDependencies();

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

  /*
   * Usecases
   */
  app.usecase.getTimezone = {
    /**
     * @return {string}
     */
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
      const newModel = clean ? null : new app.model.GeoPosition(
        Math.round(latitude * 1e5) / 1e5,
        Math.round(longitude * 1e5) / 1e5);
      if (!classes.usecase.location.GetPositionUtils.equals(oldModel, newModel)) {
        app.adapter.repository.geoPosition.set(newModel, source);
        const actualModel = app.usecase.location.GetGeoPosition.apply();
        if (!classes.usecase.location.GetPositionUtils.equals(oldModel, actualModel)) {
          app.usecase.calendar.reload.apply();
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
        const text = displayName != null && displayName.trim().length > 0 ? displayName : this.formatWgs84(latitude, longitude);
        const forgetCustomLocationCallback = app.adapter.repository.geoPosition.get('CUSTOM') == null ? null : () => {
          app.usecase.location.SetGeoPosition.apply(null, null, 'CUSTOM');
        };
        app.adapter.dom.settings.location.ValueSection.setTextContent(
          text,
          forgetCustomLocationCallback
        )
      } else {
        navigator.permissions.query({name: 'geolocation'}).then(p => app.usecase.settings.location.UpdateByPermissionState.apply(p));
      }
      app.adapter.dom.settings.locationDependencies.show(hasLocation);
    }
  }

  app.adapter.dom.ics.copyLink.init();


  app.usecase.calendar.reload = {
    reloadCalendarDatesRequestId: null,
    reasonNameById: app.adapter.dom.calendarEventReasons.getAll(),

    /**
     * @return {null|{latitude:Number, longitude:Number, prayerTimeMethod:String}}
     */
    fastingTimeCalculationInput() {
      const getGeoPosition = app.usecase.location.GetGeoPosition.apply();
      return !getGeoPosition ? null : {
        latitude: getGeoPosition.latitude,
        longitude: getGeoPosition.longitude,
        prayerTimeMethod: app.adapter.repository.prayerTimeMethod.get() ?? 'AUTO_BY_LOCATION'
      };
    },

    apply() {
      this.reloadCalendarDatesRequestId = crypto.randomUUID();

      app.adapter.dom.pageLoadingSpinner.show()

      const today = new Date();
      const month = app.adapter.repository.Month;
      const isTodayMonth = month.getFullYear() === today.getFullYear() && month.getMonth() === today.getMonth();

      const args = {
        input: {
          millis: isTodayMonth ? today.getTime() : month.getTime(),
          timezone: app.adapter.repository.timezone.getOrDefault(Intl.DateTimeFormat().resolvedOptions().timeZone),
          hijriMethod: '${HijriMethod.UAQ}',
          fastingTimeCalculationInput: this.fastingTimeCalculationInput()
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
      }`, this.reloadCalendarDatesRequestId, args)
        .then(httpResp => {
          const requestId = httpResp.headers.get('X-Request-ID');
          if (requestId === this.reloadCalendarDatesRequestId) {
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
          app.adapter.dom.calendar.grid.renderCalendarHeader(dto.dayOfWeekNames);
          const locationInput = args.input.fastingTimeCalculationInput;
          const hasLocation = locationInput != null;
          app.adapter.dom.calendar.grid.renderDates(dto, hasLocation, this.reasonNameById);
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
  };

  app.usecase.calendar.monthFolding.decrementMonth = () => {
    const repository = app.adapter.repository.Month;
    repository.setUTCMonth(repository.getUTCMonth() - 1);
    app.usecase.calendar.reload.apply();
  };

  app.usecase.calendar.monthFolding.incrementMonth = () => {
    const repository = app.adapter.repository.Month;
    repository.setUTCMonth(repository.getUTCMonth() + 1);
    app.usecase.calendar.reload.apply();
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

  app.usecase.settings.location.pickOnMap = {
    async apply() {
      let geoPosition = app.usecase.location.GetGeoPosition.apply();
      if (!geoPosition) {
        geoPosition = { // default Kaaba
          latitude: 21.4225,
          longitude: 39.8262,
        };
      }
      await app.adapter.dom.settings.location.changeLocationWindow.open(geoPosition, (latitude, longitude) => {
        app.usecase.location.SetGeoPosition.apply(latitude, longitude, 'CUSTOM');
      });
    }
  }

  /**
   * App initialization
   */
  requestIdleCallback(() => {
    app.usecase.calendar.reload.apply();
  });

  {// init calendar month folding
    app.adapter.dom.calendar.monthFolding.prevMonthEl.addEventListener('click', app.usecase.calendar.monthFolding.decrementMonth)
    app.adapter.dom.calendar.monthFolding.nextMonthEl.addEventListener('click', app.usecase.calendar.monthFolding.incrementMonth);
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
  d.getElementById('pick-location-on-map-button').addEventListener('click', async _ => {
    await app.usecase.settings.location.pickOnMap.apply();
  });

  {// init settings timezone dropdown
    app.adapter.dom.settings.timezoneDropdown.init(
      app.usecase.getTimezone.apply());
  }

  {// init prayer-time-method dropdown
    let oldVal = app.adapter.repository.prayerTimeMethod.get();
    if (!oldVal) {
      oldVal = '${PrayerTimeMethod.AUTO_BY_LOCATION}';
      app.adapter.repository.prayerTimeMethod.set(oldVal)
    }
    const onChange = e => {
      app.adapter.repository.prayerTimeMethod.set(e.target.selectedValues[0])
      app.usecase.calendar.reload.apply();
    };
    app.adapter.dom.settings.prayerTimeMethod.dropdown.init(oldVal, onChange);
  }

</script>
</body>
</html>
