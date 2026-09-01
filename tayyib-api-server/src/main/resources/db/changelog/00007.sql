--liquibase formatted sql
--changeset id:00007 author:edwgiz@zohomail.eu

insert into i18n_bundles (locale, code, value)
values ('en', 'prayer-time-method.names.AUTO_BY_LOCATION', 'Automatic based on location');


create table prayer_time_method_by_country
(
    iso3166_1_alpha2   char(2)     not null,
    prayer_time_method varchar(32) not null,
    constraint prayer_time_method_by_country_pk primary key (iso3166_1_alpha2)
);

insert into prayer_time_method_by_country (iso3166_1_alpha2, prayer_time_method)
values
-- North America
('US', 'ISNA'),
('CA', 'ISNA'),

-- Egypt
('EG', 'EGAS'),

-- Saudi Arabia
('SA', 'UMM_AL_QURA'),

-- Pakistan
('PK', 'KARACHI'),

-- Iran
('IR', 'TEHRAN'),

-- Iraq (majority Shia institutions)
('IQ', 'SHIA_QUM'),

-- Gulf states
('AE', 'DUBAI'),
('KW', 'KUWAIT'),
('QA', 'QATAR'),
('BH', 'GULF'),
('OM', 'GULF'),

-- Singapore
('SG', 'MUIS'),

-- Turkey
('TR', 'DIYANET'),

-- Russia
('RU', 'SAMR'),

-- Malaysia
('MY', 'JAKIM'),

-- Indonesia
('ID', 'KEMENAG'),

-- Tunisia
('TN', 'TUNISIA'),

-- Algeria
('DZ', 'ALGERIA'),

-- Morocco
('MA', 'MOROCCO'),

-- Portugal
('PT', 'CIL');
