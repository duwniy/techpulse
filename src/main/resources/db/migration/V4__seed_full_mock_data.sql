-- Clear existing data safely
TRUNCATE TABLE users, onboarding_plans, tasks, activity_events, risk_scores, notifications, material_progress, pulse_responses CASCADE;


-- Re-insert users with valid UUIDs and standard password
INSERT INTO users (id, email, password_hash, full_name, role, avatar_initials) VALUES
  ('11111111-1111-1111-1111-111111111111', 'anna.smirnova@company.dev', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.TVuHOnu', 'Анна Смирнова', 'HR', 'АС'),
  ('22222222-2222-2222-2222-222222222222', 'oleg.ivanov@company.dev', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.TVuHOnu', 'Олег Иванов', 'MANAGER', 'ОИ'),
  ('33333333-3333-3333-3333-333333333333', 'ivan.petrov@company.dev', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.TVuHOnu', 'Иван Петров', 'EMPLOYEE', 'ИП'),
  ('44444444-4444-4444-4444-444444444444', 'maria.popova@company.dev', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.TVuHOnu', 'Мария Попова', 'EMPLOYEE', 'МП');

-- Onboarding Plans
INSERT INTO onboarding_plans (id, employee_id, manager_id, start_date, day_current, total_days) VALUES
  ('55555555-5555-5555-5555-555555555555', '33333333-3333-3333-3333-333333333333', '22222222-2222-2222-2222-222222222222', CURRENT_DATE - INTERVAL '15 days', 15, 90),
  ('66666666-6666-6666-6666-666666666666', '44444444-4444-4444-4444-444444444444', '22222222-2222-2222-2222-222222222222', CURRENT_DATE - INTERVAL '3 days', 3, 90);

-- Tasks
INSERT INTO tasks (plan_id, title, description, due_date, completed, stage) VALUES
  -- Ivan Petrov (Many overdue -> High risk)
  ('55555555-5555-5555-5555-555555555555', 'Ознакомиться с Wiki', 'Прочитать базовую документацию', CURRENT_DATE - INTERVAL '10 days', true, 1),
  ('55555555-5555-5555-5555-555555555555', 'Первый PR', 'Сделать коммит в тестовый репозиторий', CURRENT_DATE - INTERVAL '5 days', false, 1),
  ('55555555-5555-5555-5555-555555555555', 'Встреча с наставником', 'Обзор первой недели', CURRENT_DATE - INTERVAL '2 days', false, 1),
  ('55555555-5555-5555-5555-555555555555', 'Настройка доступов', 'Запросить доступ в Jira', CURRENT_DATE - INTERVAL '8 days', false, 1),
  
  -- Maria Popova (All good -> Low risk)
  ('66666666-6666-6666-6666-666666666666', 'Знакомство с командой', 'Представиться в Slack', CURRENT_DATE - INTERVAL '1 days', true, 1),
  ('66666666-6666-6666-6666-666666666666', 'Выбор оборудования', 'Заполнить форму на технику', CURRENT_DATE - INTERVAL '1 days', true, 1),
  ('66666666-6666-6666-6666-666666666666', 'Получение пропусков', 'Забрать бейдж в офисе', CURRENT_DATE + INTERVAL '2 days', false, 1);

-- Materials are already in V2 so we won't delete or re-insert them, 
-- but let's grab arbitrary IDs by looking at material_progress or just use subqueries
DO $$
DECLARE
    mat1_id UUID;
    mat2_id UUID;
BEGIN
    SELECT id INTO mat1_id FROM materials LIMIT 1;
    SELECT id INTO mat2_id FROM materials OFFSET 1 LIMIT 1;
    
    IF mat1_id IS NOT NULL THEN
        INSERT INTO material_progress (user_id, material_id, status) VALUES 
        ('33333333-3333-3333-3333-333333333333', mat1_id, 'PENDING'),
        ('44444444-4444-4444-4444-444444444444', mat1_id, 'DONE'),
        ('44444444-4444-4444-4444-444444444444', mat2_id, 'IN_PROGRESS');
    END IF;
END $$;

-- Risk Scores
INSERT INTO risk_scores (user_id, score, level, signals) VALUES
  ('33333333-3333-3333-3333-333333333333', 75, 'RED', '{"overdue_tasks": 3, "low_pulse": true, "activity_drop": true}'),
  ('44444444-4444-4444-4444-444444444444', 15, 'GREEN', '{"active_tasks": 1, "recent_login": true}');

-- Pulse Responses
INSERT INTO pulse_responses (user_id, workload_rating, open_text, created_at) VALUES
  ('33333333-3333-3333-3333-333333333333', 2, 'Тяжело, много непонятного', CURRENT_DATE - INTERVAL '1 days'),
  ('44444444-4444-4444-4444-444444444444', 5, 'Всё супер, команда очень помогает!', CURRENT_DATE - INTERVAL '1 days');

-- Notifications
INSERT INTO notifications (manager_id, employee_id, type, message) VALUES
  ('22222222-2222-2222-2222-222222222222', '33333333-3333-3333-3333-333333333333', 'RISK_RED', 'Ivan Petrov is at high risk (RED). 3 tasks are overdue.');

-- Activity Events
INSERT INTO activity_events (user_id, event_type, metadata, created_at) VALUES
  ('33333333-3333-3333-3333-333333333333', 'LOGIN', '{"device": "macbook"}', CURRENT_DATE - INTERVAL '5 days'),
  ('44444444-4444-4444-4444-444444444444', 'LOGIN', '{"device": "macbook"}', CURRENT_DATE);
