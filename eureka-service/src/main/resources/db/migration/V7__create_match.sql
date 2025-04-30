create table p_match
(
    id           bigint auto_increment primary key,
    home_team_id bigint                          not null,
    away_team_id bigint                          not null,
    match_day    date                            not null,
    stadium_id   bigint                          not null,
    status       varchar(20) default 'PROGRESS' not null
) default character set utf8mb4;