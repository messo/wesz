package hu.magic.wesz.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

@Entity
@NamedQueries({ @NamedQuery(name = Application.GET_TOTAL, query = "SELECT SUM(s.cost) FROM Application a LEFT JOIN a.service s WHERE a.user.username = :username") })
public class Application {

	public static final String GET_TOTAL = "Application.getTotal";
	
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Id
	private long id;
	@ManyToOne
	private User user;
	@ManyToOne
	private Service service;

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Service getService() {
		return service;
	}

	public void setService(Service service) {
		this.service = service;
	}
}
