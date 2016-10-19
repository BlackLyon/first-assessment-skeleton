package com.cooksys.assessment;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cooksys.assessment.model.UserList;
import com.cooksys.assessment.server.Server;

public class Main {
	private static Logger log = LoggerFactory.getLogger(Main.class);

	public static void main(String[] args) {
		ExecutorService executor = Executors.newCachedThreadPool();
		
		Server server = new Server(8080, executor);
		
		Future<?> done = executor.submit(server);
		UserList users = new UserList();
		
		try {
			//users.AddUsers("Duane");
			//users.AddUsers("Rick");
			//log.info(users.GetUsers().toString());
			done.get();
			executor.shutdown();
			executor.awaitTermination(5, TimeUnit.SECONDS);
		} catch (InterruptedException | ExecutionException e) {
			log.error("Something went wrong :/", e);
		}
	}
	
}
