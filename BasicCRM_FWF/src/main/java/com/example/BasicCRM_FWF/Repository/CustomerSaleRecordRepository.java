package com.example.BasicCRM_FWF.Repository;

import com.example.BasicCRM_FWF.DTOResponse.DailyCustomerCount;
import com.example.BasicCRM_FWF.Model.CustomerSaleRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface CustomerSaleRecordRepository extends JpaRepository<CustomerSaleRecord, Integer> {

    @Query("SELECT new com.example.BasicCRM_FWF.DTOResponse.DailyCustomerCount(DATE(c.createdAt), COUNT(c)) " +
            "FROM CustomerSaleRecord c " +
            "WHERE c.createdAt BETWEEN :start AND :end " +
            "GROUP BY DATE(c.createdAt) " +
            "ORDER BY DATE(c.createdAt)")
    List<DailyCustomerCount> countNewCustomersByDate(@Param("start") LocalDateTime start,
                                                     @Param("end") LocalDateTime end);


    @Query(value = "SELECT gender, COUNT(*) FROM customer_sale_record " +
            "WHERE created_at BETWEEN :start AND :end " +
            "GROUP BY gender", nativeQuery = true)
    List<Object[]> countGenderGroup(@Param("start") LocalDateTime start,
                                               @Param("end") LocalDateTime end);

    @Query(value =
            "SELECT customer_type, DATE(created_at) as day, COUNT(*) as total " +
                    "FROM customer_sale_record " +
                    "WHERE created_at BETWEEN :start AND :end " +
                    "GROUP BY customer_type, day " +
                    "ORDER BY day", nativeQuery = true)
    List<Object[]> countCustomerByTypeAndDay(@Param("start") LocalDateTime start,
                                             @Param("end") LocalDateTime end);

    @Query(value =
            "SELECT source, DATE(created_at) as day, COUNT(*) as total " +
                    "FROM customer_sale_record " +
                    "WHERE created_at BETWEEN :start AND :end " +
                    "GROUP BY source, day " +
                    "ORDER BY day", nativeQuery = true)
    List<Object[]> countCustomerBySourceAndDay(@Param("start") LocalDateTime start,
                                               @Param("end") LocalDateTime end);

    @Query(value = "SELECT DISTINCT phone_number FROM customer_sale_record " +
            "WHERE created_at BETWEEN :start AND :end", nativeQuery = true)
    List<String> findPhonesByCreatedAtBetween(@Param("start") LocalDateTime start,
                                              @Param("end") LocalDateTime end);

}
