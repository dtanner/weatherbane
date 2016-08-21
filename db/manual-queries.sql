select target_date, location, provider, high, low, pop
from prediction
where target_date = '2016-07-27'
      and date(predicted_on) = '2016-07-27'
      and location = 'MSP'
order by location, provider;

select date, location, high, low, precip
from observation
where date = '2016-07-27'
      and location = 'MSP';


select location, days_before, target_date, provider, high, low, pop
from prediction
where days_before <= 1
      and location = 'MSP'
      and target_date = '2016-08-05'
order by provider, days_before;

select date, location, high, low, precip
from observation
where location = 'MSP'
      and date = '2016-08-05'
order by date desc
limit 100;
