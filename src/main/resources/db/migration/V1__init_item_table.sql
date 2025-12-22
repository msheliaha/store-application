drop table if exists item;

create table item (
    id varchar(36) not null,
    item_name varchar(255) not null,
    available integer,
    price decimal not null,
    create_date datetime(6),
    update_date datetime(6),
    version integer,
    primary key (id)
);