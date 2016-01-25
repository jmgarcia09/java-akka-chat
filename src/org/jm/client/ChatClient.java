package org.jm.client;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.jm.actors.client.ClientActor;
import org.jm.actors.messages.ChatMessage;
import org.jm.actors.messages.Login;
import org.jm.actors.messages.User;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Inbox;
import akka.actor.Props;
import scala.concurrent.duration.Duration;

/**
 * Console Chat client
 * @author jmgarcia
 *
 */
public class ChatClient {
	
	private static ActorSystem clientActorSystem;
	private ActorRef clientActor;
	private ChatMessage chatMessage;
	private Inbox clientInbox;
	private String actorName;
	private String userName;
	private String host;
	private int port;
	
	
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	
	public ChatClient(String host, int port, String actorName) {
		this.host = host;
		this.port = port;
		this.actorName = actorName;
	
	}
	
	public void loadClient(){
		
		if(clientActorSystem != null){
			return;
		}
		
		String hostname = null;
		try {
			hostname = InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			
		}
		
		Config clientConfig = ConfigFactory.parseString("akka.remote.netty.tcp.port = 0");
		clientConfig = clientConfig.withFallback(ConfigFactory.parseString("akka.remote.netty.tcp.hostname = " + hostname));
		
		
		clientActorSystem = ActorSystem.create("clientActorSystem",clientConfig.withFallback(ConfigFactory.load("common")));
		
		setClientInbox(Inbox.create(clientActorSystem));
	}
	
	
	public Login connectClient(String userName){
		
		Login login = null;
		
		if(clientActor == null){
			clientActor = clientActorSystem.actorOf(Props.create(ClientActor.class,
					host,port), actorName);
		}
		
		//Send user to log server.
		clientInbox.send(clientActor, User.createUser(userName));
		
		
		//get login response from server.
		try {
			login = (Login) clientInbox.receive(Duration.create(15, TimeUnit.SECONDS));
			
			if(login.isLogged()){
				chatMessage = ChatMessage.createMessageSender(userName);
				chatMessage.setToServer(true);
			}
		} catch (TimeoutException | ClassCastException e ) {
			System.out.println(e);
			login = new Login(false);
			login.setError("Connection to server timeout. Try again later.");
			
		}
		 
		return login;
	}
	
	
	public void sendMessage(String message){
		
		chatMessage.setMessage(message);
		
		
		clientActor.tell(chatMessage, ActorRef.noSender());
		
	}
	
	public Inbox getClientInbox() {
		return clientInbox;
	}
	public void setClientInbox(Inbox clientInbox) {
		this.clientInbox = clientInbox;
	}
	public String getActorName() {
		return actorName;
	}
	public void setActorName(String actorName) {
		this.actorName = actorName;
	}
	
	public ActorSystem getClientActorSystem(){
		return clientActorSystem;
	}
}
