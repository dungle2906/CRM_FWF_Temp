package com.example.BasicCRM_FWF.Controller;

import com.example.BasicCRM_FWF.DTORequest.CustomerReportRequest;
import com.example.BasicCRM_FWF.DTOResponse.DailyServiceTypeStatDTO;
import com.example.BasicCRM_FWF.DTOResponse.RegionServiceTypeUsageDTO;
import com.example.BasicCRM_FWF.DTOResponse.ServiceSummaryDTO;
import com.example.BasicCRM_FWF.DTOResponse.ServiceUsageDTO;
import com.example.BasicCRM_FWF.Service.ServiceRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

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

    @PostMapping("/service-type-breakdown")
    public ResponseEntity<List<DailyServiceTypeStatDTO>> getServiceTypeBreakdown(@RequestBody CustomerReportRequest request) {
        return ResponseEntity.ok(service.getServiceTypeBreakdown(request));
    }

    @PostMapping("/service-summary")
    public ResponseEntity<ServiceSummaryDTO> getServiceSummary(@RequestBody CustomerReportRequest request) {
        return ResponseEntity.ok(service.getServiceSummary(request));
    }

    @PostMapping("/region")
    public ResponseEntity<List<RegionServiceTypeUsageDTO>> getServiceUsageByRegion(
            @RequestBody CustomerReportRequest request) {
        return ResponseEntity.ok(service.getServiceUsageByRegion(request));
    }

    @PostMapping("/shop")
    public List<ServiceUsageDTO> getUsageByShop(@RequestBody CustomerReportRequest request) {
        return service.getServiceUsageByShop(request);
    }
}
