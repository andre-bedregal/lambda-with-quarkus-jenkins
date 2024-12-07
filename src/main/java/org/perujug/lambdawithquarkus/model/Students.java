package org.perujug.lambdawithquarkus.model;

import java.util.List;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public record Students(
                List<Student> students) {

}
