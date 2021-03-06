package logic;

import service.DBWrapper;
import shared.ReviewDTO;
import shared.StudentDTO;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;


/**
 * hentet fra vores fælles server: https://github.com/emilstepanian/Undervisningsevaluering
 * StudentControlleren bruges for metoder som specifikt kun den studerende kan.
 * Derudover har studerende adgang til UserController gennem nedarvning.
 */
public class StudentController extends UserController {

    private StudentDTO currentStudent;

    public StudentController() {
        super();
    }

    /*
    public static void main(String[] args) {

        StudentController controller = new StudentController();
        controller.addReview(new ReviewDTO(1, 1, 1, "1", true));
    }
*/

    /**
     * hentet fra vores fælles server: https://github.com/emilstepanian/Undervisningsevaluering
     * Metode der initiere currentstudent
     * @param currentStudent
     */
    public void loadStudent(StudentDTO currentStudent) {
        this.currentStudent = currentStudent;
    }


    /**
     * hentet fra vores fælles server: https://github.com/emilstepanian/Undervisningsevaluering
     * Metoden der sætter parametre for SQL statementet til at tilføje et review.
     * @param review DTO'et overfører værdierne til metoden, som er hentet fra klienten.
     * @return
     */
    //Metode til at indsætte et review i databasen
    public boolean addReview(ReviewDTO review) {
        boolean isAdded = true;

        try {
            Map<String, String> values = new HashMap();

            values.put("user_id", String.valueOf(review.getUserId()));
            values.put("lecture_id", String.valueOf(review.getLectureId()));
            values.put("rating", String.valueOf(review.getRating()));
            values.put("comment", review.getComment());
            values.put("is_deleted", "0");

            DBWrapper.insertIntoRecords("review", values);
            return isAdded;

        } catch (SQLException e) {
            e.printStackTrace();
            isAdded = false;
        }
        return isAdded;
    }

}