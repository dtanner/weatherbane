drop table if exists public.prediction_response;
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


drop table if exists public.prediction;
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


