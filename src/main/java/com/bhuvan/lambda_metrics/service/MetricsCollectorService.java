package com.bhuvan.lambda_metrics.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.cloudwatch.CloudWatchClient;
import software.amazon.awssdk.services.cloudwatch.model.*;
import software.amazon.awssdk.services.cloudwatch.model.Dimension;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MetricsCollectorService {
    private final CloudWatchClient cloudWatchClient;

    @Autowired
    public MetricsCollectorService(CloudWatchClient cloudWatchClient) {
        this.cloudWatchClient = cloudWatchClient;
    }

    public Map<String, Double> collectMetrics(String functionName) {
        Instant endTime = Instant.now();
        Instant startTime = endTime.minusSeconds(3600);  // Collect metrics for the last hour

        Dimension functionDimension = Dimension.builder()
                .name("FunctionName")
                .value(functionName)
                .build();

        List<MetricDataQuery> queries = List.of(
                createMetricDataQuery("Invocations", "AWS/Lambda", functionDimension),
                createMetricDataQuery("Errors", "AWS/Lambda", functionDimension),
                createMetricDataQuery("Duration", "AWS/Lambda", functionDimension),
                createMetricDataQuery("Throttles", "AWS/Lambda", functionDimension),
                createMetricDataQuery("DeadLetterErrors", "AWS/Lambda", functionDimension),
                createMetricDataQuery("IteratorAge", "AWS/Lambda", functionDimension),
                createMetricDataQuery("ConcurrentExecutions", "AWS/Lambda", functionDimension),
                createMetricDataQuery("UnreservedConcurrentExecutions", "AWS/Lambda", functionDimension),
                createMetricDataQuery("Cost", "AWS/Lambda", functionDimension) // Estimated cost is not a direct metric, need calculation
        );

        GetMetricDataRequest request = GetMetricDataRequest.builder()
                .startTime(startTime)
                .endTime(endTime)
                .metricDataQueries(queries)
                .build();

        GetMetricDataResponse response = cloudWatchClient.getMetricData(request);

        // Process the response to extract metric values
        return processMetrics(response);
    }

    private MetricDataQuery createMetricDataQuery(String metricName, String namespace, Dimension dimension) {
        Metric metric = Metric.builder()
                .namespace(namespace)
                .metricName(metricName)
                .dimensions(dimension)
                .build();

        MetricStat metricStat = MetricStat.builder()
                .metric(metric)
                .period(60)
                .stat("Sum")
                .build();

        return MetricDataQuery.builder()
                .id(metricName.toLowerCase())
                .metricStat(metricStat)
                .returnData(true)
                .build();
    }

    private Map<String, Double> processMetrics(GetMetricDataResponse response) {
        return response.metricDataResults().stream()
                .collect(Collectors.toMap(
                        MetricDataResult::id,
                        result -> result.values().isEmpty() ? 0.0 : result.values().get(0)
                ));
    }
}