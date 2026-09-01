--liquibase formatted sql
--changeset id:00004 author:edwgiz@zohomail.eu

update i18n_bundles set code='calendar.event.fajr', value='Fajr' where code='calendar.sunrise';
update i18n_bundles set code='calendar.event.sunset' where code='calendar.sunset';
