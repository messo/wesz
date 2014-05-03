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
	 * Felhaszn�l� authentik�l�sa
	 * 
	 * @param username
	 *            felhaszn�l� neve
	 * @param passwordMd5Hash
	 *            jelsz�
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
	 * Jelsz� v�ltoztat�s
	 * 
	 * @param username
	 *            felhaszn�l� neve
	 * @param oldPasswordHash
	 *            r�gi jelsz�
	 * @param newPasswordHash
	 *            �j jelsz�
	 * @return sikeress�g
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
	 * T�mogatott wellnessszolg�ltat�sok �s azok adatainak lek�rdez�se
	 * 
	 * @return szolg�ltat�sok list�ja
	 */
	@WebMethod
	public List<Service> getServices() {
		return wm.getServices();
	}

	/**
	 * Konkr�t szolg�ltat�s lek�rdez�se ID alapj�n
	 * 
	 * @param serviceId
	 *            szolg�ltat�s azonos�t�
	 * @return szolg�ltat�s
	 */
	@WebMethod
	public Service getServiceById(long serviceId) {
		return wm.getServiceById(serviceId);
	}

	/**
	 * Felhaszn�l� jelentkez�seinek lek�rdez�se
	 * 
	 * @param token
	 *            bel�pett felhaszn�l� tokenje
	 * @return jelentkez�s lista
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
	 * H�tral�v� t�mogat�si keret�sszeg lek�rdez�se
	 * 
	 * @param token
	 *            bel�pett felhaszn�l� tokenje
	 * @return h�tral�v� keret�sszeg
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
	 * Jelentkez�s egy adott szolg�ltat�sra.
	 * 
	 * @param token
	 *            jelentkez� felhaszn�l� tokenje
	 * @param service
	 *            szolg�ltat�s
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
