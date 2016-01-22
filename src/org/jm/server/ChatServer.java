package org.jm.server;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.jm.actors.server.ServerActor;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import akka.actor.ActorSystem;
import akka.actor.Props;

public class ChatServer {
	
	private final ActorSystem actorSystem;
	
	private int port;
	private String extraConfig;
	
	public ChatServer(String host, int port , String extraConfig) {
		this.port = port;
		this.extraConfig = extraConfig;
		
		
		if(host.isEmpty()){
			try {
				host = InetAddress.getLocalHost().getHostName();
			} catch (UnknownHostException e) {
				
			}
		}
		
		Config serverConfig = ConfigFactory.parseString("akka.remote.netty.tcp.port =" + port);
		serverConfig = serverConfig.withFallback(ConfigFactory.parseString("akka.remote.netty.tcp.hostname = " + host));
		
		if(!(extraConfig == null || extraConfig.isEmpty())){
			serverConfig = serverConfig.withFallback(ConfigFactory.parseString(extraConfig));
		}
		
		
		serverConfig = serverConfig.withFallback(ConfigFactory.load("common"));
		
		actorSystem = ActorSystem.create("ChatServerSystem", serverConfig);
		
		actorSystem.actorOf(
				Props.create(ServerActor.class,
						host,
						port), "serverActor");
		
	}
	
	public String getExtraConfig() {
		return extraConfig;
	}
	
	public int getPort() {
		return port;
	}
	
	public void setExtraConfig(String extraConfig) {
		this.extraConfig = extraConfig;
	}
	
	public void setPort(int port) {
		this.port = port;
	}

	public ActorSystem getActorSystem() {
		return actorSystem;
	}

}
