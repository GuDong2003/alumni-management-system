-- 创建捐赠记录表
CREATE TABLE IF NOT EXISTS donation (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    donor_id BIGINT NOT NULL COMMENT '捐赠人ID',
    amount DECIMAL(10,2) NOT NULL COMMENT '捐赠金额',
    donation_type VARCHAR(50) NOT NULL COMMENT '捐赠类型（现金/物资/其他）',
    donation_date DATETIME NOT NULL COMMENT '捐赠日期',
    description TEXT COMMENT '捐赠描述',
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT '状态（PENDING待确认/APPROVED已确认/REJECTED已拒绝）',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (donor_id) REFERENCES user(id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='捐赠记录表';

-- 创建索引
CREATE INDEX idx_donor_id ON donation(donor_id);
CREATE INDEX idx_donation_date ON donation(donation_date);
CREATE INDEX idx_status ON donation(status);

-- 插入测试数据
INSERT INTO donation (donor_id, amount, donation_type, donation_date, description, status) VALUES
(1, 1000.00, '现金', NOW(), '测试捐赠1', 'PENDING'),
(2, 2000.00, '物资', NOW(), '测试捐赠2', 'APPROVED'),
(3, 3000.00, '其他', NOW(), '测试捐赠3', 'REJECTED'); 