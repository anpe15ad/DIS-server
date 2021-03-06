package logic;

import service.DBWrapper;
import shared.LectureDTO;
import shared.ReviewDTO;
import shared.TeacherDTO;

import javax.sql.rowset.CachedRowSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * hentet fra vores fælles server: https://github.com/emilstepanian/Undervisningsevaluering
 * Klassen er udarbejdet til hackathon
 */

public class TeacherController extends UserController {

    private TeacherDTO currentTeacher;

    public TeacherController() {
        super();
    }

    public void loadTeacher(TeacherDTO currentTeacher) {
        this.currentTeacher = currentTeacher;
    }

    public double calculateAverageRatingOnLecture(int lectureId) {
        //DecimalFormat df = new DecimalFormat("#.00");

        getReviews(lectureId);

        int numberOfReviews = getReviews(lectureId).size();
        int sumOfRatings = 0;

        for (ReviewDTO review : getReviews(lectureId)) {
            sumOfRatings = sumOfRatings + review.getRating();
        }

        double average = sumOfRatings / numberOfReviews;

        return average;
    }

    /**
     * Beregner et gns for kursets ratings.
     * @param course er f.eks. den binto kode et fag har fot ha.it
     * @param average er det gennemsnit som metoden returnere
     * @return
     */

    public double calculateAverageRatingOnCourse(String course,double average) {

        int lectureId = 0;
        double sumOfRatings = 0;
        double numberOfReviews = 0;

        // for (LectureDTO lecture : getLectures1(courseId)) {
        for (LectureDTO lecture : getLectures(course)) {
            lectureId = lecture.getId();
        }

        //for (ReviewDTO review : getReviews1(lectureId)) {
        for (ReviewDTO review : getReviews(lectureId)) {
            sumOfRatings = sumOfRatings + review.getRating();
        }

        //numberOfReviews = getReviews1(lectureId).size();
        numberOfReviews = getReviews(lectureId).size();

         average = sumOfRatings / numberOfReviews;
        System.out.println(average);

        return average;
    }

    /**
     * Metode som soft deleter et review, ved at sætte isDeleted kolonnen til 1.
     * I metoden bruges userId og reviewId til at specifere hvilket review der skal slettes.
     * @param
     * @param reviewId
     * @return
     */
    public boolean softDeleteReview(int reviewId) {
        boolean isSoftDeleted = true;

        try {
            Map<String, String> isDeleted = new HashMap();

            isDeleted.put("is_deleted", "1");

            Map<String, String> params = new HashMap();
            params.put("id", String.valueOf(reviewId));
            // params.put("user_id", String.valueOf(userId));

            DBWrapper.updateRecords("review", isDeleted, params);
            return isSoftDeleted;

        } catch (SQLException e) {
            e.printStackTrace();
            isSoftDeleted = false;
        }
        return isSoftDeleted;
    }

    /**
     * Henter antallet brugere der er tilknyttet et courseId
     * @param courseId som bliver fundet deltagelse for.
     * @return
     */
    public int getCourseParticipants(int courseId) {
        //Forbered MySQL statement
        String table = "course_attendant";
        Map<String, String> whereStmt = new HashMap<String, String>();
        whereStmt.put("course_id", String.valueOf(courseId));
        CachedRowSet rs = null;
        int courseAttendants = 0;


        //Query courseattendant og find samtlige instanser af det courseID
        try {
            rs = DBWrapper.getRecords(table, null, whereStmt, null, 0);
            courseAttendants = rs.size();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        System.out.println("kursus deltagere: "+courseAttendants);
        return courseAttendants;

    }
}