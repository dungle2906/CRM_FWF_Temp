package com.example.BasicCRM_FWF.DTOResponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ServiceStatsDTO {
    private String serviceName;
    private String type; // e.g., Combo, Dịch vụ, Added on, etc.
    private long usageCount;
    private long usageDeltaCount;
    private double usagePercent;
    private BigDecimal totalRevenue;
    private double revenueDeltaPercent;
    private double revenuePercent;       // % difference in revenue compared to previous range
}