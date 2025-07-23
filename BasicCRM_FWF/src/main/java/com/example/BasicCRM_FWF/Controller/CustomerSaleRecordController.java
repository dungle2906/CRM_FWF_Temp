package com.example.BasicCRM_FWF.Controller;

import com.example.BasicCRM_FWF.DTORequest.CustomerReportRequest;
import com.example.BasicCRM_FWF.DTOResponse.*;
import com.example.BasicCRM_FWF.Service.CustomerSaleRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

// BÁO CÁO KHÁCH
@RestController
@RequestMapping("/api/customer-sale")
@RequiredArgsConstructor
// danh sach ban hang
public class CustomerSaleRecordController {

    private final CustomerSaleRecordService service;

    @PostMapping("/upload")
    public ResponseEntity<String> upload(@RequestParam("file") MultipartFile file) {
        service.importFromExcel(file);
        return ResponseEntity.ok("Upload completed. Check logs for details." + file.getOriginalFilename());
    }

    @PostMapping("/new-customer-lineChart")
    public ResponseEntity<CustomerReportResponse> getNewCustomerReport(@RequestBody CustomerReportRequest request) {
        return ResponseEntity.ok(service.getNewCustomerReport(request));
    }

    @PostMapping("/gender-ratio")
    public ResponseEntity<GenderRatioResponse> getGenderRatio(@RequestBody CustomerReportRequest request) {
        return ResponseEntity.ok(service.getGenderRatio(request));
    }

    @PostMapping("/customer-summary")
    public ResponseEntity<CustomerSummaryDTO> getCustomerSummary(@RequestBody CustomerReportRequest request) {
        CustomerSummaryDTO summary = service.calculateCustomerSummary(request);
        return ResponseEntity.ok(summary);
    }

    @PostMapping("/customer-type-trend")
    public ResponseEntity<Map<String, List<DailyCountDTO>>> getCustomerTypeTrend(
            @RequestBody CustomerReportRequest request) {
        return ResponseEntity.ok(service.getCustomerTypeTrend(request));
    }

    @PostMapping("/customer-source-trend")
    public ResponseEntity<Map<String, List<DailyCountDTO>>> getCustomerSourceTrend(
            @RequestBody CustomerReportRequest request) {
        return ResponseEntity.ok(service.getCustomerSourceTrend(request));
    }

    @PostMapping("/app-download-status")
    public ResponseEntity<List<AppDownloadStatus>> getAppDownloadStats(
            @RequestBody CustomerReportRequest request
    ) {
        return ResponseEntity.ok(service.calculateAppDownloadStatus(request));
    }

    @PostMapping("/customer-old-new-order-trends")
    public ResponseEntity<List<DailyCustomerOrderTrendDTO>> getTrends(@RequestBody CustomerReportRequest request) {
        return ResponseEntity.ok(service.calculateCustomerOrderTrends(request.getFromDate(), request.getToDate()));
    }

    @PostMapping("/customer-old-new-order-pieChart")
    public CustomerOrderSummaryDTO getSummary(@RequestBody CustomerReportRequest request) {
        return service.calculateCustomerOrderSummary(request.getFromDate(), request.getToDate());
    }

    @PostMapping("/app-download-pieChart")
    public CustomerOrderSummaryDTO getAppDownloadSummary(@RequestBody CustomerReportRequest request) {
        return service.calculateAppDownloadSummary(request.getFromDate(), request.getToDate());
    }

    @PostMapping("/gender-distribution")
    public CustomerOrderSummaryDTO getGenderSummary(@RequestBody CustomerReportRequest request) {
        return service.calculateGenderSummary(request.getFromDate(), request.getToDate());
    }

    @PostMapping("/gender-revenue")
    public GenderRevenueDTO getGenderRevenue(@RequestBody CustomerReportRequest request) {
        return service.calculateGenderRevenue(request.getFromDate(), request.getToDate());
    }

    @PostMapping("/payment-percent-new")
    public PaymentBreakdownDTO getNewCustomerPayments(@RequestBody CustomerReportRequest request) {
        return service.calculatePaymentStatus(request, true);
    }

    @PostMapping("/payment-percent-old")
    public PaymentBreakdownDTO getOldCustomerPayments(@RequestBody CustomerReportRequest request) {
        return service.calculatePaymentStatus(request, false);
    }

    @PostMapping("/unique-customers-comparison")
    public TotalCustomerResponse compareUniqueCustomers(@RequestBody CustomerReportRequest request) {
        return service.getCustomerSaleRecord(request);
    }

    @PostMapping("/facility-hour-service")
    public ResponseEntity<List<HourlyFacilityStatsDTO>> getHourlyReport(@RequestBody CustomerReportRequest request) {
        LocalDateTime start = request.getFromDate();
        LocalDateTime end = request.getToDate();

        List<HourlyFacilityStatsDTO> stats = service.getHourlyStats(start, end);
        return ResponseEntity.ok(stats);
    }
}
