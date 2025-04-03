package com.devdynamo.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class DashboardSummaryDTO {
    private BigDecimal totalSales;
    private Long totalOrders;
    private Long totalProducts;
    private Long totalCustomers;
    private RevenueTrendDTO revenueTrend;
    private List<CategorySalesDTO> topCategories;

    @Data
    public static class RevenueTrendDTO {
        private BigDecimal currentMonth;
        private BigDecimal previousMonth;
        private BigDecimal percentageChange;
    }
    @Data
    @AllArgsConstructor
    public static class CategorySalesDTO {
        private Long categoryId;
        private String categoryName;
        private BigDecimal sales;
    }
}



