package org.jm.client;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Scanner;

import org.jm.actor.messages.ChatMessage;
import org.jm.actors.client.ClientActor;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;

public class ChatClient {
	
	private ActorSystem clientActorSystem;
	private Scanner scannerEntry;
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
	
	public ChatClient(String host, int port) {
		this.host = host;
		this.port = port;
	
	}
	
	public void startClient(){
		
		String hostname = null;
		try {
			hostname = InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			
		}
		
		Config serverConfig = ConfigFactory.parseString("akka.remote.netty.tcp.port = 0");
		serverConfig = serverConfig.withFallback(ConfigFactory.parseString("akka.remote.netty.tcp.hostname = " + hostname));
		scannerEntry = new Scanner(System.in);
		System.out.println("Chat user name: ");
		userName = scannerEntry.next();
		clientActorSystem = ActorSystem.create("clientActorSystem",serverConfig.withFallback(ConfigFactory.load("common")));
		ActorRef clientActor = clientActorSystem.actorOf(Props.create(ClientActor.class,
				host,port,userName), "clientActor");
		
		boolean continueSend = true;
		String message = "";
		ChatMessage clientMessage = ChatMessage.createMessageSender(userName);
		clientMessage.setToServer(true);
		while(continueSend){
			
			System.out.print("Message: ");
			message = scannerEntry.next();			
			clientMessage.setMessage(message);
			clientActor.tell(clientMessage, ActorRef.noSender());
			System.out.println("----------------------------------");
		}
		
	}
	
}
