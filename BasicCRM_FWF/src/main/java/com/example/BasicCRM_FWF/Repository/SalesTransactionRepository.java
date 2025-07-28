package com.example.BasicCRM_FWF.Repository;

import com.example.BasicCRM_FWF.Model.SalesTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface SalesTransactionRepository extends JpaRepository<SalesTransaction,Integer> {
    List<SalesTransaction> findByOrderDateBetween(LocalDateTime start, LocalDateTime end);

    @Query(value = """
        SELECT r.region AS region, 
               DATE(s.order_date) AS date,
               SUM(s.total_amount) AS totalRevenue
        FROM sales_transaction s
        JOIN region r ON s.facility_id = r.id
        WHERE s.order_date BETWEEN :start AND :end
        GROUP BY r.region, DATE(s.order_date)
        ORDER BY DATE(s.order_date)
    """, nativeQuery = true)
    List<Object[]> fetchRevenueByRegionAndDate(@Param("start") LocalDateTime start,
                                               @Param("end") LocalDateTime end);

    @Query(value = """
        SELECT r.shop_type AS shopType,
               DATE(s.order_date) AS date,
               SUM(s.total_amount) AS totalRevenue
        FROM sales_transaction s
        JOIN region r ON s.facility_id = r.id
        WHERE s.order_date BETWEEN :start AND :end
        GROUP BY r.shop_type, DATE(s.order_date)
        ORDER BY DATE(s.order_date)
    """, nativeQuery = true)
    List<Object[]> fetchRevenueByShopTypeAndDate(@Param("start") LocalDateTime start,
                                                 @Param("end") LocalDateTime end);

    @Query(value = """
        SELECT SUM(s.original_price - s.price_change)
        FROM sales_transaction s
        WHERE s.order_date BETWEEN :start AND :end
    """, nativeQuery = true)
    BigDecimal fetchRevenueSummary(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query(value = """
        SELECT SUM(s.cash_transfer_credit)
        FROM sales_transaction s
        WHERE s.order_date BETWEEN :start AND :end
    """, nativeQuery = true)
    BigDecimal fetchActualRevenueSummary(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query(value = """
        SELECT r.region AS region,
               COUNT(*) AS orders,
               SUM(s.cash_transfer_credit) AS revenue
        FROM sales_transaction s
        JOIN region r ON s.facility_id = r.id
        WHERE s.order_date BETWEEN :start AND :end
        GROUP BY r.region
    """, nativeQuery = true)
    List<Object[]> fetchOrderAndRevenueByRegion(@Param("start") LocalDateTime start,
                                                 @Param("end") LocalDateTime end);

    @Query(value = """
    SELECT r.region AS region,
           SUM(s.cash_transfer_credit) AS actualRevenue
    FROM sales_transaction s
    JOIN region r ON s.facility_id = r.id
    WHERE s.order_date BETWEEN :start AND :end
    GROUP BY r.region
""", nativeQuery = true)
    List<Object[]> fetchActualRevenueByRegion(@Param("start") LocalDateTime start,
                                              @Param("end") LocalDateTime end);

}
