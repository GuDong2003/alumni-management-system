-- 创建临时表存储校友信息
CREATE TABLE temp_alumni_info (
    user_id BIGINT PRIMARY KEY,
    current_company VARCHAR(100),
    current_position VARCHAR(100),
    industry VARCHAR(50),
    location VARCHAR(100),
    bio TEXT,
    birth_date DATE,
    active TINYINT(1),
    last_activity_time DATETIME(6),
    created_at DATETIME(6),
    updated_at DATETIME(6)
);

-- 将校友信息从alumni表复制到临时表
INSERT INTO temp_alumni_info (
    user_id, current_company, current_position, industry, location, bio,
    birth_date, active, last_activity_time, created_at, updated_at
)
SELECT 
    user_id, current_company, current_position, industry, location, bio,
    birth_date, active, last_activity_time, created_at, updated_at
FROM alumni;

-- 修改alumni表结构
ALTER TABLE alumni
    DROP COLUMN name,
    DROP COLUMN student_id,
    DROP COLUMN major,
    DROP COLUMN graduation_year,
    DROP COLUMN gender;

-- 清空alumni表
TRUNCATE TABLE alumni;

-- 从临时表恢复校友信息
INSERT INTO alumni (
    user_id, current_company, current_position, industry, location, bio,
    birth_date, active, last_activity_time, created_at, updated_at
)
SELECT 
    user_id, current_company, current_position, industry, location, bio,
    birth_date, active, last_activity_time, created_at, updated_at
FROM temp_alumni_info;

-- 删除临时表
DROP TABLE temp_alumni_info;

-- 添加新的索引
CREATE INDEX idx_alumni_user_id ON alumni(user_id);
CREATE INDEX idx_alumni_active ON alumni(active);
CREATE INDEX idx_alumni_last_activity ON alumni(last_activity_time);

-- 添加外键约束
ALTER TABLE alumni
    ADD CONSTRAINT fk_alumni_user
    FOREIGN KEY (user_id)
    REFERENCES user(id)
    ON DELETE CASCADE
    ON UPDATE CASCADE; 