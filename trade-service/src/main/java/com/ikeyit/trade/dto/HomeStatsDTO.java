package com.ikeyit.trade.dto;

import java.math.BigDecimal;

public class HomeStatsDTO {

    private long waitBuyerPayOrderCount;

    private long paidOrderCount;

    private BigDecimal todayTurnover;

    private long todayOrderCount;

    public long getWaitBuyerPayOrderCount() {
        return waitBuyerPayOrderCount;
    }

    public void setWaitBuyerPayOrderCount(long waitBuyerPayOrderCount) {
        this.waitBuyerPayOrderCount = waitBuyerPayOrderCount;
    }

    public long getPaidOrderCount() {
        return paidOrderCount;
    }

    public void setPaidOrderCount(long paidOrderCount) {
        this.paidOrderCount = paidOrderCount;
    }

    public BigDecimal getTodayTurnover() {
        return todayTurnover;
    }

    public void setTodayTurnover(BigDecimal todayTurnover) {
        this.todayTurnover = todayTurnover;
    }

    public long getTodayOrderCount() {
        return todayOrderCount;
    }

    public void setTodayOrderCount(long todayOrderCount) {
        this.todayOrderCount = todayOrderCount;
    }
}
