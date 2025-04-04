package com.devdynamo.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class CategorySalesStats {
    String categoryName;
    double totalSales;
    int orderCount;
    int productCount;
}
