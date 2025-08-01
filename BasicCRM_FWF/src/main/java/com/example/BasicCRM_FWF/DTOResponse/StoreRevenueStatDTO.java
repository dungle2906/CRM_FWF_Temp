package com.example.BasicCRM_FWF.DTOResponse;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class StoreRevenueStatDTO {
    private String storeName;
    private long currentOrders;
    private long deltaOrders;
    private BigDecimal actualRevenue;
    private BigDecimal foxieRevenue;
    private double revenueGrowth;
    private double revenuePercent;
    private double foxiePercent;
    private double orderPercent;

    public StoreRevenueStatDTO(String storeName, long currentOrders, long deltaOrders, BigDecimal actualRevenue, BigDecimal foxieRevenue, double revenueGrowth, double revenuePercent, double foxiePercent, double orderPercent) {
        this.storeName = storeName;
        this.currentOrders = currentOrders;
        this.deltaOrders = deltaOrders;
        this.actualRevenue = actualRevenue;
        this.foxieRevenue = foxieRevenue;
        this.revenueGrowth = revenueGrowth;
        this.revenuePercent = revenuePercent;
        this.foxiePercent = foxiePercent;
        this.orderPercent = orderPercent;
    }

    // constructor, getter, setter
}
