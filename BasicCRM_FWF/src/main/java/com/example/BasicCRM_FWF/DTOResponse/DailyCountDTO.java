package com.example.BasicCRM_FWF.DTOResponse;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DailyCountDTO {
    private LocalDateTime date;
    private long count;

    public DailyCountDTO(LocalDateTime date, long count) {
        this.date = date;
        this.count = count;
    }

    public LocalDateTime getDate() { return date; }
    public long getCount() { return count; }
}

