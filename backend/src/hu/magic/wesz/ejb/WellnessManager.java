package hu.magic.wesz.ejb;

import hu.magic.wesz.model.Application;
import hu.magic.wesz.model.Service;
import hu.magic.wesz.model.User;

import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@LocalBean
@Stateless
public class WellnessManager {

	@PersistenceContext
	private EntityManager em;

	/**
	 * Felhasználó authentikálása
	 * 
	 * @param username
	 *            felhasználó neve
	 * @param passwordMd5Hash
	 *            jelszó
	 * @return felhasználó
	 */
	public User authenticate(String username, String passwordMd5Hash) {
		User user = em.find(User.class, username);
		if (user == null)
			return null;

		// ha a két hash egyezik akkor sikeres.
		if (user.getPassword().equals(passwordMd5Hash)) {
			return user;
		} else {
			return null;
		}
	}

	/**
	 * Támogatott wellnessszolgáltatások és azok adatainak lekérdezése
	 * 
	 * @return
	 */
	public List<Service> getServices() {
		return em.createNamedQuery(Service.FIND_ALL, Service.class)
				.getResultList();
	}

	/**
	 * Hátralévõ támogatási keretösszeg lekérdezése
	 * 
	 * @param username
	 *            felhasználó
	 * 
	 * @return
	 */
	public long getRemainingSum(String username) {
		User user = em.find(User.class, username);
		if (user == null)
			return -1L;

		Long spent = em.createNamedQuery(Application.GET_TOTAL, Long.class)
				.setParameter("username", username).getSingleResult();

		if (spent == null) {
			spent = 0L;
		}

		return user.getTotalPoints() - spent;
	}

	/**
	 * Jelentkezés egy adott szolgáltatásra.
	 * 
	 * @param username
	 *            jelentkezni kívánó felhasználó
	 * @param serviceId
	 *            szolgáltatás
	 * @return sikeres-e vagy sem
	 */
	public Boolean apply(String username, long serviceId) {
		User user = em.find(User.class, username);
		Service service = em.find(Service.class, serviceId);

		if (user == null || service == null)
			return null;

		long remaining = getRemainingSum(username);
		if (remaining < service.getCost())
			return false;

		Application app = new Application();
		app.setService(service);
		app.setUser(user);

		em.persist(app);

		user.getApplications().add(app);
		em.merge(user);

		return true;
	}

	/**
	 * Felhasználó frissítése az adatbázisban
	 * 
	 * @param user
	 *            frissítendõ felhasználó
	 * 
	 * @return sikeresség
	 */
	public boolean update(User user) {
		try {
			em.merge(user);
			return true;
		} catch (Exception ex) {
			ex.printStackTrace(System.err);
			return false;
		}
	}

	/**
	 * Jelentkezések lekérdezése
	 * 
	 * @param username
	 *            felhasználó
	 * @return jelentkezések
	 */
	public List<Application> getApplications(String username) {
		User u = em.find(User.class, username);
		if (u == null)
			return null;

		return u.getApplications();
	}

	/**
	 * Szolgáltatás lekérdezése
	 * 
	 * @param serviceId
	 *            szolgáltatás azonosítója
	 * @return szolgáltatás
	 */
	public Service getServiceById(long serviceId) {
		return em.find(Service.class, serviceId);
	}
}
