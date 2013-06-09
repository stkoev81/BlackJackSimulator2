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
			if(gameId == game.gameID){
				return i;
			}
			i++; 
		}
		return -1; 
	}
	
	private int getNextGameId(){
		int id = 0;
		for(Game game : games){
			if(game.gameID > id){
				id = game.gameID;
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
		game.gameID = getNextGameId();
		games.add(game);
	}
	
	public Collection<Game> getGames(){
		return games;
	}
	
	public Collection<Integer> getGameIds(){
		Collection<Integer> gameIds = new ArrayList<Integer>();
		for(Game game : games){
			gameIds.add(game.gameID);
		}
		return gameIds;
	}

}


