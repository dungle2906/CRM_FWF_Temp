package com.example.BasicCRM_FWF.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "sales_transaction")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SalesTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String orderCode;
    private String facility;
    private LocalDateTime orderDate;
    private String customerName;
    private String phoneNumber;
    private BigDecimal originalPrice;
    private BigDecimal priceChange;
    private BigDecimal totalAmount;
    private BigDecimal cash;
    private BigDecimal transfer;
    private BigDecimal prepaidCard;
    private BigDecimal debt;
    private String note;

    @Lob
    private String details;
}