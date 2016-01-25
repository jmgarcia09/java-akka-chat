package org.jm.chat;

import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.jm.actors.messages.ChatMessage;
import org.jm.actors.messages.Login;
import org.jm.client.ChatClient;
import scala.concurrent.duration.Duration;

public class ClientChatTest {

	private ChatClient chatClient;
	private boolean isConnected;
	private Scanner scannerEntry;

	public ClientChatTest(ChatClient chatClient) {
		this.chatClient = chatClient;
		scannerEntry = new Scanner(System.in);
	}

	public void login() {
		isConnected = false;
		String userName = null;
		while (!isConnected) {
			System.out.println("Chat user name: ");
			userName = scannerEntry.nextLine();

			Login login = chatClient.connectClient(userName);

			if (!login.isLogged()) {
				System.out.println(login.getError());
			}

			isConnected = login.isLogged();
		}

		chatClient.getClientActorSystem().scheduler().schedule(Duration.create(0, TimeUnit.SECONDS),
				Duration.create(1, TimeUnit.SECONDS), new Runnable() {

					@Override
					public void run() {
						try {
							ChatMessage message = (ChatMessage) chatClient.getClientInbox()
									.receive(Duration.create(15, TimeUnit.SECONDS));

							if(message.getMessage().startsWith("/disconnect")){
								System.out.println("Leaving chat. Goodbye :).");
								System.exit(0);
							}
							System.out.println("[" + message.getUser() + "] : " + message.getMessage());
						} catch (TimeoutException e) {}

					}
				}, chatClient.getClientActorSystem().dispatcher());

	}

	public void sendMessage() {

		while (isConnected) {

			String message = scannerEntry.nextLine();
			chatClient.sendMessage(message);

		}

	}
	
}
