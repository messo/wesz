package hu.magic.wesz.model;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.xml.bind.annotation.XmlTransient;

@Entity
public class User {

	@Id
	private String username;
	private String password;
	private String email;
	/**
	 * Jelentkezési keretösszeg.
	 */
	private long totalPoints;

	@OneToMany
	private List<Application> applications;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@XmlTransient
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public long getTotalPoints() {
		return totalPoints;
	}

	public void setTotalPoints(long limit) {
		this.totalPoints = limit;
	}

	@XmlTransient
	public List<Application> getApplications() {
		return applications;
	}

	public void setApplications(List<Application> applications) {
		this.applications = applications;
	}
}
