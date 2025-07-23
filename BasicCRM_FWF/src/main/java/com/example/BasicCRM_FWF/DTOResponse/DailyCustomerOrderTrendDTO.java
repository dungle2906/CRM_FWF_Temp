package com.example.BasicCRM_FWF.DTOResponse;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class DailyCustomerOrderTrendDTO {
    private LocalDateTime date;
    private long newCustomers;
    private long oldCustomers;

    public DailyCustomerOrderTrendDTO(LocalDateTime date, long newCustomers, long oldCustomers) {
        this.date = date;
        this.newCustomers = newCustomers;
        this.oldCustomers = oldCustomers;
    }

    // getters and setters
}