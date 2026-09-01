--liquibase formatted sql
--changeset id:00006 author:edwgiz@zohomail.eu

update i18n_bundles set code='settings.by-device', value='By device' where code='settings.default-value';
