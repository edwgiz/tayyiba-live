--liquibase formatted sql
--changeset id:00007 author:edwgiz@zohomail.com

insert into i18n_bundles (locale, code, value)
values ('en', 'prayer-time-method-groups.AUTOMATIC.name', 'Automatic'),
       ('en', 'prayer-time-method-groups.STANDARD.name', 'Standard');

update i18n_bundles set code='prayer-time-method-groups.AUTOMATIC.methodNames.AUTO_BY_LOCATION' where code='prayer-time-method.names.AUTO_BY_LOCATION';
update i18n_bundles set code='prayer-time-method-groups.STANDARD.methodNames.MWL' where code='prayer-time-method.names.MWL';
update i18n_bundles set code='prayer-time-method-groups.STANDARD.methodNames.ISNA' where code='prayer-time-method.names.ISNA';
update i18n_bundles set code='prayer-time-method-groups.STANDARD.methodNames.EGAS' where code='prayer-time-method.names.EGAS';
update i18n_bundles set code='prayer-time-method-groups.STANDARD.methodNames.UMM_AL_QURA' where code='prayer-time-method.names.UMM_AL_QURA';
update i18n_bundles set code='prayer-time-method-groups.STANDARD.methodNames.KARACHI' where code='prayer-time-method.names.KARACHI';
update i18n_bundles set code='prayer-time-method-groups.STANDARD.methodNames.TEHRAN' where code='prayer-time-method.names.TEHRAN';
update i18n_bundles set code='prayer-time-method-groups.STANDARD.methodNames.SHIA_QUM' where code='prayer-time-method.names.SHIA_QUM';
update i18n_bundles set code='prayer-time-method-groups.STANDARD.methodNames.GULF' where code='prayer-time-method.names.GULF';
update i18n_bundles set code='prayer-time-method-groups.STANDARD.methodNames.KUWAIT' where code='prayer-time-method.names.KUWAIT';
update i18n_bundles set code='prayer-time-method-groups.STANDARD.methodNames.QATAR' where code='prayer-time-method.names.QATAR';
update i18n_bundles set code='prayer-time-method-groups.STANDARD.methodNames.MUIS' where code='prayer-time-method.names.MUIS';
update i18n_bundles set code='prayer-time-method-groups.STANDARD.methodNames.UOIF' where code='prayer-time-method.names.UOIF';
update i18n_bundles set code='prayer-time-method-groups.STANDARD.methodNames.DIYANET' where code='prayer-time-method.names.DIYANET';
update i18n_bundles set code='prayer-time-method-groups.STANDARD.methodNames.SAMR' where code='prayer-time-method.names.SAMR';
update i18n_bundles set code='prayer-time-method-groups.STANDARD.methodNames.MSC' where code='prayer-time-method.names.MSC';
update i18n_bundles set code='prayer-time-method-groups.STANDARD.methodNames.DUBAI' where code='prayer-time-method.names.DUBAI';
update i18n_bundles set code='prayer-time-method-groups.STANDARD.methodNames.JAKIM' where code='prayer-time-method.names.JAKIM';
update i18n_bundles set code='prayer-time-method-groups.STANDARD.methodNames.TUNISIA' where code='prayer-time-method.names.TUNISIA';
update i18n_bundles set code='prayer-time-method-groups.STANDARD.methodNames.ALGERIA' where code='prayer-time-method.names.ALGERIA';
update i18n_bundles set code='prayer-time-method-groups.STANDARD.methodNames.KEMENAG' where code='prayer-time-method.names.KEMENAG';
update i18n_bundles set code='prayer-time-method-groups.STANDARD.methodNames.MOROCCO' where code='prayer-time-method.names.MOROCCO';
update i18n_bundles set code='prayer-time-method-groups.STANDARD.methodNames.CIL' where code='prayer-time-method.names.CIL';
