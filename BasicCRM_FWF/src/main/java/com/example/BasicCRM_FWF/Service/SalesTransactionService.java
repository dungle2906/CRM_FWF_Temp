package com.example.BasicCRM_FWF.Service;

import com.example.BasicCRM_FWF.DTORequest.CustomerReportRequest;
import com.example.BasicCRM_FWF.DTOResponse.*;
import com.example.BasicCRM_FWF.Model.Region;
import com.example.BasicCRM_FWF.Model.SalesTransaction;
import com.example.BasicCRM_FWF.Repository.RegionRepository;
import com.example.BasicCRM_FWF.Repository.SalesTransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
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
                            .details(getString(row.getCell(26)))
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
                        ((Date) row[1]).toLocalDate(),
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
        long totalOrders = 0;
        long totalDelta = 0;
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

            totalOrders += currOrders;
            totalDelta += delta;
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

}