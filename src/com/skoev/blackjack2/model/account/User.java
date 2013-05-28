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
	
	public void addNewGame(Game game){
		
	}

}

//todo next: enter the ability to cancel by entering an empty input
// add validation to "which game number"
//add games with default options. 
//enter the game summary view
//modify the user repository to save the old stuff

