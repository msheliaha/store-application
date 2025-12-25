drop table if exists reset_token;

create table reset_token(
    id varchar(36) not null,
    token varchar(255) not null,
    expire_date timestamp not null,
    user_email varchar(255) not null,
    primary key (id),
    constraint fk_user
        foreign key (user_email)
            references user_table(email)
            on delete cascade
);