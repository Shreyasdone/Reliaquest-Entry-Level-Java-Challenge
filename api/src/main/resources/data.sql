INSERT INTO employees (
    uuid,
    first_name,
    last_name,
    full_name,
    salary,
    age,
    job_title,
    email,
    contract_hire_date,
    contract_termination_date
) VALUES
(
    'a1b2c3d4-e5f6-7890-abcd-ef1234567890',
    'Alice',
    'Johnson',
    'Alice Johnson',
    75000,
    32,
    'Software Engineer',
    'alice.johnson@example.com',
    '2023-01-15T09:00:00Z',
    NULL
),
(
    'b2c3d4e5-f6a7-8901-bcde-f12345678901',
    'Bob',
    'Williams',
    'Bob Williams',
    92000,
    45,
    'Senior Product Manager',
    'bob.williams@example.com',
    '2021-06-01T09:00:00Z',
    NULL
);
