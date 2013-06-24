package com.skoev.blackjack2.service;
import java.io.ObjectInputStream.GetField;

import com.skoev.blackjack2.infrastructure.*;
import com.skoev.blackjack2.model.account.User;
import com.skoev.blackjack2.model.game.*;

/**
 * Provides basic account related functionality. Checks if user exists and retrieves it, creates a new user (checking if the new username and password 
 * obey the rules). 
 * @author stefan.t.koev
 *
 */
public class AccountService {
	private static final int[] PASSWORD_LENGTH_RULES = {2, 10};
	private static final int[] USERNAME_LENGTH_RULES = {2, 10};
	public static final String RULES_MESSAGE = "Password and username must be between 2 and 10 characters.";
	
	private static UserRepository userRepository = UserRepositoryImpl.getInstance(); 
	
	/**
	 * @param username
	 * @param password
	 * @return The User object for the authenticated user; null if user could not be authenticated
	 */
	public static User authenticateUser(String username, String password) {
		User user = userRepository.getUser(username);
		if(user == null || !user.getPassword().equals(password)){
			return null;
		}
		else{
			return user;
		}
	}
	
	/**
	 * 
	 * @param username
	 * @param password
	 * @return The newly created User object; null if it could not be created because the username/password violate rules or because username unavailable. 
	 */
	public static User createNewUser(String username, String password){
		User user = null;
		if(checkUsernameAvailable(username) && checkUsernamePasswordRules(username, password)){
			user = new User(username, password);
			userRepository.saveUser(user);
		}
		return user;
	}
	/**
	 * 
	 * @param username
	 * @param password
	 * @return True if the username and password obey the rules of the application; false otherwise. 
	 */
	public static boolean checkUsernamePasswordRules(String username, String password){
		if (checkLength(PASSWORD_LENGTH_RULES, password) && checkLength(USERNAME_LENGTH_RULES, username)){
			return true;
		}
		else{
			return false; 
		}
	}

	private static boolean checkLength(int[] cons, String toCheck){
		assert cons.length == 2;
		if (toCheck == null || toCheck.length() < cons[0] || toCheck.length() > cons[1]){
			return false;
		}
		else{
			return true;
		}
	}
	
	/**
	 * 
	 * @param username
	 * @return True if this username is available (not already used); false otherwise. 
	 */
	public static boolean checkUsernameAvailable(String username){
		User user = userRepository.getUser(username);
		if(user == null){
			return true;
		}
		else{
			return false;
		}
	}
	
	
}
