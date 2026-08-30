DROP TABLE IF EXISTS employees;


CREATE TABLE IF NOT EXISTS employees (
    uuid UUID NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    full_name VARCHAR(255),
    salary INTEGER NOT NULL,
    age INTEGER NOT NULL,
    job_title VARCHAR(150) NOT NULL,
    email VARCHAR(255) NOT NULL,
    contract_hire_date TIMESTAMP NOT NULL,
    contract_termination_date TIMESTAMP,

    CONSTRAINT pk_employees PRIMARY KEY (uuid)
);
