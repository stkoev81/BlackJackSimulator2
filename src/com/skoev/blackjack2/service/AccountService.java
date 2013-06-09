package com.skoev.blackjack2.service;
import java.io.ObjectInputStream.GetField;

import com.skoev.blackjack2.infrastructure.*;
import com.skoev.blackjack2.model.account.User;
import com.skoev.blackjack2.model.game.*;

/**
 * The responsibility is to provide basic account related functionality. Checks if user exists and retrieves it, creates a new user (checking if the new username and password 
 * obey the rules). Creates a new game for the user or retrieves and continues existing games. Pauses and saves games.
 * @author stefan.t.koev
 *
 */
public class AccountService {
	private static final int[] PASSWORD_LENGTH_RULES = {2, 10};
	private static final int[] USERNAME_LENGTH_RULES = {2, 10};
	public static final String RULES_MESSAGE = "Password and username must be between 2 and 10 characters.";
	
	//todo basic: add exceptions to the user authentication and creation
	private static UserRepository userRepository = UserRepositoryImpl.getInstance(); 
	
	public static User authenticateUser(String username, String password) {
		User user = userRepository.getUser(username);
		if(user == null || !user.password.equals(password)){
			return null;
		}
		else{
			return user;
		}
	}
	
	public static User createNewUser(String username, String password){
		User user = null;
		if(checkUsernameAvailable(username) && checkUsernamePasswordRules(username, password)){
			user = new User(username, password);
			userRepository.saveUser(user);
		}
		return user;
	}
	
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
