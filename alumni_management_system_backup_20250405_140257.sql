-- MySQL dump 10.13  Distrib 8.4.4, for macos15 (arm64)
--
-- Host: localhost    Database: alumni_management_system
-- ------------------------------------------------------
-- Server version	8.4.4

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `activity`
--

DROP TABLE IF EXISTS `activity`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `activity` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `title` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '活动标题',
  `description` text COLLATE utf8mb4_unicode_ci COMMENT '活动描述',
  `type` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '活动类型',
  `status` enum('DRAFT','PUBLISHED','ONGOING','COMPLETED','CANCELLED') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'DRAFT' COMMENT '活动状态',
  `start_time` datetime(6) NOT NULL COMMENT '开始时间',
  `end_time` datetime(6) NOT NULL COMMENT '结束时间',
  `location` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '活动地点',
  `max_participants` int NOT NULL COMMENT '最大参与人数',
  `current_participants` int NOT NULL DEFAULT '0' COMMENT '当前参与人数',
  `created_at` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT '创建时间',
  `updated_at` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6) COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_type` (`type`),
  KEY `idx_status` (`status`),
  KEY `idx_start_time` (`start_time`),
  KEY `idx_end_time` (`end_time`),
  KEY `idx_location` (`location`)
) ENGINE=InnoDB AUTO_INCREMENT=402 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='活动表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `activity`
--

LOCK TABLES `activity` WRITE;
/*!40000 ALTER TABLE `activity` DISABLE KEYS */;
INSERT INTO `activity` VALUES (1,'2024年春季校友返校日','欢迎各位校友回到母校，共叙情谊，共话发展。活动包括校园参观、座谈会、晚宴等。','REUNION','PUBLISHED','2024-03-15 09:00:00.000000','2024-03-15 17:00:00.000000','学校大礼堂',200,0,'2025-04-04 17:06:11.788169','2025-04-04 19:27:32.000000'),(2,'校友创业分享会','邀请成功创业的校友分享创业经验，为在校学生和年轻校友提供创业指导。','SHARING','PUBLISHED','2024-04-20 14:00:00.000000','2024-04-20 17:00:00.000000','创业中心报告厅',100,0,'2025-04-04 17:06:11.788169','2025-04-04 19:23:02.558024'),(3,'校友企业招聘会','为校友企业提供招聘平台，为在校学生和校友提供就业机会。','CAREER','PUBLISHED','2024-05-10 09:00:00.000000','2024-05-10 16:00:00.000000','学生活动中心',500,0,'2025-04-04 17:06:11.788169','2025-04-04 19:23:04.151175'),(4,'校友足球友谊赛','校友足球俱乐部组织的友谊赛，欢迎所有热爱足球的校友参加。','SPORTS','PUBLISHED','2024-06-08 15:00:00.000000','2024-06-08 18:00:00.000000','学校足球场',40,0,'2025-04-04 17:06:11.788169','2025-04-04 19:23:05.870017'),(5,'校友导师计划启动仪式','启动校友导师计划，为在校学生提供职业发展指导。','MENTORING','ONGOING','2024-03-01 10:00:00.000000','2024-03-01 12:00:00.000000','校友之家',50,0,'2025-04-04 17:06:11.788169','2025-04-04 19:23:06.921273'),(6,'校友捐赠仪式','感谢校友对学校的捐赠，并举行捐赠仪式。','DONATION','COMPLETED','2024-02-20 14:00:00.000000','2024-02-20 16:00:00.000000','行政楼会议室',30,0,'2025-04-04 17:06:11.788169','2025-04-04 19:23:07.733391'),(7,'校友艺术展','展示校友的艺术作品，促进校友文化交流。','CULTURE','DRAFT','2024-07-15 09:00:00.000000','2024-07-20 17:00:00.000000','艺术展览馆',300,0,'2025-04-04 17:06:11.788169','2025-04-04 19:23:08.854523'),(8,'校友科技创新论坛','探讨科技创新发展趋势，促进校友企业间的合作。','FORUM','CANCELLED','2024-08-10 09:00:00.000000','2024-08-11 17:00:00.000000','科技楼报告厅',150,0,'2025-04-04 17:06:11.788169','2025-04-04 19:23:09.511312'),(9,'校友迎新晚会','欢迎新一届毕业生加入校友会，促进新老校友交流。','WELCOME','PUBLISHED','2024-09-15 19:00:00.000000','2024-09-15 22:00:00.000000','学生活动中心',300,0,'2025-04-04 17:06:11.788169','2025-04-04 19:23:23.473427'),(10,'校友年度大会','总结年度工作，规划未来发展，选举新一届校友会理事。','ANNUAL','PUBLISHED','2024-12-20 09:00:00.000000','2024-12-20 17:00:00.000000','学校大礼堂',500,0,'2025-04-04 17:06:11.788169','2025-04-04 19:23:21.194165'),(400,'111','111','SHARING','DRAFT','2025-03-31 16:00:00.000000','2025-04-02 16:00:00.000000','111',50,0,'2025-04-04 19:33:51.000000','2025-04-04 19:33:51.000000');
/*!40000 ALTER TABLE `activity` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `activity_participant`
--

DROP TABLE IF EXISTS `activity_participant`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `activity_participant` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `activity_id` bigint NOT NULL COMMENT '活动ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `status` enum('PENDING','APPROVED','REJECTED','CANCELLED') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'PENDING' COMMENT '参与状态',
  `created_at` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT '创建时间',
  `updated_at` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6) COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_activity_user` (`activity_id`,`user_id`),
  KEY `user_id` (`user_id`),
  KEY `idx_status` (`status`),
  CONSTRAINT `activity_participant_ibfk_1` FOREIGN KEY (`activity_id`) REFERENCES `activity` (`id`) ON DELETE CASCADE,
  CONSTRAINT `activity_participant_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=41 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='活动参与者表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `activity_participant`
--

LOCK TABLES `activity_participant` WRITE;
/*!40000 ALTER TABLE `activity_participant` DISABLE KEYS */;
/*!40000 ALTER TABLE `activity_participant` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `alumni`
--

DROP TABLE IF EXISTS `alumni`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `alumni` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '校友姓名',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `student_id` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '学号',
  `major` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '专业',
  `graduation_year` int NOT NULL COMMENT '毕业年份',
  `current_company` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '当前公司',
  `current_position` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '当前职位',
  `industry` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '所属行业',
  `location` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '所在地',
  `bio` text COLLATE utf8mb4_unicode_ci COMMENT '个人简介',
  `gender` enum('男','女') COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '性别',
  `birth_date` date DEFAULT NULL COMMENT '出生日期',
  `active` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否活跃',
  `last_activity_time` datetime(6) DEFAULT NULL COMMENT '最后活动时间',
  `created_at` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT '创建时间',
  `updated_at` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6) COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_student_id` (`student_id`),
  KEY `user_id` (`user_id`),
  KEY `idx_industry` (`industry`),
  KEY `idx_location` (`location`),
  KEY `idx_active` (`active`),
  CONSTRAINT `alumni_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='校友信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `alumni`
--

LOCK TABLES `alumni` WRITE;
/*!40000 ALTER TABLE `alumni` DISABLE KEYS */;
INSERT INTO `alumni` VALUES (1,'张明',1,'20180001','计算机科学与技术',2022,'校友管理系统','系统管理员','教育','北京','负责校友管理系统的整体运营','男','2000-01-01',1,NULL,'2025-04-04 01:27:19.456132','2025-04-05 13:28:26.000000'),(2,'李华',2,'20180002','软件工程',2022,'校友管理系统','活动管理员','教育','北京','负责校友活动的组织和管理','男','2000-01-01',1,NULL,'2025-04-04 01:27:19.456132','2025-04-04 23:51:57.605663'),(3,'王芳',3,'20180003','信息安全',2022,'校友管理系统','内容管理员','教育','北京','负责校友信息的管理和维护','女','2000-01-01',1,NULL,'2025-04-04 01:27:19.456132','2025-04-05 01:03:24.000000'),(4,'刘强',4,'20180004','计算机科学与技术',2022,'字节跳动','后端工程师','互联网','北京','热爱编程，专注于后端开发','男','2000-01-01',1,NULL,'2025-04-04 01:27:19.456132','2025-04-04 23:51:57.607588'),(5,'陈静',5,'20180005','软件工程',2022,'美团','全栈工程师','互联网','北京','全栈开发，热爱技术','女','2000-01-01',1,NULL,'2025-04-04 01:27:19.456132','2025-04-05 01:03:36.000000'),(6,'赵阳',6,'20180006','信息安全',2022,'奇虎360','安全工程师','互联网','北京','专注于网络安全研究','男','2000-01-01',1,NULL,'2025-04-04 01:27:19.456132','2025-04-04 23:51:57.610516'),(7,'周杰',7,'20180007','计算机科学与技术',2022,'小米科技','移动开发工程师','互联网','北京','专注于移动应用开发','男','2000-01-01',1,NULL,'2025-04-04 01:27:19.456132','2025-04-04 23:51:57.612051'),(8,'吴婷',8,'20180008','软件工程',2022,'滴滴出行','前端工程师','互联网','北京','热爱前端开发，追求用户体验','女','2000-01-01',1,NULL,'2025-04-04 01:27:19.456132','2025-04-05 01:03:43.000000'),(9,'郑伟',9,'20180009','信息安全',2022,'京东科技','安全研究员','互联网','北京','专注于安全漏洞研究','男','2000-01-01',1,NULL,'2025-04-04 01:27:19.456132','2025-04-04 23:51:57.616895'),(10,'孙莉',10,'20180010','计算机科学与技术',2022,'百度','AI工程师','互联网','北京','专注于人工智能和机器学习','女','2000-01-01',1,NULL,'2025-04-04 01:27:19.456132','2025-04-05 01:03:50.000000'),(11,'杨帆',11,'2021105220116','软件工程',2025,NULL,NULL,NULL,NULL,NULL,'男',NULL,0,NULL,'2025-04-04 19:44:47.061162','2025-04-05 01:04:09.000000'),(12,'胡顺其',14,'2021105220115','软件工程',2025,NULL,NULL,NULL,NULL,NULL,'男',NULL,0,NULL,'2025-04-05 00:02:00.389478','2025-04-05 01:06:14.000000'),(13,'无名氏',15,'2022105211025','通信工程',2026,NULL,NULL,NULL,NULL,NULL,'女',NULL,0,NULL,'2025-04-05 00:53:01.060885','2025-04-05 01:06:25.000000'),(14,'测试',16,'2023105220114','通信工程',2027,NULL,NULL,NULL,NULL,NULL,'男',NULL,0,NULL,'2025-04-05 01:06:55.806456','2025-04-05 01:07:13.000000'),(15,'咕咚',17,'2020105220114','通信工程',2024,NULL,NULL,NULL,NULL,NULL,'男',NULL,0,NULL,'2025-04-05 01:09:44.647541','2025-04-05 01:09:58.000000'),(16,NULL,18,'2019105220114','通信工程',2023,NULL,NULL,NULL,NULL,NULL,NULL,NULL,0,NULL,'2025-04-05 01:14:11.324537','2025-04-05 01:14:11.324549');
/*!40000 ALTER TABLE `alumni` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `donation`
--

DROP TABLE IF EXISTS `donation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `donation` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `donor_id` bigint NOT NULL COMMENT '捐赠人ID',
  `amount` decimal(10,2) NOT NULL COMMENT '捐赠金额',
  `donation_type` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '捐赠类型（现金/物资/其他）',
  `donation_date` datetime NOT NULL COMMENT '捐赠日期',
  `description` text COLLATE utf8mb4_unicode_ci COMMENT '捐赠描述',
  `status` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'PENDING' COMMENT '状态（PENDING待确认/APPROVED已确认/REJECTED已拒绝）',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `donor_id` (`donor_id`),
  CONSTRAINT `donation_ibfk_1` FOREIGN KEY (`donor_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='捐赠记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `donation`
--

LOCK TABLES `donation` WRITE;
/*!40000 ALTER TABLE `donation` DISABLE KEYS */;
INSERT INTO `donation` VALUES (1,1,1000.00,'现金','2025-04-04 22:53:55','3748378','APPROVED','2025-04-04 22:53:56','2025-04-04 23:27:19'),(4,2,5000.00,'物资','2025-04-04 23:25:02','111','APPROVED','2025-04-04 23:25:05','2025-04-04 23:28:46'),(5,3,8000.00,'其他','2025-04-04 23:26:35','257247','PENDING','2025-04-04 23:26:39','2025-04-04 23:45:23'),(6,4,7000.00,'现金','2025-05-03 00:00:00','698383','REJECTED','2025-04-04 23:29:30','2025-04-04 23:29:35');
/*!40000 ALTER TABLE `donation` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户名',
  `password` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '密码',
  `email` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '邮箱',
  `phone` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '手机号',
  `role` enum('SUPER_ADMIN','ADMIN','ALUMNI') COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '角色',
  `status` enum('ACTIVE','DISABLED') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'ACTIVE' COMMENT '状态',
  `student_id` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '学号',
  `major` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '专业',
  `graduation_year` int NOT NULL COMMENT '毕业年份',
  `created_at` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT '创建时间',
  `updated_at` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6) COMMENT '更新时间',
  `last_login` datetime(6) DEFAULT NULL COMMENT '最后登录时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_username` (`username`),
  UNIQUE KEY `UK_student_id` (`student_id`),
  UNIQUE KEY `UK_email` (`email`),
  UNIQUE KEY `UK_phone` (`phone`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1,'superadmin','123456','superadmin@example.com','13800000001','SUPER_ADMIN','ACTIVE','20180001','计算机科学与技术',2022,'2025-04-04 01:27:19.455403','2025-04-05 13:28:26.000000',NULL),(2,'admin1','123456','admin1@example.com','13800000002','ADMIN','ACTIVE','20180002','软件工程',2022,'2025-04-04 01:27:19.455403','2025-04-04 13:48:43.000000',NULL),(3,'admin2','123456','admin2@example.com','13800000003','ADMIN','ACTIVE','20180003','信息安全',2022,'2025-04-04 01:27:19.455403','2025-04-05 01:03:24.000000',NULL),(4,'alumni1','123456','alumni1@example.com','13800000004','ALUMNI','ACTIVE','20180004','计算机科学与技术',2022,'2025-04-04 01:27:19.455403','2025-04-04 13:40:21.000000',NULL),(5,'alumni2','123456','alumni2@example.com','13800000005','ALUMNI','ACTIVE','20180005','软件工程',2022,'2025-04-04 01:27:19.455403','2025-04-05 01:03:36.000000',NULL),(6,'alumni3','123456','alumni3@example.com','13800000006','ALUMNI','ACTIVE','20180006','信息安全',2022,'2025-04-04 01:27:19.455403','2025-04-04 01:27:19.455403',NULL),(7,'alumni4','123456','alumni4@example.com','13800000007','ALUMNI','ACTIVE','20180007','计算机科学与技术',2022,'2025-04-04 01:27:19.455403','2025-04-04 01:27:19.455403',NULL),(8,'alumni5','123456','alumni5@example.com','13800000008','ALUMNI','ACTIVE','20180008','软件工程',2022,'2025-04-04 01:27:19.455403','2025-04-05 01:03:43.000000',NULL),(9,'alumni6','123456','alumni6@example.com','13800000009','ALUMNI','ACTIVE','20180009','信息安全',2022,'2025-04-04 01:27:19.455403','2025-04-04 01:27:19.455403',NULL),(10,'alumni7','123456','alumni7@example.com','13800000010','ALUMNI','ACTIVE','20180010','计算机科学与技术',2022,'2025-04-04 01:27:19.455403','2025-04-05 01:03:50.000000',NULL),(11,'gudong','123456',NULL,NULL,'ALUMNI','ACTIVE','2021105220110','软件工程',2025,'2025-04-04 19:44:47.058253','2025-04-05 01:04:09.000000',NULL),(14,'gudong226','123456','1841293753@qq.com','13213882738','ALUMNI','ACTIVE','2021105220115','软件工程',2025,'2025-04-05 00:02:00.384114','2025-04-05 01:06:14.000000',NULL),(15,'asdfghjkl','123456',NULL,NULL,'ALUMNI','ACTIVE','2022105211025','通信工程',2026,'2025-04-05 00:53:01.050586','2025-04-05 13:49:02.587621',NULL),(16,'ceshi111','123456',NULL,NULL,'ALUMNI','ACTIVE','2023105220114','通信工程',2027,'2025-04-05 01:06:55.796807','2025-04-05 01:07:13.000000',NULL),(17,'test11','123456',NULL,NULL,'ALUMNI','ACTIVE','2020105220114','通信工程',2024,'2025-04-05 01:09:44.640341','2025-04-05 01:09:58.000000',NULL),(18,'test1112','123456',NULL,NULL,'ALUMNI','ACTIVE','2019105220114','通信工程',2023,'2025-04-05 01:14:11.269428','2025-04-05 01:14:11.269501',NULL);
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-04-05 14:03:49
