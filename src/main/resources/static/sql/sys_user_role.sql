/*
 Navicat Premium Data Transfer

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 50725
 Source Host           : 192.168.81.250:3306
 Source Schema         : zhu

 Target Server Type    : MySQL
 Target Server Version : 50725
 File Encoding         : 65001

 Date: 15/08/2019 11:08:15
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for sys_user_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role`  (
  `user_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `role_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  PRIMARY KEY (`user_name`, `role_name`) USING BTREE,
  INDEX `fk2`(`role_name`) USING BTREE,
  CONSTRAINT `fk1` FOREIGN KEY (`user_name`) REFERENCES `sys_user` (`user_name`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk2` FOREIGN KEY (`role_name`) REFERENCES `sys_role` (`role_name`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_bin ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
