package com.example.BasicCRM_FWF.Service;

import com.example.BasicCRM_FWF.Model.SalesTransaction;
import com.example.BasicCRM_FWF.Repository.SalesTransactionRepository;
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
public class SalesTransactionService {

    private final SalesTransactionRepository repository;

    public void importFromExcel(MultipartFile file) {
        int successCount = 0;
        int failCount = 0;

        try (InputStream is = file.getInputStream()) {
            Workbook workbook = WorkbookFactory.create(is);
            Sheet sheet = workbook.getSheetAt(0);

            for (int i = 2; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);

                // STOP if entire row is blank (end of data)
                if (row == null || isRowEmpty(row)) {
                    log.info("Stopped at row {} (blank)", i);
                    break;
                }

                try {
                    String dateTimeStr = getString(row.getCell(3));
                    if (getString(row.getCell(1)) == null || dateTimeStr == null) {
                        log.warn("Row {} skipped: missing required fields", i);
                        failCount++;
                        continue;
                    }

                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy");
                    LocalDateTime orderDate = LocalDateTime.parse(dateTimeStr, formatter);

                    SalesTransaction st = SalesTransaction.builder()
                            .orderCode(getString(row.getCell(1)))
                            .facility(getString(row.getCell(2)))
                            .orderDate(orderDate)
                            .customerName(getString(row.getCell(5)))
                            .phoneNumber(getString(row.getCell(6)))
                            .originalPrice(toBigDecimal(row.getCell(7)))
                            .priceChange(toBigDecimal(row.getCell(8)))
                            .totalAmount(toBigDecimal(row.getCell(9)))
                            .cash(toBigDecimal(row.getCell(18)))
                            .transfer(toBigDecimal(row.getCell(19)))
                            .prepaidCard(toBigDecimal(row.getCell(22)))
                            .debt(toBigDecimal(row.getCell(23)))
                            .note(getString(row.getCell(25)))
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
        for (int c = 0; c <= 5; c++) {
            Cell cell = row.getCell(c);
            if (cell != null && cell.getCellType() != CellType.BLANK && !getString(cell).isEmpty()) {
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
}