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
