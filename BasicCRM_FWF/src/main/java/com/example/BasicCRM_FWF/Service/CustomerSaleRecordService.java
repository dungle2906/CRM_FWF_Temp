package com.example.BasicCRM_FWF.Service;

import com.example.BasicCRM_FWF.DTORequest.CustomerReportRequest;
import com.example.BasicCRM_FWF.DTOResponse.*;
import com.example.BasicCRM_FWF.Model.AppUsageRecord;
import com.example.BasicCRM_FWF.Model.CustomerSaleRecord;
import com.example.BasicCRM_FWF.Model.SalesTransaction;
import com.example.BasicCRM_FWF.Model.ServiceRecord;
import com.example.BasicCRM_FWF.Repository.AppUsageRecordRepository;
import com.example.BasicCRM_FWF.Repository.CustomerSaleRecordRepository;
import com.example.BasicCRM_FWF.Repository.SalesTransactionRepository;
import com.example.BasicCRM_FWF.Repository.ServiceRecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerSaleRecordService {

    private final CustomerSaleRecordRepository customerSaleRecordRepository;
    private final SalesTransactionRepository salesTransactionRepository;
    private final AppUsageRecordRepository appUsageRecordRepository;
    private final ServiceRecordRepository serviceRecordRepository;

    public void importFromExcel(MultipartFile file) {
        int success = 0;
        int failed = 0;

        try (InputStream is = file.getInputStream()) {
            Workbook workbook = WorkbookFactory.create(is);
            Sheet sheet = workbook.getSheetAt(0);

            for (int i = 2; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null || isRowEmpty(row)) {
                    log.info("Stopped at row {} (blank)", i);
                    break;
                }

                try {
                    String createdStr = getString(row.getCell(1));
                    LocalDateTime createdAt = LocalDateTime.parse(createdStr, DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy"));

                    CustomerSaleRecord record = CustomerSaleRecord.builder()
                            .createdAt(createdAt)
                            .customerName(getString(row.getCell(2)))
                            .customerId(getString(row.getCell(3)))
                            .phoneNumber(getString(row.getCell(4)))
                            .email(getString(row.getCell(5)))
                            .dob(getString(row.getCell(6)))
                            .gender(getString(row.getCell(7)))
                            .address(getString(row.getCell(8)))
                            .district(getString(row.getCell(9)))
                            .province(getString(row.getCell(10)))
                            .facility(getString(row.getCell(11)))
                            .customerType(getString(row.getCell(12)))
                            .source(getString(row.getCell(13)))
                            .cardCode(getString(row.getCell(14)))
                            .careStaff(getString(row.getCell(15)))
                            .wallet(toBigDecimal(row.getCell(16)))
                            .debt(toBigDecimal(row.getCell(17)))
                            .prepaidCard(toBigDecimal(row.getCell(18)))
                            .rewardPoint(toBigDecimal(row.getCell(19)))
                            .build();

                    customerSaleRecordRepository.save(record);
                    success++;

                } catch (Exception e) {
                    failed++;
                    log.warn("Row {} skipped due to error: {}", i, e.getMessage());
                }
            }

            log.info("IMPORT CUSTOMER SALE: Success = {}, Failed = {}", success, failed);

        } catch (Exception e) {
            throw new RuntimeException("Failed to import customer sale Excel", e);
        }
    }

    private boolean isRowEmpty(Row row) {
        for (int c = 0; c <= 5; c++) {
            Cell cell = row.getCell(c);
            if (cell != null && cell.getCellType() != CellType.BLANK && !getString(cell).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private String getString(Cell cell) {
        return cell == null ? null : cell.toString().trim();
    }

    private BigDecimal toBigDecimal(Cell cell) {
        try {
            return new BigDecimal(cell.toString().trim().replace(",", ""));
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }

    public CustomerReportResponse getNewCustomerReport(CustomerReportRequest request) {
        LocalDateTime fromDate = request.getFromDate().with(LocalTime.MIN); // 00:00:00
        LocalDateTime toDate = request.getToDate().with(LocalTime.MAX);     // 23:59:59.999999999

        long daysBetween = ChronoUnit.DAYS.between(fromDate.toLocalDate(), toDate.toLocalDate()) + 1;

        LocalDateTime prevFrom = fromDate.minusDays(daysBetween);
        LocalDateTime prevTo = toDate.minusDays(daysBetween);


        List<DailyCustomerCount> currentRange = customerSaleRecordRepository.countNewCustomersByDate(fromDate, toDate);
        List<DailyCustomerCount> previousRange = customerSaleRecordRepository.countNewCustomersByDate(prevFrom, prevTo);

        return new CustomerReportResponse(currentRange, previousRange);
    }

    public GenderRatioResponse getGenderRatio(CustomerReportRequest request) {
        LocalDateTime start = request.getFromDate();
        LocalDateTime end = request.getToDate();

        List<Object[]> results = customerSaleRecordRepository.countGenderGroup(start, end);

        long male = 0;
        long female = 0;

        for (Object[] row : results) {
            String gender = row[0] != null ? row[0].toString().trim() : "";
            long count = ((Number) row[1]).longValue();

            if (gender.equalsIgnoreCase("Nam")) {
                male += count;
            } else if (gender.equalsIgnoreCase("Nữ")) {
                female += count;
            }
        }

        return new GenderRatioResponse(male, female);
    }

    public Map<String, List<DailyCountDTO>> getCustomerTypeTrend(CustomerReportRequest request) {
        LocalDateTime start = request.getFromDate();
        LocalDateTime end = request.getToDate();

        List<Object[]> rawData = customerSaleRecordRepository.countCustomerByTypeAndDay(start, end);

        Map<String, List<DailyCountDTO>> result = new HashMap<>();

        for (Object[] row : rawData) {
            String type = row[0] != null && !row[0].toString().trim().isEmpty()
                    ? row[0].toString().trim()
                    : "Không xác định";
            LocalDateTime date = ((Date) row[1]).toLocalDate().atStartOfDay();
            long count = ((Number) row[2]).longValue();

            result.computeIfAbsent(type, k -> new ArrayList<>())
                    .add(new DailyCountDTO(date, count));
        }

        return result;
    }

    public Map<String, List<DailyCountDTO>> getCustomerSourceTrend(CustomerReportRequest request) {
        LocalDateTime start = request.getFromDate();
        LocalDateTime end = request.getToDate();

        List<Object[]> rawData = customerSaleRecordRepository.countCustomerBySourceAndDay(start, end);

        Map<String, List<DailyCountDTO>> result = new HashMap<>();

        for (Object[] row : rawData) {
            String source = row[0] != null ? row[0].toString().trim() : "null";
            LocalDateTime date = ((Date) row[1]).toLocalDate().atStartOfDay();
            long count = ((Number) row[2]).longValue();

            result.computeIfAbsent(source, k -> new ArrayList<>())
                    .add(new DailyCountDTO(date, count));
        }

        return result;
    }

    public List<AppDownloadStatus> calculateAppDownloadStatus(CustomerReportRequest request) {
        LocalDateTime start = request.getFromDate();
        LocalDateTime end = request.getToDate();

        List<SalesTransaction> transactions = salesTransactionRepository.findAll().stream()
                .filter(tx -> !tx.getOrderDate().isBefore(start) && !tx.getOrderDate().isAfter(end))
                .collect(Collectors.toList());

        List<CustomerSaleRecord> customerSaleRecords = customerSaleRecordRepository.findAll();
        List<ServiceRecord> serviceRecords = serviceRecordRepository.findAll();

        Set<String> downloadedPhones = appUsageRecordRepository.findAll().stream()
                .map(AppUsageRecord::getPhoneNumber)
                .collect(Collectors.toSet());

        Set<String> allPhones = new HashSet<>();
        transactions.forEach(tx -> allPhones.add(tx.getPhoneNumber()));
        customerSaleRecords.forEach(c -> allPhones.add(c.getPhoneNumber()));
        serviceRecords.forEach(s -> allPhones.add(s.getPhoneNumber()));

        Map<LocalDateTime, Set<String>> phoneByDate = new HashMap<>();
        for (SalesTransaction tx : transactions) {
            LocalDateTime date = tx.getOrderDate().toLocalDate().atStartOfDay();
            phoneByDate.computeIfAbsent(date, k -> new HashSet<>()).add(tx.getPhoneNumber());
        }

        List<AppDownloadStatus> result = new ArrayList<>();

        for (Map.Entry<LocalDateTime, Set<String>> entry : phoneByDate.entrySet()) {
            LocalDateTime date = entry.getKey();
            Set<String> phonesOnDate = entry.getValue();

            long downloaded = phonesOnDate.stream()
                    .filter(downloadedPhones::contains)
                    .count();

            long notDownloaded = phonesOnDate.size() - downloaded;

            result.add(new AppDownloadStatus(date, downloaded, notDownloaded));
        }

        return result;
    }

    public CustomerOrderSummaryDTO calculateAppDownloadSummary(LocalDateTime start, LocalDateTime end) {
        List<SalesTransaction> transactions = salesTransactionRepository.findByOrderDateBetween(start, end);
        List<CustomerSaleRecord> customerSaleRecords = customerSaleRecordRepository.findAll();
        List<ServiceRecord> serviceRecords = serviceRecordRepository.findAll();

        Set<String> downloadedPhones = appUsageRecordRepository.findAll().stream()
                .map(AppUsageRecord::getPhoneNumber)
                .collect(Collectors.toSet());

        Set<String> allPhones = new HashSet<>();
        transactions.forEach(tx -> allPhones.add(tx.getPhoneNumber()));
        customerSaleRecords.forEach(c -> allPhones.add(c.getPhoneNumber()));
        serviceRecords.forEach(s -> allPhones.add(s.getPhoneNumber()));

        long downloaded = allPhones.stream().filter(downloadedPhones::contains).count();
        long notDownloaded = allPhones.size() - downloaded;

        return new CustomerOrderSummaryDTO(downloaded, notDownloaded);
    }

    // Service method only
    public CustomerSummaryDTO calculateCustomerSummary(CustomerReportRequest request) {
        LocalDateTime start = request.getFromDate();
        LocalDateTime end = request.getToDate();

        List<String> currentPhones = customerSaleRecordRepository.findPhonesByCreatedAtBetween(start, end)
                .stream().map(this::safePhone).toList();

        Set<String> baseServicePhones = new HashSet<>(
                serviceRecordRepository.findServicePhonesWithBaseServiceBetween(start, end)
                        .stream().map(this::safePhone).toList()
        );

        long total = currentPhones.stream().distinct().count();
        long actual = currentPhones.stream().filter(baseServicePhones::contains).distinct().count();

        // So sánh kỳ trước
        LocalDateTime previousStart = start.minusDays(end.toLocalDate().toEpochDay() - start.toLocalDate().toEpochDay() + 1);
        LocalDateTime previousEnd = start.minusSeconds(1);

        List<String> prevPhones = customerSaleRecordRepository.findPhonesByCreatedAtBetween(previousStart, previousEnd)
                .stream().map(this::safePhone).toList();

        Set<String> prevBaseServicePhones = new HashSet<>(
                serviceRecordRepository.findServicePhonesWithBaseServiceBetween(previousStart, previousEnd)
                        .stream().map(this::safePhone).toList()
        );

        long prevTotal = prevPhones.stream().distinct().count();
        long prevActual = prevPhones.stream().filter(prevBaseServicePhones::contains).distinct().count();

        double growthTotal = calculateGrowth(prevTotal, total);
        double growthActual = calculateGrowth(prevActual, actual);

        return new CustomerSummaryDTO(total, actual, growthTotal, growthActual);
    }

    private double calculateGrowth(long previous, long current) {
        if (previous == 0) return 100.0;
        return ((double) (current - previous) / previous) * 100.0;
    }

    private String safePhone(String phone) {
        return phone == null ? "" : phone.trim();
    }

    public List<DailyCustomerOrderTrendDTO> calculateCustomerOrderTrends(LocalDateTime start, LocalDateTime end) {
        List<SalesTransaction> transactions = salesTransactionRepository.findByOrderDateBetween(start, end);

        Set<String> seenPhones = new HashSet<>();
        Map<LocalDateTime, List<SalesTransaction>> grouped = transactions.stream()
                .collect(Collectors.groupingBy(tx -> tx.getOrderDate().toLocalDate().atStartOfDay()));

        List<DailyCustomerOrderTrendDTO> result = new ArrayList<>();

        for (Map.Entry<LocalDateTime, List<SalesTransaction>> entry : grouped.entrySet()) {
            LocalDateTime date = entry.getKey();
            List<SalesTransaction> dayTx = entry.getValue();

            long newCount = 0;
            long oldCount = 0;

            for (SalesTransaction tx : dayTx) {
                if (tx.getPhoneNumber() != null && seenPhones.add(tx.getPhoneNumber())) {
                    newCount++;
                } else {
                    oldCount++;
                }
            }

            result.add(new DailyCustomerOrderTrendDTO(date, newCount, oldCount));
        }

        return result;
    }

    public CustomerOrderSummaryDTO calculateCustomerOrderSummary(LocalDateTime start, LocalDateTime end) {
        List<SalesTransaction> transactions = salesTransactionRepository.findByOrderDateBetween(start, end);

        Set<String> seenPhones = new HashSet<>();
        long newCustomers = 0;
        long oldCustomers = 0;

        for (SalesTransaction tx : transactions) {
            if (tx.getPhoneNumber() != null && seenPhones.add(tx.getPhoneNumber())) {
                newCustomers++;
            } else {
                oldCustomers++;
            }
        }

        return new CustomerOrderSummaryDTO(newCustomers, oldCustomers);
    }

    public CustomerOrderSummaryDTO calculateGenderSummary(LocalDateTime start, LocalDateTime end) {
        List<CustomerSaleRecord> customers = customerSaleRecordRepository.findAll();
        List<ServiceRecord> services = serviceRecordRepository.findAll().stream()
                .filter(s -> s.getBookingDate() != null && !s.getBookingDate().isBefore(start) && !s.getBookingDate().isAfter(end))
                .collect(Collectors.toList());

        Set<String> servicePhones = services.stream()
                .map(ServiceRecord::getPhoneNumber)
                .collect(Collectors.toSet());

        Map<String, String> phoneToGender = customers.stream()
                .filter(c -> c.getPhoneNumber() != null && c.getGender() != null)
                .collect(Collectors.toMap(CustomerSaleRecord::getPhoneNumber, CustomerSaleRecord::getGender, (g1, g2) -> g1));

        Set<String> counted = new HashSet<>();
        long male = 0;
        long female = 0;

        for (String phone : servicePhones) {
            if (counted.contains(phone)) continue;
            String gender = phoneToGender.get(phone);
            if (gender == null) continue;

            if (gender.equalsIgnoreCase("Nam")) male++;
            else if (gender.equalsIgnoreCase("Nữ")) female++;

            counted.add(phone);
        }

        return new CustomerOrderSummaryDTO(female, male);
    }

    public GenderRevenueDTO calculateGenderRevenue(LocalDateTime start, LocalDateTime end) {
        List<CustomerSaleRecord> customers = customerSaleRecordRepository.findAll();
        Map<String, String> phoneToGender = customers.stream()
                .filter(c -> c.getPhoneNumber() != null && c.getGender() != null)
                .collect(Collectors.toMap(CustomerSaleRecord::getPhoneNumber, CustomerSaleRecord::getGender, (g1, g2) -> g1));

        List<SalesTransaction> sales = salesTransactionRepository.findByOrderDateBetween(start, end);
        List<ServiceRecord> services = serviceRecordRepository.findAll().stream()
                .filter(s -> s.getBookingDate() != null && !s.getBookingDate().isBefore(start) && !s.getBookingDate().isAfter(end))
                .collect(Collectors.toList());

        Map<String, List<BigDecimal>> revenueMap = new HashMap<>();
        Map<String, List<BigDecimal>> serviceMap = new HashMap<>();

        for (SalesTransaction tx : sales) {
            String gender = phoneToGender.get(tx.getPhoneNumber());
            if (gender != null) {
                revenueMap.computeIfAbsent(gender, k -> new ArrayList<>()).add(tx.getTotalAmount());
            }
        }

        for (ServiceRecord sr : services) {
            String gender = phoneToGender.get(sr.getPhoneNumber());
            if (gender != null) {
                serviceMap.computeIfAbsent(gender, k -> new ArrayList<>()).add(sr.getSessionPrice());
            }
        }

        BigDecimal avgRevenueMale = avg(revenueMap.get("Nam"));
        BigDecimal avgRevenueFemale = avg(revenueMap.get("Nữ"));
        BigDecimal avgServiceMale = avg(serviceMap.get("Nam"));
        BigDecimal avgServiceFemale = avg(serviceMap.get("Nữ"));

        return new GenderRevenueDTO(avgRevenueMale, avgRevenueFemale, avgServiceMale, avgServiceFemale);
    }

    private BigDecimal avg(List<BigDecimal> values) {
        if (values == null || values.isEmpty()) return BigDecimal.ZERO;
        return values.stream().reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(values.size()), RoundingMode.HALF_UP);
    }

    public PaymentBreakdownDTO calculatePaymentStatus(CustomerReportRequest request, boolean isNew) {
        LocalDateTime start = request.getFromDate();
        LocalDateTime end = request.getToDate();

        Set<String> knownPhones = customerSaleRecordRepository.findAll().stream()
                .map(CustomerSaleRecord::getPhoneNumber)
                .collect(Collectors.toSet());

        List<SalesTransaction> transactions = salesTransactionRepository.findByOrderDateBetween(start, end);

        if (isNew) {
            transactions = transactions.stream()
                    .filter(tx -> !knownPhones.contains(tx.getPhoneNumber()))
                    .collect(Collectors.toList());
        } else {
            transactions = transactions.stream()
                    .filter(tx -> knownPhones.contains(tx.getPhoneNumber()))
                    .collect(Collectors.toList());
        }

        BigDecimal totalCash = BigDecimal.ZERO;
        BigDecimal totalTransfer = BigDecimal.ZERO;
        BigDecimal totalPrepaid = BigDecimal.ZERO;
        BigDecimal totalDebt = BigDecimal.ZERO;

        for (SalesTransaction tx : transactions) {
            totalCash = totalCash.add(Optional.ofNullable(tx.getCash()).orElse(BigDecimal.ZERO));
            totalTransfer = totalTransfer.add(Optional.ofNullable(tx.getTransfer()).orElse(BigDecimal.ZERO));
            totalPrepaid = totalPrepaid.add(Optional.ofNullable(tx.getPrepaidCard()).orElse(BigDecimal.ZERO));
            totalDebt = totalDebt.add(Optional.ofNullable(tx.getDebt()).orElse(BigDecimal.ZERO));
        }

        return new PaymentBreakdownDTO(totalCash, totalTransfer, totalPrepaid, totalDebt);
    }

    public TotalCustomerResponse getCustomerSaleRecord(CustomerReportRequest request) {
        LocalDateTime fromDate = request.getFromDate().with(LocalTime.MIN);
        LocalDateTime toDate = request.getToDate().with(LocalTime.MAX);

        long days = ChronoUnit.DAYS.between(fromDate.toLocalDate(), toDate.toLocalDate()) + 1;

        LocalDateTime prevFrom = fromDate.minusDays(days);
        LocalDateTime prevTo = toDate.minusDays(days);

        long current = salesTransactionRepository.findByOrderDateBetween(fromDate, toDate).stream()
                .map(SalesTransaction::getPhoneNumber)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet()).size();

        long previous = salesTransactionRepository.findByOrderDateBetween(prevFrom, prevTo).stream()
                .map(SalesTransaction::getPhoneNumber)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet()).size();

        double percentChange = 0.0;
        if (previous > 0) {
            percentChange = ((double) (current - previous) / previous) * 100.0;
        }

        return new TotalCustomerResponse(current, previous, percentChange);
    }

    public List<HourlyFacilityStatsDTO> getHourlyStats(LocalDateTime start, LocalDateTime end) {
        List<ServiceRecord> records = serviceRecordRepository.findByBookingDateBetween(start, end);

        Map<String, HourlyFacilityStatsDTO> facilityStatsMap = new HashMap<>();

        for (ServiceRecord record : records) {
            if (record.getBookingDate() == null || record.getFacility() == null) continue;

            String facility = record.getFacility();
            int hour = record.getBookingDate().getHour();

            facilityStatsMap
                    .computeIfAbsent(facility, HourlyFacilityStatsDTO::new)
                    .addCount(hour);
        }

        return facilityStatsMap.values().stream()
                .sorted((a, b) -> Long.compare(b.getTotal(), a.getTotal())) // sort descending
                .collect(Collectors.toList());
    }
}
