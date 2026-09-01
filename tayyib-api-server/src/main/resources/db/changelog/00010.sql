--liquibase formatted sql
--changeset id:00010 author:edwgiz@zohomail.eu


update i18n_bundles
set code='settings.location.permission.change-in-browser',
    value='Use current location'
where code = 'settings.location.permission.grant';

insert into i18n_bundles (locale, code, value)
values ('en', 'settings.location.permission.grant-reason', 'For Fajr and sunrise times');
