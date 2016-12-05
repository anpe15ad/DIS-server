package logic;

import jdk.nashorn.internal.objects.annotations.Where;
import shared.LectureDTO;
import shared.Logging;
import shared.ReviewDTO;
import shared.StudyDTO;

import java.sql.Timestamp;
import java.util.ArrayList;

import service.DBWrapper;
import shared.CourseDTO;
import shared.UserDTO;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * hentet fra vores fælles server: https://github.com/emilstepanian/Undervisningsevaluering
 * Denne klasse har alle metoderne for brugerne.
 */
public class UserController {

    public static void main(String[] args) {
        UserController controller = new UserController();
        controller.getCourses(1);
    }

    public UserController() {
    }

    /**
     * Login metoden
     * @param cbs_email det login credential metoden tager.
     * @param password det loging password metoden tager.
     * @return en UserDTO af brugeren.
     */
    public UserDTO login(String cbs_email, String password) {

        UserDTO user = new UserDTO();

        try {
            Map<String, String> params = new HashMap();
            params.put("cbs_mail", String.valueOf(cbs_email));
            params.put("password", String.valueOf(password));

            String[] attributes = {"id","type"};
            ResultSet rs = DBWrapper.getRecords("user", attributes, params, null, 0);

            while (rs.next()) {
                user.setId(rs.getInt("id"));
                user.setType(rs.getString("type"));
                System.out.println("User found");
                return user;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        System.out.println("User not found");
        return null;
    }

    /**
     * henter studier for et shortname f.eks. BINTO1056E16
     * @param shortname det parameter metoden tager.
     * @return returnere en arraylist af studier
     */
    public ArrayList<StudyDTO> getStudies(String shortname) {

        ArrayList<StudyDTO> studies = new ArrayList<StudyDTO>();

        try {
            Map<String, String> params = new HashMap();

            params.put("shortname", String.valueOf(shortname));

            String[] attributes = new String[]{"name", "shortname", "id"};
            ResultSet rs = DBWrapper.getRecords("study", attributes, params, null, 0);

            while (rs.next()) {
                StudyDTO study = new StudyDTO();

                study.setName(rs.getString("name"));
                study.setShortname(rs.getString("shortname"));
                study.setId(rs.getInt("id"));
                studies.add(study);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Logging.log(e,2,"Kunne ikke hente Study");
        }
        return studies;
    }

    /**
     * hentet fra vores fælles server: https://github.com/emilstepanian/Undervisningsevaluering
     * Henter alle reviews
     * @param lectureId ud fra det lecture id brugeren oplyser.
     * @return returnere en arrayliste af reviews.
     */
    public ArrayList<ReviewDTO> getReviews(int lectureId) {

        ArrayList<ReviewDTO> reviews = new ArrayList<ReviewDTO>();

        try {
            Map<String, String> params = new HashMap();
            params.put("lecture_id", String.valueOf(lectureId));
            params.put("is_deleted", "0");
            String[] attributes = {"id", "user_id", "lecture_id", "rating", "comment"};

            ResultSet rs = DBWrapper.getRecords("review", attributes, params, null, 0);

            while (rs.next()) {
                ReviewDTO review = new ReviewDTO();
                review.setId(rs.getInt("id"));
                review.setUserId(rs.getInt("user_id"));
                review.setLectureId(rs.getInt("lecture_id"));
                review.setRating(rs.getInt("rating"));
                review.setComment(rs.getString("comment"));

                reviews.add(review);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            Logging.log(e,2,"Kunne ikke hente getReviews");
        }
        return reviews;
    }


    /**
     * hentet fra vores fælles server: https://github.com/emilstepanian/Undervisningsevaluering
     * Henter reviews som en bruger har oprettet.
     * @param userId tager userId for den bruger der har oprettet reviews
     * @return returnere en arraylist af reviews.
     */
    public ArrayList<ReviewDTO> getReviewsUser(int userId) {

        ArrayList<ReviewDTO> reviews = new ArrayList<ReviewDTO>();

        try {
            Map<String, String> params = new HashMap();
            params.put("user_id", String.valueOf(userId));
            params.put("is_deleted", "0");
            String[] attributes = {"id", "user_id", "lecture_id", "rating", "comment"};

            ResultSet rs = DBWrapper.getRecords("review", attributes, params, null, 0);

            while (rs.next()) {
                ReviewDTO review = new ReviewDTO();
                review.setId(rs.getInt("id"));
                review.setUserId(rs.getInt("user_id"));
                review.setLectureId(rs.getInt("lecture_id"));
                review.setRating(rs.getInt("rating"));
                review.setComment(rs.getString("comment"));

                reviews.add(review);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            Logging.log(e,2,"Kunne ikke hente getReviews");
        }
        return reviews;
    }

    /**
     * hentet fra vores fælles server: https://github.com/emilstepanian/Undervisningsevaluering
     * Henter lectures
     * @param code ud fra en code som bruger har oplyst
     * @return returnere en Arraylist af lectures.
     */
    public ArrayList<LectureDTO> getLectures(String code) {

        ArrayList<LectureDTO> lectures = new ArrayList<LectureDTO>();

        try {
            Map<String, String> params = new HashMap();

            params.put("course_id", code);

            ResultSet rs = DBWrapper.getRecords("lecture", null, params, null, 0);

            while (rs.next()) {
                LectureDTO lecture = new LectureDTO();

                lecture.setStartDate(rs.getTimestamp("start"));
                lecture.setEndDate(rs.getTimestamp("end"));
                lecture.setId(rs.getInt("id"));
                lecture.setType(rs.getString("type"));
                lecture.setDescription(rs.getString("description"));


                lectures.add(lecture);
            }


        }
        catch (SQLException e){
            e.printStackTrace();
        Logging.log(e,2,"Kunne ikke hente getLecture");

        }
        return lectures;
    }


    //Metode der softdeleter et review fra databasen - skal ind i AdminControlleren, da dette er moden for at slette et review uafhængigt af brugertype.

    /**
     * sletter et review ved at sætte isDeleted = 1
     * @param userId tager det userId som brugeren er logget ind som
     * @param reviewId tager det review id der skal slettes
     * @return returnere en boolean om den er slettet.
     */
    public boolean softDeleteReview(int userId, int reviewId) {
        boolean isSoftDeleted = true;

        try {
            Map<String, String> isDeleted = new HashMap();

            isDeleted.put("is_deleted", "1");

            Map<String, String> whereParams = new HashMap();

            if(userId != 0) {
                whereParams.put("user_id", String.valueOf(userId));
                whereParams.put("id", String.valueOf(reviewId));
            }

            whereParams.put("id", String.valueOf(reviewId));

            DBWrapper.updateRecords("review", isDeleted, whereParams);
            return isSoftDeleted;

        } catch (SQLException e) {
            e.printStackTrace();
            Logging.log(e,2,"Softdelete kunne ikke slette review, SoftDeleteReview.");
            isSoftDeleted = false;
        }
        return isSoftDeleted;
    }

    /**
     * hentet fra vores fælles server: https://github.com/emilstepanian/Undervisningsevaluering
     * Henter kurser
     * @param userId ud fra det user id som brugeren har.
     * @return returnere en arraylist af kurser.
     */
    public ArrayList<CourseDTO> getCourses(int userId) {

        ArrayList<CourseDTO> courses = new ArrayList<CourseDTO>();

        try {
            Map<String, String> params = new HashMap();
            Map<String, String> joins = new HashMap();

            params.put("course_attendant.user_id", String.valueOf(userId));
            joins.put("course_attendant", "course_id");

            String[] attributes = new String[]{"name", "code", "course.id"};
            ResultSet rs = DBWrapper.getRecords("course", attributes, params, joins, 0);

            while (rs.next()) {
                CourseDTO course = new CourseDTO();

                course.setDisplaytext(rs.getString("name"));
                course.setCode(rs.getString("code"));
                course.setId(rs.getString("id"));
                courses.add(course);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Logging.log(e,2,"Kunne ikke hente getCourses");
        }
        return courses;
    }

}