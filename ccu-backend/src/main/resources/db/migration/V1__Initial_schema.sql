CREATE TABLE users (
    id UUID PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL
);

CREATE TABLE course_content (
    id UUID PRIMARY KEY,
    original_file_name VARCHAR(255) NOT NULL,
    content_type VARCHAR(100) NOT NULL,
    extension VARCHAR(20) NOT NULL,
    size_bytes BIGINT NOT NULL,
    upload_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    storage_key VARCHAR(500) NOT NULL,
    storage_type VARCHAR(20) NOT NULL,
    uploaded_by VARCHAR(255) NOT NULL,
    CONSTRAINT fk_course_content_user FOREIGN KEY (uploaded_by) REFERENCES users (username)
);
