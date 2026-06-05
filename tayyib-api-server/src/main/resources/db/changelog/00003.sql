--liquibase formatted sql
--changeset id:00003 author:edwgiz@gmail.com

create type hijri_method as enum (
    'HJCoSA',
    'UAQ',
    'Diyanet'
);

create table gregorian_hijri_mapping
(
    hijri_method hijri_method not null,
    gregorian date not null,
    hijri date not null,
    constraint gregorian_hijri_mapping_pk primary key (hijri_method, gregorian)
);
