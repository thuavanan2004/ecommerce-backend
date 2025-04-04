package com.devdynamo.repositories;

import com.devdynamo.dtos.response.DashboardSummaryDTO;
import com.devdynamo.entities.OrderEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DashboardRepository extends JpaRepository<OrderEntity, Long> {

    @Query("SELECT COALESCE(SUM(o.totalPrice), 0) FROM OrderEntity o " +
            "WHERE o.status = 'delivered'")
    BigDecimal getTotalSales();

    @Query("SELECT COUNT(o) FROM OrderEntity o")
    Long countAllOrders();

    @Query("SELECT COUNT(p) FROM ProductEntity p WHERE p.deleted = false")
    Long countActiveProducts();

    @Query("SELECT COUNT(u) FROM UserEntity u WHERE u.role = 'customer'")
    Long countAllCustomers();

    @Query("SELECT FUNCTION('MONTH', o.createdAt), FUNCTION('YEAR', o.createdAt), " +
            "COALESCE(SUM(o.totalPrice), 0) " +
            "FROM OrderEntity o " +
            "WHERE o.status = 'delivered' AND o.createdAt BETWEEN :start AND :end " +
            "GROUP BY FUNCTION('MONTH', o.createdAt), FUNCTION('YEAR', o.createdAt)")
    List<Object[]> getMonthlyRevenue(@Param("start") LocalDateTime start,
                                     @Param("end") LocalDateTime end);

    @Query("SELECT c.id, c.name, SUM(oi.quantity * oi.price) " +
            "FROM OrderItemEntity oi " +
            "JOIN oi.product p " +
            "JOIN p.category c " +
            "GROUP BY c.id, c.name " +
            "ORDER BY SUM(oi.quantity * oi.price) DESC")
    List<Object[]> getTopCategories(Pageable pageable);
}
