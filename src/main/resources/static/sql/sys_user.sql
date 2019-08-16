/*
 Navicat Premium Data Transfer

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 50725
 Source Host           : 192.168.81.250:3306
 Source Schema         : act

 Target Server Type    : MySQL
 Target Server Version : 50725
 File Encoding         : 65001

 Date: 15/08/2019 10:43:13
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`  (
  `user_name` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '账号',
  `user_alias` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '昵称',
  `user_pwd` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '密码',
  PRIMARY KEY (`user_name`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_bin ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
