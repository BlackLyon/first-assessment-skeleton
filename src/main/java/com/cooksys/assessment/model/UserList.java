package com.cooksys.assessment.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentHashMap.KeySetView;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cooksys.assessment.server.ClientHandler;
import com.fasterxml.jackson.databind.ObjectMapper;

public class UserList {
	private Logger log = LoggerFactory.getLogger(ClientHandler.class);
	//private ConcurrentHashMap<String, LinkedBlockingQueue<Message>> users = new ConcurrentHashMap<String, LinkedBlockingQueue<Message>>();
	private  ConcurrentHashMap<String, Socket> users = new ConcurrentHashMap<String, Socket>();
	private String username;
	ObjectMapper mapper = new ObjectMapper();
	BufferedReader reader; //= new BufferedReader(new InputStreamReader(socket.getInputStream()));
	PrintWriter writer; //= new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
	
	public String GetUsername() 
	{
		return username;
	}
	
	public void SetUserName(String un)
	{
		username = un;
	}
	
	public synchronized void GetUsers(Message msg)
	{
		try {
			writer = new PrintWriter(new OutputStreamWriter(users.get(msg.getUsername()).getOutputStream()));
			
			msg.setContents(users.keySet().toString());
			String re = mapper.writeValueAsString(msg);
			log.info("Re contains: <{}>", re);
			writer.write(re);
			writer.flush();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//return users.keySet();
	}
	
	public synchronized void AddUsers(String name, Socket sock) 
	{
		users.put(name, sock);
		log.info("AddUsers name <{}>: ", name);
		log.info("AddUsers sock <{}>: ", sock);
	}
	
	public synchronized void RemoveUsers(String name) 
	{
		users.remove(name);
	}
	
	public synchronized void Echo(Message msg)
	{
		log.info("Echo Socket <{}", users.get(msg.getUsername()));
		try {
			writer = new PrintWriter(new OutputStreamWriter(users.get(msg.getUsername()).getOutputStream()));
			
			String response = mapper.writeValueAsString(msg);
			log.info("Message contains: <{}>", msg.getContents());
			writer.write(response);
			writer.flush();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public synchronized void DirectMessage(Message msg) 
	{
		String temp = msg.getContents().split(" ")[0];
		log.info("Users: <{}>", users.keySet());
		log.info("Temp array contains: <{}>", temp);
		log.info("Msg Username is: <{}>", msg.getUsername());
		log.info("Msg contents contains: <{}>", msg.getContents());
		if(users.containsKey(temp))
		{
			//users.get(temp).add(msg);
		}
	}

}
