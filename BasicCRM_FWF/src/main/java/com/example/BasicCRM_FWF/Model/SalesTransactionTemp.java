package com.example.BasicCRM_FWF.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "sales_transaction_temp")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SalesTransactionTemp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer orderCode;

    @ManyToOne
    @JoinColumn(name = "facility_id")
    private Region facility;

    private String note;

    @Lob
    private String details;

    @ManyToOne
    @JoinColumn(name = "service_type_id")
    private ServiceType serviceType;
}
