package com.example.BasicCRM_FWF.Service;

import com.example.BasicCRM_FWF.Model.AppUsageRecord;
import com.example.BasicCRM_FWF.Repository.AppUsageRecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Slf4j
public class AppUsageRecordService {

    private final AppUsageRecordRepository repository;

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
                    String dateStr = getString(row.getCell(6));
                    LocalDateTime installedAt = LocalDateTime.parse(dateStr, DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy"));

                    AppUsageRecord record = AppUsageRecord.builder()
                            .customerId(getString(row.getCell(1)))
                            .customerName(getString(row.getCell(2)))
                            .phoneNumber(getString(row.getCell(3)))
                            .device(getString(row.getCell(4)))
                            .status(getString(row.getCell(5)))
                            .installedAt(installedAt)
                            .build();

                    repository.save(record);
                    success++;

                } catch (Exception e) {
                    failed++;
                    log.warn("Row {} failed: {}", i, e.getMessage());
                }
            }

            log.info("IMPORT APP USAGE: Success = {}, Failed = {}", success, failed);

        } catch (Exception e) {
            throw new RuntimeException("Failed to import app usage Excel", e);
        }
    }

    private boolean isRowEmpty(Row row) {
        for (int c = 0; c <= 4; c++) {
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
}
