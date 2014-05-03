package hu.magic.wesz.ws;

import hu.magic.wesz.ejb.WellnessManager;
import hu.magic.wesz.misc.SessionHolder;
import hu.magic.wesz.model.Application;
import hu.magic.wesz.model.Service;
import hu.magic.wesz.model.User;

import java.util.List;

import javax.ejb.EJB;
import javax.jws.WebMethod;
import javax.jws.WebService;

@WebService
public class Wellness {

	@EJB
	private WellnessManager wm;

	@EJB
	private SessionHolder sh;

	/**
	 * Felhasználó authentikálása
	 * 
	 * @param username
	 *            felhasználó neve
	 * @param passwordMd5Hash
	 *            jelszó
	 * @return token
	 */
	@WebMethod
	public String authenticate(String username, String passwordMd5Hash) {
		User user = wm.authenticate(username, passwordMd5Hash);
		if (user != null) {
			return sh.getToken(user.getUsername());
		} else {
			return null;
		}
	}

	/**
	 * Jelszó változtatás
	 * 
	 * @param username
	 *            felhasználó neve
	 * @param oldPasswordHash
	 *            régi jelszó
	 * @param newPasswordHash
	 *            új jelszó
	 * @return sikeresség
	 */
	@WebMethod
	public boolean changePassword(String username, String oldPasswordHash,
			String newPasswordHash) {
		User user = wm.authenticate(username, oldPasswordHash);

		if (user == null)
			return false;

		user.setPassword(newPasswordHash);
		return wm.update(user);
	}

	/**
	 * Támogatott wellnessszolgáltatások és azok adatainak lekérdezése
	 * 
	 * @return szolgáltatások listája
	 */
	@WebMethod
	public List<Service> getServices() {
		return wm.getServices();
	}

	/**
	 * Konkrét szolgáltatás lekérdezése ID alapján
	 * 
	 * @param serviceId
	 *            szolgáltatás azonosító
	 * @return szolgáltatás
	 */
	@WebMethod
	public Service getServiceById(long serviceId) {
		return wm.getServiceById(serviceId);
	}

	/**
	 * Felhasználó jelentkezéseinek lekérdezése
	 * 
	 * @param token
	 *            belépett felhasználó tokenje
	 * @return jelentkezés lista
	 */
	@WebMethod
	public List<Application> getApplications(String token) {
		String username = sh.getUsernameByToken(token);
		if (username != null) {
			return wm.getApplications(username);
		} else {
			return null;
		}
	}

	/**
	 * Hátralévõ támogatási keretösszeg lekérdezése
	 * 
	 * @param token
	 *            belépett felhasználó tokenje
	 * @return hátralévõ keretösszeg
	 */
	@WebMethod
	public long getRemainingSum(String token) {
		String username = sh.getUsernameByToken(token);
		if (username != null) {
			return wm.getRemainingSum(username);
		} else {
			return -1L;
		}
	}

	/**
	 * Jelentkezés egy adott szolgáltatásra.
	 * 
	 * @param token
	 *            jelentkezõ felhasználó tokenje
	 * @param service
	 *            szolgáltatás
	 * @return sikeres-e vagy sem
	 */
	@WebMethod
	public Boolean apply(String token, long serviceId) {
		String username = sh.getUsernameByToken(token);
		if (username != null) {
			return wm.apply(username, serviceId);
		} else {
			return null;
		}
	}
}
