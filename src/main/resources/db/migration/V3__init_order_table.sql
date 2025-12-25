drop table if exists order_table;
drop table if exists order_item;


create table order_table(
    id varchar(36) not null,
    user_email varchar(255) not null,
    order_status smallint not null,
    create_date datetime(6),
    update_date datetime(6),
    total decimal(10, 2) not null,
    primary key (id)
);

create table order_item(
    id varchar(36) not null,
    order_id varchar(36) not null,
    item_id varchar(36) not null,
    quantity integer not null,
    price decimal(10, 2) not null,
    primary key (id),
    constraint fk_order
        foreign key (order_id)
        references order_table(id)
        on delete cascade,

    constraint fk_item
        foreign key (item_id)
        references item(id)
);
