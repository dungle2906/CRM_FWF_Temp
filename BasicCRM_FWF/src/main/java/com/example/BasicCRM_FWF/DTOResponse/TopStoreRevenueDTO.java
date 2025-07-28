package com.example.BasicCRM_FWF.DTOResponse;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class TopStoreRevenueDTO {
    private String shopName;
    private BigDecimal actualRevenue;
    private BigDecimal foxieCardRevenue;

    public TopStoreRevenueDTO(String shopName, BigDecimal actualRevenue, BigDecimal foxieCardRevenue) {
        this.shopName = shopName;
        this.actualRevenue = actualRevenue;
        this.foxieCardRevenue = foxieCardRevenue;
    }

    // getters and setters
}
