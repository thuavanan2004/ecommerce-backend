package com.devdynamo.repositories;

import com.devdynamo.entities.OrderItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItemEntity, Long> {
    List<OrderItemEntity> findAllByOrderId(Long orderId);

    @Query(value = """
        SELECT 
            c.name AS categoryName,
            SUM(oi.price * oi.quantity) AS totalSales,
            COUNT(DISTINCT o.id) AS orderCount,
            SUM(oi.quantity) AS productCount
        FROM order_items oi
        JOIN products p ON oi.product_id = p.id
        JOIN categories c ON p.category_id = c.id
        JOIN orders o ON oi.order_id = o.id
        WHERE 1 = 1
        GROUP BY c.name
        ORDER BY totalSales DESC
        """, nativeQuery = true)
    List<Object[]> findSalesByCategory();

}
