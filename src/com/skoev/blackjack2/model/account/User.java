package com.skoev.blackjack2.model.account;
import com.skoev.blackjack2.model.game.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class User {
	public String username; 
	public String password;
	public List<Game> games = new ArrayList<Game>();
	
	public User(String username, String password) {
		this.username = username;
		this.password = password;
	}

	public void deleteGame(int gameId){
		
	}
	
	public Game getGame(int gameId){
		for(Game game : games){
			if(gameId == game.gameID){
				return game;
			}
		}
		return null;
	}
	//todo basic: add validation rules for the aggregate root modifying its internals. For example, is this game allowed to be added for this user? Is this game allowed to be deleted for this user? 
	public void addNewGame(Game game){
		games.add(game);
	}
	
	public Collection<Game> getGames(){
		return games;
	}

}

//todo next 3: add games with default options. 
//todo next 1: modify the user repository to save the old stuff
//todo next 1.1 : add Game Service in addition to account service. They will serve as facades that the controller calls. so controller doesn't need to know about playing the game. 
//todo next 1.2 : figure out where you should save game automatically and where not. 

