package org.jm.actors.client;


import org.jm.actor.messages.ChatMessage;
import org.jm.actor.messages.Login;
import org.jm.actor.messages.User;

import akka.actor.ActorSelection;
import akka.actor.UntypedActor;

/**
 * Actor to communicate client to server.
 * @author jmgarcia
 *
 */
public class ClientActor extends UntypedActor{

	/**
	 * Pattern to connect to server actor. need host and port.
	 */
	private static String REMOTE_PATTERN = "akka.tcp://ChatServerSystem@%s:%d/user/serverActor";
	
	/**
	 * Actor Selection to server actor.
	 */
	private final ActorSelection server;
	
	/**
	 * Final remote path that contains host and port.
	 */
	private String serverRemotePath;
	
	/**
	 * Indicates if connection is established with server.
	 */
	private boolean connected;
	
	
	/**
	 * Constructor of ClientActor.
	 * @param host
	 * @param port
	 * @param userName
	 * @throws ExceptionInInitializerError - If host is empty or cannot create a User.
	 */
	public ClientActor(String host, int port,String userName) throws ExceptionInInitializerError {
		if(host == null || host.isEmpty()){
			throw new ExceptionInInitializerError("Cannot start chat to unknown server.");
		}
		serverRemotePath = String.format(REMOTE_PATTERN, host,port);
		server = getContext().actorSelection(serverRemotePath);
		
		server.tell(User.createUser(userName), getSelf());
		setConnected(false);
	}
	
	
	public void onReceive(Object message) throws Exception {
		if(message instanceof Login){
			Login loginResponse = (Login) message;
			
			if(loginResponse.isLogged()){
				System.out.println("Connection Established with server.");
				connected = true;
			}else{
				System.out.println("Error trying to log. ");
				connected = false;
			}
		}else if (message instanceof ChatMessage){
			ChatMessage chatMessage = (ChatMessage) message;
			
			System.out.println(chatMessage.getUser() + "--->" + chatMessage.getMessage());
			
			if(chatMessage.isToServer()){
				server.tell(message, getSelf());
			}
			
		}
		
		
		
	}


	public boolean isConnected() {
		return connected;
	}


	public void setConnected(boolean connected) {
		this.connected = connected;
	}


	public ActorSelection getServer() {
		return server;
	}
	
	

}