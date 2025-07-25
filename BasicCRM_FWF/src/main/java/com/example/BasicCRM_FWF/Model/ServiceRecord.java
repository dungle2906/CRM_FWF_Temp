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

    private Integer recordId;
    private Integer orderId;
    private LocalDateTime bookingDate;

    @ManyToOne
    @JoinColumn(name = "facility_id")
    private Region facility;

    private String customerName;
    private String phoneNumber;

    @ManyToOne
    @JoinColumn(name = "base_service_id")
    private ServiceType baseService;

    @ManyToOne
    @JoinColumn(name = "applied_card_id")
    private AppliedCard appliedCard;

    private BigDecimal sessionPrice;
    private String sessionType;
    private String surcharge;
    private BigDecimal totalSurcharge;
    private String shiftEmployee;
    private String performingEmployee;
    private BigDecimal employeeSalary;
    private String status;
    private Double rating;
    private String reviewContent;
    private String note;
}
