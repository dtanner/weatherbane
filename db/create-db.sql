create table public.prediction_response
(
  id                uuid primary key,
  created_timestamp timestamp with time zone not null,
  provider          varchar(100)             not null,
  response_code     int                      not null,
  response_text     jsonb                    not null
)
with (
oids = false
);


create table public.prediction
(
  id           uuid primary key,
  predicted_on timestamp with time zone not null,
  target_date  date                     not null,
  location     varchar(100)             not null,
  provider     varchar(100)             not null,
  high         int                      not null,
  low          int                      not null,
  pop          numeric(3, 2)            not null,
  response_id  uuid                     not null
)
with (
oids = false
);

-- CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
-- SELECT uuid_generate_v1();

create table public.observation
(
  id       uuid primary key default uuid_generate_v1(),
  date     date          not null,
  location varchar(100)  not null,
  high     int           not null,
  low      int           not null,
  precip   numeric(3, 2) not null
)
with (
oids = false
);


alter table prediction add column days_before smallint;
update prediction set days_before = (select target_date::date - predicted_on::date);