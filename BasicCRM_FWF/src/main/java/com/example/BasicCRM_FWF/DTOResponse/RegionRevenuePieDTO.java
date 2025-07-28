package com.example.BasicCRM_FWF.DTOResponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class RegionRevenuePieDTO {
    private String region;
    private BigDecimal actualRevenue;

    public RegionRevenuePieDTO(String region, BigDecimal actualRevenue, double revenuePercent) {
        this.region = region;
        this.actualRevenue = actualRevenue;
    }
}
