package view.endpoints;

import com.google.gson.Gson;

import logic.TeacherController;
import logic.UserController;
import security.Digester;
import shared.LectureDTO;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.ArrayList;

/**
 * Denne klasse er endpoint for underviser (teacher) med de metoder der er unik for en underviser.
 */
@Path("/api/teacher")
public class TeacherEndpoint extends UserEndpoint {

    /**
     * hentet fra vores fælles server: https://github.com/emilstepanian/Undervisningsevaluering
     * Denne metode soft deleter en kommentar
     * @param reviewId det review id den sletter for.
     * @return returnere en boolean som en String.
     */
    @DELETE
    @Consumes("application/json")
    @Path("/review/{reviewId}")
    public Response deleteReview(@PathParam("reviewId") String reviewId) {
        Gson gson = new Gson();

        String  decrypted = Digester.decrypt(reviewId);

        int inted = Integer.valueOf(decrypted);

        //  ReviewDTO review = gson.fromJson(data, ReviewDTO.class);
        TeacherController teacherController = new TeacherController();

        boolean isDeleted = teacherController.softDeleteReview(inted);

        if (isDeleted) {
            // String toJson = gson.toJson(Digester.encrypt(gson.toJson(isDeleted)));
            String toJson = gson.toJson(gson.toJson(isDeleted));
            System.out.println(isDeleted);
            System.out.println("Returnerede boolean ved sletning af review fra Teacherendpoint");
            return successResponse(200, toJson);
        } else {
            return errorResponse(404, "Failed. Couldn't delete the chosen review.");
        }
    }

    /**
     * Dette endpoint returnere et gennemsnitet for et kursus.
     * @param code for det kursus der bliver lavet gennemsnit for.
     * @return returnere en String med gennemsnittet for kurset.
     */
    @GET
    @Consumes("applications/json")
    @Produces("applications/json")
    @Path("/average/{code}")
    public Response average(@PathParam("code") String code) {
        String codeDecrypted = Digester.decrypt(code);

        TeacherController teacherController = new TeacherController();
        double average = 0;
        average = teacherController.calculateAverageRatingOnCourse(codeDecrypted,average);

        if (average != 0) {
            String returnIT = String.valueOf(average);
            System.out.println("Returnerede average for et kursus");
            return successResponse(200, returnIT);
        } else {
            return errorResponse(404, "Failed. Couldn't get lectures.");
        }
    }

    /**
     * Dette endpoint sender antallet af deltagere til et kursus tilbage til klienten som en String.
     * @param courseId som den finder antallet af kursus deltagere ud fra.
     * @return returnere en string af antallet af deltagere.
     */
    @GET
    @Consumes("applications/json")
    @Produces("applications/json")
    @Path("/participents/{courseId}")
    public Response participents(@PathParam("courseId") String courseId) {
        String codeDecrypted = Digester.decrypt(courseId);
        int course = Integer.valueOf(codeDecrypted);

        TeacherController teacherController = new TeacherController();

         int participants = teacherController.getCourseParticipants(course);

        if (participants != 0) {
            String returnIt = String.valueOf(participants);
            System.out.println("Returnerede average for et kursus: "+ returnIt);
            return successResponse(200, returnIt);
        } else {
            return errorResponse(404, "Failed. Couldn't get lectures.");
        }
    }



}
