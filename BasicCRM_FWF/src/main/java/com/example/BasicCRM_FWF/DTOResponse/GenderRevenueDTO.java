package com.example.BasicCRM_FWF.DTOResponse;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class GenderRevenueDTO {
    private BigDecimal avgRevenueMale;
    private BigDecimal avgRevenueFemale;
    private BigDecimal avgServiceMale;
    private BigDecimal avgServiceFemale;

    public GenderRevenueDTO(BigDecimal avgRevenueMale, BigDecimal avgRevenueFemale,
                             BigDecimal avgServiceMale, BigDecimal avgServiceFemale) {
        this.avgRevenueMale = avgRevenueMale;
        this.avgRevenueFemale = avgRevenueFemale;
        this.avgServiceMale = avgServiceMale;
        this.avgServiceFemale = avgServiceFemale;
    }

    // getters and setters
}
