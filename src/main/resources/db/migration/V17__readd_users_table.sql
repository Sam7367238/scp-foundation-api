create table users
(
    uuid            binary(16) default (uuid_to_bin(uuid())) not null
        primary key,
    name            VARCHAR(150)                             not null,
    email           VARCHAR(100)                             not null,
    phone_number    VARCHAR(25)                              not null,
    clearance_level tinyint    default 0                     not null,
    profile_id      binary(16)                               not null,
    created         datetime   default (NOW())               not null,
    constraint users_profiles_uuid_fk
        foreign key (profile_id) references profiles (uuid)
);

