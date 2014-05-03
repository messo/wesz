package hu.magic.wesz.misc;

import java.util.Date;

public class Session {

	private final Date createdAt;
	private final String username;

	public Session(String username) {
		createdAt = new Date();
		this.username = username;
	}
	
	public Date getCreatedAt() {
		return createdAt;
	}
	
	public String getUsername() {
		return username;
	}
}
