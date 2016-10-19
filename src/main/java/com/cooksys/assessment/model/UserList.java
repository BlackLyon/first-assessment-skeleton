package com.cooksys.assessment.model;

import java.util.ArrayList;

public class UserList {
	private ArrayList<String> users = new ArrayList<String>();
	private String username = "Duane";
	
	public String GetUsername() 
	{
		return username;
	}
	
	public void SetUserName(String un)
	{
		username = un;
	}
	
	public synchronized ArrayList<String> GetUsers()
	{
		return users;
	}
	
	public synchronized void AddUsers(String name) 
	{
		users.add(name);
	}
	
	public synchronized void RemoveUsers(String name) 
	{
		users.remove(name);
	}

}
