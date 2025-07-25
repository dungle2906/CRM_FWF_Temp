package com.example.BasicCRM_FWF.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "app_usage_record")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AppUsageRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer customerId;
    private String customerName;
    private String phoneNumber;
    private Boolean device;
    private Boolean status;
    private LocalDateTime installedAt;
}