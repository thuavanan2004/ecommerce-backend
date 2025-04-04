package com.devdynamo.services.Impl;

import com.devdynamo.dtos.response.CategorySalesStats;
import com.devdynamo.dtos.response.DashboardSummaryDTO;
import com.devdynamo.dtos.response.OrderStatusCount;
import com.devdynamo.dtos.response.SalesStatsResponse;
import com.devdynamo.enums.OrderStatus;
import com.devdynamo.enums.Role;
import com.devdynamo.repositories.*;
import com.devdynamo.services.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {
    private final DashboardRepository dashboardRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    @Override
    public DashboardSummaryDTO getSummaryStats() {
        DashboardSummaryDTO summary = new DashboardSummaryDTO();

        summary.setTotalSales(dashboardRepository.getTotalSales());
        summary.setTotalOrders(dashboardRepository.countAllOrders());
        summary.setTotalProducts(productRepository.countByDeletedFalse());
        summary.setTotalCustomers(userRepository.countByRole(Role.customer));

        // Revenue trend calculation
        LocalDate now = LocalDate.now();
        LocalDateTime startOfCurrentMonth = now.withDayOfMonth(1).atStartOfDay();
        LocalDateTime endOfCurrentMonth = now.withDayOfMonth(now.lengthOfMonth()).atTime(23, 59, 59);

        LocalDateTime startOfPreviousMonth = startOfCurrentMonth.minusMonths(1);
        LocalDateTime endOfPreviousMonth = endOfCurrentMonth.minusMonths(1);

        BigDecimal currentMonthRevenue = dashboardRepository.getMonthlyRevenue(
                startOfCurrentMonth, endOfCurrentMonth
        ).stream().findFirst().map(arr -> (BigDecimal) arr[2]).orElse(BigDecimal.ZERO);

        BigDecimal previousMonthRevenue = dashboardRepository.getMonthlyRevenue(
                startOfPreviousMonth, endOfPreviousMonth
        ).stream().findFirst().map(arr -> (BigDecimal) arr[2]).orElse(BigDecimal.ZERO);

        DashboardSummaryDTO.RevenueTrendDTO trend = new DashboardSummaryDTO.RevenueTrendDTO();
        trend.setCurrentMonth(currentMonthRevenue);
        trend.setPreviousMonth(previousMonthRevenue);
        trend.setPercentageChange(calculatePercentageChange(previousMonthRevenue, currentMonthRevenue));
        summary.setRevenueTrend(trend);

//        summary.setTopCategories(dashboardRepository.getTopCategories(PageRequest.of(0, 5)));

        return summary;
    }

    @Override
    public SalesStatsResponse getSalesStats(String period) {
        List<Object[]> statsData;
        String periodFormat;

        switch (period.toLowerCase()) {
            case "daily":
                statsData = orderRepository.findDailySalesStats();
                periodFormat = "day";
                break;
            case "weekly":
                statsData = orderRepository.findWeeklySalesStats();
                periodFormat = "week";
                break;
            case "monthly":
            default:
                statsData = orderRepository.findMonthlySalesStats();
                periodFormat = "month";
        }

        List<String> labels = new ArrayList<>();
        List<Double> salesData = new ArrayList<>();
        List<Integer> orderCounts = new ArrayList<>();

        for (Object[] row : statsData) {
            labels.add(row[0].toString());
            salesData.add(Double.parseDouble(row[1].toString()));
            orderCounts.add(Integer.parseInt(row[2].toString()));
        }

        return new SalesStatsResponse(
                periodFormat,
                labels,
                salesData,
                orderCounts
        );

    }

    @Override
    public List<CategorySalesStats> getSalesByCategory() {
        List<Object[]> results = orderItemRepository.findSalesByCategory();

        return results.stream()
                .map(row -> new CategorySalesStats(
                        (String) row[0],    // categoryName
                        ((Number) row[1]).doubleValue(),  // totalSales
                        ((Number) row[2]).intValue(),     // orderCount
                        ((Number) row[3]).intValue()       // productCount
                ))
                .collect(Collectors.toList());
    }

    @Override
    public List<OrderStatusCount> getOrderStatusStats() {
        List<OrderStatusCount> result = new ArrayList<>();
        List<Object[]> records = orderRepository.getOrderStatusStatistics();
        for(Object[] o: records){
            result.add(new OrderStatusCount((OrderStatus) o[0], (Long) o[1]));
        }
        return result;
    }

    private BigDecimal calculatePercentageChange(BigDecimal previous, BigDecimal current) {
        if (previous.compareTo(BigDecimal.ZERO) == 0) {
            return current.compareTo(BigDecimal.ZERO) == 0
                    ? BigDecimal.ZERO
                    : new BigDecimal(100);
        }
        return current.subtract(previous)
                .divide(previous, 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal(100))
                .setScale(2, RoundingMode.HALF_UP);
    }

}
