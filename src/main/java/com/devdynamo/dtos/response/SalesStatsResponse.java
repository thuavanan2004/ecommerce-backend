package com.devdynamo.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class SalesStatsResponse {
    String periodType;
    List<String> labels;
    List<Double> salesData;
    List<Integer> orderCounts;
}
