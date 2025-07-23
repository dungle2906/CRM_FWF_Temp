package com.example.BasicCRM_FWF.Controller;

import com.example.BasicCRM_FWF.Service.ServiceRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

// BÁO CÁO DỊCH VỤ
@RestController
@RequestMapping("/api/service-record")
@RequiredArgsConstructor
// dich vu
public class ServiceRecordController {

    private final ServiceRecordService service;

    @PostMapping("/upload")
    public ResponseEntity<String> upload(@RequestParam("file") MultipartFile file) {
        service.importFromExcel(file);
        return ResponseEntity.ok("Upload completed. Check logs for details." + file.getOriginalFilename());
    }
}
