package co.com.pragma.dynamodb.helper;

import co.com.pragma.dynamodb.DynamoDBTemplateAdapter;
import co.com.pragma.dynamodb.ModelEntity;
import co.com.pragma.model.report.Report;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.reactivecommons.utils.ObjectMapper;
import reactor.test.StepVerifier;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncTable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.time.LocalDate;
import java.util.Date;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TemplateAdapterOperationsTest {

    @Mock
    private DynamoDbEnhancedAsyncClient dynamoDbEnhancedAsyncClient;

    @Mock
    private DynamoDbAsyncClient dynamoDbAsyncClient;

    @Mock
    private ObjectMapper mapper;

    @Mock
    private DynamoDbAsyncTable<ModelEntity> customerTable;

    private ModelEntity modelEntity;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        when(dynamoDbEnhancedAsyncClient.table("table_name", TableSchema.fromBean(ModelEntity.class)))
                .thenReturn(customerTable);

        mock(dynamoDbAsyncClient);

        modelEntity = new ModelEntity();
        modelEntity.setId("id");
        modelEntity.getCount();
    }

    @Test
    void modelEntityPropertiesMustNotBeNull() {
        ModelEntity modelEntityUnderTest = new ModelEntity("id", 1L, 500000D, LocalDate.now(), LocalDate.now());

        assertNotNull(modelEntityUnderTest.getId());
        assertNotNull(modelEntityUnderTest.getCount());
        assertNotNull(modelEntityUnderTest.getTotalAmount());
    }

    @Test
    void testSave() {
        when(customerTable.putItem(modelEntity)).thenReturn(CompletableFuture.runAsync(()->{}));
        when(mapper.map(modelEntity, ModelEntity.class)).thenReturn(modelEntity);

        DynamoDBTemplateAdapter dynamoDBTemplateAdapter =
                new DynamoDBTemplateAdapter(dynamoDbEnhancedAsyncClient, mapper, dynamoDbAsyncClient);

        StepVerifier.create(dynamoDBTemplateAdapter.save(new Report()))
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void testGetById() {
        String id = "id";

        when(customerTable.getItem(
                Key.builder().partitionValue(AttributeValue.builder().s(id).build()).build()))
                .thenReturn(CompletableFuture.completedFuture(modelEntity));
        when(mapper.map(modelEntity, Object.class)).thenReturn("value");

        DynamoDBTemplateAdapter dynamoDBTemplateAdapter =
                new DynamoDBTemplateAdapter(dynamoDbEnhancedAsyncClient, mapper, dynamoDbAsyncClient);

        StepVerifier.create(dynamoDBTemplateAdapter.getById("id"))
                .expectNext()
                .verifyComplete();
    }

    @Test
    void testDelete() {
        when(mapper.map(modelEntity, ModelEntity.class)).thenReturn(modelEntity);
        when(mapper.map(modelEntity, Object.class)).thenReturn("value");

        when(customerTable.deleteItem(modelEntity))
                .thenReturn(CompletableFuture.completedFuture(modelEntity));

        DynamoDBTemplateAdapter dynamoDBTemplateAdapter =
                new DynamoDBTemplateAdapter(dynamoDbEnhancedAsyncClient, mapper, dynamoDbAsyncClient);

        StepVerifier.create(dynamoDBTemplateAdapter.delete(new Report()))
                .expectNext()
                .verifyComplete();
    }
}