package com.example.BasicCRM_FWF.DTOResponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GenderRatioResponse {
    private long male;
    private long female;

    public GenderRatioResponse(GenderRatioResponse male, GenderRatioResponse female) {
    }
}
