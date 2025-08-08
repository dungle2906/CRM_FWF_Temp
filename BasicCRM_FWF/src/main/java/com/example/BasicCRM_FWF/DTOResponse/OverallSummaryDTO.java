package com.example.BasicCRM_FWF.DTOResponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OverallSummaryDTO {
    private BigDecimal totalRevenue;
    private BigDecimal serviceRevenue;
    private BigDecimal foxieCardRevenue;
    private BigDecimal productRevenue;
    private BigDecimal cardPurchaseRevenue;

    private BigDecimal deltaTotalRevenue;
    private BigDecimal deltaServiceRevenue;
    private BigDecimal deltaFoxieCardRevenue;
    private BigDecimal deltaProductRevenue;
    private BigDecimal deltaCardPurchaseRevenue;

    private Double percentTotalRevenue;
    private Double percentServiceRevenue;
    private Double percentFoxieCardRevenue;
    private Double percentProductRevenue;
    private Double percentCardPurchaseRevenue;
}
