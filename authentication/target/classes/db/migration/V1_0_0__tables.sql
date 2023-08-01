create table if not exists tokens(
     id serial primary key,
     token varchar unique not null,
     user_id int not null
);