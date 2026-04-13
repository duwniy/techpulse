-- Cleanup incompatible users table if it exists as bigint
DO $$ 
BEGIN 
    IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='users' AND column_name='id' AND data_type<>'uuid') THEN
        DROP TABLE IF EXISTS users CASCADE;
    END IF;
END $$;

-- Пользователи
CREATE TABLE IF NOT EXISTS users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL CHECK (role IN ('EMPLOYEE', 'MANAGER', 'HR')),
    avatar_initials VARCHAR(5),
    created_at TIMESTAMP DEFAULT NOW()
);

-- Онбординг-маршруты
CREATE TABLE IF NOT EXISTS onboarding_plans (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    employee_id UUID REFERENCES users(id) ON DELETE CASCADE,
    manager_id UUID REFERENCES users(id),
    start_date DATE NOT NULL,
    day_current INT DEFAULT 1,
    total_days INT DEFAULT 90,
    created_at TIMESTAMP DEFAULT NOW()
);

-- Задачи
CREATE TABLE IF NOT EXISTS tasks (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    plan_id UUID REFERENCES onboarding_plans(id) ON DELETE CASCADE,
    title VARCHAR(500) NOT NULL,
    description TEXT,
    due_date DATE,
    completed BOOLEAN DEFAULT FALSE,
    completed_at TIMESTAMP,
    stage INT DEFAULT 1,
    created_at TIMESTAMP DEFAULT NOW()
);

-- События активности (для Risk Engine)
CREATE TABLE IF NOT EXISTS activity_events (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID REFERENCES users(id) ON DELETE CASCADE,
    event_type VARCHAR(50) NOT NULL,
    -- LOGIN, TASK_OPEN, TASK_COMPLETE, MATERIAL_OPEN, PULSE_SUBMIT
    metadata JSONB,
    created_at TIMESTAMP DEFAULT NOW()
);

-- Risk Score (пересчитывается ежедневно)
CREATE TABLE IF NOT EXISTS risk_scores (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID REFERENCES users(id) ON DELETE CASCADE,
    score INT NOT NULL DEFAULT 0,
    level VARCHAR(10) NOT NULL DEFAULT 'GREEN',
    -- GREEN (0-30), AMBER (31-60), RED (61-100)
    signals JSONB,
    -- массив активных сигналов с весами
    calculated_at TIMESTAMP DEFAULT NOW()
);

-- Уведомления менеджеру
CREATE TABLE IF NOT EXISTS notifications (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    manager_id UUID REFERENCES users(id) ON DELETE CASCADE,
    employee_id UUID REFERENCES users(id) ON DELETE CASCADE,
    type VARCHAR(50) NOT NULL,
    -- RISK_RED, RISK_AMBER, TASK_OVERDUE
    message TEXT NOT NULL,
    dismissed BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT NOW()
);

-- Материалы
CREATE TABLE IF NOT EXISTS materials (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    title VARCHAR(500) NOT NULL,
    description TEXT,
    icon VARCHAR(10),
    estimated_minutes INT,
    stage INT DEFAULT 1,
    created_at TIMESTAMP DEFAULT NOW()
);

-- Прогресс по материалам
CREATE TABLE IF NOT EXISTS material_progress (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID REFERENCES users(id) ON DELETE CASCADE,
    material_id UUID REFERENCES materials(id) ON DELETE CASCADE,
    status VARCHAR(20) DEFAULT 'PENDING',
    -- PENDING, IN_PROGRESS, DONE
    updated_at TIMESTAMP DEFAULT NOW(),
    UNIQUE(user_id, material_id)
);

-- Пульс-опросы
CREATE TABLE IF NOT EXISTS pulse_responses (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID REFERENCES users(id) ON DELETE CASCADE,
    workload_rating INT CHECK (workload_rating BETWEEN 1 AND 5),
    open_text TEXT,
    created_at TIMESTAMP DEFAULT NOW()
);
