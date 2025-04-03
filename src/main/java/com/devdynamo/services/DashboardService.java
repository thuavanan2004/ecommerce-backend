package com.devdynamo.services;

import com.devdynamo.dtos.response.DashboardSummaryDTO;
import com.devdynamo.dtos.response.SalesStatsResponse;

public interface DashboardService {
    DashboardSummaryDTO getSummaryStats();

    SalesStatsResponse getSalesStats(String period);
}
