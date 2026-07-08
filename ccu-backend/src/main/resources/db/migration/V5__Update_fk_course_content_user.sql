ALTER TABLE course_content DROP CONSTRAINT fk_course_content_user;
ALTER TABLE course_content ADD CONSTRAINT fk_course_content_user FOREIGN KEY (uploaded_by) REFERENCES users (email);
