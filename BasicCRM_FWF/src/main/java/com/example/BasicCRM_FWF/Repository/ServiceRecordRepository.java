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
            "AND base_service_id IS NOT NULL AND base_service_id <> ''", nativeQuery = true)
    List<String> findServicePhonesWithBaseServiceBetween(@Param("start") LocalDateTime start,
                                                         @Param("end") LocalDateTime end);

    List<ServiceRecord> findByBookingDateBetween(LocalDateTime start, LocalDateTime end);

    @Query(value = "SELECT DATE(booking_date) AS date, " +
            "CASE " +
            " WHEN st.service_name LIKE 'combo%' THEN 'Combo' " +
            " WHEN st.service_name LIKE 'DV%' THEN 'Dịch vụ' " +
            " WHEN st.service_name LIKE 'QT%' THEN 'Added on' " +
            " WHEN st.service_name LIKE 'fox%' THEN 'Fox card' " +
            " ELSE 'Khác' END AS type, " +
            "COUNT(*) AS total " +
            "FROM service_record sr " +
            "JOIN service_type st ON sr.base_service_id = st.id " +
            "WHERE booking_date BETWEEN :start AND :end " +
            "GROUP BY DATE(booking_date), type " +
            "ORDER BY DATE(booking_date), type", nativeQuery = true)
    List<Object[]> countServiceTypesPerDay(@Param("start") LocalDateTime start,
                                           @Param("end") LocalDateTime end);

    @Query(value = "SELECT COUNT(*) FROM service_record sr \n" +
            "JOIN service_type st ON sr.base_service_id = st.id \n" +
            "WHERE sr.booking_date BETWEEN :start AND :end \n" +
            "AND st.service_name LIKE :prefix%", nativeQuery = true)
    long countByServiceCodePrefix(@Param("prefix") String prefix,
                                  @Param("start") LocalDateTime start,
                                  @Param("end") LocalDateTime end);

    @Query(value = "SELECT r.region AS region, " +
            "CASE " +
            " WHEN st.service_name LIKE 'combo%' THEN 'Combo' " +
            " WHEN st.service_name LIKE 'DV%' THEN 'Dịch vụ' " +
            " WHEN st.service_name LIKE 'QT%' THEN 'Added on' " +
            " WHEN st.service_name LIKE 'fox%' THEN 'Fox card' " +
            " ELSE 'Khác' END AS type, " +
            "COUNT(*) AS total " +
            "FROM service_record sr " +
            "JOIN service_type st ON sr.base_service_id = st.id " +
            "JOIN region r ON sr.facility_id = r.id " +
            "WHERE sr.booking_date BETWEEN :start AND :end " +
            "GROUP BY r.region, type " +
            "ORDER BY r.region, type", nativeQuery = true)
    List<Object[]> findRegionServiceTypeCount(@Param("start") LocalDateTime start,
                                              @Param("end") LocalDateTime end);

    @Query(value = "SELECT r.shop_name AS shop, " +
            "CASE " +
            " WHEN st.service_name LIKE 'combo%' THEN 'Combo' " +
            " WHEN st.service_name LIKE 'DV%' THEN 'Dịch vụ' " +
            " WHEN st.service_name LIKE 'QT%' THEN 'Added on' " +
            " WHEN st.service_name LIKE 'fox%' THEN 'Fox card' " +
            " ELSE 'Khác' END AS type, " +
            "COUNT(*) AS total " +
            "FROM service_record sr " +
            "JOIN service_type st ON sr.base_service_id = st.id " +
            "JOIN region r ON sr.facility_id = r.id " +
            "WHERE sr.booking_date BETWEEN :start AND :end " +
            "GROUP BY r.shop_name, type " +
            "ORDER BY r.shop_name, type", nativeQuery = true)
    List<Object[]> findServiceUsageByShop(@Param("start") LocalDateTime start,
                                          @Param("end") LocalDateTime end);
}