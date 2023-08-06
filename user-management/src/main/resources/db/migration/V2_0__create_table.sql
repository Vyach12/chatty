create table _users(
    id uuid primary key,
    username varchar not null unique,
    email varchar not null unique,
    date_of_birth date not null
);

