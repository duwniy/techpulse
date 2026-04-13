-- Add missing avatar_initials column to users table if it doesn't exist
DO $$ 
BEGIN 
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='users' AND column_name='avatar_initials') THEN
        ALTER TABLE users ADD COLUMN avatar_initials VARCHAR(5);
    END IF;
END $$;
