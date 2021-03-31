CREATE DATABASE `passport`;
USE `passport`;
CREATE TABLE `user`
(
    `id`         bigint(20) NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `password`   char(64)            DEFAULT NULL COMMENT '密码，存放MD5后的值',
    `loginName`  char(64)            DEFAULT NULL COMMENT '用户名',
    `mobile`     char(32)            DEFAULT NULL COMMENT '手机号',
    `email`      char(64)            DEFAULT NULL COMMENT 'email地址',
    `nick`       char(64)            DEFAULT NULL,
    `avatar`     varchar(255)        DEFAULT NULL,
    `location`   varchar(255)        DEFAULT NULL,
    `sex`        tinyint(1)          DEFAULT NULL,
    `enabled`    tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否禁用，杀档, 0: 禁用，1：可用',
    `verified`   tinyint(1) NOT NULL DEFAULT '0' COMMENT '身份是否验证，0：未验证，1：验证',
    `createTime` timestamp  NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建日期',
    `updateTime` timestamp  NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改日期',
    PRIMARY KEY (`id`),
    UNIQUE KEY `username` (`loginName`),
    UNIQUE KEY `mobile` (`mobile`),
    UNIQUE KEY `email` (`email`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1000000000
  DEFAULT CHARSET = utf8 COMMENT ='用户表';

CREATE TABLE `user_connection`
(
    `userId`             char(64)     NOT NULL COMMENT '用户在我方的账户名',
    `provider`           varchar(100) NOT NULL COMMENT '第三方提供者，比如taobao,weibo,qq',
    `providerUserId`     varchar(100) NOT NULL COMMENT '用户在第三方的OPENID',
    `providerUserName`   varchar(100)          DEFAULT NULL COMMENT '用户在第三方的unionName',
    `providerUserNick`   varchar(100)          DEFAULT NULL,
    `providerUserAvatar` varchar(255)          DEFAULT NULL COMMENT '用户在第三方的头像',
    `createTime`         timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建日期',
    `updateTime`         timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改日期',
    PRIMARY KEY (`userId`, `provider`, `providerUserId`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8 COMMENT ='第三方用户绑定表';

CREATE TABLE `weixin_connection`
(
    `userId`     bigint(20)   NOT NULL COMMENT '用户在我方的账户名',
    `appId`      varchar(32)  NOT NULL COMMENT '微信应用ID',
    `openId`     varchar(128) NOT NULL COMMENT 'openId',
    `unionId`    varchar(128) NOT NULL COMMENT 'unionId',
    `createTime` timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建日期',
    `updateTime` timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改日期',
    PRIMARY KEY (`userId`, `appId`),
    UNIQUE KEY `openId` (`openId`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8 COMMENT ='微信用户绑定表';

CREATE TABLE `authority`
(
    `userId`     bigint(20)  NOT NULL COMMENT '用户ID',
    `role`       varchar(32) NOT NULL COMMENT '权利指派',
    `createTime` timestamp   NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建日期',
    `updateTime` timestamp   NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改日期',
    PRIMARY KEY (`userId`, `role`),
    CONSTRAINT `authority_ibfk_1` FOREIGN KEY (`userId`) REFERENCES `user` (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8 COMMENT ='权限表';

CREATE TABLE `address`
(
    `id`         bigint(20)   NOT NULL AUTO_INCREMENT COMMENT '地址ID',
    `userId`     bigint(20)   NOT NULL DEFAULT '0' COMMENT '用户ID',
    `name`       varchar(50)  NOT NULL DEFAULT '' COMMENT '联系人姓名',
    `phone`      varchar(50)  NOT NULL DEFAULT '' COMMENT '联系电话',
    `province`   varchar(255) NOT NULL DEFAULT '',
    `city`       varchar(255) NOT NULL DEFAULT '',
    `district`   varchar(255) NOT NULL DEFAULT '',
    `street`     varchar(225) NOT NULL DEFAULT '' COMMENT '街道地址',
    `zipCode`    varchar(50)           DEFAULT NULL COMMENT '邮编',
    `preferred`  tinyint(4)   NOT NULL DEFAULT '0' COMMENT '优先级',
    `createTime` datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建日期',
    `updateTime` datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改日期',
    PRIMARY KEY (`id`),
    KEY `userId` (`userId`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 100000
  DEFAULT CHARSET = utf8 COMMENT ='地址表';

CREATE TABLE `message`
(
    `id`          bigint(20) NOT NULL AUTO_INCREMENT,
    `fromId`      bigint(20)          DEFAULT NULL COMMENT '发送者ID',
    `toId`        bigint(20) NOT NULL COMMENT '接受者ID',
    `status`      int(11)    NOT NULL DEFAULT '0' COMMENT '状态',
    `messageType` int(11)    NOT NULL DEFAULT '0' COMMENT '类型',
    `topic`       varchar(64)         DEFAULT NULL COMMENT '话题',
    `content`     text COMMENT '内容',
    `createTime`  timestamp  NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建日期',
    `updateTime`  timestamp  NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改日期',
    PRIMARY KEY (`id`),
    KEY `index2` (`toId`, `messageType`, `status`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 100000
  DEFAULT CHARSET = utf8 COMMENT ='消息';

INSERT INTO `user` (`id`, `password`, `loginName`, `mobile`, `email`, `nick`, `avatar`, `location`, `sex`, `enabled`,
                    `verified`)
VALUES ('1', '$2a$10$7euayjUpvbMPdj2rG1H2ueysP/AezgRaHlDAstZgN5Cw9Li/PXwOi', 'ikeyi', '10000000000', 'ikeyi@ikeyi.com',
        '盘古',
        'http://thirdwx.qlogo.cn/mmopen/vi_32/Q0j4TwGTfTKeRIqwBBgdTiaF3DicEnZMdee9KrP5fibNX8iahk3CNFc4gF5Gnb9Unu3lmsQdmeO0CSYaIoH1tIb3xQ/132',
        '上天', '1', '1', '1');
INSERT INTO `user` (`id`, `password`, `loginName`, `mobile`, `email`, `nick`, `avatar`, `location`, `sex`, `enabled`,
                    `verified`)
VALUES ('2', '$2a$10$7euayjUpvbMPdj2rG1H2ueysP/AezgRaHlDAstZgN5Cw9Li/PXwOi', 'super', '10000000001', 'super@ikeyi.com',
        '元始天尊',
        'http://thirdwx.qlogo.cn/mmopen/vi_32/Q0j4TwGTfTKeRIqwBBgdTiaF3DicEnZMdee9KrP5fibNX8iahk3CNFc4gF5Gnb9Unu3lmsQdmeO0CSYaIoH1tIb3xQ/132',
        '上天', '0', '1', '1');


INSERT INTO `authority` (`userId`, `role`)
VALUES ('1', 'ROLE_BUYER');
INSERT INTO `authority` (`userId`, `role`)
VALUES ('1', 'ROLE_SELLER');
INSERT INTO `authority` (`userId`, `role`)
VALUES ('1', 'ROLE_SUPER');
INSERT INTO `authority` (`userId`, `role`)
VALUES ('2', 'ROLE_BUYER');
INSERT INTO `authority` (`userId`, `role`)
VALUES ('2', 'ROLE_SELLER');
INSERT INTO `authority` (`userId`, `role`)
VALUES ('2', 'ROLE_SUPER');