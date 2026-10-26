alter table addresses
drop foreign key addresses_users_uuid_fk;

alter table departments
drop foreign key departments_users_uuid_fk;

alter table restoration_tokens
drop foreign key restoration_tokens_users_uuid_fk;

alter table scps
drop foreign key scps_users_uuid_fk;

alter table user_departments
drop foreign key user_departments_users_uuid_fk;

drop table users;