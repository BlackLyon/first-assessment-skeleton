package com.cooksys.assessment.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cooksys.assessment.model.Message;
import com.cooksys.assessment.model.UserList;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ClientHandler implements Runnable {
	private Logger log = LoggerFactory.getLogger(ClientHandler.class);

	private Socket socket;
	private static UserList users = new UserList();

	public ClientHandler(Socket socket) {
		super();
		this.socket = socket;
	}

	public void run() {
		try {

			ObjectMapper mapper = new ObjectMapper();
			BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

			while (!socket.isClosed()) {
				String raw = reader.readLine();
				Message message = mapper.readValue(raw, Message.class);
				
				switch (message.getCommand()) {
					case "connect":
						log.info("user <{}> connected", message.getUsername());
						users.AddUsers(message.getUsername(), socket);
						String newUser = String.format("User %s has connected", message.getUsername());
						message.setContents(newUser);
						users.ServerMessage(message);
						break;
					case "disconnect":
						log.info("user <{}> disconnected", message.getUsername());
						users.RemoveUsers(message.getUsername());
						newUser = String.format("User %s has disconnected", message.getUsername());
						message.setContents(newUser);
						users.ServerMessage(message);
						this.socket.close();
						break;
					case "echo":
						log.info("user <{}> echoed message <{}>", message.getUsername(), message.getContents());
						users.Echo(message);
						break;
					case "users":
						log.info("'user <{}> requested user list.", message.getUsername());
						users.GetUsers(message);
						break;
					case "@":
						log.info("'user <{}> requested direct message.", message.getUsername());
						users.DirectMessage(message);
						break;
					case "broadcast":
						log.info("User <{}> is broadcasting a message", message.getUsername());
						users.Broadcast(message);
						break;
				}
			}

		} catch (IOException e) {
			log.error("Something went wrong :/", e);
		}
	}

}
