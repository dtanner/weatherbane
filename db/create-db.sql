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


alter table prediction
  add column days_before smallint;
update prediction
set days_before = (select target_date :: date - predicted_on :: date);

create table public.prediction_results
(
  predicted_on   timestamp with time zone not null,
  target_date    date                     not null,
  days_before    smallint                 not null,
  location       varchar(100)             not null,
  provider       varchar(100)             not null,
  predicted_low  int                      not null,
  actual_low     int                      not null,
  low_abs_error  integer                  not null,
  predicted_high int                      not null,
  actual_high    int                      not null,
  high_abs_error integer                  not null,
  pop            numeric(3, 2)            not null,
  precip         numeric(3, 2)            not null
)
with (
oids = false
);

insert into prediction_results (predicted_on, target_date, days_before, location, provider, predicted_low, actual_low, low_abs_error, predicted_high, actual_high, high_abs_error, pop, precip)
  select p.predicted_on, p.target_date, (select p.target_date :: date - p.predicted_on :: date), p.location, p.provider, p.low, o.low, abs(p.low - o.low),
    p.high, o.high, abs(p.high - o.high), p.pop, o.precip
  from prediction p
    join observation o on p.target_date = o.date and p.location = o.location;
