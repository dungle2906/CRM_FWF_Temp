package com.example.BasicCRM_FWF.Service;

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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ServiceRecordService {

    private final ServiceRecordRepository repository;
    private final RegionRepository regionRepository;
    private final ServiceTypeRepository serviceTypeRepository;
    private final AppliedCardRepository appliedCardRepository;

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

                    List<Region> regionList = regionRepository.findAll();
                    Region facilityRecordService = null;
                    for(Region region : regionList){
                        if(getString(row.getCell(4)).equals(region.getShop_name())){
                            facilityRecordService = region;
                        }
                    }

                    List<ServiceType> serviceTypeList = serviceTypeRepository.findAll();
                    ServiceType service_type = null;
                    for(ServiceType serviceType : serviceTypeList){
                        if(getString(row.getCell(4)).equals(serviceType.getService_name())){
                            service_type = serviceType;
                        }
                    }

                    List<AppliedCard> appliedCardList = appliedCardRepository.findAll();
                    AppliedCard appliedCard = null;
                    for(AppliedCard appliedCard1 : appliedCardList){
                        if(getString(row.getCell(4)).equals(appliedCard1.getCard_name())){
                            appliedCard = appliedCard1;
                        }
                    }

                    ServiceRecord record = ServiceRecord.builder()
                            .recordId(Integer.parseInt(getString(row.getCell(1)).substring(1)))
                            .orderId(Integer.parseInt(getString(row.getCell(2)).substring(1)))
                            .bookingDate(bookingDate)
                            .facility(facilityRecordService)
                            .customerName(getString(row.getCell(5)))
                            .phoneNumber(getString(row.getCell(6)))
                            .baseService(service_type)
                            .appliedCard(appliedCard)
                            .sessionPrice(toBigDecimal(row.getCell(9)))
                            .sessionType(getString(row.getCell(10)).startsWith("Buổi thường") || getString(row.getCell(19)).isBlank()?null:getString(row.getCell(10)))
                            .surcharge(getString(row.getCell(11)).startsWith("Không có") || getString(row.getCell(19)).isBlank()?null:getString(row.getCell(11)))
                            .totalSurcharge(getString(row.getCell(12)).startsWith("0") || getString(row.getCell(19)).isBlank()?null:toBigDecimal(row.getCell(12)))
                            .shiftEmployee(getString(row.getCell(13)))
                            .performingEmployee(getString(row.getCell(14)))
                            .employeeSalary(toBigDecimal(row.getCell(15)))
                            .status(getString(row.getCell(16)).startsWith("Hoàn thành") || getString(row.getCell(19)).isBlank()?null:getString(row.getCell(16)))
                            .rating(getString(row.getCell(17)).startsWith("Chưa đánh giá") || getString(row.getCell(19)).isBlank()?null:getString(row.getCell(17)))
                            .reviewContent(getString(row.getCell(18)).startsWith("Chưa có") || getString(row.getCell(19)).isBlank()?null:getString(row.getCell(18)))
                            .note(getString(row.getCell(19)).startsWith("Chưa có") || getString(row.getCell(19)).isBlank()?null:getString(row.getCell(19)))
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
