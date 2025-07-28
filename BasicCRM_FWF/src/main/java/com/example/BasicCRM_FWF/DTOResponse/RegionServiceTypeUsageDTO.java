package com.example.BasicCRM_FWF.DTOResponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// DTO
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegionServiceTypeUsageDTO {
    private String region;
    private String type;
    private long total;
}