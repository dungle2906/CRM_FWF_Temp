package com.example.BasicCRM_FWF.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "applied_card")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AppliedCard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String card_name;
}
