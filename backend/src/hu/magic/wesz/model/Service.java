package hu.magic.wesz.model;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.xml.bind.annotation.XmlTransient;

@Entity
@NamedQueries({ @NamedQuery(name = Service.FIND_ALL, query = "SELECT s FROM Service s") })
public class Service {

	public static final String FIND_ALL = "Service.findAll";

	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Id
	private long id;
	private String title;
	private String description;
	private long cost;

	@OneToMany
	private List<Application> applications;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public long getCost() {
		return cost;
	}

	public void setCost(long cost) {
		this.cost = cost;
	}

	@XmlTransient
	public List<Application> getApplications() {
		return applications;
	}

	public void setApplications(List<Application> applications) {
		this.applications = applications;
	}
}
