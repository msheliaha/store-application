drop table if exists user_table;

create table user_table(
    email varchar(255) not null,
    password varchar(255) not null,

    primary key (email)
);