create table if not exists _users(
    id serial primary key,
    username varchar not null unique,
    password varchar not null,
    email varchar not null unique,
    date_of_birth date not null,
    is_enabled boolean not null,
    role_id int references roles(id) not null
);


create table if not exists roles(
    id serial primary key,
    name varchar not null unique
);

create table if not exists privileges(
    id serial primary key,
    name varchar not null unique
);

create table if not exists role_settings(
    id serial primary key,
    role_id int not null,
    privilege_id int not null
);

create table if not exists messages(
    id serial primary key,
    sender_id int references _users(id) not null,
    recipient_id int references _users(id) not null,
    message varchar not null,
    date_of_sending timestamp not null,
    date_of_change timestamp
);

create table if not exists tokens(
    id serial primary key,
    token varchar unique not null,
    user_id int references _users(id) not null
);

INSERT INTO roles(name) VALUES ('ROLE_USER');
INSERT INTO roles(name) VALUES ('ROLE_ADMIN');

INSERT INTO privileges(name) VALUES('ban_users');

INSERT INTO role_settings(role_id, privilege_id) VALUES (2, 1);