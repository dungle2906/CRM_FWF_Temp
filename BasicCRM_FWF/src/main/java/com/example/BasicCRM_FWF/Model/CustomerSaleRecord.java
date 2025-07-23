package com.example.BasicCRM_FWF.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "customer_sale_record")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CustomerSaleRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime createdAt;
    private String customerName;
    private String customerId;
    private String phoneNumber;
    private String email;
    private String dob;
    private String gender;
    private String address;
    private String district;
    private String province;
    private String facility;
    private String customerType;
    private String source;
    private String cardCode;
    private String careStaff;
    private BigDecimal wallet;
    private BigDecimal debt;
    private BigDecimal prepaidCard;
    private BigDecimal rewardPoint;
}
