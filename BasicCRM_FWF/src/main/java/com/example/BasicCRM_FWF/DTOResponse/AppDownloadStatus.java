package com.example.BasicCRM_FWF.DTOResponse;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AppDownloadStatus {
    private LocalDateTime date;
    private long downloaded;
    private long notDownloaded;

    public AppDownloadStatus(LocalDateTime date, long downloaded, long notDownloaded) {
        this.date = date;
        this.downloaded = downloaded;
        this.notDownloaded = notDownloaded;
    }
}