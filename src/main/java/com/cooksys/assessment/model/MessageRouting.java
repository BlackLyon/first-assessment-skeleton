package com.cooksys.assessment.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cooksys.assessment.server.ClientHandler;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MessageRouting implements Runnable{

	//private ConcurrentHashMap<String, LinkedBlockingQueue<Message>> users;
	private  ConcurrentHashMap<String, Socket> users;
	private String userName;
	//private Socket socket;
	Message message;
	private Logger log = LoggerFactory.getLogger(ClientHandler.class);

	public MessageRouting(ConcurrentHashMap<String, Socket> list, String name)
	{
		users = list;
		userName = name;
	}
	
	public synchronized void Waiting()
	{
		try {
			wait();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public synchronized void SetMessages(Socket sock, Message msg)
	{
		
		message = msg;
	}
	
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		log.info("Run() name <{}>: ", users.keySet());
		log.info("Run() sock <{}>: ", users.values());
		/*if(users.get(userName).isEmpty())
		{
			Waiting();
		}
		else
		{
		try {
			ObjectMapper mapper = new ObjectMapper();
			BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
			
			//String response = mapper.writeValueAsString(message);
			//log.info("Message contains: <{}>", message.getContents());
			//writer.write(response);
			//writer.flush();
			
			String response = mapper.writeValueAsString(users.get(userName));
			log.info("Message Routing userName: <{}>", userName);
			log.info("Message contains: <{}>", response);
			//writer.write(response);
			//writer.flush();
			
			//message.setContents(users.GetUsers().toString());
			//String re = mapper.writeValueAsString(message);
			//log.info("Re contains: <{}>", re);
			//writer.write(re);
			//writer.flush();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}
		*/}
}

