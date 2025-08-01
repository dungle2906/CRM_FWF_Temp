package com.example.BasicCRM_FWF.DTOResponse;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class DailyShopTypeRevenueDTO {
    private LocalDateTime date;
    private String shopType;
    private BigDecimal revenue;

    public DailyShopTypeRevenueDTO(LocalDateTime date, String shopType, BigDecimal revenue) {
        this.date = date;
        this.shopType = shopType;
        this.revenue = revenue;
    }
}