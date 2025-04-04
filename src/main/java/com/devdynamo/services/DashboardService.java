package com.devdynamo.services;

import com.devdynamo.dtos.response.CategorySalesStats;
import com.devdynamo.dtos.response.DashboardSummaryDTO;
import com.devdynamo.dtos.response.OrderStatusCount;
import com.devdynamo.dtos.response.SalesStatsResponse;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface DashboardService {
    DashboardSummaryDTO getSummaryStats();

    SalesStatsResponse getSalesStats(String period);

    List<CategorySalesStats> getSalesByCategory();

    List<OrderStatusCount> getOrderStatusStats();
}
