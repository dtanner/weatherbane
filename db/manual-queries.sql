-- set prediction absolute error values after getting observations

-- get the actual observations for a given date/location
select date, location, high, low, precip
from observation
where date = '2016-07-27'
      and location = 'MSP';


-- get predictions for a given location/date/days_before
select location, days_before, target_date, provider, high, low, pop
from prediction
where days_before <= 1
      and location = 'MSP'
      and target_date = '2016-08-05'
order by provider, days_before;

