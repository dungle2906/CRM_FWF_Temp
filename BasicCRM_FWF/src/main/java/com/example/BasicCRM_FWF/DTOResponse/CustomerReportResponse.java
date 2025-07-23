package com.example.BasicCRM_FWF.DTOResponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CustomerReportResponse {
    private List<DailyCustomerCount> currentRange;
    private List<DailyCustomerCount> previousRange;
}
