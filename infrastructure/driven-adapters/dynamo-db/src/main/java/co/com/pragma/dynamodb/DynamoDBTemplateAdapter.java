package co.com.pragma.dynamodb;

import co.com.pragma.dynamodb.helper.TemplateAdapterOperations;
import co.com.pragma.model.report.Report;
import co.com.pragma.model.report.gateways.ReportRepository;
import org.reactivecommons.utils.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Repository
public class DynamoDBTemplateAdapter extends TemplateAdapterOperations<Report, String, ModelEntity
        > implements ReportRepository {

    private static final Logger log = LoggerFactory.getLogger(DynamoDBTemplateAdapter.class);

    private static final String REPORT_ID = "approvedLoans";
    private static final String TABLE_NAME = "approved_report";
    private final DynamoDbAsyncClient dynamoDbAsyncClient;

    public DynamoDBTemplateAdapter(DynamoDbEnhancedAsyncClient connectionFactory, ObjectMapper mapper, DynamoDbAsyncClient dynamoDbAsyncClient) {
        /**
         *  Could be use mapper.mapBuilder if your domain model implement builder pattern
         *  super(repository, mapper, d -> mapper.mapBuilder(d,ObjectModel.ObjectModelBuilder.class).build());
         *  Or using mapper.map with the class of the object model
         */
        super(connectionFactory, mapper, d -> mapper.map(d, Report.class), TABLE_NAME);
        this.dynamoDbAsyncClient = dynamoDbAsyncClient;
    }

    @Override
    public Mono<Report> incrementCounterAndAmount(String id, Long countToAdd, Double totalAmountToAdd) {
        log.info("Actualizando la tabla de reportes en DynamoDB con los siguientes datos: aumentar contador en {} y sumar al monto el valor de {}", countToAdd, totalAmountToAdd);

        Map<String, AttributeValue> key = new HashMap<>();
        key.put("id", AttributeValue.builder().s(id).build());

        Map<String, String> expressionNames = new HashMap<>();
        expressionNames.put("#count", "count");
        expressionNames.put("#totalAmount", "totalAmount");
        expressionNames.put("#createdAt", "createdAt");
        expressionNames.put("#updatedAt", "updatedAt");

        String now = LocalDateTime.now().toString();

        Map<String, AttributeValue> expressionValues = new HashMap<>();
        expressionValues.put(":incrementCount", AttributeValue.builder().n(countToAdd.toString()).build());
        expressionValues.put(":incrementTotalAmount", AttributeValue.builder().n(totalAmountToAdd.toString()).build());
        expressionValues.put(":zero", AttributeValue.builder().n("0").build());
        expressionValues.put(":now", AttributeValue.builder().s(now).build());

        UpdateItemRequest request = UpdateItemRequest.builder()
                .tableName(TABLE_NAME)
                .key(key)
                .updateExpression(
                        "ADD #count :incrementCount " +
                                "SET #totalAmount = if_not_exists(#totalAmount, :zero) + :incrementTotalAmount, " +
                                "#createdAt = if_not_exists(#createdAt, :now), " +
                                "#updatedAt = :now"
                )
                .expressionAttributeNames(expressionNames)
                .expressionAttributeValues(expressionValues)
                .returnValues(ReturnValue.ALL_NEW)
                .build();

        return Mono.fromFuture(() -> dynamoDbAsyncClient.updateItem(request))
                .map(UpdateItemResponse::attributes)
                .map(attrs -> Report.builder()
                        .id(id)
                        .count(Long.parseLong(attrs.get("count").n()))
                        .totalAmount(Double.parseDouble(attrs.get("totalAmount").n()))
                        .build())
                .doOnNext(report -> {
                    log.info("Tabla de reportes actualizada con exitosamente.");
                })
                .onErrorResume(throwable -> {
                    log.error("Error al actualizar la tabla de reportes en DynamoDB: {}", throwable.getMessage());
                    return Mono.empty();
                });
    }

    @Override
    public Mono<Report> getReport() {
        Map<String, AttributeValue> key = new HashMap<>();
        key.put("id", AttributeValue.builder().s(REPORT_ID).build());

        GetItemRequest request = GetItemRequest.builder()
                .tableName(TABLE_NAME)
                .key(key)
                .build();

        return Mono.fromFuture(() -> dynamoDbAsyncClient.getItem(request))
                .filter(GetItemResponse::hasItem)
                .map(GetItemResponse::item)
                .map(attrs -> Report.builder()
                        .id(REPORT_ID)
                        .count(Long.parseLong(attrs.getOrDefault("count", AttributeValue.builder().n("0").build()).n()))
                        .totalAmount(Double.parseDouble(attrs.getOrDefault("totalAmount", AttributeValue.builder().n("0").build()).n()))
                        .build());
    }

    public Mono<List<Report>> getEntityBySomeKeys(String partitionKey, String sortKey) {
        QueryEnhancedRequest queryExpression = generateQueryExpression(partitionKey, sortKey);
        return query(queryExpression);
    }

    public Mono<List<Report>> getEntityBySomeKeysByIndex(String partitionKey, String sortKey) {
        QueryEnhancedRequest queryExpression = generateQueryExpression(partitionKey, sortKey);
        return queryByIndex(queryExpression, "secondary_index" /*index is optional if you define in constructor*/);
    }

    private QueryEnhancedRequest generateQueryExpression(String partitionKey, String sortKey) {
        return QueryEnhancedRequest.builder()
                .queryConditional(QueryConditional.keyEqualTo(Key.builder().partitionValue(partitionKey).build()))
                .queryConditional(QueryConditional.sortGreaterThanOrEqualTo(Key.builder().sortValue(sortKey).build()))
                .build();
    }
}
