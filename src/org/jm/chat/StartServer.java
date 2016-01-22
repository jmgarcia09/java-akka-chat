package org.jm.chat;

import org.jm.client.ChatClient;
import org.jm.server.ChatServer;

public class StartServer {
	
	/**
	 * Start Chat Server
	 * @param args - 1 = Mode (Server | Client). 2 = Host to start server 3 = Port to start server. 4 = Extra config needed for server.
	 */
	public static void main(String[] args) {
		
		String mode = null;
		if(args.length > 0){
			mode = args[0];
			
			if("server".equalsIgnoreCase(mode)){
				int port = 0;
				String extraConfig = "";
				String host = "";
				
				if(args.length >= 2)
					host = args[1];
				
				if(args.length >= 3)
					port = Integer.parseInt(args[2]);
				
				if(args.length >= 4)
					extraConfig = args[3];
				
				new ChatServer(host,port, extraConfig);
				
			}else if("client".equalsIgnoreCase(mode)){
				
				int port = 0;
				String host = "";
				
				if(args.length >= 2)
					host = args[1];
				
				if(args.length >= 3)
					port = Integer.parseInt(args[2]);
				
				
				ChatClient client = new ChatClient(host, port);
				client.startClient();
				
				
			}
		}
		
		
		
		
		
		
	}

}