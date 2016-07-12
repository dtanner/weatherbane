DROP TABLE public.prediction;

CREATE TABLE public.prediction
(
  id UUID PRIMARY KEY,
  predicted_on TIMESTAMP WITH TIME ZONE NOT NULL,
  target_date TIMESTAMP WITH TIME ZONE NOT NULL,
  location varchar(100) NOT NULL,
  provider varchar(100) NOT NULL,
  high INT NOT NULL,
  low INT NOT NULL,
  pop INT NOT NULL
)
WITH (
  OIDS=FALSE
);
