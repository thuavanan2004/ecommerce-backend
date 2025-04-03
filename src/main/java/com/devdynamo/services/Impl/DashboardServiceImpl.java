package com.devdynamo.services.Impl;

import com.devdynamo.dtos.response.DashboardSummaryDTO;
import com.devdynamo.enums.Role;
import com.devdynamo.repositories.DashboardRepository;
import com.devdynamo.repositories.ProductRepository;
import com.devdynamo.repositories.UserRepository;
import com.devdynamo.services.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {
    private final DashboardRepository dashboardRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Override
    public DashboardSummaryDTO summary() {
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

        // Top categories
//        summary.setTopCategories(dashboardRepository.getTopCategories(PageRequest.of(0, 5)));

        return summary;
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
