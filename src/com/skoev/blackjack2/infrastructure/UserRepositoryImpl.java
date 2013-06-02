package com.skoev.blackjack2.infrastructure;
import java.util.HashMap;
import java.util.Map;

import com.skoev.blackjack2.model.account.*;

public class UserRepositoryImpl implements UserRepository{
	private static UserRepositoryImpl instance = null;
	
	private UserRepositoryImpl(){
		
	}
	
	public static UserRepositoryImpl getInstance() {
		if(instance == null){
			instance = new UserRepositoryImpl();
		}
		return instance;
	}	
	
	private Map<String, User> users = new HashMap<String, User>();
	
	//todo basic: add validation and exceptions when users don't obey the rules.
	//todo basic: make this thread safe either by choosing a thread safe collection or synchronizing on something. However, how do you handle the case where there needs to be transaction isolation? 
	//todo advanced: make this support transactions -- making a backup object that is then restored if something goes wrong. Maybe make it support transactions api and locking api.
	//todo advanced: there shoudl be commit transaction and the client should commit because it knows best what to do if things fail. 
	
	@Override
	public User getUser(String username) {
		return users.get(username);
	}

	@Override
	public User createUser(String username, String password) {
		User user = new User(username, password);
		return users.put(username, user);
	}

	@Override
	public void saveUser(User user) {
		
	}
	
	

}
