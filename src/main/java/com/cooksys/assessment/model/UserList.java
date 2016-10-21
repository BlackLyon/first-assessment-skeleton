package com.cooksys.assessment.model;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cooksys.assessment.server.ClientHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


/*
 * The UserList class contains a hashmap where the user name of the connecting client is the key and their associated socket is the value.
 * This class is also responsible for passing messages to the client based on the command the client selected on their side.  
 * I did this to keep the switch statement in the client handler more readable to make it easier to track what call or feature was 
 * causing an issue.
 * This class also is responsible for formatting the messages sent to the client.
 */
public class UserList {
	private Logger log = LoggerFactory.getLogger(ClientHandler.class);
	private ConcurrentHashMap<String, Socket> users = new ConcurrentHashMap<String, Socket>();
	private ObjectMapper mapper = new ObjectMapper();
	private PrintWriter writer; 
	
	
	/*
	 * Called when a user selects the "users" command on the client end.
	 * Function takes a keyset of all the users in the users hashmap and appends them into a single string
	 * it then creates an outputstream using the caller's socket and puts the string of users into the msg object's
	 * contents and returns the message to the client.
	 * 
	 * msg is a Message object passed in from client handler
	 */
	public synchronized void GetUsers(Message msg)
	{
		String list = new String();
		
		for(String key : users.keySet())
		{
			list = list.concat(key);
			list = list.concat("\n");
		}
		
		try {
			writer = new PrintWriter(new OutputStreamWriter(users.get(msg.getUsername()).getOutputStream()));
			
			String temp = String.format("${timestamp}: currently connected users: \n" + "%s", list );
			msg.setContents(temp);
			String response= mapper.writeValueAsString(msg);
			writer.write(response);
			writer.flush();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/*
	 * Adds a user and their socket to the users hashmap.  Name is used as the key and sock as the key's value
	 */
	public synchronized void AddUsers(String name, Socket sock) 
	{
		users.put(name, sock);
	}
	
	/* 
	 * remove user and their key from the map
	 * name is the key to search for
	 */
	public synchronized void RemoveUsers(String name) 
	{
		users.remove(name);
	}
	
	/*
	 * Called when the user selects the "echo" command from the client end.  
	 * Function creates an output stream using the requesting client's socket 
	 * and, after formatting the string, places it in the contents of the msg object
	 * and sends it back to the calling client.
	 * 
	 * msg is a Message object passed in from client handler
	 */
	public synchronized void Echo(Message msg)
	{
		java.util.Date date = new java.util.Date();
		java.sql.Timestamp timestamp =  new java.sql.Timestamp(date.getTime());;
		
		try {
			writer = new PrintWriter(new OutputStreamWriter(users.get(msg.getUsername()).getOutputStream()));
			
			String temp = String.format("{%s} <%s> (echo): %s", timestamp, msg.getUsername(), msg.getContents());
			msg.setContents(temp);
			String response = mapper.writeValueAsString(msg);
			writer.write(response);
			writer.flush();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/*
	 * Called when the user selects the "@ (whisper)" command from the client end.  
	 * Function separates the first word from the string (since the command to send a direct message
	 * is a combination of both the @ symbol and the receiving user's name we know the first word in the string
	 * will be the receiving user's name) and sets it in its own string.  
	 * If the user is in the users hashmap (and thus connected) it then creates an output stream using the socket 
	 * associated with the recieving user and, after formatting the string, places it in the contents of the msg object
	 * and sends it back to the calling client.
	 * 
	 * If the user is not in the hashmap (and therefore not connected) it sends the calling client a formatted message saying the
	 * user is not connected.
	 * 
	 * msg is a Message object passed in from client handler
	 */
	public synchronized void DirectMessage(Message msg) 
	{
		java.util.Date date = new java.util.Date();
		java.sql.Timestamp timestamp =  new java.sql.Timestamp(date.getTime());;
		
		try {
			String name = msg.getContents().split(" ")[0];
			writer = new PrintWriter(new OutputStreamWriter(users.get(name).getOutputStream()));
			
			if(users.containsKey(name))
			{
				String temp = String.format("{%s} <%s> (whisper): %s", timestamp, msg.getUsername(), msg.getContents().replaceFirst(name, ""));
				msg.setContents(temp);
				String response = mapper.writeValueAsString(msg);
				writer.write(response);
				writer.flush();
			}
			else 
			{
				String temp = String.format("{%s} <%s> is not connected", timestamp, name);
				msg.setContents(temp);
				String response = mapper.writeValueAsString(msg);
				writer.write(response);
				writer.flush();
			}
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/*
	 * Called when the user selects the "broadcast" command from the client end.  
	 * After formatting the message, the function loops through each user in the users hashmap, calling each one's respective socket
	 * and sending that user a copy of the message. 
	 * the ServerMessage function also piggybacks off of this function to send server alerts
	 * 
	 * msg is a Message object passed in from client handler
	 */
	public synchronized void Broadcast(Message msg)
	{
		java.util.Date date = new java.util.Date();
		java.sql.Timestamp timestamp =  new java.sql.Timestamp(date.getTime());;
		
		log.info("In Broadcast Method");
		String temp = String.format("{%s} <%s> (all): %s", timestamp, msg.getUsername(), msg.getContents());
		msg.setContents(temp);
		
		for(String key : users.keySet())
		{
			System.out.println(key  +" :: "+ users.get(key));
			try {
				writer = new PrintWriter(new OutputStreamWriter(users.get(key).getOutputStream()));
				
				String response = mapper.writeValueAsString(msg);
				writer.write(response);
				writer.flush();
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/*
	 * This function is not called on the client end, instead clients receive this message from the server for events like when
	 * a new user connects or disconnects or other server events they need to be aware of.
	 * After formatting the message from client handler it simply passes it to Broadcast to send to the users.
	 * 
	 * msg is a Message object passed in from client handler
	 */
	public synchronized void ServerMessage(Message msg)
	{
		java.util.Date date = new java.util.Date();
		java.sql.Timestamp timestamp =  new java.sql.Timestamp(date.getTime());;
		
		msg.setCommand("servermsg");
		String temp = String.format("{%s} <%s>: %s", timestamp, msg.getUsername(), msg.getContents());
		msg.setContents(temp);
		Broadcast(msg);
	}
}
