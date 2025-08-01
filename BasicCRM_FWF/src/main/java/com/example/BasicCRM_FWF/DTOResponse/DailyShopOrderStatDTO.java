package com.example.BasicCRM_FWF.DTOResponse;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
public class DailyShopOrderStatDTO {
    private LocalDate date;
    private Long totalOrders;
    private Integer shopCount;
    private Double avgOrdersPerShop;

    public DailyShopOrderStatDTO(LocalDate date, Long totalOrders, Integer shopCount) {
        this.date = date;
        this.totalOrders = totalOrders;
        this.shopCount = shopCount;
        this.avgOrdersPerShop = (shopCount == 0) ? 0.0 : (double) totalOrders / shopCount;
    }
}