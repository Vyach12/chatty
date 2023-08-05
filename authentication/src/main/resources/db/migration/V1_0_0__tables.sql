create table roles(
    id serial primary key,
    name varchar not null unique
);

create table privileges(
    id serial primary key,
    name varchar not null unique
);

create table role_settings(
    id serial primary key,
    role_id int references roles(id) not null,
    privilege_id int references privileges(id) not null
);

create table _users(
    id uuid primary key,
    email varchar not null unique,
    username varchar unique not null,
    password varchar not null,
    is_enabled boolean not null,
    role_id int references roles(id) not null
);

create table tokens(
    id serial primary key,
    token varchar unique not null,
    user_id uuid references _users(id) not null
);

INSERT INTO roles(name) VALUES ('ROLE_USER');
INSERT INTO roles(name) VALUES ('ROLE_ADMIN');
INSERT INTO privileges(name) VALUES('ban_users');
INSERT INTO role_settings(role_id, privilege_id) VALUES (2, 1);