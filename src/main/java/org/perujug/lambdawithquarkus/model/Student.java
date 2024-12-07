package org.perujug.lambdawithquarkus.model;

import java.time.Instant;
import java.util.UUID;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public record Student(
        UUID id, String fullName, Integer age, Instant createdAt) {

    public static Student studentFromDTO(StudentDTO studentDTO) {
        return new Student(
                UUID.randomUUID(), studentDTO.fullName(), studentDTO.age(), Instant.now());
    }

}
