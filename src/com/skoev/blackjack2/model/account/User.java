package com.skoev.blackjack2.model.account;
import com.skoev.blackjack2.model.game.*;

import java.util.Collection;

public class User {
	public String username; 
	public String password;
	public Collection<Game> games;
	
	
	public void deleteGame(int gameId){
		
	}
	
	public Game getGame(int gameId){
		return null; 
	}

}
