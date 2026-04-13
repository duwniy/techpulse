-- Ensure exactly one demo persona per core role used by frontend login.
-- Password for all users: "password123"
UPDATE users
SET full_name = 'Анна Смирнова',
    role = 'HR',
    avatar_initials = 'АС'
WHERE email = 'anna.smirnova@company.dev';

INSERT INTO users (id, email, password_hash, full_name, role, avatar_initials)
SELECT '11111111-1111-1111-1111-111111111111', 'anna.smirnova@company.dev', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.TVuHOnu', 'Анна Смирнова', 'HR', 'АС'
WHERE NOT EXISTS (
    SELECT 1 FROM users WHERE email = 'anna.smirnova@company.dev'
);

UPDATE users
SET full_name = 'Олег Иванов',
    role = 'MANAGER',
    avatar_initials = 'ОИ'
WHERE email = 'oleg.ivanov@company.dev';

INSERT INTO users (id, email, password_hash, full_name, role, avatar_initials)
SELECT '22222222-2222-2222-2222-222222222222', 'oleg.ivanov@company.dev', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.TVuHOnu', 'Олег Иванов', 'MANAGER', 'ОИ'
WHERE NOT EXISTS (
    SELECT 1 FROM users WHERE email = 'oleg.ivanov@company.dev'
);

UPDATE users
SET full_name = 'Иван Петров',
    role = 'EMPLOYEE',
    avatar_initials = 'ИП'
WHERE email = 'ivan.petrov@company.dev';

INSERT INTO users (id, email, password_hash, full_name, role, avatar_initials)
SELECT '33333333-3333-3333-3333-333333333333', 'ivan.petrov@company.dev', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.TVuHOnu', 'Иван Петров', 'EMPLOYEE', 'ИП'
WHERE NOT EXISTS (
    SELECT 1 FROM users WHERE email = 'ivan.petrov@company.dev'
);

-- Guarantee employee onboarding linkage for the demo employee.
UPDATE onboarding_plans
SET manager_id = '22222222-2222-2222-2222-222222222222',
    day_current = 5,
    total_days = 90
WHERE employee_id = '33333333-3333-3333-3333-333333333333';

INSERT INTO onboarding_plans (id, employee_id, manager_id, start_date, day_current, total_days)
SELECT '55555555-5555-5555-5555-555555555555', '33333333-3333-3333-3333-333333333333', '22222222-2222-2222-2222-222222222222', CURRENT_DATE - INTERVAL '5 days', 5, 90
WHERE NOT EXISTS (
    SELECT 1 FROM onboarding_plans WHERE employee_id = '33333333-3333-3333-3333-333333333333'
);
