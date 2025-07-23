package com.example.BasicCRM_FWF.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "service_record")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ServiceRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String recordId;
    private String orderId;
    private LocalDateTime bookingDate;
    private String facility;
    private String customerName;
    private String phoneNumber;
    private String baseService;
    private String appliedCard;
    private BigDecimal sessionPrice;
    private String sessionType;
    private String surcharge;
    private BigDecimal totalSurcharge;
    private String shiftEmployee;
    private String performingEmployee;
    private BigDecimal employeeSalary;
    private String status;
    private String rating;
    private String reviewContent;
    private String note;
}
