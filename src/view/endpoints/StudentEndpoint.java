package view.endpoints;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import logic.StudentController;
import logic.UserController;
import security.Digester;
import shared.ReviewDTO;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.ArrayList;

/**
 * Created by Kasper on 19/10/2016.
 */

@Path("/api/student")
public class StudentEndpoint extends UserEndpoint {

    /**
     * hentet fra vores fælles server: https://github.com/emilstepanian/Undervisningsevaluering
     * Dette endpoint returnere reviews i et ArrayList for den bruger userId er.
     * @param userId tager userId for den bruger, der er logget ind.
     * @return returnere Arraylist af reviews.
     */
    @GET
    @Consumes("applications/json")
    @Path("/review/{userId}")
    public Response getReviews(@PathParam("userId") String userId) {

        String decrypted = Digester.decrypt(userId);

        int userDecrypted = Integer.valueOf(decrypted);
        System.out.println(userId + " fandt endpoint review/userid");

        UserController userCtrl = new UserController();
        ArrayList<ReviewDTO> reviews = userCtrl.getReviewsUser(userDecrypted);

        if (!reviews.isEmpty()) {
            System.out.println("Hentede review for user specifik id");
            return successResponse(200, reviews);

        } else {
            return errorResponse(404, "Failed. Couldn't get reviews.");
        }
    }

    /**
     * hentet fra vores fælles server: https://github.com/emilstepanian/Undervisningsevaluering
     * Dette endpoint modtager oplysninger om at
     * @param json den parameter om det review der skal oprettes.
     * @return returnere en String af en boolean om det er slettet
     */
    @POST
    @Consumes("application/json")
    @Path("/review")
    public Response addReview(String json) {

        Gson gson = new Gson();
        ReviewDTO review = new Gson().fromJson(Digester.decrypt(json), ReviewDTO.class);

        StudentController studentCtrl = new StudentController();
        boolean isAdded = studentCtrl.addReview(review);

        if (isAdded ) {
           // String toJson = gson.toJson(Digester.encrypt(gson.toJson(isAdded)));

            String toJson = gson.toJson((gson.toJson(isAdded)));
            System.out.println("Tilføjede et review og sendte en boolean til klient");

            return successResponse(200, toJson);

        } else {
            System.out.println("gik i Error ved addreview");
            return errorResponse(404, "Failed. Couldn't get reviews.");

        }
    }

    /**
     * Dette endpoint er hvor server modtager request om at slette et review.
     * @param reviewId identifikator for hvilket review der skal slettes
     * @param userId sikre at det er den samme bruger der har oprettet som ønkser at slette reviewet.
     * @return returnere en string af en boolean om det er slettet.
     */
    @DELETE
    @Consumes("application/json")
    @Path("/review/{reviewId}/{userId}")
    public Response deleteReview(@PathParam("reviewId") String reviewId, @PathParam("userId") String userId) {
        Gson gson = new Gson();
        /**
         * Disse 2 string navne giver ikke mening de skal kun være her for at dekryptere det modtagne data.
         */
        String rdid = Digester.decrypt(reviewId);
        String kef = Digester.decrypt(userId);

        int reviewdecrypted = Integer.valueOf(rdid);
        int userDecrypted = Integer.valueOf(kef);


      //  ReviewDTO review = gson.fromJson(data, ReviewDTO.class);
        StudentController studentCtrl = new StudentController();

        boolean isDeleted = studentCtrl.softDeleteReview(userDecrypted,reviewdecrypted);

        if (isDeleted) {
           // String toJson = gson.toJson(Digester.encrypt(gson.toJson(isDeleted)));
            String toJson = gson.toJson(gson.toJson(isDeleted));

            return successResponse(200, toJson);
        } else {
            return errorResponse(404, "Failed. Couldn't delete the chosen review.");
        }
    }
}
