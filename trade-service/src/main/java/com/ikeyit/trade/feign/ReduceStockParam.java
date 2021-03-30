package com.ikeyit.trade.feign;

import java.util.List;

public class ReduceStockParam {

    public static class ReduceStockItem {
        Long skuId;

        Integer quantity;

        public Long getSkuId() {
            return skuId;
        }

        public void setSkuId(Long skuId) {
            this.skuId = skuId;
        }

        public Integer getQuantity() {
            return quantity;
        }

        public void setQuantity(Integer quantity) {
            this.quantity = quantity;
        }
    }


    Long orderId;

    List<ReduceStockItem> items;

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public List<ReduceStockItem> getItems() {
        return items;
    }

    public void setItems(List<ReduceStockItem> items) {
        this.items = items;
    }
}
