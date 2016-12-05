package shared;

/**
 * StudentDTO klassen der opretter getter og setters for alle de variable der skal bruges for en user.
 * Hele klassen er hentet fra vores fælles server: https://github.com/emilstepanian/Undervisningsevaluering
 */

public class StudentDTO extends UserDTO {

	private static final long serialVersionUID = 1L;
	
	private String study;
	
	public StudentDTO() {
		super();
	}

	public StudentDTO(int id, String mail, String password, String type, String study) {
		super(id, mail, password, type);
		this.study = study;
	}

	public String getStudy() {
		return study;
	}

	public void setStudy(String study) {
		this.study = study;
	}
	
	@Override
	public String toString() {
		return "StudentDTO [getId()=" + getId() + ", getCbsMail()=" + getCbsMail() + ", getPassword()=" + getPassword()
				+ ", getType()=" + getType() + "]" + ", getStudy()=" + getStudy() + "]";
	}
}