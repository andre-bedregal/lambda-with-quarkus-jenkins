package org.perujug.lambdawithquarkus.store;

import org.perujug.lambdawithquarkus.model.Student;
import org.perujug.lambdawithquarkus.model.Students;

public interface StudentStore {

    Student putStudent(Student student);

    Students getAllStudents();

}
