package com.example.BasicCRM_FWF.DTOResponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OverallOrderSummaryDTO {
    private Long totalOrders;
    private Long serviceOrders;
    private Long foxieCardOrders;
    private Long productOrders;
    private Long cardPurchaseOrders;

    private Long deltaTotalOrders;
    private Long deltaServiceOrders;
    private Long deltaFoxieCardOrders;
    private Long deltaProductOrders;
    private Long deltaCardPurchaseOrders;
}