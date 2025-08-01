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

    @Query(value = """
        SELECT DATE(s.order_date) AS order_date,
               r.region AS region,
               SUM(s.cash_transfer_credit) AS revenue
        FROM sales_transaction s
        JOIN region r ON s.facility_id = r.id
        WHERE s.order_date BETWEEN :start AND :end
        GROUP BY DATE(s.order_date), r.region
        ORDER BY DATE(s.order_date), r.region
    """, nativeQuery = true)
    List<Object[]> fetchDailyRevenueByRegion(@Param("start") LocalDateTime start,
                                             @Param("end") LocalDateTime end);

    @Query(value = """
        SELECT DATE(st.order_date) AS order_date,
               r.shop_type AS shop_type,
               SUM(st.cash_transfer_credit) AS revenue
        FROM sales_transaction st
                 JOIN region r ON st.facility_id = r.id
        WHERE st.order_date BETWEEN :start AND :end
        GROUP BY DATE(st.order_date), r.shop_type
        ORDER BY DATE(st.order_date), r.shop_type
        """, nativeQuery = true)
    List<Object[]> getDailyRevenueByShopType(@Param("start") LocalDateTime start,
                                             @Param("end") LocalDateTime end);

    @Query(value = "SELECT c.customer_type, DATE(s.order_date), SUM(s.cash_transfer_credit) " +
            "FROM customer_sale_record c " +
            "JOIN sales_transaction s ON c.phone_number = s.phone_number " +
            "WHERE s.order_date BETWEEN :start AND :end " +
            "GROUP BY c.customer_type, DATE(s.order_date)", nativeQuery = true)
    List<Object[]> findRevenueByCustomerTypeAndDate(@Param("start") LocalDateTime start,
                                                    @Param("end") LocalDateTime end);

    @Query(value = """
        SELECT r.shop_name,
               SUM(s.cash_transfer_credit) AS actualRevenue,
               SUM(s.prepaid_card) AS foxieCardRevenue
        FROM sales_transaction s
        JOIN region r ON s.facility_id = r.id
        WHERE s.order_date BETWEEN :start AND :end
        GROUP BY r.shop_name
        ORDER BY actualRevenue DESC
        LIMIT 10
    """, nativeQuery = true)
    List<Object[]> findTop10StoreRevenue(@Param("start") LocalDateTime start,
                                         @Param("end") LocalDateTime end);

    @Query(value = "SELECT r.shop_name, COUNT(st.id), " +
            "SUM(st.cash_transfer_credit), SUM(st.prepaid_card) " +
            "FROM sales_transaction st " +
            "JOIN region r ON st.facility_id = r.id " +
            "WHERE st.order_date BETWEEN :start AND :end " +
            "GROUP BY r.shop_name",
            nativeQuery = true)
    List<Object[]> findStoreRevenueStatsBetween(@Param("start") LocalDateTime start,
                                                @Param("end") LocalDateTime end);

    @Query(value = "SELECT r.shop_name, COUNT(st.id), " +
            "SUM(st.cash_transfer_credit), SUM(st.prepaid_card) " +
            "FROM sales_transaction st " +
            "JOIN region r ON st.facility_id = r.id " +
            "WHERE st.order_date BETWEEN :start AND :end " +
            "GROUP BY r.shop_name",
            nativeQuery = true)
    List<Object[]> findPreviousStoreRevenueStatsBetween(@Param("start") LocalDateTime start,
                                                        @Param("end") LocalDateTime end);

    @Query(value = "SELECT DATE(order_date) as order_day, COUNT(*) as total_orders, COUNT(DISTINCT facility_id) as shop_count\n" +
            "FROM sales_transaction\n" +
            "WHERE order_date BETWEEN :start AND :end\n" +
            "GROUP BY DATE(order_date)\n" +
            "ORDER BY DATE(order_date)", nativeQuery = true)
    List<Object[]> findDailyOrderAndShopStats(@Param("start") LocalDateTime start,
                                              @Param("end") LocalDateTime end);

    @Query("SELECT st.facility.region, " +
            "       COALESCE(SUM(st.cash), 0), " +
            "       COALESCE(SUM(st.transfer), 0), " +
            "       COALESCE(SUM(st.creditCard), 0) " +
            "FROM SalesTransaction st " +
            "WHERE st.orderDate BETWEEN :start AND :end " +
            "  AND st.cashTransferCredit > 0 " +
            "GROUP BY st.facility.region")
    List<Object[]> findPaymentByRegion(LocalDateTime start, LocalDateTime end);

}
