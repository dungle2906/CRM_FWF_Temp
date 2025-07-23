package com.example.BasicCRM_FWF.DTOResponse;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class PaymentBreakdownDTO {
    private BigDecimal totalCash;
    private BigDecimal totalTransfer;
    private BigDecimal totalPrepaidCard;
    private BigDecimal totalDebt;

    public PaymentBreakdownDTO(BigDecimal totalCash, BigDecimal totalTransfer,
                               BigDecimal totalPrepaidCard, BigDecimal totalDebt) {
        this.totalCash = totalCash;
        this.totalTransfer = totalTransfer;
        this.totalPrepaidCard = totalPrepaidCard;
        this.totalDebt = totalDebt;
    }

    // getters and setters
}