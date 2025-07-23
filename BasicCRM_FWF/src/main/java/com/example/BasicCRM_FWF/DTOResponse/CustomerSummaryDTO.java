package com.example.BasicCRM_FWF.DTOResponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CustomerSummaryDTO {
    private long totalNewCustomers;
    private long actualCustomers;
    private double growthTotal;
    private double growthActual;

    public CustomerSummaryDTO(long totalNewCustomers, long actualCustomers, double growthTotal, double growthActual) {
        this.totalNewCustomers = totalNewCustomers;
        this.actualCustomers = actualCustomers;
        this.growthTotal = growthTotal;
        this.growthActual = growthActual;
    }
}