--liquibase formatted sql
--changeset id:00013 author:edwgiz@zohomail.com

delete from i18n_bundles where code='settings.by-device';

insert into i18n_bundles (locale, code, value)
values ('en', 'commons.cancel', 'Cancel'),
       ('en', 'settings.location.forget-custom-location', 'Forget the picked up one');
