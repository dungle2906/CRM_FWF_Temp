package com.example.BasicCRM_FWF.DTOResponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

// DTO class to represent the service usage summary per shop
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServiceUsageDTO {
    private String shopName;     // Name of the shop
    private String serviceType;  // Type of service (Combo, Dịch vụ, etc.)
    private int total;           // Total number of services performed
} 