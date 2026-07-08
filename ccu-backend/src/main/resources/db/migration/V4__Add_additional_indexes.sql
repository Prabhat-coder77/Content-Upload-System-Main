-- Add additional indexes to users
CREATE INDEX IF NOT EXISTS idx_users_username ON users (username);
CREATE INDEX IF NOT EXISTS idx_users_email ON users (email);

-- Add additional indexes to course_content
CREATE INDEX IF NOT EXISTS idx_course_content_original_file_name ON course_content (original_file_name);
CREATE INDEX IF NOT EXISTS idx_course_content_content_type ON course_content (content_type);
