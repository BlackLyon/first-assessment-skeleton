package com.cooksys.assessment.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

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
			PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));

			while (!socket.isClosed()) {
				String raw = reader.readLine();
				Message message = mapper.readValue(raw, Message.class);
				
				switch (message.getCommand()) {
					case "connect":
						log.info("user <{}> connected", message.getUsername());
						users.AddUsers(message.getUsername());
						log.info("User List contains: <{}>", users.GetUsers().toString());
						break;
					case "disconnect":
						log.info("user <{}> disconnected", message.getUsername());
						users.RemoveUsers(message.getUsername());
						log.info("User List contains: <{}>", users.GetUsers().toString());
						this.socket.close();
						break;
					case "echo":
						log.info("user <{}> echoed message <{}>", message.getUsername(), message.getContents());
						String response = mapper.writeValueAsString(message);
						log.info("Message contains: <{}>", message.getContents());
						writer.write(response);
						writer.flush();
						break;
					case "users":
						log.info("'user <{}> requested user list.", message.getUsername());
						message.setContents(users.GetUsers().toString());
						String re = mapper.writeValueAsString(message);
						log.info("Re contains: <{}>", re);
						writer.write(re);
						writer.flush();
						break;
					case "direct duane":
						log.info("'user <{}> requested direct message.", message.getUsername());
						break;
				}
			}

		} catch (IOException e) {
			log.error("Something went wrong :/", e);
		}
	}

}
