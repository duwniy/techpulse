-- V5: Expand mock data with specific risk scenarios
-- This migration adds a diverse set of employees under manager Oleg Ivanov to demonstrate platform features.

-- 1. Create a Primary Lead Manager and HR
INSERT INTO users (id, email, password_hash, full_name, role, avatar_initials) VALUES
  ('a1a1a1a1-a1a1-a1a1-a1a1-a1a1a1a1a1a1', 'elena.petrova@company.dev', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.TVuHOnu', 'Елена Петрова', 'HR', 'ЕП'),
  ('b2b2b2b2-b2b2-b2b2-b2b2-b2b22b2b2b2b', 'artem.lead@company.dev', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.TVuHOnu', 'Артем Лидов', 'MANAGER', 'АЛ')
ON CONFLICT (email) DO NOTHING;

-- 2. Define Employees for Oleg Ivanov (22222222-2222-2222-2222-222222222222)
INSERT INTO users (id, email, password_hash, full_name, role, avatar_initials) VALUES
  ('c3c3c3c3-c3c3-c3c3-c3c3-c3c3c3c3c3c3', 'ivan.sidorov@company.dev', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.TVuHOnu', 'Иван Сидоров', 'EMPLOYEE', 'ИС'),
  ('d4d4d4d4-d4d4-d4d4-d4d4-d4d4d4d4d4d4', 'maria.kuznetsova@company.dev', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.TVuHOnu', 'Мария Кузнецова', 'EMPLOYEE', 'МК'),
  ('e5e5e5e5-e5e5-e5e5-e5e5-e5e5e5e5e5e5', 'alexey.volkov@company.dev', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.TVuHOnu', 'Алексей Волков', 'EMPLOYEE', 'АВ'),
  ('f6f6f6f6-f6f6-f6f6-f6f6-f6f6f6f6f6f6', 'svetlana.burnout@company.dev', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.TVuHOnu', 'Светлана Соколова', 'EMPLOYEE', 'СС')
ON CONFLICT (email) DO NOTHING;

-- 3. Onboarding Plans
INSERT INTO onboarding_plans (id, employee_id, manager_id, start_date, day_current, total_days) VALUES
  -- Ivan (RED RISK)
  ('10000000-0000-0000-0000-000000000001', 'c3c3c3c3-c3c3-c3c3-c3c3-c3c3c3c3c3c3', '22222222-2222-2222-2222-222222222222', CURRENT_DATE - INTERVAL '20 days', 20, 90),
  -- Maria (AMBER RISK)
  ('10000000-0000-0000-0000-000000000002', 'd4d4d4d4-d4d4-d4d4-d4d4-d4d4d4d4d4d4', '22222222-2222-2222-2222-222222222222', CURRENT_DATE - INTERVAL '10 days', 10, 90),
  -- Alexey (GREEN RISK)
  ('10000000-0000-0000-0000-000000000003', 'e5e5e5e5-e5e5-e5e5-e5e5-e5e5e5e5e5e5', '22222222-2222-2222-2222-222222222222', CURRENT_DATE - INTERVAL '5 days', 5, 90),
  -- Svetlana (CONTRADICTION RISK)
  ('10000000-0000-0000-0000-000000000004', 'f6f6f6f6-f6f6-f6f6-f6f6-f6f6f6f6f6f6', '22222222-2222-2222-2222-222222222222', CURRENT_DATE - INTERVAL '12 days', 12, 90)
ON CONFLICT DO NOTHING;

-- 4. Tasks that trigger Risk level calculation
INSERT INTO tasks (plan_id, title, description, due_date, completed, stage) VALUES
  -- Ivan Sidorov (RED): 3 Overdue tasks
  ('10000000-0000-0000-0000-000000000001', 'Ознакомиться с архитектурой', 'Прочитать про Risk Engine', CURRENT_DATE - INTERVAL '5 days', false, 1),
  ('10000000-0000-0000-0000-000000000001', 'Настроить БД', 'Подключить Supabase', CURRENT_DATE - INTERVAL '3 days', false, 1),
  ('10000000-0000-0000-0000-000000000001', 'Выполнить замер производительности', 'Использовать JMeter', CURRENT_DATE - INTERVAL '1 days', false, 1),

  -- Maria Kuznetsova (AMBER): 2 Overdue tasks
  ('10000000-0000-0000-0000-000000000002', 'Установить Node.js', 'Использовать nvm', CURRENT_DATE - INTERVAL '2 days', false, 1),
  ('10000000-0000-0000-0000-000000000002', 'Проверить доступы', 'Slack и Email', CURRENT_DATE - INTERVAL '1 days', false, 1),

  -- Svetlana Sokolova (CONTRADICTION): 3 Overdue but high pulse
  ('10000000-0000-0000-0000-000000000004', 'Описание багов', 'Завести 5 тикетов', CURRENT_DATE - INTERVAL '4 days', false, 1),
  ('10000000-0000-0000-0000-000000000004', 'Ревью документации', 'Обновить Wiki', CURRENT_DATE - INTERVAL '2 days', false, 1)
ON CONFLICT DO NOTHING;

-- 5. Pulse Responses
INSERT INTO pulse_responses (user_id, workload_rating, open_text, created_at) VALUES
  ('c3c3c3c3-c3c3-c3c3-c3c3-c3c3c3c3c3c3', 1, 'Ничего не понимаю, задачи стоят.', CURRENT_DATE - INTERVAL '1 days'),
  ('d4d4d4d4-d4d4-d4d4-d4d4-d4d4d4d4d4d4', 2, 'Много встреч, не успеваю кодить.', CURRENT_DATE - INTERVAL '1 days'),
  ('e5e5e5e5-e5e5-e5e5-e5e5-e5e5e5e5e5e5', 5, 'Прекрасный коллектив и интересные задачи!', CURRENT_DATE - INTERVAL '1 days'),
  ('f6f6f6f6-f6f6-f6f6-f6f6-f6f6f6f6f6f6', 5, 'Всё супер! Я летаю!', CURRENT_DATE - INTERVAL '1 days'); -- High pulse for contradiction detection

-- 6. Activity Events (Signals for Risk Engine)
INSERT INTO activity_events (user_id, event_type, metadata, created_at) VALUES
  -- Ivan: Last login was far away
  ('c3c3c3c3-c3c3-c3c3-c3c3-c3c3c3c3c3c3', 'LOGIN', '{"device": "pc"}', CURRENT_DATE - INTERVAL '5 days'),
  -- Svetlana: No recent activity too
  ('f6f6f6f6-f6f6-f6f6-f6f6-f6f6f6f6f6f6', 'LOGIN', '{"device": "pc"}', CURRENT_DATE - INTERVAL '4 days'),
  ('f6f6f6f6-f6f6-f6f6-f6f6-f6f6f6f6f6f6', 'TASK_OPEN', '{"id": "dummy"}', CURRENT_DATE - INTERVAL '4 days'),
  -- Alexey: Very active
  ('e5e5e5e5-e5e5-e5e5-e5e5-e5e5e5e5e5e5', 'LOGIN', '{"device": "mac"}', CURRENT_DATE),
  ('e5e5e5e5-e5e5-e5e5-e5e5-e5e5e5e5e5e5', 'TASK_OPEN', '{"id": "A1"}', CURRENT_DATE),
  ('e5e5e5e5-e5e5-e5e5-e5e5-e5e5e5e5e5e5', 'TASK_COMPLETE', '{"id": "A1"}', CURRENT_DATE),
  ('e5e5e5e5-e5e5-e5e5-e5e5-e5e5e5e5e5e5', 'MATERIAL_OPEN', '{"id": "M1"}', CURRENT_DATE),
  ('e5e5e5e5-e5e5-e5e5-e5e5-e5e5e5e5e5e5', 'LOGIN', '{"device": "mac"}', CURRENT_DATE - INTERVAL '1 days'),
  ('e5e5e5e5-e5e5-e5e5-e5e5-e5e5e5e5e5e5', 'PULSE_SUBMIT', '{}', CURRENT_DATE - INTERVAL '1 days');
