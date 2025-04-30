create table p_team
(
    id       bigint auto_increment primary key,
    name     varchar(20) not null,
    name_eng varchar(20) not null
) default character set utf8mb4;