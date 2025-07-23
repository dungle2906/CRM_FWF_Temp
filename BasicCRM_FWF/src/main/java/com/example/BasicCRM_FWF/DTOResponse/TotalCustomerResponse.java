package com.example.BasicCRM_FWF.DTOResponse;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TotalCustomerResponse {
    private long current;
    private long previous;
    private double changePercent;

    public TotalCustomerResponse(long current, long previous, double changePercent) {
        this.current = current;
        this.previous = previous;
        this.changePercent = changePercent;
    }
}
