package com.example.BasicCRM_FWF.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "service_type_temp")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ServiceTypeTemp {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String service_code;

    private String service_name;

    private String price;

    private String category;
}
