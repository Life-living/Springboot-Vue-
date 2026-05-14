/*
 Navicat Premium Data Transfer

 Source Server         : book_manager
 Source Server Type    : MySQL
 Source Server Version : 90400 (9.4.0)
 Source Host           : localhost:3306
 Source Schema         : bookmysql

 Target Server Type    : MySQL
 Target Server Version : 90400 (9.4.0)
 File Encoding         : 65001

 Date: 22/10/2025 17:00:02
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for book_info
-- ----------------------------
DROP TABLE IF EXISTS `book_info`;
CREATE TABLE `book_info`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '书籍ID',
  `book_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '书名',
  `book_author` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '作者',
  `book_price` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '价格',
  `book_desc` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '书籍简介',
  `book_img` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '封面图片路径',
  `is_borrowed` tinyint NULL DEFAULT 0 COMMENT '是否被借出：0-未借，1-已借',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '书籍信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of book_info
-- ----------------------------
INSERT INTO `book_info` VALUES (1, '三体', '刘慈欣', 39.80, '中国科幻里程碑之作', '/images/santi.jpg', 0);
INSERT INTO `book_info` VALUES (2, '活着', '余华', 25.00, '讲述一个普通人在动荡年代中的命运', '/images/huozhe.jpg', 0);
INSERT INTO `book_info` VALUES (3, '人类简史', '尤瓦尔·赫拉利', 68.00, '从动物到上帝的人类发展史', '/images/human_history.jpg', 0);
INSERT INTO `book_info` VALUES (4, '穷爸爸富爸爸', '罗伯特·清崎', 45.00, '财商启蒙经典读物', '/images/rich_dad.jpg', 0);
INSERT INTO `book_info` VALUES (5, '小王子', '安托万·德·圣-埃克苏佩里', 22.00, '写给成年人的童话', '/images/little_prince.jpg', 0);
INSERT INTO `book_info` VALUES (6, 'JAVA 程序设计', '黑马程序员', 51.00, '学IT找黑马，黑马程序员给你不一样的道路', NULL, 0);
INSERT INTO `book_info` VALUES (10, 'C++ 程序设计', '英雄哥', 50.00, 'ACM金牌 杭电、力扣、洛谷数据结构教', NULL, 0);

-- ----------------------------
-- Table structure for borrow
-- ----------------------------
DROP TABLE IF EXISTS `borrow`;
CREATE TABLE `borrow`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '借阅记录ID',
  `user_id` int NOT NULL COMMENT '用户ID',
  `book_id` int NOT NULL COMMENT '书籍ID',
  `borrow_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '借阅时间',
  `return_time` datetime NULL DEFAULT NULL COMMENT '归还时间（NULL表示未归还）',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user`(`user_id` ASC) USING BTREE,
  INDEX `idx_book`(`book_id` ASC) USING BTREE,
  CONSTRAINT `fk_borrow_book` FOREIGN KEY (`book_id`) REFERENCES `book_info` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_borrow_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 15 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '借阅记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of borrow
-- ----------------------------
INSERT INTO `borrow` VALUES (4, 4, 1, '2025-10-21 12:17:32', '2025-10-21 12:17:45');
INSERT INTO `borrow` VALUES (5, 4, 2, '2025-10-21 12:17:33', '2025-10-21 12:17:46');
INSERT INTO `borrow` VALUES (6, 4, 3, '2025-10-21 12:17:33', '2025-10-21 12:17:46');
INSERT INTO `borrow` VALUES (7, 4, 4, '2025-10-21 12:17:34', '2025-10-21 12:17:47');
INSERT INTO `borrow` VALUES (8, 4, 5, '2025-10-21 12:17:35', '2025-10-21 12:17:47');
INSERT INTO `borrow` VALUES (9, 4, 6, '2025-10-21 12:17:36', '2025-10-21 12:17:48');
INSERT INTO `borrow` VALUES (10, 4, 1, '2025-10-21 21:31:33', '2025-10-21 21:31:58');
INSERT INTO `borrow` VALUES (11, 4, 2, '2025-10-21 21:59:45', '2025-10-21 21:59:58');
INSERT INTO `borrow` VALUES (12, 4, 10, '2025-10-22 16:44:48', '2025-10-22 16:44:55');

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户名',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '密码',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `username`(`username` ASC) USING BTREE,
  INDEX `idx_user_name`(`username` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (4, 'admin', 'admin', '2025-10-19 07:31:57');
INSERT INTO `user` VALUES (6, 'mooc', 'mooc123', '2025-10-22 08:46:36');

-- ----------------------------
-- Table structure for user_token
-- ----------------------------
DROP TABLE IF EXISTS `user_token`;
CREATE TABLE `user_token`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` int NOT NULL COMMENT '关联 user.id',
  `token` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '登录 token',
  `expire_time` datetime NOT NULL COMMENT 'token 过期时间',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `token`(`token` ASC) USING BTREE,
  INDEX `idx_token`(`token` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_expire_time`(`expire_time` ASC) USING BTREE,
  CONSTRAINT `fk_user_token_user_id` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 78 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户登录 token 表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_token
-- ----------------------------
INSERT INTO `user_token` VALUES (72, 4, '1761122516216-49769', '2025-10-23 16:41:56', '2025-10-22 08:41:56');
INSERT INTO `user_token` VALUES (77, 6, '1761123527912-391054', '2025-10-23 16:58:48', '2025-10-22 08:58:48');

SET FOREIGN_KEY_CHECKS = 1;
