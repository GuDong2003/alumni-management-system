-- 修改user表结构
ALTER TABLE user
    DROP COLUMN current_company,
    DROP COLUMN current_position,
    DROP COLUMN industry,
    DROP COLUMN location,
    DROP COLUMN bio;

-- 修改alumni表结构
ALTER TABLE alumni
    DROP COLUMN email,
    DROP COLUMN phone,
    DROP COLUMN name,
    DROP COLUMN student_id,
    DROP COLUMN major,
    DROP COLUMN graduation_year,
    DROP COLUMN gender;

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