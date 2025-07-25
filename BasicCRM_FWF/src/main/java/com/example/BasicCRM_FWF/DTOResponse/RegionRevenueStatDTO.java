package com.example.BasicCRM_FWF.DTOResponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class RegionRevenueStatDTO {
    private String region;
    private long orders;
    private long delta;             // chênh lệch số đơn so với kỳ trước
    private BigDecimal revenue;
    private double growthPercent;   // % tăng giảm số đơn so với kỳ trước
    private double revenuePercent;  // % doanh thu đóng góp so với tổng doanh thu hiện tại

    public RegionRevenueStatDTO(String region, long orders, long delta, BigDecimal revenue, double growthPercent, double revenuePercent) {
        this.region = region;
        this.orders = orders;
        this.delta = delta;
        this.revenue = revenue;
        this.growthPercent = growthPercent;
        this.revenuePercent = revenuePercent;
    }
}