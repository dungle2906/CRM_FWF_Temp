package com.example.BasicCRM_FWF.DTOResponse;

import lombok.Data;

import java.math.BigDecimal;

// DTOs
@Data
public class RevenueSummaryDTO {
    private BigDecimal totalRevenue;
    private BigDecimal actualRevenue;
    private double revenueGrowth;
    private double actualGrowth;

    public RevenueSummaryDTO(BigDecimal totalRevenue, BigDecimal actualRevenue,
                              double revenueGrowth, double actualGrowth) {
        this.totalRevenue = totalRevenue;
        this.actualRevenue = actualRevenue;
        this.revenueGrowth = revenueGrowth;
        this.actualGrowth = actualGrowth;
    }
}