package com.skoev.blackjack2.model.account;
import com.skoev.blackjack2.model.game.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Represents a user of the application. 
 */

public class User {
	private String username; 
	private String password;
	private List<Game> games = new ArrayList<Game>();
	
	public User(String username, String password) {
		this.username = username;
		this.password = password;
	}

	public boolean deleteGame(int gameId){
		int i = getGameIndex(gameId);
		if(i < 0){
			return false;
		}
		else {
			games.remove(i); 
			return true;
		}
	}
	
	private int getGameIndex(int gameId){
		int i = 0;
		for(Game game : games){
			if(gameId == game.getGameID()){
				return i;
			}
			i++; 
		}
		return -1; 
	}
	
	private int getNextGameId(){
		int id = 0;
		for(Game game : games){
			if(game.getGameID() > id){
				id = game.getGameID();
			}
		}
		id = id + 1;
		return id;
	}
	
		
	public Game getGame(int gameId){
		int i = getGameIndex(gameId);
		if(i < 0){
			return null;
		}
		else{
			return games.get(i);
		}
	}
	//todo advanced: add validation rules for the aggregate root modifying its internals. For example, is this game allowed to be added for this user? Is this game allowed to be deleted for this user? 
	public void addNewGame(Game game){
		game.setGameID(getNextGameId());
		games.add(game);
	}
	
	public Collection<Game> getGames(){
		return games;
	}
	
	public Collection<Integer> getGameIds(){
		Collection<Integer> gameIds = new ArrayList<Integer>();
		for(Game game : games){
			gameIds.add(game.getGameID());
		}
		return gameIds;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}
	
	

}


