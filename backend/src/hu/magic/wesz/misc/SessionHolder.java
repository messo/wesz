package hu.magic.wesz.misc;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.ejb.Singleton;
import javax.ejb.Startup;

@Singleton
@Startup
public class SessionHolder {

	private Map<String, Session> usersByToken = new HashMap<>();
	
	public String getToken(String username) {
		String token = UUID.randomUUID().toString();
		usersByToken.put(token, new Session(username));
		return token;
	}
	
	public String getUsernameByToken(String token) {
		Session session = usersByToken.get(token);
		if(session == null) {
			return null;
		}
		
		long diff = new Date().getTime() - session.getCreatedAt().getTime();
		if(diff / (1000 * 60) > 30) {
			usersByToken.remove(token);
			return null;
		}
		
		return session.getUsername();
	}
}
