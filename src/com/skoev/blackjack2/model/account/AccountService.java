package com.skoev.blackjack2.model.account;
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
	public static User authenticateUser(String username, String password){
		return null;
	}
	public static User createNewUser(String username, String password){
		return null;
	}
	
	


}
