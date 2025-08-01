package com.example.BasicCRM_FWF.DTOResponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegionPaymentDTO {
    private String region;
    private BigDecimal cash;
    private BigDecimal transfer;
    private BigDecimal creditCard;
}
