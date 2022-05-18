create table Board
(
    id varchar(255),
    name varchar(255),
    primary key (id)
);
create table Comment
(
    id varchar(255),
    post_id varchar(255),
    level_id varchar(255),
    group_id varchar(255),
    created_date varchar(255),
    user_id varchar(255),
    is_deleted varchar(255),
    report_cnt bigint,
    primary key (id)
);
create table Post
(
    id varchar(255),
    user_id varchar(255),
    content varchar(255),
    up_cnt varchar(255),
    down_cnt varchar(255),
    created_date varchar(255),
    report_cnt varchar(255),
    view_cnt bigint,
    primary key (id)
);
create table User
(
    social_id varchar(255),
    id varchar(255),
    nickname varchar(255),
    point bigint,
    primary key (social_id)
);