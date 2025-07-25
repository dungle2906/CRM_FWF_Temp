package com.example.BasicCRM_FWF.DTOResponse;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class RegionRevenueDTO {
    private String region;
    private LocalDate date;
    private BigDecimal totalRevenue;

    public RegionRevenueDTO(String region, LocalDate date, BigDecimal totalRevenue) {
        this.region = region;
        this.date = date;
        this.totalRevenue = totalRevenue;
    }
}
