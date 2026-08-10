--liquibase formatted sql
--changeset id:00011 author:edwgiz@zohomail.com


delete from i18n_bundles where code='fasting.meta.keywords';

update i18n_bundles
set value='Tayyib Fasting Calendar: Authentic Islamic fasting schedules aligned with Sunnah practices and verified Hijri dates.'
where code = 'fasting.head.description';

insert into public.i18n_bundles (locale, code, value)
values ('en', 'fasting.reason-groups.generic.ics-title', 'Fasting'),
       ('en', 'fasting.reason-groups.obligatory.ics-title', 'Obligatory Fasting'),
       ('en', 'fasting.reason-groups.voluntary.ics-title', 'Voluntary Fasting'),
       ('en', 'fasting.reason-groups.prohibiting.ics-title', 'Prohibiting Fasting');

