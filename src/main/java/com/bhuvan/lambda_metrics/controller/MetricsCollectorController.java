package com.bhuvan.lambda_metrics.controller;

import com.bhuvan.lambda_metrics.service.MetricsCollectorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class MetricsCollectorController {
    private final MetricsCollectorService metricsCollectorService;

    @Autowired
    public MetricsCollectorController(MetricsCollectorService metricsCollectorService) {
        this.metricsCollectorService = metricsCollectorService;
    }

    @GetMapping("/collect-metrics")
    public Map<String, Double> collectMetrics(@RequestParam String functionName) {
        return metricsCollectorService.collectMetrics(functionName);
    }
}
