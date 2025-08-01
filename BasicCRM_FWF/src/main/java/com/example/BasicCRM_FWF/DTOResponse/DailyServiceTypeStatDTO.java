package com.example.BasicCRM_FWF.DTOResponse;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
public class DailyServiceTypeStatDTO {
    private LocalDate date;
    private String type; // Combo, Dịch vụ, Added on, Fox card
    private Long count;

    public DailyServiceTypeStatDTO(LocalDate date, String type, Long count) {
        this.date = date;
        this.type = type;
        this.count = count;
    }

    // Getters and setters omitted for brevity
}