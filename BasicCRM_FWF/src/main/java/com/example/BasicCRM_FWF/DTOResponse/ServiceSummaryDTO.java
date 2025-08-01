package com.example.BasicCRM_FWF.DTOResponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServiceSummaryDTO {
    private long totalCombo;
    private long totalLe;
    private long totalCT;
    private long totalGift;
    private long totalAll;

    private long prevCombo;
    private long prevLe;
    private long prevCT;
    private long prevGift;
    private long prevAll;

    private double comboGrowth;
    private double leGrowth;
    private double ctGrowth;
    private double giftGrowth;
    private double allGrowth;
}