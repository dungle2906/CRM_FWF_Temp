package com.example.BasicCRM_FWF.Repository;

import com.example.BasicCRM_FWF.Model.ServiceRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ServiceRecordRepository extends JpaRepository<ServiceRecord, Long> {

    @Query(value = "SELECT DISTINCT phone_number FROM service_record " +
            "WHERE booking_date BETWEEN :start AND :end " +
            "AND base_service IS NOT NULL AND base_service <> ''", nativeQuery = true)
    List<String> findServicePhonesWithBaseServiceBetween(@Param("start") LocalDateTime start,
                                                         @Param("end") LocalDateTime end);

    List<ServiceRecord> findByBookingDateBetween(LocalDateTime start, LocalDateTime end);
}