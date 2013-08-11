package com.skoev.blackjack2.infrastructure;
import java.util.HashMap;
import java.util.Map;

import com.skoev.blackjack2.model.account.*;

/**
 * Implements a very simple in-memory repository for User objects. The data in this repository is not persistent and is lost when the application is restarted.
 * Also, this repository lacks most of the features that a 
 * database would have, such as concurrency and transactions.  
 */

public class UserRepositoryInMemory implements UserRepository{
	private static UserRepositoryInMemory instance = null;
	
	private UserRepositoryInMemory(){
		
	}
	
	public static UserRepositoryInMemory getInstance() {
		if(instance == null){
			instance = new UserRepositoryInMemory();
		}
		return instance;
	}	
	
	private Map<String, User> users = new HashMap<String, User>();
	
	 
	//todo advanced: make this thread safe either by choosing a thread safe collection or synchronizing on something. However, how do you handle the case where there needs to be transaction isolation? 
	//todo advanced: make this support transactions -- making a backup object that is then restored if something goes wrong. Maybe make it support transactions api and locking api.
	//todo advanced: there should be commit transaction and the client should commit because it knows best what to do if things fail. 
	
	@Override
	public User getUser(String username) {
		return users.get(username);
	}

	@Override
	public void saveUser(User user) {
		users.put(user.getUsername(), user);
	}
	
	

}
