package co.com.pragma.dynamodb;

import lombok.Setter;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.*;

import java.time.LocalDate;

/* Enhanced DynamoDB annotations are incompatible with Lombok #1932
         https://github.com/aws/aws-sdk-java-v2/issues/1932*/
@DynamoDbBean
public class ModelEntity {

    @Setter
    private String id;
    @Setter
    private Long count;
    private Double totalAmount;
    private LocalDate createdAt;
    private LocalDate updatedAt;

    public ModelEntity() {
    }

    public ModelEntity(String id, Long count, Double totalAmount, LocalDate createdAt, LocalDate updatedAt) {
        this.id = id;
        this.count = count;
        this.totalAmount = totalAmount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    @DynamoDbPartitionKey
    @DynamoDbAttribute("id")
    public String getId() {
        return id;
    }

    @DynamoDbAttribute("count")
    public Long getCount() {
        return count;
    }

    @DynamoDbAttribute("totalAmount")
    public Double getTotalAmount() {
        return totalAmount;
    }

}
