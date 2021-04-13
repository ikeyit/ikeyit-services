CREATE DATABASE `passport`;
USE `passport`;
CREATE TABLE `address` (
                           `id` bigint NOT NULL AUTO_INCREMENT COMMENT '地址ID',
                           `userId` bigint NOT NULL DEFAULT '0' COMMENT '用户ID',
                           `name` varchar(50) NOT NULL DEFAULT '' COMMENT '联系人姓名',
                           `phone` varchar(50) NOT NULL DEFAULT '' COMMENT '联系电话',
                           `province` varchar(255) NOT NULL DEFAULT '',
                           `city` varchar(255) NOT NULL DEFAULT '',
                           `district` varchar(255) NOT NULL DEFAULT '',
                           `street` varchar(225) NOT NULL DEFAULT '' COMMENT '街道地址',
                           `zipCode` varchar(50) DEFAULT NULL COMMENT '邮编',
                           `preferred` tinyint NOT NULL DEFAULT '0' COMMENT '优先级',
                           `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建日期',
                           `updateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改日期',
                           PRIMARY KEY (`id`),
                           KEY `userId` (`userId`)
) ENGINE=InnoDB AUTO_INCREMENT=100001 DEFAULT CHARSET=utf8 COMMENT='地址表';

CREATE TABLE `message` (
                           `id` bigint NOT NULL AUTO_INCREMENT,
                           `fromId` bigint DEFAULT NULL COMMENT '发送者ID',
                           `toId` bigint NOT NULL COMMENT '接受者ID',
                           `status` int NOT NULL DEFAULT '0' COMMENT '状态',
                           `messageType` int NOT NULL DEFAULT '0' COMMENT '类型',
                           `topic` varchar(64) DEFAULT NULL COMMENT '话题',
                           `content` text COMMENT '内容',
                           `createTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建日期',
                           `updateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改日期',
                           PRIMARY KEY (`id`),
                           KEY `index2` (`toId`,`messageType`,`status`)
) ENGINE=InnoDB AUTO_INCREMENT=100004 DEFAULT CHARSET=utf8 COMMENT='消息';

CREATE TABLE `permission` (
                              `id` bigint NOT NULL,
                              `name` varchar(45) NOT NULL COMMENT '权限名',
                              `displayName` varchar(45) NOT NULL COMMENT '显示名',
                              `groupName` varchar(45) NOT NULL,
                              `description` varchar(255) DEFAULT NULL,
                              PRIMARY KEY (`id`),
                              UNIQUE KEY `name_UNIQUE` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `role` (
                        `id` bigint NOT NULL AUTO_INCREMENT,
                        `name` varchar(45) NOT NULL COMMENT '角色名，代码里用',
                        `displayName` varchar(45) NOT NULL COMMENT '显示角色名，界面里显示',
                        `description` varchar(255) DEFAULT NULL COMMENT '描述',
                        PRIMARY KEY (`id`),
                        UNIQUE KEY `name_UNIQUE` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `role_permission` (
                                   `roleId` bigint NOT NULL,
                                   `permissionId` bigint NOT NULL,
                                   PRIMARY KEY (`roleId`,`permissionId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `user` (
                        `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
                        `password` char(64) DEFAULT NULL COMMENT '密码，存放MD5后的值',
                        `loginName` char(64) DEFAULT NULL COMMENT '用户名',
                        `mobile` char(32) DEFAULT NULL COMMENT '手机号',
                        `email` char(64) DEFAULT NULL COMMENT 'email地址',
                        `nick` char(64) DEFAULT NULL,
                        `avatar` varchar(255) DEFAULT NULL,
                        `location` varchar(255) DEFAULT NULL,
                        `sex` tinyint(1) DEFAULT NULL,
                        `enabled` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否禁用，杀档, 0: 禁用，1：可用',
                        `verified` tinyint(1) NOT NULL DEFAULT '0' COMMENT '身份是否验证，0：未验证，1：验证',
                        `createTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建日期',
                        `updateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改日期',
                        PRIMARY KEY (`id`),
                        UNIQUE KEY `username` (`loginName`),
                        UNIQUE KEY `mobile` (`mobile`),
                        UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=1000000002 DEFAULT CHARSET=utf8 COMMENT='用户表';

CREATE TABLE `user_connection` (
                                   `userId` char(64) NOT NULL COMMENT '用户在我方的账户名',
                                   `provider` varchar(100) NOT NULL COMMENT '第三方提供者，比如taobao,weibo,qq',
                                   `providerUserId` varchar(100) NOT NULL COMMENT '用户在第三方的OPENID',
                                   `providerUserName` varchar(100) DEFAULT NULL COMMENT '用户在第三方的unionName',
                                   `providerUserNick` varchar(100) DEFAULT NULL,
                                   `providerUserAvatar` varchar(255) DEFAULT NULL COMMENT '用户在第三方的头像',
                                   `createTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建日期',
                                   `updateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改日期',
                                   PRIMARY KEY (`userId`,`provider`,`providerUserId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='第三方用户绑定表';

CREATE TABLE `user_permission` (
                                   `userId` bigint NOT NULL,
                                   `permissionId` bigint NOT NULL,
                                   PRIMARY KEY (`userId`,`permissionId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `user_role` (
                             `userId` bigint NOT NULL,
                             `roleId` bigint NOT NULL,
                             PRIMARY KEY (`userId`,`roleId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `weixin_client` (
                                 `appId` varchar(32) NOT NULL,
                                 `appName` varchar(32) NOT NULL,
                                 `unionName` varchar(32) NOT NULL,
                                 `appSecret` varchar(45) NOT NULL,
                                 `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                 `updateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                 PRIMARY KEY (`appId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `weixin_connection` (
                                     `userId` bigint NOT NULL COMMENT '用户在我方的账户名',
                                     `appId` varchar(32) NOT NULL COMMENT '微信应用名称',
                                     `openId` varchar(128) NOT NULL COMMENT 'openId',
                                     `unionId` varchar(128) NOT NULL COMMENT 'unionId',
                                     `createTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建日期',
                                     `updateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改日期',
                                     PRIMARY KEY (`userId`,`appId`),
                                     UNIQUE KEY `openId` (`openId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='微信用户绑定表';


INSERT INTO `user` (`id`, `password`, `loginName`, `mobile`, `email`, `nick`, `avatar`, `location`, `sex`, `enabled`,
                    `verified`)
VALUES ('1', '$2a$10$7euayjUpvbMPdj2rG1H2ueysP/AezgRaHlDAstZgN5Cw9Li/PXwOi', 'ikeyit', '10000000000', 'ikeyit@qq.com',
        '盘古',
        'http://thirdwx.qlogo.cn/mmopen/vi_32/Q0j4TwGTfTKeRIqwBBgdTiaF3DicEnZMdee9KrP5fibNX8iahk3CNFc4gF5Gnb9Unu3lmsQdmeO0CSYaIoH1tIb3xQ/132',
        '上天', '1', '1', '1');
INSERT INTO `user` (`id`, `password`, `loginName`, `mobile`, `email`, `nick`, `avatar`, `location`, `sex`, `enabled`,
                    `verified`)
VALUES ('2', '$2a$10$7euayjUpvbMPdj2rG1H2ueysP/AezgRaHlDAstZgN5Cw9Li/PXwOi', 'super', '10000000001', 'ikeyit@foxmail.com',
        '元始天尊',
        'http://thirdwx.qlogo.cn/mmopen/vi_32/Q0j4TwGTfTKeRIqwBBgdTiaF3DicEnZMdee9KrP5fibNX8iahk3CNFc4gF5Gnb9Unu3lmsQdmeO0CSYaIoH1tIb3xQ/132',
        '上天', '0', '1', '1');

INSERT INTO `role` (`id`, `name`, `displayName`)
VALUES ('1', 'seller', '卖家');
INSERT INTO `role` (`id`, `name`, `displayName`)
VALUES ('2', 'super', '平台管理员');

INSERT INTO `user_role` (`userId`, `roleId`)
VALUES ('1', '1');
INSERT INTO `user_role` (`userId`, `roleId`)
VALUES ('1', '2');
INSERT INTO `user_role` (`userId`, `roleId`)
VALUES ('2', '1');
INSERT INTO `user_role` (`userId`, `roleId`)
VALUES ('2', '2');

INSERT INTO `user` (`id`, `password`, `loginName`, `mobile`, `email`, `nick`, `avatar`, `location`, `sex`, `enabled`,
                    `verified`)
VALUES ('3', '$2a$10$7euayjUpvbMPdj2rG1H2ueysP/AezgRaHlDAstZgN5Cw9Li/PXwOi', 'demo', '10000000002', 'demo@demo',
           '演示账户',
           'http://thirdwx.qlogo.cn/mmopen/vi_32/Q0j4TwGTfTKeRIqwBBgdTiaF3DicEnZMdee9KrP5fibNX8iahk3CNFc4gF5Gnb9Unu3lmsQdmeO0CSYaIoH1tIb3xQ/132',
           '上天', '0', '1', '1');

INSERT INTO `user_role` (`userId`, `roleId`)
VALUES ('3', '1');