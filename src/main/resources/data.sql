-- DROP TABLE IF EXISTS measure_time_log;

CREATE TABLE IF NOT EXISTS measure_time_log (
  id INT AUTO_INCREMENT  PRIMARY KEY,
  datetime TIMESTAMP NOT NULL,
  duration INT NOT NULL
);