package org.perujug.lambdawithquarkus.store.dynamodb;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.perujug.lambdawithquarkus.model.Student;

import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

public class StudentMapper {

    private static final String ID = "id";
    private static final String FULL_NAME = "fullName";
    private static final String AGE = "age";
    private static final String CREATED_AT = "createdAt";

    static final String ITEM_TYPE = "itemType";
    static final String STUDENTS_ITEM_TYPE = "student";

    public static Student studentFromDynamoDBItem(Map<String, AttributeValue> item) {
        if (item == null || item.isEmpty())
            return null;

        return new Student(
                UUID.fromString(item.get(ID).s()), item.get(FULL_NAME).s(), Integer.valueOf(item.get(AGE).n()),
                Instant.parse(item.get(CREATED_AT).s()));
    }

    public static Map<String, AttributeValue> studentToDynamoDbItem(Student student) {
        var itemValues = new HashMap<String, AttributeValue>();
        itemValues.put(ID, AttributeValue.builder().s(student.id().toString()).build());
        itemValues.put(ITEM_TYPE, AttributeValue.builder().s(STUDENTS_ITEM_TYPE).build());
        itemValues.put(FULL_NAME, AttributeValue.builder().s(student.fullName()).build());
        itemValues.put(AGE, AttributeValue.builder().n(student.age().toString()).build());
        itemValues.put(CREATED_AT, AttributeValue.builder().s(student.createdAt().toString()).build());

        return itemValues;
    }

}
