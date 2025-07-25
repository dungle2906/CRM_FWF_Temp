package com.example.BasicCRM_FWF.DTOResponse;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ShopTypeRevenueDTO {
    private String shopType;
    private LocalDate date;
    private BigDecimal totalRevenue;

    public ShopTypeRevenueDTO(String shopType, LocalDate date, BigDecimal totalRevenue) {
        this.shopType = shopType;
        this.date = date;
        this.totalRevenue = totalRevenue;
    }
}