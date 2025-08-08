package com.example.BasicCRM_FWF.DTOResponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegionOrderBreakdownDTO {
    private String region;
    private Long totalOrders;
    private Long serviceOrders;
    private Long foxieCardOrders;
    private Long productOrders;
    private Long cardPurchaseOrders;
}