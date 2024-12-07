package org.perujug.lambdawithquarkus.entrypoints;

import org.perujug.lambdawithquarkus.model.Student;
import org.perujug.lambdawithquarkus.model.StudentDTO;
import org.perujug.lambdawithquarkus.store.StudentStore;

import io.quarkus.logging.Log;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/students")
@Produces(MediaType.APPLICATION_JSON)
public class StudentResource {

    @Inject
    StudentStore studentsStore;

    @POST
    public Response postStudent(StudentDTO studentDTO) {
        try {
            var student = Student.studentFromDTO(studentDTO);
            var createdStudent = studentsStore.putStudent(student);
            return Response.status(Response.Status.CREATED).entity(createdStudent).build();
        } catch (Exception e) {
            Log.error("Error creating student", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    @GET
    public Response getAll() {
        try {
            var students = studentsStore.getAllStudents();
            return Response.status(Response.Status.OK).entity(students).build();
        } catch (Exception e) {
            Log.error("Error retrieving all students", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

}
