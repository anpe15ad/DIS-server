package view.endpoints;

import com.google.gson.Gson;

import logic.TeacherController;
import logic.UserController;
import shared.LectureDTO;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.ArrayList;

/**
 * Created by Kasper on 19/10/2016.
 */
@Path("/api/teacher")
public class TeacherEndpoint extends UserEndpoint {
    @DELETE
    @Consumes("application/json")
    @Path("/review/{reviewId}")
    public Response deleteReview(@PathParam("reviewId") int reviewId) {
        Gson gson = new Gson();

        //  ReviewDTO review = gson.fromJson(data, ReviewDTO.class);
        TeacherController teacherController = new TeacherController();

        boolean isDeleted = teacherController.softDeleteReview(reviewId);

        if (isDeleted) {
            // String toJson = gson.toJson(Digester.encrypt(gson.toJson(isDeleted)));
            String toJson = gson.toJson(gson.toJson(isDeleted));
            System.out.println("Returnerede boolean ved sletning af review fra Teacherendpoint");
            return successResponse(200, toJson);
        } else {
            return errorResponse(404, "Failed. Couldn't delete the chosen review.");
        }
    }

    @GET
    @Consumes("applications/json")
    @Produces("applications/json")
    @Path("/average/{code}")
    public Response average(@PathParam("code") String code) {
        System.out.println("Ramte endpointed /teacher/average/{code}");
        TeacherController teacherController = new TeacherController();
        double average = 0;
        average = teacherController.calculateAverageRatingOnCourse(code,average);

        if (average == 0) {
            System.out.println("Returnerede average op et kursus");
            return successResponse(200, average);
        } else {
            return errorResponse(404, "Failed. Couldn't get lectures.");
        }
    }

}
