package com.example.BasicCRM_FWF.DTOResponse;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class ShopTypeRevenueDTO {
    private String shopType;
    private LocalDateTime date;
    private BigDecimal actualRevenue;

    public ShopTypeRevenueDTO(String shopType, LocalDateTime date, BigDecimal actualRevenue) {
        this.shopType = shopType;
        this.date = date;
        this.actualRevenue = actualRevenue;
    }
}
