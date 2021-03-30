package com.ikeyit.product.dto;

import java.util.List;

public class ReduceStockParam {

    public static class LockStockItem {
        Long skuId;

        Long quantity;

        public Long getSkuId() {
            return skuId;
        }

        public void setSkuId(Long skuId) {
            this.skuId = skuId;
        }

        public Long getQuantity() {
            return quantity;
        }

        public void setQuantity(Long quantity) {
            this.quantity = quantity;
        }
    }


    Long orderId;

    List<LockStockItem> items;

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public List<LockStockItem> getItems() {
        return items;
    }

    public void setItems(List<LockStockItem> items) {
        this.items = items;
    }
}
