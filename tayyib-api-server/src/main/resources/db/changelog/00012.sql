--liquibase formatted sql
--changeset id:00012 author:edwgiz@zohomail.com


update i18n_bundles
set value='For Fajr and sunset times'
where code = 'settings.location.permission.grant-reason';

insert into i18n_bundles (locale, code, value)
values ('en', 'fasting.ics.calendar.name', 'Islamic Fasting'),
       ('en', 'fasting.ics.calendar.event.summary', 'Fasting'),
       ('en', 'fasting.ics.calendar.event.hijri-date-label', 'Hijri');

UPDATE i18n_bundles
SET value = 'Fasting reason'
WHERE locale = 'en'
  AND code = 'fasting.reason-groups.generic.ics-title';

UPDATE i18n_bundles
SET value = 'Obligatory (Wājib) fasting reason'
WHERE locale = 'en'
  AND code = 'fasting.reason-groups.obligatory.ics-title';

UPDATE i18n_bundles
SET value = 'Voluntary (Taṭawwuʿ) fasting reason'
WHERE locale = 'en'
  AND code = 'fasting.reason-groups.voluntary.ics-title';

UPDATE i18n_bundles
SET value = 'Prohibiting (Muḥarram) fasting reason'
WHERE locale = 'en'
  AND code = 'fasting.reason-groups.prohibiting.ics-title';
