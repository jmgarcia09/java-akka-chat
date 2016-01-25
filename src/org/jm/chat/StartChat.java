package org.jm.chat;

import org.jm.client.ChatClient;
import org.jm.server.ChatServer;

public class StartChat {
	
	/**
	 * Start Chat Server
	 * @param args
	 * <p>1 = Mode (Server | Client).</p> 
	 * <p>2 = Host to start/connect to server.</p>
	 * <p>3 = Port to start server.</p>
	 * <p>4 = Extra config needed for server.</p>
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
				
				
				ChatClient client = new ChatClient(host, port,"clientActor");
				client.loadClient();
				
				ClientChatTest test = new ClientChatTest(client);
				
				test.login();
				
				test.sendMessage();
				
				
			}
		}
		
		
		
		
		
		
	}

}
