DROP TABLE IF EXISTS curr_exch_rate;


CREATE TABLE IF NOT EXISTS curr_exch_rate (
    curr_cd VARCHAR(3) NOT NULL,
    exch_rate NUMERIC(10, 4) NOT NULL,
    eff_dt DATE NOT NULL,
    PRIMARY KEY (curr_cd, eff_dt));