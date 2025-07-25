package com.example.BasicCRM_FWF.DTOResponse;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class RegionRevenueStatResponse {
    private List<RegionRevenueStatDTO> stats;
    private long totalOrders;
    private long totalDelta;
    private BigDecimal totalActualRevenue;
    private double totalGrowth;

    public RegionRevenueStatResponse(List<RegionRevenueStatDTO> stats, long totalOrders, long totalDelta, BigDecimal totalActualRevenue, double totalGrowth) {
        this.stats = stats;
        this.totalOrders = totalOrders;
        this.totalDelta = totalDelta;
        this.totalActualRevenue = totalActualRevenue;
        this.totalGrowth = totalGrowth;
    }

    public RegionRevenueStatResponse() {

    }
}