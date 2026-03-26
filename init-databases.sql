CREATE USER IF NOT EXISTS 'skillsync'@'%' IDENTIFIED BY 'your_password';
GRANT ALL PRIVILEGES ON skillsync_auth.* TO 'skillsync'@'%';
GRANT ALL PRIVILEGES ON skillsync_user.* TO 'skillsync'@'%';
GRANT ALL PRIVILEGES ON skillsync_skill.* TO 'skillsync'@'%';
GRANT ALL PRIVILEGES ON skillsync_mentor.* TO 'skillsync'@'%';
GRANT ALL PRIVILEGES ON skillsync_session.* TO 'skillsync'@'%';
GRANT ALL PRIVILEGES ON skillsync_group.* TO 'skillsync'@'%';
GRANT ALL PRIVILEGES ON skillsync_review.* TO 'skillsync'@'%';
GRANT ALL PRIVILEGES ON skillsync_notification.* TO 'skillsync'@'%';
FLUSH PRIVILEGES;

-- User Service Schema
CREATE DATABASE IF NOT EXISTS skillsync_user;
USE skillsync_user;

CREATE TABLE IF NOT EXISTS user_profiles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255),
    bio VARCHAR(255),
    profile_image_url VARCHAR(255),
    phone VARCHAR(255),
    created_at DATETIME(6),
    updated_at DATETIME(6)
);

CREATE TABLE IF NOT EXISTS user_skills (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    skill_name VARCHAR(255) NOT NULL,
    proficiency_level VARCHAR(255)
);