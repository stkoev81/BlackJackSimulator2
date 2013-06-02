package com.skoev.blackjack2.model.account;
import java.io.ObjectInputStream.GetField;

import com.skoev.blackjack2.infrastructure.*;
import com.skoev.blackjack2.model.game.*;

/**
 * The responsibility is to provide basic account related functionality. Checks if user exists and retrieves it, creates a new user (checking if the new username and password 
 * obey the rules). Creates a new game for the user or retrieves and continues existing games. Pauses and saves games.
 * @author stefan.t.koev
 *
 */
public class AccountService {
	//User logIn(String username, String password);
	//todo basic: add exceptions to the user authentication and creation
	public static UserRepository userRepository = UserRepositoryImpl.getInstance(); 
	
	public static User authenticateUser(String username, String password){
		User user = userRepository.getUser(username);
		if(user == null || !user.password.equals(password)){
			return null;
		}
		else{
			return user;
		}
	}
	public static User createNewUser(String username, String password){
		User user = userRepository.createUser(username, password);
		return user;
	}
	
	public static void addNewGame(Game game, User user){
		user.addNewGame(game);
		userRepository.saveUser(user);
	}
	
	public static void deleteGame(int gameId, User user){
		user.deleteGame(gameId);
		userRepository.saveUser(user);
	}


}
