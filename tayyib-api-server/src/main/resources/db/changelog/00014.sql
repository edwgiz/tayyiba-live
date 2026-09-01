--liquibase formatted sql
--changeset id:00013 author:edwgiz@zohomail.eu

delete from i18n_bundles where code='settings.by-device';

insert into i18n_bundles (locale, code, value)
values ('en', 'commons.change', 'Change');
