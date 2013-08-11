package com.skoev.blackjack2.data;
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
	 
	
	@Override
	public User getUser(String username) {
		return users.get(username);
	}

	@Override
	public void saveUser(User user) {
		users.put(user.getUsername(), user);
	}
	
	

}
