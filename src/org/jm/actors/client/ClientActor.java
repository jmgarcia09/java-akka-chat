package org.jm.actors.client;


import org.jm.actors.messages.ChatMessage;
import org.jm.actors.messages.Login;
import org.jm.actors.messages.User;

import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.UntypedActor;

/**
 * Actor to communicate client to server.
 * @author jmgarcia
 *
 */
public class ClientActor extends UntypedActor{

	/**
	 * <p>Pattern to connect to server actor. need actor server name, host and port to server chat.</p>
	 * Example: 
	 * <p>Server actor name : ChatServerSystem</p>
	 * <p>Host				: 127.0.0.1</p>
	 * <p>Port				: 1523</p>
	 */
	private static String REMOTE_PATTERN = "akka.tcp://%s@%s:%d/user/serverActor";
	
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
	 * 
	 */
	private ActorRef inboxActor;
	
	private String userName;
	
	/**
	 * Constructor of ClientActor.
	 * @param actorServerName
	 * @param host
	 * @param port
	 * @throws ExceptionInInitializerError - If host is empty.
	 */
	public ClientActor(String host, int port) throws ExceptionInInitializerError {
		if(host == null || host.isEmpty()){
			throw new ExceptionInInitializerError("Cannot start chat to unknown server.");
		}
		
		serverRemotePath = String.format(REMOTE_PATTERN,
				getContext().system().settings().config().getString("chat.server.actor.name"),
				host,port);
		server = getContext().actorSelection(serverRemotePath);
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
			
			inboxActor.tell(loginResponse, ActorRef.noSender());
		}else if(message instanceof User){
			userName = ((User) message).getUserName();
			server.tell(message, getSelf());
			inboxActor = getSender();
		}else if (message instanceof ChatMessage){
			ChatMessage chatMessage = (ChatMessage) message;
			
			if(chatMessage.getMessage().startsWith("/disconnect")){
				getContext().stop(getSelf());
				return;
			}
			
			inboxActor.tell(message, ActorRef.noSender());
			
			if(chatMessage.isToServer()){
				server.tell(message, getSelf());
			}
			
		}else{
			System.out.println("Se recibio mensaje del tipo " + message.toString());
		}
		
		
		
	}
	
	@Override
	public void aroundPostStop() {
		super.aroundPostStop();
		ChatMessage message = ChatMessage.createMessageSender(userName);
		message.setMessage("/disconnect");
		server.tell(message, getSelf());
		
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
