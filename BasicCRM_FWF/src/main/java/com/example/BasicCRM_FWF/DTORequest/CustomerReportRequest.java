package com.example.BasicCRM_FWF.DTORequest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CustomerReportRequest {
    private LocalDateTime fromDate; // ngày bắt đầu user chọn
    private LocalDateTime toDate;   // ngày kết thúc user chọn
}