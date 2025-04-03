package com.devdynamo.controllers.admin;

import com.devdynamo.dtos.response.ResponseData;
import com.devdynamo.dtos.response.ResponseError;
import com.devdynamo.services.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/admin/dashboard")
@Validated
@Tag(name = "Dashboard")
public class DashboardController {
    private final DashboardService dashboardService;

    @Operation(summary = "Summary")
    @GetMapping("/summary")
    public ResponseData<?> summary(){
        log.info("Dashboard Summary");
        try{
            return new ResponseData<>(HttpStatus.OK.value(), "Get summary success", dashboardService.summary());
        } catch (Exception e){
            log.info("errorMessage={}", e.getMessage(), e.getCause());
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), "Get summary failed");
        }

    }
}
