package org.jm.actors.server;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.jm.actor.messages.ChatMessage;
import org.jm.actor.messages.Login;
import org.jm.actor.messages.User;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;

public class ServerActor extends UntypedActor {
	
	private Map<String, ActorRef> userList;
	private final String host;
	private final int port;
	
	public ServerActor(String host, int port) {
		this.host = host;
		this.port = port;
	
	}

	public void onReceive(Object message) throws Exception {
		if(message instanceof User){
			User user = (User) message;
			Login login = null;
			if(userList.containsKey(user.getUserName())){
				login = new Login(false);
				login.setError("User already exist. Use other name.");
			}else{
				login = new Login(true);
				userList.put(user.getUserName(), getSender());
			}
			
			System.out.println("User connected to chat: " + user.getUserName());
			getSender().tell(login, getSelf());
			
		}else if (message instanceof ChatMessage) {
			ChatMessage chatMessage = (ChatMessage) message;
			System.out.println("Entry message to server from [" +
					chatMessage.getUser()  + "] Message: [" +
					chatMessage.getMessage()+"]");
			
			chatMessage.setToServer(false);
			
			for(String user: userList.keySet()){
				if(!user.equals(chatMessage.getUser())){
					userList.get(user).tell(chatMessage, getSelf());
				}
			}
		}
		
		
	}
	
	@Override
	public void aroundPreStart() {
		super.aroundPreStart();
		userList = new ConcurrentHashMap<String, ActorRef>();
		System.out.println("=====Actor Server started=====");
		System.out.println("Listening on port: " + getHost() + ":" + getPort());
	}

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

}
