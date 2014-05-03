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
	 * Felhaszn�l� authentik�l�sa
	 * 
	 * @param username
	 *            felhaszn�l� neve
	 * @param passwordMd5Hash
	 *            jelsz�
	 * @return felhaszn�l�
	 */
	public User authenticate(String username, String passwordMd5Hash) {
		User user = em.find(User.class, username);
		if (user == null)
			return null;

		// ha a k�t hash egyezik akkor sikeres.
		if (user.getPassword().equals(passwordMd5Hash)) {
			return user;
		} else {
			return null;
		}
	}

	/**
	 * T�mogatott wellnessszolg�ltat�sok �s azok adatainak lek�rdez�se
	 * 
	 * @return
	 */
	public List<Service> getServices() {
		return em.createNamedQuery(Service.FIND_ALL, Service.class)
				.getResultList();
	}

	/**
	 * H�tral�v� t�mogat�si keret�sszeg lek�rdez�se
	 * 
	 * @param username
	 *            felhaszn�l�
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
	 * Jelentkez�s egy adott szolg�ltat�sra.
	 * 
	 * @param username
	 *            jelentkezni k�v�n� felhaszn�l�
	 * @param serviceId
	 *            szolg�ltat�s
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
	 * Felhaszn�l� friss�t�se az adatb�zisban
	 * 
	 * @param user
	 *            friss�tend� felhaszn�l�
	 * 
	 * @return sikeress�g
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
	 * Jelentkez�sek lek�rdez�se
	 * 
	 * @param username
	 *            felhaszn�l�
	 * @return jelentkez�sek
	 */
	public List<Application> getApplications(String username) {
		User u = em.find(User.class, username);
		if (u == null)
			return null;

		return u.getApplications();
	}

	/**
	 * Szolg�ltat�s lek�rdez�se
	 * 
	 * @param serviceId
	 *            szolg�ltat�s azonos�t�ja
	 * @return szolg�ltat�s
	 */
	public Service getServiceById(long serviceId) {
		return em.find(Service.class, serviceId);
	}
}
