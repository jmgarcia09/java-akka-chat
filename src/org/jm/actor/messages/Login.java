package org.jm.actor.messages;

import java.io.Serializable;

/**
 * Message return after server send response of a User message.
 * @author jmgarcia
 *
 */
public class Login implements Serializable{

	private static final long serialVersionUID = 1L;
	
	/**
	 * Indicate if the user is logged to the server.
	 */
	private final boolean logged;
	
	/**
	 * If {@code logged} is false, contains the cause.
	 */
	private String error;
	
	public Login(boolean logged) {
		this.logged = logged;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public boolean isLogged() {
		return logged;
	}
	
	
}