ALTER TABLE users ADD COLUMN created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL;
ALTER TABLE users ADD COLUMN updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL;

ALTER TABLE course_content ADD COLUMN created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL;
ALTER TABLE course_content ADD COLUMN updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL;

-- Create useful indexes for sorting and filtering
CREATE INDEX idx_users_created_at ON users (created_at);
CREATE INDEX idx_course_content_uploaded_by ON course_content (uploaded_by);
CREATE INDEX idx_course_content_created_at ON course_content (created_at);
