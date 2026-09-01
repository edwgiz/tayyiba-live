--liquibase formatted sql
--changeset id:00015 author:edwgiz@zohomail.eu

insert into i18n_bundles (locale, code, value)
values ('en', 'fasting.mcp.label', 'Empower your AI tools'),
       ('en', 'fasting.mcp.copy-link-label', 'Copy MCP link');

update i18n_bundles
set value='Download ICS'
where code = 'fasting.ics.link-label';
