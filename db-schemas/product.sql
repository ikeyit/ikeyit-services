CREATE DATABASE `product` /*!40100 DEFAULT CHARACTER SET utf8 */;
USE `product`;
CREATE TABLE `attribute` (
                             `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
                             `name` varchar(32) NOT NULL COMMENT '属性名称',
                             `position` int(11) NOT NULL DEFAULT '0' COMMENT '排序位置',
                             `inputType` int(11) NOT NULL DEFAULT '0' COMMENT '属性的输入类型，1.单选 2.多选 3.可输入',
                             `searchType` int(11) NOT NULL DEFAULT '0' COMMENT '检索类型',
                             `attributeType` int(11) NOT NULL DEFAULT '0' COMMENT '关键属性1/销售属性2',
                             `required` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否必填',
                             `obsolete` tinyint(1) NOT NULL DEFAULT '0',
                             `createTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建日期',
                             `updateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改日期',
                             PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=100000 DEFAULT CHARSET=utf8 COMMENT='属性';

CREATE TABLE `attribute_value` (
                                   `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
                                   `attributeId` bigint(20) NOT NULL COMMENT '属性ID',
                                   `val` varchar(255) DEFAULT NULL COMMENT '值',
                                   `productId` bigint(20) NOT NULL DEFAULT '0' COMMENT '是否为自定义',
                                   `position` int(11) NOT NULL DEFAULT '0' COMMENT '排序位置',
                                   `obsolete` tinyint(1) NOT NULL DEFAULT '0',
                                   `createTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建日期',
                                   `updateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改日期',
                                   PRIMARY KEY (`id`),
                                   KEY `attributeId` (`attributeId`,`productId`)
) ENGINE=InnoDB AUTO_INCREMENT=100000 DEFAULT CHARSET=utf8 COMMENT='属性值';

CREATE TABLE `brand` (
                         `id` bigint(20) NOT NULL AUTO_INCREMENT,
                         `name` varchar(64) NOT NULL COMMENT '品牌名',
                         `description` varchar(255) DEFAULT NULL COMMENT '品牌描述',
                         `logo` varchar(255) DEFAULT NULL COMMENT '品牌LOGO',
                         `createTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建日期',
                         `updateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改日期',
                         PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=100000 DEFAULT CHARSET=utf8 COMMENT='品牌';

CREATE TABLE `category` (
                            `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
                            `parentId` bigint(20) NOT NULL DEFAULT '0' COMMENT '父分类ID',
                            `status` int(11) NOT NULL DEFAULT '0' COMMENT '状态',
                            `level` int(11) NOT NULL DEFAULT '0' COMMENT '层级',
                            `name` varchar(64) NOT NULL COMMENT '分类名字',
                            `description` varchar(255) DEFAULT NULL COMMENT '分类描述',
                            `obsolete` tinyint(1) NOT NULL DEFAULT '0',
                            `createTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建日期',
                            `updateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改日期',
                            PRIMARY KEY (`id`),
                            KEY `parentId` (`parentId`)
) ENGINE=InnoDB AUTO_INCREMENT=100000 DEFAULT CHARSET=utf8 COMMENT='分类';

CREATE TABLE `category_attribute` (
                                      `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '类目属性ID',
                                      `categoryId` bigint(20) NOT NULL COMMENT '类目Id',
                                      `attributeId` bigint(20) NOT NULL COMMENT '属性ID',
                                      `position` int(11) NOT NULL DEFAULT '0' COMMENT '排序位置',
                                      PRIMARY KEY (`id`),
                                      UNIQUE KEY `categoryId` (`categoryId`,`attributeId`)
) ENGINE=InnoDB AUTO_INCREMENT=100000 DEFAULT CHARSET=utf8 COMMENT='类目属性表';

CREATE TABLE `category_attribute_value` (
                                            `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '分类属性ID',
                                            `categoryId` bigint(20) NOT NULL COMMENT '分类ID',
                                            `attributeValueId` bigint(20) NOT NULL COMMENT '属性值ID',
                                            `position` int(11) NOT NULL DEFAULT '0' COMMENT '排序位置',
                                            PRIMARY KEY (`id`),
                                            KEY `categoryId` (`categoryId`,`attributeValueId`)
) ENGINE=InnoDB AUTO_INCREMENT=100000 DEFAULT CHARSET=utf8 COMMENT='分类属性值';

CREATE TABLE `cms_post` (
                            `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
                            `authorId` bigint(20) NOT NULL COMMENT '作者ID',
                            `status` int(11) NOT NULL DEFAULT '0' COMMENT '状态',
                            `version` int(11) NOT NULL DEFAULT '0' COMMENT '版本',
                            `type` int(11) NOT NULL DEFAULT '0' COMMENT '类型',
                            `title` varchar(255) NULL COMMENT '标题',
                            `content` text,
                            `createTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建日期',
                            `updateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改日期',
                            PRIMARY KEY (`id`),
                            KEY `authorId` (`authorId`, `type`, `status`)
) ENGINE=InnoDB AUTO_INCREMENT=100000 DEFAULT CHARSET=utf8 COMMENT='内容管理系统POST';


CREATE TABLE `shop_page` (
                            `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
                            `sellerId` bigint(20) NOT NULL COMMENT '卖家ID',
                            `status` int(11) NOT NULL DEFAULT '0' COMMENT '状态',
                            `version` int(11) NOT NULL DEFAULT '0' COMMENT '版本',
                            `type` int(11) NOT NULL DEFAULT '0' COMMENT '类型',
                            `preferred` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否默认首页',
                            `name` varchar(64) NULL COMMENT '名称',
                            `content` text,
                            `createTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建日期',
                            `updateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改日期',
                            PRIMARY KEY (`id`),
                            KEY (`sellerId`, `type`,`preferred`,`status`)
) ENGINE=InnoDB AUTO_INCREMENT=100000 DEFAULT CHARSET=utf8 COMMENT='店铺页面';

CREATE TABLE `media_file` (
                              `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
                              `sellerId` bigint(20) NOT NULL COMMENT '卖家ID',
                              `fileType` int(11) NOT NULL DEFAULT '0' COMMENT '类型，0图片， 1视频',
                              `folderId` bigint(20) NOT NULL DEFAULT '0' COMMENT '分组',
                              `url` varchar(255) NOT NULL DEFAULT '' COMMENT 'url',
                              `fileName` varchar(255) NOT NULL DEFAULT '' COMMENT '文件名',
                              `extension` varchar(255) NOT NULL DEFAULT '' COMMENT '扩展名',
                              `size` int(11) NOT NULL DEFAULT '0' COMMENT '文件大小',
                              `height` int(11) NOT NULL DEFAULT '0' COMMENT '文件高度',
                              `width` int(11) NOT NULL DEFAULT '0' COMMENT '文件宽度',
                              `createTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建日期',
                              `updateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改日期',
                              PRIMARY KEY (`id`),
                              KEY `sellerId` (`sellerId`,`fileType`)
) ENGINE=InnoDB AUTO_INCREMENT=100000 DEFAULT CHARSET=utf8 COMMENT='素材空间';

CREATE TABLE `order_stock_log` (
                                   `orderId` bigint(20) NOT NULL COMMENT '订单ID',
                                   `status` int(11) NOT NULL DEFAULT '0' COMMENT '状态',
                                   `content` varchar(1024) NOT NULL COMMENT '数据',
                                   `createTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建日期',
                                   `updateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改日期',
                                   PRIMARY KEY (`orderId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='库存流水';

CREATE TABLE `product` (
                           `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
                           `sellerId` bigint(20) NOT NULL COMMENT '卖家ID',
                           `categoryId` bigint(20) NOT NULL COMMENT '分类ID',
                           `brandId` bigint(20) DEFAULT NULL COMMENT '品牌ID',
                           `title` varchar(255) NOT NULL COMMENT '产品标题',
                           `subtitle` varchar(255) DEFAULT NULL COMMENT '产品副标题',
                           `model` varchar(32) DEFAULT NULL COMMENT '产品型号款号',
                           `image` varchar(255) NOT NULL COMMENT '主图',
                           `images` varchar(2048) NOT NULL COMMENT '橱窗图片',
                           `video` varchar(512) DEFAULT NULL COMMENT '主图视频',
                           `detail` text,
                           `status` int(11) NOT NULL DEFAULT '0' COMMENT '状态',
                           `sales` bigint(20) NOT NULL DEFAULT '0' COMMENT '销售量',
                           `recommendation` bigint(20) NOT NULL DEFAULT '0' COMMENT '推荐度',
                           `price` decimal(10,2) NOT NULL COMMENT '价格',
                           `promotionPrice` decimal(10,2) DEFAULT NULL COMMENT '促销价',
                           `createTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建日期',
                           `updateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改日期',
                           PRIMARY KEY (`id`),
                           KEY `sellerId` (`sellerId`),
                           KEY `categoryId` (`categoryId`),
                           KEY `brandId` (`brandId`),
                           KEY `status` (`status`),
                           KEY `recommendation` (`recommendation`)
) ENGINE=InnoDB AUTO_INCREMENT=100000 DEFAULT CHARSET=utf8 COMMENT='产品';

CREATE TABLE `product_attribute_value` (
                                           `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '产品属性ID',
                                           `productId` bigint(20) NOT NULL COMMENT '产品Id',
                                           `attributeValueId` bigint(20) NOT NULL COMMENT '属性值ID',
                                           `position` int(11) NOT NULL DEFAULT '0' COMMENT '排序位置',
                                           `createTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                           PRIMARY KEY (`id`),
                                           UNIQUE KEY `productId` (`productId`,`attributeValueId`)
) ENGINE=InnoDB AUTO_INCREMENT=100000 DEFAULT CHARSET=utf8 COMMENT='产品属性';



CREATE TABLE `shop_basic_info` (
                        `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
                        `sellerId` bigint(20) NOT NULL COMMENT '卖家ID',
                        `name` varchar(32) NOT NULL COMMENT '分类名字',
                        `avatar` varchar(255) DEFAULT NULL COMMENT '店铺图标',
                        `description` text COMMENT '描述',
                        `createTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建日期',
                        `updateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改日期',
                        PRIMARY KEY (`id`),
                        KEY `sellerId` (`sellerId`)
) ENGINE=InnoDB AUTO_INCREMENT=100000 DEFAULT CHARSET=utf8 COMMENT='店铺基本信息';

CREATE TABLE `shop_category` (
                                 `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
                                 `sellerId` bigint(20) NOT NULL COMMENT '卖家ID',
                                 `parentId` bigint(20) NOT NULL DEFAULT '0' COMMENT '父分类ID',
                                 `level` int(11) NOT NULL DEFAULT '0' COMMENT '层级',
                                 `position` int(11) NOT NULL DEFAULT '0' COMMENT '位置',
                                 `name` varchar(64) NOT NULL COMMENT '分类名字',
                                 `image` varchar(255) NULL COMMENT '主图',
                                 `createTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建日期',
                                 `updateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改日期',
                                 PRIMARY KEY (`id`),
                                 KEY `sellerId` (`sellerId`),
                                 KEY `parentId` (`parentId`)
) ENGINE=InnoDB AUTO_INCREMENT=100000 DEFAULT CHARSET=utf8 COMMENT='店铺自定义分类';

CREATE TABLE `shop_category_product` (
                                         `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
                                         `shopCategoryId1` bigint(20) NOT NULL COMMENT '店铺一级分类ID',
                                         `shopCategoryId2` bigint(20) NOT NULL COMMENT '店铺二级分类ID',
                                         `productId` bigint(20) NOT NULL COMMENT '店铺ID',
                                         `createTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建日期',
                                         PRIMARY KEY (`id`),
                                         UNIQUE `productId` (`shopCategoryId1`, `shopCategoryId2`, `productId`)
) ENGINE=InnoDB AUTO_INCREMENT=100000 DEFAULT CHARSET=utf8 COMMENT='商品店铺分类';

CREATE TABLE `sku` (
                       `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'SKU ID',
                       `sellerId` bigint(20) NOT NULL COMMENT '卖家ID',
                       `productId` bigint(20) NOT NULL COMMENT '产品ID',
                       `status` int(11) NOT NULL DEFAULT '1' COMMENT '状态',
                       `code` varchar(32) DEFAULT NULL COMMENT 'SKU条形码',
                       `image` varchar(255) NOT NULL COMMENT '图片',
                       `price` decimal(10,2) NOT NULL COMMENT '价格',
                       `promotionPrice` decimal(10,2) DEFAULT NULL COMMENT '促销价',
                       `stock` bigint(20) NOT NULL DEFAULT '0' COMMENT '库存',
                       `lockedStock` bigint(20) NOT NULL DEFAULT '0' COMMENT '被锁定的库存',
                       `sales` bigint(20) NOT NULL DEFAULT '0' COMMENT '销量',
                       `attributes` varchar(255) DEFAULT NULL COMMENT '选项值',
                       `createTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建日期',
                       `updateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改日期',
                       PRIMARY KEY (`id`),
                       KEY `productId` (`productId`)
) ENGINE=InnoDB AUTO_INCREMENT=100000 DEFAULT CHARSET=utf8 COMMENT='SKU';


INSERT INTO `attribute` (`id`, `name`, `position`, `inputType`, `searchType`, `attributeType`, `required`) VALUES ('1', '颜色', '0', '10', '1', '2', '1');
INSERT INTO `attribute` (`id`, `name`, `position`, `inputType`, `searchType`, `attributeType`, `required`) VALUES ('2', '尺码', '0', '10', '1', '2', '1');
INSERT INTO `attribute_value` (`id`, `attributeId`, `val`, `productId`, `position`) VALUES ('1', '1', '黑色', '0', '0');
INSERT INTO `attribute_value` (`id`, `attributeId`, `val`, `productId`, `position`) VALUES ('2', '1', '白色', '0', '0');
INSERT INTO `attribute_value` (`id`, `attributeId`, `val`, `productId`, `position`) VALUES ('3', '1', '红色', '0', '0');
INSERT INTO `attribute_value` (`id`, `attributeId`, `val`, `productId`, `position`) VALUES ('4', '1', '绿色', '0', '0');
INSERT INTO `attribute_value` (`id`, `attributeId`, `val`, `productId`, `position`) VALUES ('5', '1', '蓝色', '0', '0');
INSERT INTO `attribute_value` (`id`, `attributeId`, `val`, `productId`, `position`) VALUES ('6', '1', '粉色', '0', '0');
INSERT INTO `attribute_value` (`id`, `attributeId`, `val`, `productId`, `position`) VALUES ('7', '1', '黄色', '0', '0');
INSERT INTO `attribute_value` (`id`, `attributeId`, `val`, `productId`, `position`) VALUES ('8', '1', '紫色', '0', '0');
INSERT INTO `attribute_value` (`id`, `attributeId`, `val`, `productId`, `position`) VALUES ('9', '1', '橙色', '0', '0');
INSERT INTO `attribute_value` (`id`, `attributeId`, `val`, `productId`, `position`) VALUES ('10', '1', '灰色', '0', '0');
INSERT INTO `attribute_value` (`id`, `attributeId`, `val`, `productId`, `position`) VALUES ('11', '2', 'XS', '0', '0');
INSERT INTO `attribute_value` (`id`, `attributeId`, `val`, `productId`, `position`) VALUES ('12', '2', 'S', '0', '0');
INSERT INTO `attribute_value` (`id`, `attributeId`, `val`, `productId`, `position`) VALUES ('13', '2', 'M', '0', '0');
INSERT INTO `attribute_value` (`id`, `attributeId`, `val`, `productId`, `position`) VALUES ('14', '2', 'L', '0', '0');
INSERT INTO `attribute_value` (`id`, `attributeId`, `val`, `productId`, `position`) VALUES ('15', '2', 'XL', '0', '0');
INSERT INTO `attribute_value` (`id`, `attributeId`, `val`, `productId`, `position`) VALUES ('16', '2', 'XXL', '0', '0');
INSERT INTO `attribute_value` (`id`, `attributeId`, `val`, `productId`, `position`) VALUES ('17', '2', 'XXXL', '0', '0');
INSERT INTO `attribute_value` (`id`, `attributeId`, `val`, `productId`, `position`) VALUES ('18', '2', 'XXXXL', '0', '0');

INSERT INTO `category` (`id`, `parentId`, `status`, `level`, `name`) VALUES ('1', '0', '0', '0', '服饰');
INSERT INTO `category` (`id`, `parentId`, `status`, `level`, `name`) VALUES ('2', '1', '0', '1', '女士内衣');
INSERT INTO `category` (`id`, `parentId`, `status`, `level`, `name`) VALUES ('3', '2', '0', '2', '文胸');

INSERT INTO `category_attribute` (`id`,`categoryId`, `attributeId`, `position`) VALUES ('1', '3', '1', '0');
INSERT INTO `category_attribute` (`id`,`categoryId`, `attributeId`, `position`) VALUES ('2', '3', '2', '0');

INSERT INTO `brand` (`id`, `name`, `description`) VALUES ('1', '其它', '未登记的品牌');

INSERT INTO `cms_post` (`id`, `authorId`, `type`, `title`, `content`) VALUES ('1', '1', '1', '文章demo', '[{\"type\":\"text\",\"content\":\"盘古开天辟地\"}]');
INSERT INTO `cms_post` (`id`, `authorId`, `type`, `title`, `content`) VALUES ('2', '2', '1', '文章demo', '[{\"type\":\"text\",\"content\":\"原始天尊hello world\"}]');

INSERT INTO `shop_page` (`id`, `sellerId`, `type`, `preferred`, `name`, `content`) VALUES ('1', '1', '1', '1', '店铺首页', '[{\"type\":\"text\",\"content\":\"盘古开天辟地\"}]');
INSERT INTO `shop_page` (`id`, `sellerId`, `type`, `preferred`, `name`, `content`) VALUES ('2', '2', '1', '1', '店铺首页', '[{\"type\":\"text\",\"content\":\"原始天尊hello world\"}]');


INSERT INTO `shop_basic_info` (`id`, `sellerId`, `name`) VALUES ('1', '1', '盘古小店');
INSERT INTO `shop_basic_info` (`id`, `sellerId`, `name`) VALUES ('2', '2', '原始天尊小店');
