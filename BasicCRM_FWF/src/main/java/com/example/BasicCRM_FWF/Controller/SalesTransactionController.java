package com.example.BasicCRM_FWF.Controller;

import com.example.BasicCRM_FWF.Service.SalesTransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

// BÁO CÁO DOANH SỐ
@RestController
@RequestMapping("/api/sales")
@RequiredArgsConstructor
// Ban hang doanh so
public class SalesTransactionController {

    private final SalesTransactionService service;

    @PostMapping("/upload")
    public ResponseEntity<String> upload(@RequestParam("file") MultipartFile file) {
        service.importFromExcel(file);
        return ResponseEntity.ok("Upload successful" + file.getOriginalFilename());
    }
}
