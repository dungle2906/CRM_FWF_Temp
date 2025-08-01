package com.example.BasicCRM_FWF.Service;

import com.example.BasicCRM_FWF.DTORequest.CustomerReportRequest;
import com.example.BasicCRM_FWF.DTOResponse.*;
import com.example.BasicCRM_FWF.Model.AppliedCard;
import com.example.BasicCRM_FWF.Model.Region;
import com.example.BasicCRM_FWF.Model.ServiceRecord;
import com.example.BasicCRM_FWF.Model.ServiceType;
import com.example.BasicCRM_FWF.Repository.AppliedCardRepository;
import com.example.BasicCRM_FWF.Repository.RegionRepository;
import com.example.BasicCRM_FWF.Repository.ServiceRecordRepository;
import com.example.BasicCRM_FWF.Repository.ServiceTypeRepository;
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
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ServiceRecordService {

    private final ServiceRecordRepository repository;
    private final RegionRepository regionRepository;
    private final ServiceTypeRepository serviceTypeRepository;
    private final AppliedCardRepository appliedCardRepository;

//    public void importFromExcel(MultipartFile file) {
//        int success = 0;
//        int failed = 0;
//
//        try (InputStream is = file.getInputStream()) {
//            Workbook workbook = WorkbookFactory.create(is);
//            Sheet sheet = workbook.getSheetAt(0);
//
//            // Map chuẩn hóa cho Region, ServiceType, AppliedCard
//            Map<String, Region> regionMap = regionRepository.findAll().stream()
//                    .collect(Collectors.toMap(r -> r.getShop_name().trim().toLowerCase(), Function.identity()));
//
//            Map<String, AppliedCard> appliedCardMap = appliedCardRepository.findAll().stream()
//                    .collect(Collectors.toMap(c -> c.getCard_name().trim().toLowerCase(), Function.identity()));
//
//            for (int i = 2; i <= sheet.getLastRowNum(); i++) {
//                Row row = sheet.getRow(i);
//                if (row == null || isRowEmpty(row)) {
//                    log.info("Stopped at row {} (blank)", i);
//                    break;
//                }
//
//                try {
//                    String dateStr = getString(row.getCell(3));
//                    LocalDateTime bookingDate = LocalDateTime.parse(dateStr, DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy"));
//
//                    String key = getString(row.getCell(4)).trim().toLowerCase();
//                    Region facility = regionMap.get(key);
//
//
//                    String key2 = getString(row.getCell(7)).trim().replaceAll("\\s+", " ");
//                    if(key2.startsWith("QUÀ TẶNG")) { key2 = "QUÀ TẶNG KÈM THẺ TIỀN FOXIE 1,2 - DV CRYO CLEANSE - RỬA MẶT CẤP ẨM, DA CĂNG BÓNG";}
//                    if(key2.startsWith("COMBO 8: SOOTHING FOR SENSITIVE SKIN - LÀM DỊU")) { key2 = "COMBO 8: SOOTHING FOR SENSITIVE SKIN - LÀM DỊU VÀ CHĂM SÓC DA NHẠY CẢM (buổi lẻ)"; }
//
//                    ServiceType serviceType = serviceTypeRepository.findByName(key2+"%");
//
//                    String key3 = getString(row.getCell(8)).trim().toLowerCase();
//                    AppliedCard appliedCard = appliedCardMap.get(key3);
//
//                    ServiceRecord record = ServiceRecord.builder()
//                            .recordId(Integer.parseInt(getString(row.getCell(1)).substring(1)))
//                            .orderId(Integer.parseInt(getString(row.getCell(2)).substring(1)))
//                            .bookingDate(bookingDate)
//                            .facility(facility)
//                            .customerName(getString(row.getCell(5)))
//                            .phoneNumber(getString(row.getCell(6)))
//                            .baseService(serviceType)
//                            .appliedCard(appliedCard)
//                            .sessionPrice(toBigDecimal(row.getCell(9)))
//                            .sessionType(getString(row.getCell(10)).startsWith("Buổi thường") || getString(row.getCell(19)).isBlank() ? null : getString(row.getCell(10)))
//                            .surcharge(getString(row.getCell(11)).startsWith("Không có") || getString(row.getCell(19)).isBlank() ? null : getString(row.getCell(11)))
//                            .totalSurcharge(getString(row.getCell(12)).startsWith("0") || getString(row.getCell(19)).isBlank() ? null : toBigDecimal(row.getCell(12)))
//                            .shiftEmployee(getString(row.getCell(13)))
//                            .performingEmployee(getString(row.getCell(14)))
//                            .employeeSalary(toBigDecimal(row.getCell(15)))
//                            .status(getString(row.getCell(16)).startsWith("Hoàn thành") || getString(row.getCell(19)).isBlank() ? null : getString(row.getCell(16)))
//                            .rating(getString(row.getCell(17)).startsWith("Chưa đánh giá") || getString(row.getCell(19)).isBlank() ? null : Double.valueOf(getString(row.getCell(17))))
//                            .reviewContent(getString(row.getCell(18)).startsWith("Chưa có") || getString(row.getCell(19)).isBlank() ? null : getString(row.getCell(18)))
//                            .note(getString(row.getCell(19)).startsWith("Chưa có") || getString(row.getCell(19)).isBlank() ? null : getString(row.getCell(19)))
//                            .build();
//
//                    repository.save(record);
//                    success++;
//
//                } catch (Exception e) {
//                    failed++;
//                    log.warn("Row {} failed: {}", i, e.getMessage());
//                }
//            }
//
//            log.info("IMPORT SERVICE RECORD: Success = {}, Failed = {}", success, failed);
//
//        } catch (Exception e) {
//            throw new RuntimeException("Failed to import service record Excel", e);
//        }
//    }

    public void importFromExcel2(MultipartFile file) {
        int success = 0;
        int failed = 0;

        try (InputStream is = file.getInputStream()) {
            Workbook workbook = WorkbookFactory.create(is);
            Sheet sheet = workbook.getSheetAt(0);

            // Map chuẩn hóa cho Region, ServiceType, AppliedCard
            Map<String, Region> regionMap = regionRepository.findAll().stream()
                    .collect(Collectors.toMap(r -> r.getShop_name().trim().toLowerCase(), Function.identity()));

            Map<String, ServiceType> serviceTypeMap = serviceTypeRepository.findAll().stream()
                    .collect(Collectors.toMap(s -> s.getService_name().trim().toLowerCase(), Function.identity()));

            Map<String, AppliedCard> appliedCardMap = appliedCardRepository.findAll().stream()
                    .collect(Collectors.toMap(c -> c.getCard_name().trim().toLowerCase(), Function.identity()));

            for (int i = 2; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null || isRowEmpty(row)) {
                    log.info("Stopped at row {} (blank)", i);
                    break;
                }

                try {
                    String dateStr = getString(row.getCell(3));
                    LocalDateTime bookingDate = LocalDateTime.parse(dateStr, DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy"));

                    String key = getString(row.getCell(4)).trim().toLowerCase();
                    Region facility = regionMap.get(key);

                    String key2 = getString(row.getCell(7)).trim().toLowerCase();
                    ServiceType serviceType = serviceTypeMap.get(key2);

                    String key3 = getString(row.getCell(8)).trim().toLowerCase();
                    AppliedCard appliedCard = appliedCardMap.get(key3);

                    ServiceRecord record = ServiceRecord.builder()
                            .recordId(Integer.parseInt(getString(row.getCell(1)).substring(1)))
                            .orderId(Integer.parseInt(getString(row.getCell(2)).substring(1)))
                            .bookingDate(bookingDate)
                            .facility(facility)
                            .customerName(getString(row.getCell(5)))
                            .phoneNumber(getString(row.getCell(6)))
                            .baseService(serviceType)
                            .appliedCard(appliedCard)
                            .sessionPrice(toBigDecimal(row.getCell(9)))
                            .sessionType(getString(row.getCell(10)).startsWith("Buổi thường") || getString(row.getCell(19)).isBlank() ? null : getString(row.getCell(10)))
                            .surcharge(getString(row.getCell(11)).startsWith("Không có") || getString(row.getCell(19)).isBlank() ? null : getString(row.getCell(11)))
                            .totalSurcharge(getString(row.getCell(12)).startsWith("0") || getString(row.getCell(19)).isBlank() ? null : toBigDecimal(row.getCell(12)))
                            .shiftEmployee(getString(row.getCell(13)))
                            .performingEmployee(getString(row.getCell(14)))
                            .employeeSalary(toBigDecimal(row.getCell(15)))
                            .status(getString(row.getCell(16)).startsWith("Hoàn thành") || getString(row.getCell(19)).isBlank() ? null : getString(row.getCell(16)))
                            .rating(getString(row.getCell(17)).startsWith("Chưa đánh giá") || getString(row.getCell(19)).isBlank() ? null : Double.valueOf(getString(row.getCell(17))))
                            .reviewContent(getString(row.getCell(18)).startsWith("Chưa có") || getString(row.getCell(19)).isBlank() ? null : getString(row.getCell(18)))
                            .note(getString(row.getCell(19)).startsWith("Chưa có") || getString(row.getCell(19)).isBlank() ? null : getString(row.getCell(19)))
                            .build();

                    repository.save(record);
                    success++;

                } catch (Exception e) {
                    failed++;
                    log.warn("Row {} failed: {}", i, e.getMessage());
                }
            }

            log.info("IMPORT SERVICE RECORD: Success = {}, Failed = {}", success, failed);

        } catch (Exception e) {
            throw new RuntimeException("Failed to import service record Excel", e);
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

    public List<DailyServiceTypeStatDTO> getServiceTypeBreakdown(CustomerReportRequest request) {
        List<Object[]> raw = repository.countServiceTypesPerDay(request.getFromDate(), request.getToDate());
        return raw.stream()
                .map(obj -> new DailyServiceTypeStatDTO(
                        ((Date) obj[0]).toLocalDate(),
                        (String) obj[1],
                        ((Number) obj[2]).longValue()
                )).collect(Collectors.toList());
    }

    public ServiceSummaryDTO getServiceSummary(CustomerReportRequest request) {
        LocalDateTime start = request.getFromDate();
        LocalDateTime end = request.getToDate();

        LocalDateTime prevStart = start.minusDays(end.toLocalDate().toEpochDay() - start.toLocalDate().toEpochDay() + 1);
        LocalDateTime prevEnd = start.minusSeconds(1);

        long combo = count("combo", start, end);
        long le = count("dv", start, end);
        long ct = count("qt", start, end);
        long gift = count("fox", start, end);
        long total = combo + le + ct + gift;

        long prevCombo = count("combo", prevStart, prevEnd);
        long prevLe = count("dv", prevStart, prevEnd);
        long prevCT = count("qt", prevStart, prevEnd);
        long prevGift = count("fox", prevStart, prevEnd);
        long prevTotal = prevCombo + prevLe + prevCT + prevGift;

        return new ServiceSummaryDTO(
                combo, le, ct, gift, total,
                prevCombo, prevLe, prevCT, prevGift, prevTotal,
                growth(prevCombo, combo),
                growth(prevLe, le),
                growth(prevCT, ct),
                growth(prevGift, gift),
                growth(prevTotal, total)
        );
    }

    private long count(String prefix, LocalDateTime start, LocalDateTime end) {
        return repository.countByServiceCodePrefix(prefix, start, end);
    }

    private double growth(long prev, long curr) {
        if (prev == 0) return 100.0;
        return ((double) (curr - prev) / prev) * 100.0;
    }

    public List<RegionServiceTypeUsageDTO> getServiceUsageByRegion(CustomerReportRequest request) {
        List<Object[]> result = repository.findRegionServiceTypeCount(
                request.getFromDate(), request.getToDate()
        );

        return result.stream().map(r -> new RegionServiceTypeUsageDTO(
                r[0].toString(),
                r[1].toString(),
                ((Number) r[2]).longValue()
        )).collect(Collectors.toList());
    }

    public List<ServiceUsageDTO> getServiceUsageByShop(CustomerReportRequest request) {
        List<Object[]> raw = repository.findServiceUsageByShop(request.getFromDate(), request.getToDate());

        // Convert each result row into a DTO
        return raw.stream().map(obj -> new ServiceUsageDTO(
                obj[0].toString(),  // shop name
                obj[1].toString(),  // service type
                ((Number) obj[2]).intValue() // total count
        )).collect(Collectors.toList());
    }

    public List<TopServiceUsage> getTop10ServiceUsage(CustomerReportRequest request) {
        LocalDateTime start = request.getFromDate();
        LocalDateTime end = request.getToDate();

        // Map raw query result into DTOs
        return repository.findTop10ServiceNames(start, end).stream()
                .map(row -> new TopServiceUsage(
                        row[0] != null ? row[0].toString() : "Không xác định",
                        ((Number) row[1]).longValue()
                ))
                .collect(Collectors.toList());
    }

    public List<TopServiceRevenue> getTop10ServicesByRevenue(CustomerReportRequest request) {
        List<Object[]> rawResults = repository.findTop10ServicesByRevenue(
                request.getFromDate(),
                request.getToDate()
        );

        // Convert raw query result to typed DTOs
        return rawResults.stream()
                .map(obj -> new TopServiceRevenue(
                        obj[0] != null ? obj[0].toString() : "Không xác định", // Service name or fallback
                        obj[1] != null ? (BigDecimal) obj[1] : BigDecimal.ZERO // Convert revenue
                ))
                .collect(Collectors.toList());
    }

    public List<TopServiceRevenue> getBottom3ServiceRevenue(CustomerReportRequest request) {
        LocalDateTime start = request.getFromDate();
        LocalDateTime end = request.getToDate();

        return repository.findTopBottomServicesRevenue(start, end).stream()
                .map(objects -> new TopServiceRevenue(
                        objects[0] != null ? objects[0].toString() : "Không xác định",
                        objects[1] != null ? (BigDecimal) objects[1] : BigDecimal.ZERO
                )).collect(Collectors.toList());
    }

    public List<TopServiceUsage> getBottom3ServicesUsage(CustomerReportRequest request) {
        LocalDateTime start = request.getFromDate();
        LocalDateTime end = request.getToDate();

        return repository.findTopBottomServicesUsage(start, end).stream()
                .map(objects -> new TopServiceUsage(
                        objects[0] != null ? objects[0].toString() : "Không xác định",
                        ((Number) objects[1]).longValue()
                )).collect(Collectors.toList());
    }

    public List<ServiceStatsDTO> getTopServiceTable(CustomerReportRequest request) {
        LocalDateTime start = request.getFromDate();
        LocalDateTime end = request.getToDate();

        long rangeDays = ChronoUnit.DAYS.between(start, end);
        LocalDateTime prevStart = start.minusDays(rangeDays);
        LocalDateTime prevEnd = end.minusDays(rangeDays);

        Map<String, Object[]> previousData = repository.findTop10ServicesWithPreviousData(prevStart, prevEnd)
                .stream()
                .collect(Collectors.toMap(
                        row -> row[0].toString(),
                        row -> row
                ));

        List<Object[]> currentData = repository.findTop10ServicesWithCurrentData(start, end);
        long totalUsage = currentData.stream().mapToLong(r -> ((Number) r[2]).longValue()).sum();
        BigDecimal totalRevenue = currentData.stream()
                .map(r -> (BigDecimal) r[3])
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return currentData.stream().map(row -> {
            String name = row[0].toString();
            String type = row[1].toString();
            long currentCount = ((Number) row[2]).longValue();
            BigDecimal currentRevenue = (BigDecimal) row[3];

            Object[] prev = previousData.get(name);
            long prevCount = prev != null ? ((Number) prev[1]).longValue() : 0;
            BigDecimal prevRevenue = prev != null && prev[2] != null ? (BigDecimal) prev[2] : BigDecimal.ZERO;

            long deltaCount = currentCount - prevCount;
            double deltaRevenuePct = prevRevenue.compareTo(BigDecimal.ZERO) == 0 ? 100.0 :
                    currentRevenue.subtract(prevRevenue).multiply(BigDecimal.valueOf(100)).divide(prevRevenue, 2, RoundingMode.HALF_UP).doubleValue();

            double usagePct = totalUsage == 0 ? 0.0 : ((double) currentCount / totalUsage) * 100.0;
            double revenuePct = totalRevenue.compareTo(BigDecimal.ZERO) == 0 ? 0.0 :
                    currentRevenue.multiply(BigDecimal.valueOf(100)).divide(totalRevenue, 2, RoundingMode.HALF_UP).doubleValue();

            return ServiceStatsDTO.builder()
                    .serviceName(name)
                    .type(type)
                    .usageCount(currentCount)
                    .usageDeltaCount(deltaCount)
                    .usagePercent(usagePct)
                    .totalRevenue(currentRevenue)
                    .revenueDeltaPercent(deltaRevenuePct)
                    .revenuePercent(revenuePct)
                    .build();
        }).collect(Collectors.toList());
    }

}
