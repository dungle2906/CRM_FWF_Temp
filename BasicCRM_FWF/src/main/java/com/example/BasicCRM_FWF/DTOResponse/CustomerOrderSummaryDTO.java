package com.example.BasicCRM_FWF.DTOResponse;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CustomerOrderSummaryDTO {
    private long totalNew;
    private long totalOld;

    public CustomerOrderSummaryDTO(long totalNew, long totalOld) {
        this.totalNew = totalNew;
        this.totalOld = totalOld;
    }

    // getters and setters
}