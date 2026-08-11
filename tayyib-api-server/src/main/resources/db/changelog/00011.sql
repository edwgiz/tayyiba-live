--liquibase formatted sql
--changeset id:00011 author:edwgiz@zohomail.com


update i18n_bundles
set value='For Fajr and sunrise times'
where code = 'settings.location.permission.grant-reason';

delete from i18n_bundles where code='fasting.meta.keywords';

update i18n_bundles
set value='Tayyib Fasting Calendar: Authentic Islamic fasting schedules aligned with Sunnah practices and verified Hijri dates.'
where code = 'fasting.head.description';

insert into i18n_bundles (locale, code, value)
values ('en', 'fasting.reason-groups.generic.ics-title', 'Fasting'),
       ('en', 'fasting.reason-groups.obligatory.ics-title', 'Obligatory Fasting'),
       ('en', 'fasting.reason-groups.voluntary.ics-title', 'Voluntary Fasting'),
       ('en', 'fasting.reason-groups.prohibiting.ics-title', 'Prohibiting Fasting'),
       ('en', 'fasting.ics.label', 'Add schedule to your calendar'),
       ('en', 'fasting.ics.link-label', 'Subscribe / Download ICS'),
       ('en', 'fasting.ics.copy-link-label', 'copy subscription link'),
       ('en', 'fasting.reasons-title', 'Fasting reasons');


