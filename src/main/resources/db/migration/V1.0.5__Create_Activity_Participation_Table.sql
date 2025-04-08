-- 创建活动参与记录表
CREATE TABLE IF NOT EXISTS `activity_participation` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `activity_id` BIGINT NOT NULL,
    `user_id` BIGINT NOT NULL,
    `status` VARCHAR(20) DEFAULT 'REGISTERED',
    `register_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (`activity_id`) REFERENCES `activity`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) ON DELETE CASCADE,
    UNIQUE KEY `unique_activity_user` (`activity_id`, `user_id`),
    INDEX `idx_activity_status` (`activity_id`, `status`),
    INDEX `idx_user_activity` (`user_id`, `activity_id`)
);

-- 创建活动状态变化触发器
DELIMITER //
CREATE TRIGGER `after_activity_status_update`
AFTER UPDATE ON `activity`
FOR EACH ROW
BEGIN
    IF NEW.status = 'CANCELLED' THEN
        UPDATE `activity_participation`
        SET `status` = 'CANCELLED',
            `updated_at` = CURRENT_TIMESTAMP
        WHERE `activity_id` = NEW.id
        AND `status` != 'CANCELLED';
    ELSEIF NEW.status = 'COMPLETED' THEN
        UPDATE `activity_participation`
        SET `status` = 'COMPLETED',
            `updated_at` = CURRENT_TIMESTAMP
        WHERE `activity_id` = NEW.id
        AND `status` = 'CONFIRMED';
    END IF;
END//
DELIMITER ;

-- 创建活动删除触发器
DELIMITER //
CREATE TRIGGER `before_activity_delete`
BEFORE DELETE ON `activity`
FOR EACH ROW
BEGIN
    DELETE FROM `activity_participation`
    WHERE `activity_id` = OLD.id;
END//
DELIMITER ; 