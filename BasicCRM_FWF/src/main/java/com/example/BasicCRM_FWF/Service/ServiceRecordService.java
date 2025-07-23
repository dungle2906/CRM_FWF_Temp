package com.example.BasicCRM_FWF.Service;

import com.example.BasicCRM_FWF.Model.ServiceRecord;
import com.example.BasicCRM_FWF.Repository.ServiceRecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Slf4j
public class ServiceRecordService {

    private final ServiceRecordRepository repository;

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
                    String dateStr = getString(row.getCell(3));
                    LocalDateTime bookingDate = LocalDateTime.parse(dateStr, DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy"));

                    ServiceRecord record = ServiceRecord.builder()
                            .recordId(getString(row.getCell(1)))
                            .orderId(getString(row.getCell(2)))
                            .bookingDate(bookingDate)
                            .facility(getString(row.getCell(4)))
                            .customerName(getString(row.getCell(5)))
                            .phoneNumber(getString(row.getCell(6)))
                            .baseService(getString(row.getCell(7)))
                            .appliedCard(getString(row.getCell(8)))
                            .sessionPrice(toBigDecimal(row.getCell(9)))
                            .sessionType(getString(row.getCell(10)))
                            .surcharge(getString(row.getCell(11)))
                            .totalSurcharge(toBigDecimal(row.getCell(12)))
                            .shiftEmployee(getString(row.getCell(13)))
                            .performingEmployee(getString(row.getCell(14)))
                            .employeeSalary(toBigDecimal(row.getCell(15)))
                            .status(getString(row.getCell(16)))
                            .rating(getString(row.getCell(17)))
                            .reviewContent(getString(row.getCell(18)))
                            .note(getString(row.getCell(19)))
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
}
