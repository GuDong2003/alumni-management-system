-- 删除外键约束
ALTER TABLE announcement DROP FOREIGN KEY fk_announcement_created_by;

-- 删除索引
ALTER TABLE announcement DROP INDEX idx_created_by;

-- 删除列
ALTER TABLE announcement DROP COLUMN created_by; 