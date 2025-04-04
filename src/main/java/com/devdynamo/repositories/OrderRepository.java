package com.devdynamo.repositories;

import com.devdynamo.dtos.response.OrderStatusCount;
import com.devdynamo.entities.OrderEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository extends JpaRepository<OrderEntity, Long>, JpaSpecificationExecutor<OrderEntity> {
    Page<OrderEntity> findAllByUserId(long userId, Pageable pageable);

    @Query(value = """
        SELECT 
            DATE_FORMAT(o.created_at, '%Y-%m') AS month,
            SUM(o.total_price) AS totalSales,
            COUNT(o.id) AS orderCount
        FROM orders o
        WHERE 1 = 1
        GROUP BY DATE_FORMAT(o.created_at, '%Y-%m')
        ORDER BY month ASC
        """, nativeQuery = true)
    List<Object[]> findMonthlySalesStats();

    @Query(value = """
        SELECT 
            DATE_FORMAT(o.created_at, '%Y-%m-%d') AS day,
            SUM(o.total_price) AS totalSales,
            COUNT(o.id) AS orderCount
        FROM orders o
        WHERE 1 = 1
          AND o.created_at >= DATE_SUB(CURRENT_DATE, INTERVAL 30 DAY)
        GROUP BY DATE_FORMAT(o.created_at, '%Y-%m-%d')
        ORDER BY day ASC
        """, nativeQuery = true)
    List<Object[]> findDailySalesStats();

    @Query(value = """
        SELECT 
            YEAR(o.created_at) AS year,
            WEEK(o.created_at) AS week,
            SUM(o.total_price) AS totalSales,
            COUNT(o.id) AS orderCount
        FROM orders o
        WHERE 1 = 1
        GROUP BY YEAR(o.created_at), WEEK(o.created_at)
        ORDER BY year, week ASC
        """, nativeQuery = true)
    List<Object[]> findWeeklySalesStats();

    @Query("SELECT o.status, COUNT(o) " +
            "FROM OrderEntity o GROUP BY o.status")
    List<Object[]> getOrderStatusStatistics();
}
