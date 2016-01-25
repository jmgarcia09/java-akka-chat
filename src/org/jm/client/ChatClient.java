package org.jm.client;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.jm.actors.client.ClientActor;
import org.jm.actors.messages.ChatMessage;
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
		
		boolean isConnected = false;
		
		String hostname = null;
		try {
			hostname = InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			
		}
		
		Config serverConfig = ConfigFactory.parseString("akka.remote.netty.tcp.port = 0");
		serverConfig = serverConfig.withFallback(ConfigFactory.parseString("akka.remote.netty.tcp.hostname = " + hostname));
		scannerEntry = new Scanner(System.in);
		
		
		clientActorSystem = ActorSystem.create("clientActorSystem",serverConfig.withFallback(ConfigFactory.load("common")));
		ActorRef clientActor = clientActorSystem.actorOf(Props.create(ClientActor.class,
				host,port,userName, isConnected), "clientActor");
		
		Inbox inbox = Inbox.create(clientActorSystem);
		
		
		while(!isConnected){
			System.out.println("Chat user name: ");
			userName = scannerEntry.next();
			
			inbox.send(clientActor, User.createUser(userName));
			try {
				isConnected = (boolean) inbox.receive(Duration.create(2, TimeUnit.SECONDS));
				if(!isConnected){
					Thread.sleep(5000);
				}
				
			} catch (InterruptedException | TimeoutException e) {
				System.err.println(e);
			}
		}
		
		boolean continueSend = true;
		String message = "";
		ChatMessage clientMessage = ChatMessage.createMessageSender(userName);
		clientMessage.setToServer(true);
		
		System.out.println("Start sending messages: ");
		while(continueSend){
			
			message = scannerEntry.next();			
			clientMessage.setMessage(message);
			clientActor.tell(clientMessage, ActorRef.noSender());
		}
		
	}
	
}
