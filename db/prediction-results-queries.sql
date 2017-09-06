update prediction_results set provider = 'NOAA' where provider = 'noaa';
update prediction_results set provider = 'Wunderground' where provider = 'wunderground';
update prediction_results set provider = 'Aeris' where provider = 'aeris';
update prediction_results set provider = 'Dark Sky' where provider = 'forecast.io';


-- count of wrong about no rain today by provider
select provider, count(provider)
from prediction_results
where pop = 0
      and days_before = 0
      and precip > 0.1
group by provider
order by count(provider) desc;

-- count by location
select location, count(location)
from prediction_results
where pop = 0
      and days_before = 0
      and precip > 0.1
group by location
order by count(location) desc;

-- more pedantic version, going out to 3 days before and including any precip
select provider, count(provider)
from prediction_results
where pop = 0
      and days_before < 4
      and precip > 0
group by provider
order by count(provider) desc;
-- i.e. you should really default to 20% for 3 or more days out, you precipital prevaricators


-- lying liars that said it would definitely rain that same day and it didn't
select provider, count(provider)
from prediction_results
where pop = 1
      and days_before = 0
      and precip = 0
group by provider;


-- count of those that nailed it for no precip
select provider, count(provider)
from prediction_results
where pop = 0
      and days_before = 0
      and precip = 0
group by provider
order by count(provider) desc;

-- ^^ noaabody does sun better, largely due to them being so zero pop happy

-- no rain long-shots
select provider, location, count(provider)
from prediction_results
where pop = 0
      and days_before = 3
      and precip = 0
group by provider, location
order by count(provider) desc;


-- count of those that nailed it from 3 days out for precip
select provider, count(provider)
from prediction_results
where pop = 1
      and days_before = 3
      and precip > 0
group by provider
order by count(provider) desc;
-- ^ forecast.io is never sure about rain
-- ^ when wunderground is sure it's gonna rain, it's gonna rain.

-- count by provider for those that nailed low from 0 days out
select provider, count(low_abs_error)
from prediction_results
where low_abs_error = 0
      and days_before = 0
group by provider
order by count(low_abs_error) desc;

-- count by location of those that nailed low from 0 days out
select location, count(low_abs_error)
from prediction_results
where low_abs_error = 0
      and days_before = 0
group by location
order by count(low_abs_error) desc, location;

-- count that nailed high from 0 days out
select provider, count(high_abs_error)
from prediction_results
where high_abs_error = 0
      and days_before = 0
group by provider
order by count(high_abs_error) desc;




-- low MAD by provider
select provider, (sum(low_abs_error)::numeric(8,3) / count(low_abs_error)::numeric(8,3)) as MAD
from prediction_results
where days_before = 0
      and low_abs_error < 50
group by provider;

-- a couple errors getting the data, and a little bit of bad data.
-- e.g. -9999 as the predicted_low a few times from wunderground

-- low MAD by location
select location, (sum(low_abs_error)::numeric(8,3) / count(low_abs_error)::numeric(8,3)) as MAD
from prediction_results
where days_before = 0
      and low_abs_error < 50
group by location
order by MAD desc, location;

-- high MAD by provider
select provider, (sum(high_abs_error)::numeric(8,3) / count(high_abs_error)::numeric(8,3)) as MAD
from prediction_results
where days_before = 0
      and high_abs_error < 50
group by provider;

-- MAD-like measurement of pop compared to actual
select provider,
  SUM (CASE WHEN precip > 0 THEN (1 - pop) ELSE pop END ) / count(low_abs_error)::numeric(8,3) AS precip_deviation
from prediction_results
where days_before = 0
group by provider;

-- a count of the "quality" of the predictions - low
select provider,
  count(*) filter (where low_abs_error = 0) as perfect,
  count(*) filter (where low_abs_error between 1 and 3) as good,
  count(*) filter (where low_abs_error between 4 and 6) as ok,
  count(*) filter (where low_abs_error > 6) as bad
from prediction_results
where days_before = 0
group by provider;
-- ^ shows forecast.io crushing it

-- quality count of high
select provider,
  count(*) filter (where high_abs_error = 0) as perfect,
  count(*) filter (where high_abs_error between 1 and 3) as good,
  count(*) filter (where high_abs_error between 4 and 6) as ok,
  count(*) filter (where high_abs_error > 6) as bad
from prediction_results
where days_before = 0
group by provider;


-- distance from NOAA 0 days before - low
select pr.provider, sum( abs(pr.predicted_low - noaapr.predicted_low) ) as noaa_difference
from prediction_results pr
  join prediction_results noaapr on pr.target_date = noaapr.target_date and pr.days_before = noaapr.days_before and noaapr.provider = 'noaa' and pr.location = noaapr.location
where pr.days_before = 0
      and pr.predicted_low > -100
      and pr.predicted_high < 150
group by pr.provider;

-- distance from NOAA 0 days before - low
select pr.provider, sum( abs(pr.predicted_high - noaapr.predicted_high) ) as noaa_difference
from prediction_results pr
  join prediction_results noaapr on pr.target_date = noaapr.target_date and pr.days_before = noaapr.days_before and noaapr.provider = 'NOAA' and pr.location = noaapr.location
where pr.days_before = 0
      and pr.predicted_low > -100
      and pr.predicted_high < 150
group by pr.provider;




-- find days where there weren't at least 4 predictions collected
select provider, target_date, count(target_date)
from prediction
where days_before = 0
group by provider, target_date
having count(target_date) < 4;
-- noaa: 9
-- wunderground: 11

