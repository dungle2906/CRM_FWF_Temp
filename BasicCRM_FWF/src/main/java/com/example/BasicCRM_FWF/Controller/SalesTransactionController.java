package com.example.BasicCRM_FWF.Controller;

import com.example.BasicCRM_FWF.DTORequest.CustomerReportRequest;
import com.example.BasicCRM_FWF.DTOResponse.*;
import com.example.BasicCRM_FWF.Service.SalesTransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

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

    @PostMapping("/region-revenue")
    public ResponseEntity<List<RegionRevenueDTO>> getRegionRevenue(@RequestBody CustomerReportRequest request) {
        return ResponseEntity.ok(service.getRevenueByRegion(request));
    }

    @PostMapping("/shop-type-revenue")
    public ResponseEntity<List<ShopTypeRevenueDTO>> getShopTypeRevenue(@RequestBody CustomerReportRequest request) {
        return ResponseEntity.ok(service.getRevenueByShopType(request));
    }

    @PostMapping("/revenue-summary")
    public ResponseEntity<RevenueSummaryDTO> getRevenueSummary(@RequestBody CustomerReportRequest request) {
        return ResponseEntity.ok(service.getRevenueSummary(request));
    }

    @PostMapping("/region-stat")
    public ResponseEntity<List<RegionRevenueStatDTO>> getRegionStat(@RequestBody CustomerReportRequest request) {
        return ResponseEntity.ok(service.getStatus(request));
    }
}
