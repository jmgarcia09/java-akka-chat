package org.jm.actors.messages;

import java.io.Serializable;

/**
 * Message object that is used by the client and server.
 * @author jmgarcia
 *
 */
public class ChatMessage implements Serializable{
	
	private static final long serialVersionUID = 1L;

	/**
	 * Client user name.
	 */
	private final String user;
	
	/**
	 * Message information
	 */
	private String message;
	
	/**
	 * Indicates if the message is going to the server.
	 */
	private boolean toServer;
	
	
	private ChatMessage(String user) {
		this.user = user;
	}
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getUser() {
		return user;
	}

	public static ChatMessage createMessageSender(String user){
		return new ChatMessage(user); 
	}
	public boolean isToServer() {
		return toServer;
	}
	public void setToServer(boolean toServer) {
		this.toServer = toServer;
	}
}
