--liquibase formatted sql
--changeset id:00005 author:edwgiz@zohomail.com

insert into i18n_bundles (locale, code, value)
values ('en', 'settings.prayer-time-method', 'Prayer Time Method'),
       ('en', 'prayer-time-method.names.MWL', 'Muslim World League'),
       ('en', 'prayer-time-method.names.ISNA', 'Islamic Society of North America (ISNA)'),
       ('en', 'prayer-time-method.names.EGAS', 'Egyptian General Authority of Survey'),
       ('en', 'prayer-time-method.names.UMM_AL_QURA', 'Umm al-Qura University, Makkah'),
       ('en', 'prayer-time-method.names.KARACHI', 'University of Islamic Sciences, Karachi'),
       ('en', 'prayer-time-method.names.TEHRAN', 'Institute of Geophysics, University of Tehran'),
       ('en', 'prayer-time-method.names.SHIA_QUM', 'Shia Ithna Ashari, Qum'),
       ('en', 'prayer-time-method.names.GULF', 'Gulf Region'),
       ('en', 'prayer-time-method.names.KUWAIT', 'Kuwait'),
       ('en', 'prayer-time-method.names.QATAR', 'Qatar'),
       ('en', 'prayer-time-method.names.MUIS', 'Majlis Ugama Islam Singapura (MUIS)'),
       ('en', 'prayer-time-method.names.UOIF', 'Union of Islamic Organizations of France (UOIF)'),
       ('en', 'prayer-time-method.names.DIYANET', 'Diyanet (Turkey)'),
       ('en', 'prayer-time-method.names.SAMR', 'Spiritual Administration of Muslims, Russia'),
       ('en', 'prayer-time-method.names.MSC', 'Moon Sighting Committee'),
       ('en', 'prayer-time-method.names.DUBAI', 'Dubai, UAE'),
       ('en', 'prayer-time-method.names.JAKIM', 'JAKIM, Malaysia'),
       ('en', 'prayer-time-method.names.TUNISIA', 'Tunisia'),
       ('en', 'prayer-time-method.names.ALGERIA', 'Algeria'),
       ('en', 'prayer-time-method.names.KEMENAG', 'Ministry of Religious Affairs, Indonesia'),
       ('en', 'prayer-time-method.names.MOROCCO', 'Morocco'),
       ('en', 'prayer-time-method.names.CIL', 'Islamic Community of Lisbon');