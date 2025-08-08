package com.example.BasicCRM_FWF.Service;

import com.example.BasicCRM_FWF.DTORequest.CustomerReportRequest;
import com.example.BasicCRM_FWF.DTOResponse.*;
import com.example.BasicCRM_FWF.Model.Region;
import com.example.BasicCRM_FWF.Model.SalesTransaction;
import com.example.BasicCRM_FWF.Model.ServiceType;
import com.example.BasicCRM_FWF.Repository.RegionRepository;
import com.example.BasicCRM_FWF.Repository.SalesTransactionRepository;
import com.example.BasicCRM_FWF.Repository.ServiceTypeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.sql.Date;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SalesTransactionService {

    private final SalesTransactionRepository repository;
    private final RegionRepository regionRepository;
    private final ServiceTypeRepository serviceTypeRepository;

    public void importFromExcel(MultipartFile file) {
        int successCount = 0;
        int failCount = 0;
        int failed = 0;

        try (InputStream is = file.getInputStream()) {
            Workbook workbook = WorkbookFactory.create(is);
            Sheet sheet = workbook.getSheetAt(0);

            // ✅ Tạo map Region: shop_name (chuẩn hoá) → Region
            Map<String, Region> regionMap = regionRepository.findAll()
                    .stream()
                    .collect(Collectors.toMap(
                            r -> r.getShop_name().trim().toLowerCase(),
                            Function.identity()
                    ));

            for (int i = 2; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);

                if (row == null || isRowEmpty(row)) {
                    log.info("Stopped at row {} (blank)", i);
                    break;
                }

                try {
                    String orderCodeStr = getString(row.getCell(1));
                    String dateTimeStr = getString(row.getCell(3));

                    if (orderCodeStr == null || dateTimeStr == null) {
                        log.warn("Row {} skipped: missing required fields", i);
                        failCount++;
                        continue;
                    }

                    LocalDateTime orderDate = LocalDateTime.parse(
                            dateTimeStr,
                            DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy")
                    );

                    // ✅ Tra Region bằng shop name (cột CƠ SỞ trong Excel, cột 11 tính từ 0)
                    String shopName = getString(row.getCell(2)).trim().toLowerCase();
                    Region facilityRecordService = regionMap.get(shopName);

                    if (facilityRecordService == null) {
                        log.warn("Row {} skipped: Không tìm thấy Region cho tên '{}'", i, shopName);
                        failed++;
                        continue;
                    }

                    ServiceType serviceType = null;
                    String allComboString = getString(row.getCell(26)).trim().replaceAll("\\s+", " ");
                    String originalString;
                    int semicolonIndex = allComboString.indexOf(";");
                    if (semicolonIndex != -1) {
                        originalString = allComboString.substring(0, semicolonIndex);
                    } else {
                        originalString = allComboString;  // fallback nếu không có dấu ;
//                        log.warn("Row {}: Semicolon not found in '{}'", i, allComboString);
                    }
                    String perfectString = cleanTailNumber(originalString);

                    if (perfectString.endsWith("))")) {
                        perfectString = perfectString.substring(0, perfectString.length() - 1);
                    } else if (perfectString.endsWith(" )")) {
                        int open = perfectString.lastIndexOf("(");
                        int close = perfectString.lastIndexOf(")");
                        if (open != -1 && close != -1 && close > open) {
                            String tag = perfectString.substring(open + 1, close).trim();  // Cắt rồi trim
                            System.out.println(tag);  // "buổi lẻ"
                        }
                    }

                    serviceType = getServiceType(perfectString);

                    SalesTransaction st = SalesTransaction.builder()
                            .orderCode(Integer.parseInt(orderCodeStr.substring(1)))
                            .facility(facilityRecordService)
                            .orderDate(orderDate)
                            .customerName(getString(row.getCell(5)))
                            .phoneNumber(getString(row.getCell(6)))
                            .originalPrice(toBigDecimal(getString(row.getCell(7)).isBlank() ? null : row.getCell(7)))
                            .priceChange(toBigDecimal(getString(row.getCell(8)).isBlank() ? null : row.getCell(8)))
                            .totalAmount(toBigDecimal(getString(row.getCell(16)).isBlank() ? null : row.getCell(16)))
                            .cashTransferCredit(toBigDecimal(getString(row.getCell(17)).isBlank() ? null : row.getCell(17)))
                            .cash(toBigDecimal(getString(row.getCell(18)).isBlank() ? null : row.getCell(18)))
                            .transfer(toBigDecimal(getString(row.getCell(19)).isBlank() ? null : row.getCell(19)))
                            .creditCard(toBigDecimal(getString(row.getCell(20)).isBlank() ? null : row.getCell(20)))
                            .wallet(toBigDecimal(getString(row.getCell(21)).startsWith("0") ? null : row.getCell(21)))
                            .prepaidCard(toBigDecimal(getString(row.getCell(22)).startsWith("0") ? null : row.getCell(22)))
                            .debt(toBigDecimal(getString(row.getCell(23)).startsWith("0") ? null : row.getCell(23)))
                            .note(getString(row.getCell(25)).isBlank() ? null : getString(row.getCell(25)))
                            .serviceType(serviceType)
                            .build();

                    repository.save(st);
                    successCount++;

                } catch (Exception e) {
                    log.error("Row {} failed: {}", i, e.getMessage());
                    failCount++;
                }
            }

            log.info("IMPORT COMPLETE: Success = {}, Failed = {}", successCount, failCount);

        } catch (Exception e) {
            throw new RuntimeException("Failed to import Excel", e);
        }
    }

    private ServiceType getServiceType(String perfectString) {
        int cutString = Math.round((float) perfectString.length() / 3);
        ServiceType serviceType;
        if (perfectString.startsWith(perfectString.substring(0, cutString)) && perfectString.endsWith("lẻ)")) {
            String startString = perfectString.substring(0, cutString);
            serviceType = serviceTypeRepository.findByServiceName(startString + "%", "%lẻ)");
        } else if (perfectString.startsWith(perfectString.substring(0, cutString)) && perfectString.endsWith("ard)")) {
            String startString = perfectString.substring(0, cutString);
            serviceType = serviceTypeRepository.findByServiceName(startString + "%", "%ard)");
        } else if (perfectString.startsWith(perfectString.substring(0, cutString)) && perfectString.endsWith("ĐẦU)")) {
            String startString = perfectString.substring(0, cutString);
            serviceType = serviceTypeRepository.findByServiceName(startString + "%", "%ĐẦU)");
        } else if (perfectString.toUpperCase().startsWith("QT KÈM THẺ TIỀN FOXIE")) {
            serviceType = serviceTypeRepository.findByCode("QT 1.1");
        } else if (perfectString.startsWith("DV 1: AQUA PEEL CLEANSE")) {
            serviceType = serviceTypeRepository.findByCode("DV 1.1"); // service_code = 'DV 1.1'
        } else if (perfectString.contains("COMBO CS 11: BURNT SKIN SOS")) {
            serviceType = serviceTypeRepository.findByCode("CBCS 11.2");
        } else if (perfectString.contains("COMBO CS 3: PRESERVE YOUTH") && perfectString.toLowerCase().contains("giá thẻ")) {
            serviceType = serviceTypeRepository.findByCode("CBCS 3.2");
        } else if (perfectString.contains("DV 4: LUMIGLOW CLEANSE") && perfectString.contains("buổi lẻ")) {
            serviceType = serviceTypeRepository.findByCode("DV 4.1");
        } else if (perfectString.contains("COMBO CS 3: PRESERVE YOUTH") && perfectString.contains("buổi lẻ")) {
            serviceType = serviceTypeRepository.findByCode("CBCS 3.1");
        } else if (perfectString.contains("CT 2: ADDED LUMIGLOW")) {
            serviceType = perfectString.toLowerCase().contains("giá") ? serviceTypeRepository.findByCode("CT 2.2") : serviceTypeRepository.findByCode("CT 2.1");
        } else if (perfectString.contains("COMBO 1: DEEP CLEANSE CRYO") && perfectString.toLowerCase().contains("giá")) {
            serviceType = serviceTypeRepository.findByCode("CB 1.2");
        } else if (perfectString.contains("COMBO CS 1: MESO TẾ BÀO GỐC DNA CÁ HỒI") && perfectString.toLowerCase().contains("giá")) {
            serviceType = serviceTypeRepository.findByCode("CBCS 1.2");
        } else if (perfectString.contains("DV 2: DEEP CLEANSE") && perfectString.toLowerCase().contains("giá")) {
            serviceType = serviceTypeRepository.findByCode("DV 2.2");
        } else if (perfectString.contains("COMBO CS 1: MESO TẾ BÀO GỐC DNA CÁ HỒI") && perfectString.contains("buổi lẻ")) {
            serviceType = serviceTypeRepository.findByCode("CBCS 1.1");
        } else if (perfectString.contains("DV 3: CRYO CLEANSE") && perfectString.toLowerCase().contains("giá")) {
            serviceType = serviceTypeRepository.findByCode("DV 3.3");
        } else if (perfectString.contains("COMBO 6") && perfectString.toLowerCase().contains("giá")) {
            serviceType = serviceTypeRepository.findByCode("CB 6.2");
        } else if (perfectString.contains("DV 1: AQUA PEEL CLEANSE") && perfectString.contains("buổi lẻ")) {
            serviceType = serviceTypeRepository.findByCode("DV 1.1");
        } else if (perfectString.contains("DV 1: AQUA PEEL CLEANSE") && perfectString.toLowerCase().contains("giá")) {
            serviceType = serviceTypeRepository.findByCode("DV 1.3");
        } else if (perfectString.contains("DV 5: GYMMING CLEANSE") && perfectString.toLowerCase().contains("giá")) {
            serviceType = serviceTypeRepository.findByCode("DV 5.2");
        } else if (perfectString.contains("DV 6: EYE-REVIVE CLEANSE") && perfectString.toLowerCase().contains("giá")) {
            serviceType = serviceTypeRepository.findByCode("DV 6.2");
        } else if (perfectString.contains("COMBO 1: DEEP CLEANSE CRYO") && perfectString.contains("buổi lẻ")) {
            serviceType = serviceTypeRepository.findByCode("CB 1.1");
        } else if (perfectString.contains("DV 4: LUMIGLOW CLEANSE") && perfectString.contains("buổi lẻ")) {
            serviceType = serviceTypeRepository.findByCode("DV 4.1");
        } else if (perfectString.contains("DV 5: GYMMING CLEANSE") && perfectString.contains("buổi lẻ")) {
            serviceType = serviceTypeRepository.findByCode("DV 5.1");
        } else if (perfectString.contains("DV 6: EYE-REVIVE CLEANSE") && perfectString.contains("buổi lẻ")) {
            serviceType = serviceTypeRepository.findByCode("DV 6.1");
        } else if (perfectString.contains("TRẢI NGHIỆM LẦN ĐẦU") && perfectString.contains("COMBO 9")) {
            serviceType = serviceTypeRepository.findByCode("CB9.3");
        } else if (perfectString.contains("KHUYẾN MÃI TRẢI NGHIỆM LẦN ĐẦU") && perfectString.contains("COMBO 4")) {
            serviceType = serviceTypeRepository.findByCode("CB 4.3");
        } else if (perfectString.contains("SỮA RỬA MẶT LÀM DỊU ELRAVIE")) {
            serviceType = serviceTypeRepository.findByCode("MP000037");
        } else if (perfectString.contains("BỘ SẢN PHẨM DƯỠNG DA FULL SIZE ELRAVIE")) {
            serviceType = serviceTypeRepository.findByCode("MP000024");
        } else if (perfectString.contains("KEM DƯỠNG MẮT ELRAVIE")) {
            serviceType = serviceTypeRepository.findByCode("MP000014");
        } else if (perfectString.startsWith("CT 6: ADDED GOODBYE ACNE")) {
            serviceType = serviceTypeRepository.findByCode("CT 6.1");
        } else if (perfectString.startsWith("COMBO CS 3: PRESERVE YOUTH")) {
            serviceType = serviceTypeRepository.findByCode("CBCS 3.1");
        } else if (perfectString.startsWith("COMBO CS 9: PHỤC HỒI NÂNG CAO PDRN")) {
            serviceType = serviceTypeRepository.findByCode("CBCS 9.1");
        } else if (perfectString.startsWith("CT 4: ADDED EYE-REVIVE - CỘNG THÊM CHĂM SÓC MẮT")) {
            serviceType = serviceTypeRepository.findByCode("CT 4.1");
        } else if (perfectString.startsWith("CT 3: ADDED GYMMING - CỘNG THÊM SĂN CHẮC DA")) {
            serviceType = serviceTypeRepository.findByCode("CT 3.1");
        } else if (perfectString.startsWith("COMBO CS 1: MESO TẾ BÀO GỐC DNA CÁ HỒI (Giá Foxie Member Card)")) {
            serviceType = serviceTypeRepository.findByCode("CBCS 1.1");
        } else if (perfectString.startsWith("COMBO 6 : LUMIGLOW CLEANSE CRYO GYMMING (Giá Foxie Member Card)")) {
            serviceType = serviceTypeRepository.findByCode("CB 6.1");
        } else {
            serviceType = serviceTypeRepository.findByName(perfectString);
        }
        return serviceType;
    }

//    public void importFromExcelOrigin(MultipartFile file) {
//        int successCount = 0;
//        int failCount = 0;
//        int failed = 0;
//
//        try (InputStream is = file.getInputStream()) {
//            Workbook workbook = WorkbookFactory.create(is);
//            Sheet sheet = workbook.getSheetAt(0);
//
//            // ✅ Tạo map Region: shop_name (chuẩn hoá) → Region
//            Map<String, Region> regionMap = regionRepository.findAll()
//                    .stream()
//                    .collect(Collectors.toMap(
//                            r -> r.getShop_name().trim().toLowerCase(),
//                            Function.identity()
//                    ));
//
//            for (int i = 2; i <= sheet.getLastRowNum(); i++) {
//                Row row = sheet.getRow(i);
//
//                if (row == null || isRowEmpty(row)) {
//                    log.info("Stopped at row {} (blank)", i);
//                    break;
//                }
//
//                try {
//                    String orderCodeStr = getString(row.getCell(1));
//                    String dateTimeStr = getString(row.getCell(3));
//
//                    if (orderCodeStr == null || dateTimeStr == null) {
//                        log.warn("Row {} skipped: missing required fields", i);
//                        failCount++;
//                        continue;
//                    }
//
//                    LocalDateTime orderDate = LocalDateTime.parse(
//                            dateTimeStr,
//                            DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy")
//                    );
//
//                    // ✅ Tra Region bằng shop name (cột CƠ SỞ trong Excel, cột 11 tính từ 0)
//                    String shopName = getString(row.getCell(2)).trim().toLowerCase();
//                    Region facilityRecordService = regionMap.get(shopName);
//
//                    if (facilityRecordService == null) {
//                        log.warn("Row {} skipped: Không tìm thấy Region cho tên '{}'", i, shopName);
//                        failed++;
//                        continue;
//                    }
//
//                    SalesTransaction st = SalesTransaction.builder()
//                            .orderCode(Integer.parseInt(orderCodeStr.substring(1)))
//                            .facility(facilityRecordService)
//                            .orderDate(orderDate)
//                            .customerName(getString(row.getCell(5)))
//                            .phoneNumber(getString(row.getCell(6)))
//                            .originalPrice(toBigDecimal(getString(row.getCell(7)).isBlank() ? null : row.getCell(7)))
//                            .priceChange(toBigDecimal(getString(row.getCell(8)).isBlank() ? null : row.getCell(8)))
//                            .totalAmount(toBigDecimal(getString(row.getCell(16)).isBlank() ? null : row.getCell(16)))
//                            .cashTransferCredit(toBigDecimal(getString(row.getCell(17)).isBlank() ? null : row.getCell(17)))
//                            .cash(toBigDecimal(getString(row.getCell(18)).isBlank() ? null : row.getCell(18)))
//                            .transfer(toBigDecimal(getString(row.getCell(19)).isBlank() ? null : row.getCell(19)))
//                            .creditCard(toBigDecimal(getString(row.getCell(20)).isBlank() ? null : row.getCell(20)))
//                            .wallet(toBigDecimal(getString(row.getCell(21)).startsWith("0") ? null : row.getCell(21)))
//                            .prepaidCard(toBigDecimal(getString(row.getCell(22)).startsWith("0") ? null : row.getCell(22)))
//                            .debt(toBigDecimal(getString(row.getCell(23)).startsWith("0") ? null : row.getCell(23)))
//                            .note(getString(row.getCell(25)).isBlank() ? null : getString(row.getCell(25)))
//                            .details(getString(row.getCell(26)))
//                            .build();
//
//                    repository.save(st);
//                    successCount++;
//
//                } catch (Exception e) {
//                    log.error("Row {} failed: {}", i, e.getMessage());
//                    failCount++;
//                }
//            }
//
//            log.info("IMPORT COMPLETE: Success = {}, Failed = {}", successCount, failCount);
//
//        } catch (Exception e) {
//            throw new RuntimeException("Failed to import Excel", e);
//        }
//    }

    private boolean isRowEmpty(Row row) {
        for (int c = 0; c < row.getLastCellNum(); c++) {
            Cell cell = row.getCell(c);
            if (cell != null && cell.getCellType() != CellType.BLANK && !getString(cell).isBlank()) {
                return false;
            }
        }
        return true;
    }

    private BigDecimal toBigDecimal(Cell cell) {
        try {
            return new BigDecimal(cell.toString().trim().replace(",", ""));
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }

    private String getString(Cell cell) {
        return cell == null ? null : cell.toString().trim();
    }

    public List<RegionRevenueDTO> getRevenueByRegion(CustomerReportRequest request) {
        List<Object[]> rawData = repository.fetchRevenueByRegionAndDate(request.getFromDate(), request.getToDate());

        return rawData.stream()
                .map(row -> new RegionRevenueDTO(
                        (String) row[0],
                        ((Date) row[1]).toLocalDate(), // ✅ sửa ở đây
                        (BigDecimal) row[2]))
                .collect(Collectors.toList());
    }

    public List<ShopTypeRevenueDTO> getRevenueByShopType(CustomerReportRequest request) {
        List<Object[]> rawData = repository.fetchRevenueByShopTypeAndDate(request.getFromDate(), request.getToDate());

        return rawData.stream()
                .map(row -> new ShopTypeRevenueDTO(
                        (String) row[0],
                        ((Date) row[1]).toLocalDate().atStartOfDay(),
                        (BigDecimal) row[2]))
                .collect(Collectors.toList());
    }

    public RevenueSummaryDTO getRevenueSummary(CustomerReportRequest request) {
        LocalDateTime start = request.getFromDate();
        LocalDateTime end = request.getToDate();

        BigDecimal total = repository.fetchRevenueSummary(start, end);
        BigDecimal actual = repository.fetchActualRevenueSummary(start, end);

        // Previous range
        long days = Duration.between(start, end).toDays();
        LocalDateTime prevStart = start.minusDays(days + 1);
        LocalDateTime prevEnd = start.minusSeconds(1);

        BigDecimal prevTotal = repository.fetchRevenueSummary(prevStart, prevEnd);
        BigDecimal prevActual = repository.fetchActualRevenueSummary(prevStart, prevEnd);

        double growthTotal = calculateGrowth(prevTotal, total);
        double growthActual = calculateGrowth(prevActual, actual);

        return new RevenueSummaryDTO(total, actual, growthTotal, growthActual);
    }

    private double calculateGrowth(BigDecimal previous, BigDecimal current) {
        if (previous == null || previous.compareTo(BigDecimal.ZERO) == 0) return 100.0;
        return current.subtract(previous)
                .divide(previous, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .doubleValue();
    }

    public List<RegionRevenueStatDTO> getStatus(CustomerReportRequest request) {
        LocalDateTime start = request.getFromDate();
        LocalDateTime end = request.getToDate();

        // Kỳ hiện tại
        Map<String, Object[]> current = toMap(repository.fetchOrderAndRevenueByRegion(start, end));

        // Kỳ trước
        long days = Duration.between(start, end).toDays() + 1;
        LocalDateTime prevStart = start.minusDays(days);
        LocalDateTime prevEnd = start.minusSeconds(1);
        Map<String, Object[]> previous = toMap(repository.fetchOrderAndRevenueByRegion(prevStart, prevEnd));

        List<RegionRevenueStatDTO> result = new ArrayList<>();
        BigDecimal totalRevenue = BigDecimal.ZERO;

        for (String region : current.keySet()) {
            Number currOrderNum = (Number) current.get(region)[0];
            long currOrders = currOrderNum.longValue();
            BigDecimal currRevenue = (BigDecimal) current.get(region)[1];

            long prevOrders = 0;
            if (previous.containsKey(region)) {
                Number prevOrderNum = (Number) previous.get(region)[0];
                prevOrders = prevOrderNum.longValue();
            }

            long delta = currOrders - prevOrders;
            double growth = prevOrders == 0 ? 100.0 : ((double) delta / prevOrders) * 100.0;

            result.add(new RegionRevenueStatDTO(
                    region,
                    currOrders,
                    delta,
                    currRevenue,
                    growth,
                    0.0 // placeholder, sẽ tính sau
            ));

            totalRevenue = totalRevenue.add(currRevenue);
        }

        // Tính phần trăm đóng góp doanh thu
        for (RegionRevenueStatDTO dto : result) {
            dto.setRevenuePercent(
                    totalRevenue.compareTo(BigDecimal.ZERO) == 0 ? 0.0 :
                            dto.getRevenue().multiply(BigDecimal.valueOf(100)).divide(totalRevenue, 2, RoundingMode.HALF_UP).doubleValue()
            );
        }

        return result;
    }

    private Map<String, Object[]> toMap(List<Object[]> raw) {
        return raw.stream().collect(Collectors.toMap(
                row -> (String) row[0],
                row -> new Object[]{ row[1], row[2] }
        ));
    }

    public List<RegionRevenuePieDTO> getActualRevenuePie(CustomerReportRequest request) {
        List<Object[]> raw = repository.fetchActualRevenueByRegion(request.getFromDate(), request.getToDate());

        BigDecimal total = raw.stream()
                .map(row -> (BigDecimal) row[1])
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return raw.stream()
                .map(row -> {
                    String region = (String) row[0];
                    BigDecimal revenue = (BigDecimal) row[1];
                    double percent = total.compareTo(BigDecimal.ZERO) == 0 ? 0.0 :
                            revenue.multiply(BigDecimal.valueOf(100))
                                    .divide(total, 2, RoundingMode.HALF_UP).doubleValue();
                    return new RegionRevenuePieDTO(region, revenue, percent);
                })
                .toList();
    }

    public List<DailyShopTypeRevenueDTO> getDailyRevenueByShopType(CustomerReportRequest request) {
        List<Object[]> raw = repository.getDailyRevenueByShopType(request.getFromDate(), request.getToDate());

        return raw.stream()
                .map(obj -> new DailyShopTypeRevenueDTO(
                        obj[0] instanceof Date date ? date.toLocalDate().atStartOfDay() : null,
                        (String) obj[1],
                        (BigDecimal) obj[2]
                ))
                .collect(Collectors.toList());
    }

    public List<DailyCustomerTypeRevenueDTO> getRevenueByCustomerTypePerDay(CustomerReportRequest request) {
        List<Object[]> rawData = repository
                .findRevenueByCustomerTypeAndDate(request.getFromDate(), request.getToDate());

        return rawData.stream().map(obj -> new DailyCustomerTypeRevenueDTO(
                ((Date) obj[1]).toLocalDate(),
                obj[0] == null || obj[0].toString().isBlank() ? "Không xác định" : obj[0].toString(),
                (BigDecimal) obj[2]
        )).collect(Collectors.toList());
    }

    public List<TopStoreRevenueDTO> getTopStoreRevenue(CustomerReportRequest request) {
        List<Object[]> rawData = repository.findTop10StoreRevenue(request.getFromDate(), request.getToDate());
        return rawData.stream().map(row -> new TopStoreRevenueDTO(
                (String) row[0],
                (BigDecimal) row[1],
                (BigDecimal) row[2]
        )).collect(Collectors.toList());
    }

    public List<StoreRevenueStatDTO> getFullStoreRevenueStats(CustomerReportRequest request) {
        LocalDateTime start = request.getFromDate();
        LocalDateTime end = request.getToDate();

        List<Object[]> current = repository.findStoreRevenueStatsBetween(start, end);

        LocalDateTime prevEnd = start.minusSeconds(1);
        LocalDateTime prevStart = prevEnd.minusDays(end.toLocalDate().toEpochDay() - start.toLocalDate().toEpochDay());
        List<Object[]> previous = repository.findPreviousStoreRevenueStatsBetween(prevStart, prevEnd);

        Map<String, Object[]> prevMap = previous.stream()
                .collect(Collectors.toMap(row -> row[0].toString(), Function.identity()));

        List<StoreRevenueStatDTO> result = new ArrayList<>();

        BigDecimal totalRevenue = BigDecimal.ZERO;
        BigDecimal totalFoxie = BigDecimal.ZERO;
        long totalOrders = 0;

        for (Object[] row : current) {
            String name = row[0].toString();
            long orders = ((Number) row[1]).longValue();
            BigDecimal revenue = (BigDecimal) row[2];
            BigDecimal foxie = (BigDecimal) row[3];

            Object[] prevRow = prevMap.getOrDefault(name, new Object[]{name, 0L, BigDecimal.ZERO, BigDecimal.ZERO});
            long prevOrders = ((Number) prevRow[1]).longValue();
            BigDecimal prevRevenue = (BigDecimal) prevRow[2];

            long delta = orders - prevOrders;
            double growth = prevRevenue.compareTo(BigDecimal.ZERO) == 0 ? 100.0 :
                    revenue.subtract(prevRevenue).multiply(BigDecimal.valueOf(100)).divide(prevRevenue, 2, RoundingMode.HALF_UP).doubleValue();

            result.add(new StoreRevenueStatDTO(name, orders, delta, revenue, foxie, growth, 0.0, 0.0, 0.0));

            totalRevenue = totalRevenue.add(revenue);
            totalFoxie = totalFoxie.add(foxie);
            totalOrders += orders;
        }

        for (StoreRevenueStatDTO dto : result) {
            dto.setRevenuePercent(
                    totalRevenue.compareTo(BigDecimal.ZERO) == 0 ? 0.0 :
                            dto.getActualRevenue().multiply(BigDecimal.valueOf(100)).divide(totalRevenue, 2, RoundingMode.HALF_UP).doubleValue()
            );
            dto.setFoxiePercent(
                    totalFoxie.compareTo(BigDecimal.ZERO) == 0 ? 0.0 :
                            dto.getFoxieRevenue().multiply(BigDecimal.valueOf(100)).divide(totalFoxie, 2, RoundingMode.HALF_UP).doubleValue()
            );
            dto.setOrderPercent(
                    totalOrders == 0 ? 0.0 :
                            ((double) dto.getCurrentOrders() * 100) / totalOrders
            );
        }

        return result;
    }

    public List<DailyShopOrderStatDTO> getDailyOrderStats(CustomerReportRequest request) {
        List<Object[]> rawData = repository.findDailyOrderAndShopStats(request.getFromDate(), request.getToDate());

        return rawData.stream()
                .map(obj -> new DailyShopOrderStatDTO(
                        ((Date) obj[0]).toLocalDate(),
                        ((Number) obj[1]).longValue(),
                        ((Number) obj[2]).intValue()
                ))
                .collect(Collectors.toList());
    }

    public List<DailyRegionRevenueDTO> getDailyRevenue(CustomerReportRequest request) {
        List<Object[]> raw = repository.fetchDailyRevenueByRegion(request.getFromDate(), request.getToDate());
        List<DailyRegionRevenueDTO> result = new ArrayList<>();

        for (Object[] row : raw) {
            Date sqlDate = (Date) row[0];
            LocalDate date = sqlDate.toLocalDate();
            String region = (String) row[1];
            BigDecimal revenue = (BigDecimal) row[2];

            result.add(new DailyRegionRevenueDTO(date, region, revenue));
        }

        return result;
    }

    public String cleanTailNumber(String s) {
        return s.replaceAll("\\s*\\(\\d+\\)$", "");
    }

    public List<RegionPaymentDTO> getPaymentByRegion(CustomerReportRequest request) {
        List<Object[]> result = repository.findPaymentByRegion(
                request.getFromDate(),
                request.getToDate()
        );

        // Map raw Object[] into typed DTO
        return result.stream().map(row -> new RegionPaymentDTO(
                row[0] != null ? row[0].toString() : "Không xác định",
                (BigDecimal) row[1],
                (BigDecimal) row[2],
                (BigDecimal) row[3]
        )).collect(Collectors.toList());
    }

    public List<RegionOrderBreakdownDTO> getRegionOrderBreakdown(CustomerReportRequest request) {
        List<Object[]> raw = repository.fetchRegionOrderBreakdown(
                request.getFromDate(), request.getToDate());
        List<RegionOrderBreakdownDTO> result = new ArrayList<>();

        for (Object[] row : raw) {
            String region = (String) row[0];
            Long total = ((Number) row[1]).longValue();
            Long service = ((Number) row[2]).longValue();
            Long foxie = ((Number) row[3]).longValue();
            Long product = ((Number) row[4]).longValue();
            Long card = ((Number) row[5]).longValue();

            result.add(new RegionOrderBreakdownDTO(region, total, service, foxie, product, card));
        }
        return result;
    }

    public List<RegionOrderBreakdownTableDTO> getRegionOrderBreakdownTable(CustomerReportRequest request) {
        LocalDateTime start = request.getFromDate();
        LocalDateTime end = request.getToDate();

        List<Object[]> current = repository.fetchRegionOrderBreakdown(start, end);

        // Previous range
        long days = Duration.between(start, end).toDays();
        LocalDateTime prevStart = start.minusDays(days + 1);
        LocalDateTime prevEnd = start.minusSeconds(1);
        List<Object[]> previous = repository.fetchRegionOrderBreakdown(prevStart, prevEnd);

        // Map previous by shop name for lookup
        Map<String, Object[]> prevMap = previous.stream().collect(Collectors.toMap(
                row -> (String) row[0], row -> row
        ));

        List<RegionOrderBreakdownTableDTO> result = new ArrayList<>();

        for (Object[] row : current) {
            String shopName = (String) row[0];
            Long total = ((Number) row[1]).longValue();
            Long service = ((Number) row[2]).longValue();
            Long foxie = ((Number) row[3]).longValue();
            Long product = ((Number) row[4]).longValue();
            Long card = ((Number) row[5]).longValue();

            Object[] prev = prevMap.get(shopName);
            Long prevTotal = prev != null ? ((Number) prev[1]).longValue() : 0L;
            Long prevService = prev != null ? ((Number) prev[2]).longValue() : 0L;
            Long prevFoxie = prev != null ? ((Number) prev[3]).longValue() : 0L;
            Long prevProduct = prev != null ? ((Number) prev[4]).longValue() : 0L;
            Long prevCard = prev != null ? ((Number) prev[5]).longValue() : 0L;

            RegionOrderBreakdownTableDTO dto = new RegionOrderBreakdownTableDTO();
            dto.setShopName(shopName);
            dto.setTotalOrders(total);
            dto.setServiceOrders(service);
            dto.setFoxieCardOrders(foxie);
            dto.setProductOrders(product);
            dto.setCardPurchaseOrders(card);

            dto.setDeltaTotalOrders(total - prevTotal);
            dto.setDeltaServiceOrders(service - prevService);
            dto.setDeltaFoxieCardOrders(foxie - prevFoxie);
            dto.setDeltaProductOrders(product - prevProduct);
            dto.setDeltaCardPurchaseOrders(card - prevCard);

            result.add(dto);
        }
        return result;
    }

    public OverallOrderSummaryDTO getOverallOrderSummary(CustomerReportRequest request) {
        LocalDateTime start = request.getFromDate();
        LocalDateTime end = request.getToDate();

        List<Object[]> currentList = repository.fetchOverallOrderSummary(start, end);
        List<Object[]> previousList = repository.fetchOverallOrderSummary(
                start.minusDays(Duration.between(start, end).toDays() + 1),
                start.minusSeconds(1)
        );

        Object[] current = currentList.get(0);
        Object[] previous = previousList.get(0);

        OverallOrderSummaryDTO dto = new OverallOrderSummaryDTO();

        Long total = ((Number) current[0]).longValue();
        Long service = ((Number) current[1]).longValue();
        Long foxie = ((Number) current[2]).longValue();
        Long product = ((Number) current[3]).longValue();
        Long card = ((Number) current[4]).longValue();

        Long prevTotal = ((Number) previous[0]).longValue();
        Long prevService = ((Number) previous[1]).longValue();
        Long prevFoxie = ((Number) previous[2]).longValue();
        Long prevProduct = ((Number) previous[3]).longValue();
        Long prevCard = ((Number) previous[4]).longValue();

        dto.setTotalOrders(total);
        dto.setServiceOrders(service);
        dto.setFoxieCardOrders(foxie);
        dto.setProductOrders(product);
        dto.setCardPurchaseOrders(card);

        dto.setDeltaTotalOrders(total - prevTotal);
        dto.setDeltaServiceOrders(service - prevService);
        dto.setDeltaFoxieCardOrders(foxie - prevFoxie);
        dto.setDeltaProductOrders(product - prevProduct);
        dto.setDeltaCardPurchaseOrders(card - prevCard);

        return dto;
    }

    public OverallSummaryDTO getOverallSummary(CustomerReportRequest request) {
        LocalDateTime start = request.getFromDate();
        LocalDateTime end = request.getToDate();

        List<Object[]> currentList = repository.fetchOverallRevenueSummary(start, end);
        List<Object[]> previousList = repository.fetchOverallRevenueSummary(
                start.minusDays(Duration.between(start, end).toDays() + 1),
                start.minusSeconds(1)
        );

        Object[] current = currentList.get(0);
        Object[] previous = previousList.get(0);

        OverallSummaryDTO dto = new OverallSummaryDTO();

        BigDecimal total = (BigDecimal) current[0];
        BigDecimal service = (BigDecimal) current[1];
        BigDecimal foxie = (BigDecimal) current[2];
        BigDecimal product = (BigDecimal) current[3];
        BigDecimal card = (BigDecimal) current[4];

        BigDecimal prevTotal = (BigDecimal) previous[0];
        BigDecimal prevService = (BigDecimal) previous[1];
        BigDecimal prevFoxie = (BigDecimal) previous[2];
        BigDecimal prevProduct = (BigDecimal) previous[3];
        BigDecimal prevCard = (BigDecimal) previous[4];

        dto.setTotalRevenue(total);
        dto.setServiceRevenue(service);
        dto.setFoxieCardRevenue(foxie);
        dto.setProductRevenue(product);
        dto.setCardPurchaseRevenue(card);

        dto.setDeltaTotalRevenue(total.subtract(prevTotal));
        dto.setDeltaServiceRevenue(service.subtract(prevService));
        dto.setDeltaFoxieCardRevenue(foxie.subtract(prevFoxie));
        dto.setDeltaProductRevenue(product.subtract(prevProduct));
        dto.setDeltaCardPurchaseRevenue(card.subtract(prevCard));

        dto.setPercentTotalRevenue(calculatePercentChange(prevTotal, total));
        dto.setPercentServiceRevenue(calculatePercentChange(prevService, service));
        dto.setPercentFoxieCardRevenue(calculatePercentChange(prevFoxie, foxie));
        dto.setPercentProductRevenue(calculatePercentChange(prevProduct, product));
        dto.setPercentCardPurchaseRevenue(calculatePercentChange(prevCard, card));

        return dto;
    }

    private double calculatePercentChange(BigDecimal previous, BigDecimal current) {
        if (previous.compareTo(BigDecimal.ZERO) == 0) {
            return current.compareTo(BigDecimal.ZERO) == 0 ? 0 : 100.0;
        }
        return current.subtract(previous)
                .divide(previous, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100)).doubleValue();
    }
}