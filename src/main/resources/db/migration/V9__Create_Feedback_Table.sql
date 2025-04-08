-- 创建反馈表
CREATE TABLE IF NOT EXISTS feedback (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '关联用户ID',
    user_name VARCHAR(100) COMMENT '用户姓名',
    title VARCHAR(100) NOT NULL COMMENT '反馈标题',
    content TEXT NOT NULL COMMENT '反馈内容',
    type VARCHAR(20) COMMENT '反馈类型：建议、问题、其他',
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT '状态：待处理、处理中、已解决、已关闭',
    reply TEXT COMMENT '回复内容',
    reply_user_id BIGINT COMMENT '回复人ID',
    reply_user_name VARCHAR(100) COMMENT '回复人姓名',
    reply_time DATETIME COMMENT '回复时间',
    created_at DATETIME NOT NULL COMMENT '创建时间',
    updated_at DATETIME NOT NULL COMMENT '更新时间',
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE
); 