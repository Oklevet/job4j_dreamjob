drop  table candidates
create table candidates
(
    id                  serial primary key,
    name                varchar not null,
    description         varchar not null,
    creationDate       timestamp,
    workingPosition     varchar not null,
    salary              int,
    cityId              int references cities(id)
);