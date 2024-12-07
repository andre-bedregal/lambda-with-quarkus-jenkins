package org.perujug.lambdawithquarkus.store.dynamodb;

import java.util.ArrayList;
import java.util.Map;

import org.perujug.lambdawithquarkus.model.Student;
import org.perujug.lambdawithquarkus.model.Students;
import org.perujug.lambdawithquarkus.store.StudentStore;

import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;
import software.amazon.awssdk.services.dynamodb.model.ReturnValue;

@ApplicationScoped
public class DynamoDbStudentsStore implements StudentStore {

    @Inject
    private DynamoDbClient dynamoDbClient;

    private final String TABLE_NAME = System.getenv("TABLE_NAME");

    @Override
    public Student putStudent(Student student) {
        Log.infof("Saving student [%s] into [%s] table...", student.id(), TABLE_NAME);
        // create the PUT ITEM request
        var request = PutItemRequest.builder()
                .tableName(TABLE_NAME)
                .item(StudentMapper.studentToDynamoDbItem(student))
                .returnValues(ReturnValue.ALL_OLD)
                .build();

        try {
            dynamoDbClient.putItem(request);
            Log.infof("Saving student [%s] into [%s] table... Done.", student.fullName(), TABLE_NAME);
        } catch (DynamoDbException e) {
            Log.error(e.getMessage(), e);
        }

        return student;
    }

    @Override
    public Students getAllStudents() {
        Log.debugf("Searching for all students in [%s] table...", TABLE_NAME);
        // create the QUERY request
        var request = QueryRequest.builder()
                .tableName(TABLE_NAME)
                .indexName("type_index")
                .keyConditionExpression(String.format("%s = :k", StudentMapper.ITEM_TYPE))
                .expressionAttributeValues(
                        Map.of(":k", AttributeValue.builder().s(StudentMapper.STUDENTS_ITEM_TYPE).build()))
                .scanIndexForward(false)
                .build();

        var students = new ArrayList<Student>();
        var queryResponse = dynamoDbClient.query(request);
        queryResponse.items().forEach(item -> students.add(StudentMapper.studentFromDynamoDBItem(item)));

        Log.debugf("Searching for all students in [%s] table... Done.", TABLE_NAME);
        return new Students(students);
    }

}
