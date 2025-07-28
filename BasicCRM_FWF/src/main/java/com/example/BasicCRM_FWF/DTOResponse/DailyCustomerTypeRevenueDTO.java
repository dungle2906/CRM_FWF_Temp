package com.example.BasicCRM_FWF.DTOResponse;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
public class DailyCustomerTypeRevenueDTO {
    private LocalDate date;
    private String customerType;
    private BigDecimal revenue;

    public DailyCustomerTypeRevenueDTO(LocalDate date, String customerType, BigDecimal revenue) {
        this.date = date;
        this.customerType = customerType;
        this.revenue = revenue;
    }
}