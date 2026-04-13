-- password для всех: "password123" (bcrypt hash: $2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.TVuHOnu)
-- Примечание: В реальномBCrypt хэши могут отличаться, используем стандартный для демо.

INSERT INTO users (id, email, password_hash, full_name, role, avatar_initials) VALUES
  ('00000000-0000-0000-0000-000000000001', 'employee@techpulse.dev', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.TVuHOnu', 'Алексей Петров',  'EMPLOYEE', 'АП'),
  ('00000000-0000-0000-0000-000000000002', 'manager@techpulse.dev',  '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.TVuHOnu', 'Дмитрий Ким',     'MANAGER',  'ДК'),
  ('00000000-0000-0000-0000-000000000003', 'hr@techpulse.dev',       '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.TVuHOnu', 'Анна Смирнова',   'HR',       'АС');

-- Onboarding Plan
INSERT INTO onboarding_plans (id, employee_id, manager_id, start_date, day_current, total_days) VALUES
  ('00000000-0000-0000-0000-000000000011', '00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000002', CURRENT_DATE - INTERVAL '10 days', 10, 90);

-- Tasks
INSERT INTO tasks (plan_id, title, description, due_date, completed, stage) VALUES
  ('00000000-0000-0000-0000-000000000011', 'Настроить рабочее окружение', 'Установить IDE, Docker, Git', CURRENT_DATE - INTERVAL '9 days', true, 1),
  ('00000000-0000-0000-0000-000000000011', 'Ознакомиться с документацией', 'Прочитать Wiki проекта', CURRENT_DATE - INTERVAL '8 days', true, 1),
  ('00000000-0000-0000-0000-000000000011', 'Первый коммит', 'Исправить минорную багу', CURRENT_DATE - INTERVAL '5 days', false, 1),
  ('00000000-0000-0000-0000-000000000011', 'Встреча с ментором', 'Обсудить цели на первый месяц', CURRENT_DATE + INTERVAL '2 days', false, 1),
  ('00000000-0000-0000-0000-000000000011', 'Code Review', 'Пройти ревью своего первого PR', CURRENT_DATE + INTERVAL '5 days', false, 1);

-- Materials
INSERT INTO materials (title, description, icon, estimated_minutes, stage) VALUES
  ('Welcome to Techpulse', 'General overview of our company and culture', '🏠', 15, 1),
  ('Java Best Practices', 'How we write clean code in Java', '☕', 45, 1),
  ('Git Workflow', 'Branching strategy and PR guidelines', '🌿', 20, 1),
  ('Spring Boot Deep Dive', 'Advanced patterns in our backend', '🍃', 60, 2),
  ('Monitoring & Logging', 'Using ELK and Prometheus', '📊', 30, 2),
  ('Team Management', 'For new managers and leads', '👥', 40, 3);

-- Risk Score
INSERT INTO risk_scores (user_id, score, level, signals) VALUES
  ('00000000-0000-0000-0000-000000000001', 18, 'GREEN', '{"active_tasks": 3, "last_login": "today"}');

-- Notification
INSERT INTO notifications (manager_id, employee_id, type, message) VALUES
  ('00000000-0000-0000-0000-000000000002', '00000000-0000-0000-0000-000000000001', 'RISK_AMBER', 'Employee Aleksey Petrov has 2 overdue tasks.');
