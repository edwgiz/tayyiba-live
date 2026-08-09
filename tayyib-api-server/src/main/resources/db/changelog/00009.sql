--liquibase formatted sql
--changeset id:00009 author:edwgiz@zohomail.com


update i18n_bundles
set code='settings.location.label'
where code = 'settings.location';

update i18n_bundles
set code='settings.location.pick-on-map',
    value='Pick location on map'
where code = 'settings.change';

update i18n_bundles
set code='settings.location.permission.grant',
    value='Allow location for Fajr and Sunrise times'
where code = 'settings.grant-location-permission';

insert into i18n_bundles (locale, code, value)
values ('en', 'settings.location.permission.revoke-blocking', 'please revoke blocking in site settings');


update i18n_bundles
set code='commons.ok'
where code = 'settings.ok';

insert into i18n_bundles (locale, code, value)
values ('en', 'commons.or', 'or');


delete
from i18n_bundles
where code = 'settings.change-location-window-title';

