package com.ikeyit.trade.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;


/**
 * 订单资金流向！用于分账，结算
 */
public class OrderFlow {

    public static final Integer STATUS_UNPAID = 0;

    public static final Integer STATUS_PAID = 1;

    public static final Integer STATUS_PAYING = 2;

    private Long id;

    //业务订单ID
    private Long orderId;
    //状态
    private Integer status;

    private String tradeNo;

    private String payWay;

    //支付金额
    private BigDecimal paymentAmount;

    //退款金额
    private BigDecimal refundAmount;

    //服务费 = (支付金额 - 退款金额) * 服务费率
    private BigDecimal serviceFee;

    //结算金额 = 支付金额 - 退款金额 - 服务费
    private BigDecimal settlementAmount;

    private String subject;

    private String transactionId;

    private String transactionData;

    private String payerIp;

    private LocalDateTime successTime;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;


}
