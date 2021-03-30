CREATE DATABASE `trade` /*!40100 DEFAULT CHARACTER SET utf8 */;
USE `trade`;
CREATE TABLE `cart_item` (
                             `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
                             `userId` bigint(20) NOT NULL COMMENT '用户ID',
                             `skuId` bigint(20) NOT NULL COMMENT 'SKU ID',
                             `status` int(11) NOT NULL DEFAULT '1' COMMENT '状态',
                             `price` decimal(10,2) NOT NULL COMMENT '价格',
                             `quantity` int(11) NOT NULL DEFAULT '0' COMMENT '数量',
                             `createTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建日期',
                             `updateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改日期',
                             PRIMARY KEY (`id`),
                             KEY `userId` (`userId`),
                             KEY `skuId` (`skuId`)
) ENGINE=InnoDB AUTO_INCREMENT=100000 DEFAULT CHARSET=utf8 COMMENT='购物车项';

CREATE TABLE `mq_message` (
                              `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '本地消息ID',
                              `status` int(11) NOT NULL COMMENT '状态',
                              `topic` varchar(64) NOT NULL COMMENT '主题',
                              `payload` varchar(5120) NOT NULL COMMENT '负载',
                              `messageKeys` varchar(256) NOT NULL COMMENT 'messageKeys',
                              `deliverTime` timestamp NULL DEFAULT NULL,
                              `createTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建日期',
                              `updateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改日期',
                              PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=100000 DEFAULT CHARSET=utf8 COMMENT='本地消息表';

CREATE TABLE `pay_order` (
                             `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '支付订单ID',
                             `buyerId` bigint(20) NOT NULL COMMENT '买家ID',
                             `orderType` char(16) NOT NULL COMMENT '订单类型',
                             `orderId` bigint(20) NOT NULL COMMENT '订单ID',
                             `status` int(11) NOT NULL COMMENT '状态',
                             `tradeNo` varchar(64) NOT NULL COMMENT '对外订单编号',
                             `payWay` varchar(32) DEFAULT NULL COMMENT '支付方式',
                             `paymentAmount` decimal(10,2) NOT NULL COMMENT '支付金额',
                             `refundAmount` decimal(10,2) NOT NULL COMMENT '退款金额',
                             `subject` varchar(128) DEFAULT NULL COMMENT '描述支付内容',
                             `transactionId` varchar(64) DEFAULT NULL COMMENT '流水号',
                             `transactionData` text COMMENT '支付系统原始报文',
                             `clientIp` varchar(32) DEFAULT NULL COMMENT '支付客户端IP',
                             `expireTime` timestamp NULL DEFAULT NULL COMMENT '过期时间',
                             `successTime` timestamp NULL DEFAULT NULL COMMENT '发货时间',
                             `createTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建日期',
                             `updateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改日期',
                             PRIMARY KEY (`id`),
                             UNIQUE KEY `orderType` (`orderType`,`orderId`),
                             UNIQUE KEY `tradeNo` (`tradeNo`)
) ENGINE=InnoDB AUTO_INCREMENT=1000000010 DEFAULT CHARSET=utf8 COMMENT='支付结果';

CREATE TABLE `pay_refund_order` (
                                    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '退款订单ID',
                                    `payOrderId` bigint(20) NOT NULL COMMENT '支付订单ID',
                                    `refundType` char(16) NOT NULL COMMENT '退款类型',
                                    `refundId` bigint(20) NOT NULL COMMENT '退款ID',
                                    `status` int(11) NOT NULL COMMENT '状态',
                                    `refundNo` varchar(64) NOT NULL COMMENT '对外退款编号',
                                    `tradeNo` varchar(64) NOT NULL COMMENT '对外订单编号',
                                    `reason` varchar(64) NOT NULL COMMENT '退款原因',
                                    `payWay` varchar(32) NOT NULL COMMENT '退款方式',
                                    `refundAmount` decimal(10,2) NOT NULL COMMENT '退款价格',
                                    `paymentAmount` decimal(10,2) NOT NULL COMMENT '支付金额',
                                    `refundData` text COMMENT '支付系统原始报文',
                                    `successTime` timestamp NULL DEFAULT NULL COMMENT '退款成功时间',
                                    `createTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建日期',
                                    `updateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改日期',
                                    PRIMARY KEY (`id`),
                                    UNIQUE KEY `refundType` (`refundType`,`refundId`),
                                    UNIQUE KEY `refundNo` (`refundNo`)
) ENGINE=InnoDB AUTO_INCREMENT=2000000002 DEFAULT CHARSET=utf8 COMMENT='支付结果';

CREATE TABLE `trade_order` (
                               `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
                               `status` int(11) NOT NULL DEFAULT '1' COMMENT '状态',
                               `closeReason` int(11) NOT NULL DEFAULT '0' COMMENT '关闭原因',
                               `buyerId` bigint(20) NOT NULL COMMENT '买方ID',
                               `buyerName` varchar(64) NOT NULL COMMENT '买方名字',
                               `buyerMemo` varchar(255) DEFAULT NULL COMMENT '买方备注',
                               `sellerId` bigint(20) NOT NULL COMMENT '卖方ID',
                               `sellerMemo` varchar(255) DEFAULT NULL COMMENT '卖方备注',
                               `orderType` int(11) NOT NULL COMMENT '订单类型',
                               `goodsQuantity` bigint(20) NOT NULL COMMENT '商品总数量',
                               `goodsAmount` decimal(10,2) NOT NULL COMMENT '商品总金额',
                               `paymentAmount` decimal(10,2) NOT NULL COMMENT '支付金额',
                               `discountAmount` decimal(10,2) NOT NULL COMMENT '优惠金额',
                               `freightAmount` decimal(10,2) NOT NULL COMMENT '邮费金额',
                               `receiverName` varchar(64) NOT NULL COMMENT '收货人名字',
                               `receiverPhone` varchar(64) NOT NULL COMMENT '收货人手机号',
                               `receiverProvince` varchar(64) NOT NULL COMMENT '收货人省份',
                               `receiverCity` varchar(64) NOT NULL COMMENT '收货人市',
                               `receiverDistrict` varchar(64) NOT NULL COMMENT '收货人区县',
                               `receiverStreet` varchar(128) NOT NULL COMMENT '收货人详细地址',
                               `invoiceTitle` varchar(128) DEFAULT NULL COMMENT '发表抬头',
                               `invoiceContent` varchar(128) DEFAULT NULL COMMENT '发表内容',
                               `invoicePayerTaxId` varchar(128) DEFAULT NULL COMMENT '消费方税号',
                               `payWay` varchar(32) DEFAULT NULL COMMENT '支付方式',
                               `payOrderId` bigint(20) DEFAULT NULL COMMENT '支付订单号',
                               `payTime` timestamp NULL DEFAULT NULL COMMENT '支付时间',
                               `expireTime` timestamp NULL DEFAULT NULL,
                               `source` varchar(64) DEFAULT NULL COMMENT '来源',
                               `logisticsCompany` varchar(64) DEFAULT NULL COMMENT '物流公司',
                               `trackingNumber` varchar(64) DEFAULT NULL COMMENT '物流单号',
                               `shipTime` timestamp NULL DEFAULT NULL COMMENT '发货时间',
                               `finishTime` timestamp NULL DEFAULT NULL COMMENT '完成时间',
                               `createTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建日期',
                               `updateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改日期',
                               PRIMARY KEY (`id`),
                               KEY `buyerId` (`buyerId`),
                               KEY `sellerId` (`sellerId`),
                               KEY `status` (`status`),
                               KEY `receiverName` (`receiverName`),
                               KEY `receiverPhone` (`receiverPhone`),
                               KEY `createTime` (`createTime`)
) ENGINE=InnoDB AUTO_INCREMENT=110000000000011 DEFAULT CHARSET=utf8 COMMENT='订单';

CREATE TABLE `trade_order_item` (
                                    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
                                    `orderId` bigint(20) NOT NULL COMMENT '订单ID',
                                    `buyerId` bigint(20) NOT NULL COMMENT '买家ID',
                                    `sellerId` bigint(20) NOT NULL COMMENT '卖家ID',
                                    `productId` bigint(20) NOT NULL COMMENT '商品ID',
                                    `skuId` bigint(20) NOT NULL COMMENT 'SKU ID',
                                    `status` int(11) NOT NULL DEFAULT '0' COMMENT '状态',
                                    `refundStatus` int(11) NOT NULL DEFAULT '0' COMMENT '退款状态',
                                    `price` decimal(10,2) NOT NULL COMMENT '价格',
                                    `paymentAmount` decimal(10,2) NOT NULL COMMENT '支付金额',
                                    `quantity` int(11) NOT NULL DEFAULT '1' COMMENT '数量',
                                    `skuCode` varchar(255) DEFAULT NULL COMMENT '编码',
                                    `skuAttributes` varchar(255) NOT NULL COMMENT 'sku规格名称',
                                    `image` varchar(255) NOT NULL COMMENT '图片',
                                    `title` varchar(255) NOT NULL COMMENT '标题',
                                    `logisticsCompany` varchar(64) DEFAULT NULL COMMENT '物流公司',
                                    `trackingNumber` varchar(64) DEFAULT NULL COMMENT '物流单号',
                                    `shipTime` timestamp NULL DEFAULT NULL COMMENT '发货时间',
                                    `finishTime` timestamp NULL DEFAULT NULL COMMENT '完成时间',
                                    `createTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建日期',
                                    `updateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改日期',
                                    PRIMARY KEY (`id`),
                                    KEY `orderId` (`orderId`)
) ENGINE=InnoDB AUTO_INCREMENT=5000000000000011 DEFAULT CHARSET=utf8 COMMENT='订单项';

CREATE TABLE `trade_order_log` (
                                   `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
                                   `orderId` bigint(20) NOT NULL COMMENT '订单ID',
                                   `orderStatus` int(11) NOT NULL DEFAULT '0' COMMENT '状态',
                                   `operator` varchar(255) NOT NULL COMMENT '操作员',
                                   `message` varchar(512) NOT NULL COMMENT '备注',
                                   `createTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建日期',
                                   `updateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改日期',
                                   PRIMARY KEY (`id`),
                                   KEY `orderId` (`orderId`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8 COMMENT='订单操作流水';

CREATE TABLE `trade_refund` (
                                `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
                                `orderId` bigint(20) NOT NULL COMMENT '订单ID',
                                `orderItemId` bigint(20) NOT NULL COMMENT '子订单ID',
                                `buyerId` bigint(20) NOT NULL COMMENT '买家ID',
                                `sellerId` bigint(20) NOT NULL COMMENT '卖家ID',
                                `productId` bigint(20) NOT NULL COMMENT '商品ID',
                                `skuId` bigint(20) NOT NULL COMMENT 'SKU ID',
                                `skuCode` varchar(255) DEFAULT NULL COMMENT '编码',
                                `skuAttributes` varchar(255) NOT NULL COMMENT 'sku规格名称',
                                `image` varchar(255) NOT NULL COMMENT '图片',
                                `title` varchar(255) NOT NULL COMMENT '标题',
                                `price` decimal(10,2) NOT NULL COMMENT '价格',
                                `paymentAmount` decimal(10,2) NOT NULL COMMENT '支付金额',
                                `quantity` int(11) NOT NULL DEFAULT '1' COMMENT '数量',
                                `status` int(11) NOT NULL DEFAULT '0' COMMENT '状态',
                                `refundType` int(11) NOT NULL DEFAULT '0' COMMENT '退款类型',
                                `amount` decimal(10,2) NOT NULL COMMENT '退款金额',
                                `reason` varchar(64) DEFAULT NULL COMMENT '退货退款原因',
                                `memo` varchar(255) DEFAULT NULL COMMENT '备注',
                                `logisticsCompany` varchar(64) DEFAULT NULL COMMENT '退货物流公司',
                                `trackingNumber` varchar(64) DEFAULT NULL COMMENT '退货物流单号',
                                `shipTime` timestamp NULL DEFAULT NULL COMMENT '退货发出时间',
                                `finishTime` timestamp NULL DEFAULT NULL COMMENT '完成时间',
                                `createTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建日期',
                                `updateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改日期',
                                PRIMARY KEY (`id`),
                                KEY `orderItemId` (`orderItemId`,`status`),
                                KEY `buyerId` (`buyerId`),
                                KEY `sellerId` (`sellerId`,`status`)
) ENGINE=InnoDB AUTO_INCREMENT=1100000003 DEFAULT CHARSET=utf8 COMMENT='退货退款';

CREATE TABLE `trade_refund_log` (
                                    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
                                    `refundId` bigint(20) NOT NULL COMMENT '订单ID',
                                    `operator` varchar(255) NOT NULL COMMENT '操作员',
                                    `message` varchar(512) NOT NULL COMMENT '备注',
                                    `createTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建日期',
                                    `updateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改日期',
                                    PRIMARY KEY (`id`),
                                    KEY `refundId` (`refundId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='退款操作流水';

CREATE TABLE `trade_refund_negotiation` (
                                            `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
                                            `refundId` bigint(20) NOT NULL COMMENT '订单ID',
                                            `operator` varchar(255) NOT NULL COMMENT '操作员',
                                            `operatorId` bigint(20) DEFAULT NULL COMMENT '操作员Id',
                                            `operation` varchar(255) NOT NULL COMMENT '操作',
                                            `message` varchar(512) NOT NULL COMMENT '备注',
                                            `createTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建日期',
                                            `updateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改日期',
                                            PRIMARY KEY (`id`),
                                            KEY `refundId` (`refundId`)
) ENGINE=InnoDB AUTO_INCREMENT=1000000006 DEFAULT CHARSET=utf8 COMMENT='退款操作流水';

CREATE TABLE `trade_seller_address` (
                                        `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '地址ID',
                                        `sellerId` bigint(20) NOT NULL DEFAULT '0' COMMENT '买家ID',
                                        `name` varchar(50) NOT NULL DEFAULT '' COMMENT '联系人姓名',
                                        `phone` varchar(50) NOT NULL DEFAULT '' COMMENT '联系电话',
                                        `province` varchar(255) NOT NULL DEFAULT '',
                                        `city` varchar(255) NOT NULL DEFAULT '',
                                        `district` varchar(255) NOT NULL DEFAULT '',
                                        `street` varchar(225) NOT NULL DEFAULT '' COMMENT '街道地址',
                                        `zipCode` varchar(50) DEFAULT NULL COMMENT '邮编',
                                        `defaultShipFrom` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否默认发货地址',
                                        `defaultReturnTo` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否默认退货地址',
                                        `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建日期',
                                        `updateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改日期',
                                        PRIMARY KEY (`id`),
                                        KEY `sellerId` (`sellerId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='卖家地址';