package com.example.BasicCRM_FWF.Controller;

import com.example.BasicCRM_FWF.Service.AppUsageRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

// BÁO CÁO KHÁCH SỬ DỤNG APP
@RestController
@RequestMapping("/api/app-usage")
@RequiredArgsConstructor
// khach hang su dung app
public class AppUsageRecordController {

    private final AppUsageRecordService service;

    @PostMapping("/upload")
    public ResponseEntity<String> upload(@RequestParam("file") MultipartFile file) {
        service.importFromExcel(file);
        return ResponseEntity.ok("Upload completed. Check logs for details." + file.getOriginalFilename());
    }

    @GetMapping("/test")
    public String test() {
        return "Hello from test controller";
    }
}