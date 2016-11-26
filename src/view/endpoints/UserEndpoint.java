package view.endpoints;

import com.google.gson.Gson;
import logic.UserController;
import security.Digester;
import shared.CourseDTO;
import shared.LectureDTO;
import shared.ReviewDTO;
import shared.UserDTO;
import shared.StudyDTO;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.ArrayList;


@Path("/api")
public class UserEndpoint {

    /**
     * En metode til at hente lektioner for et enkelt kursus i form af en JSON String.
     *
     * @param code Fagkoden på det kursus man ønsker at hente.
     * @return En JSON String
     */
    @GET
    @Consumes("applications/json")
    @Produces("applications/json")
    @Path("/lecture/{code}")
    public Response getLectures(@PathParam("code") String code) {
        UserController userCtrl = new UserController();
        ArrayList<LectureDTO> lectures = userCtrl.getLectures(code);

        if (!lectures.isEmpty()) {
            System.out.println("Returnerede lectures");
            return successResponse(200, lectures);
        } else {
            return errorResponse(404, "Failed. Couldn't get lectures.");
        }
    }

    @GET
    @Path("/study/{shortname}")
    public Response getStudy(@PathParam("shortname") String shortname) {

        Gson gson = new Gson();
        UserController userCtrl = new UserController();
        ArrayList<StudyDTO> studies = userCtrl.getStudies(shortname);

        if (!studies.isEmpty()) {
            return successResponse(200, studies);
        } else {
            return errorResponse(410, "Failed. Couldn't get courses.");
        }
    }


    /**
     * En metode til at hente de kurser en bruger er tilmeldt.
     *
     * @param userId Id'et på den bruger man ønsker at hente kurser for.
     * @return De givne kurser i form af en JSON String.
     */
    @GET
    @Path("/course/{userId}")
    public Response getCourses(@PathParam("userId") int userId) {

        Gson gson = new Gson();
        UserController userCtrl = new UserController();
        ArrayList<CourseDTO> courses = userCtrl.getCourses(userId);

        if (!courses.isEmpty()) {
            return successResponse(200, courses);
        } else {
            return errorResponse(404, "Failed. Couldn't get reviews.");
        }
    }

    /**
     * End point for at hente de reviews der hører til et lecture
     * @param lectureId id'et er det som review listen er hentet udfra.
     * @return retunere enten succes med reviewDTO eller fejl med en meddelse om fejlen
     */
    @GET
    @Consumes("applications/json")
    @Path("/review/{lectureId}")
    public Response getReviews(@PathParam("lectureId") int lectureId) {
        UserController userCtrl = new UserController();
        ArrayList<ReviewDTO> reviews = userCtrl.getReviews(lectureId);

        if (!reviews.isEmpty()) {
            System.out.println("returnede reviews fra UserEndPoint");
            return successResponse(200, reviews);
        } else {
            return errorResponse(404, "Failed. Couldn't get reviews.");
        }
    }




    /**
     * Login options bruges til at tillade tilgang fra flere indgange adgangen, i Google Chrome.
     * Dette bliver kun brugt ved JavaScript klient
     * @return
     */

    @OPTIONS
    @Path("/login")
    public Response optionsLogin() {
        return Response
                .status(200)
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Headers", "Content-Type")
                .build();
    }

    /**
     * Login metode til at logge brugere ind.
     * @param data tager en streng af data som er mail og password
     * @return returnere et objekt af UserDTO og status kode, eller en fejlmeddelse ved fejl.
     */
    @POST
    @Consumes("application/json")
    @Path("/login")
    public Response login(String data) {
       // String decrypted = Digester.decrypt(data);

        Gson gson = new Gson();
        UserDTO user = new Gson().fromJson(data, UserDTO.class);
        UserController userCtrl = new UserController();
        String hashed = Digester.hashWithSalt(user.getPassword());

        if (user != null) {
            UserDTO foo =  userCtrl.login(user.getCbsMail(), hashed);
            return successResponse(200,foo);
        } else {
            return errorResponse(401, "Couldn't login. Try again!");
        }
    }

    /**
     * Metode kaldt i alle endpoint for at sende en fejlstatus og meddelse.
     * @param status
     * @param message
     * @return
     */
    protected Response errorResponse(int status, String message) {

        //return Response.status(status).entity(new Gson().toJson(Digester.encrypt("{\"message\": \"" + message + "\"}"))).build();
        return Response.status(status).entity(new Gson().toJson("{\"message\": \"" + message + "\"}")).build();
    }

    /**
     * Metode kaldt i alle endpoints for at sende en status og et objekt af den data der skal bruges i de enkelte endpoints.
     * @param status
     * @param data
     * @return
     */
    protected Response successResponse(int status, Object data) {
        Gson gson = new Gson();

        //return Response.status(status).entity(Digester.encrypt(gson.toJson(data))).build();
        return Response.status(status).entity(gson.toJson(data)).header("Access-Control-Allow-Origin", "*").build();
    }
}
