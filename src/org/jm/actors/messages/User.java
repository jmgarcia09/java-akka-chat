package org.jm.actors.messages;

import java.io.Serializable;

/**
 * Message that the client send to the server when tries to establish connection.
 * @author jmgarcia
 *
 */
public class User implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * Client user name.
	 */
	private final String userName;
	
	private User(String userName) {
		this.userName = userName;
	}
	
	public String getUserName() {
		return userName;
	}
	
	/**
	 * Creator of User Object message. 
	 * @param userName
	 * @return an instance of {@code User}.
	 * @throws ExceptionInInitializerError - If {@code userName} is empty.
	 */
	public static User createUser(String userName) throws ExceptionInInitializerError{
		if(!(userName == null || userName.isEmpty()))
			return new User(userName);
		else
			throw new ExceptionInInitializerError("Cannot create a empty user name. Please specified a name.");
	}

	
}