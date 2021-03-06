package com.skoev.blackjack2.model.account;
import com.skoev.blackjack2.common.AggregateRoot;
import com.skoev.blackjack2.common.Util;
import com.skoev.blackjack2.model.game.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.io.Serializable;

/**
 * Represents a user of the application.  
 */

public class User implements AggregateRoot {
	private String username; 
	private String password;
	private List<Game> games = new ArrayList<Game>();
	
	public User(String username, String password) {
		Util.assertNotEmpty(username);
		Util.assertNotEmpty(password);
		this.username = username;
		this.password = password;
	}

	public void deleteGame(int gameId){
		int i = getGameIndex(gameId);
		Util.assertTrue(i >= 0);
		games.remove(i); 
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
		Util.assertTrue(i >= 0);
		return games.get(i);

	}
	public void addNewGame(Game game){
		Util.assertNotNull(game);
		game.setGameID(getNextGameId());
		games.add(game);
	}
	
	public Collection<Game> getGames(){
		return games;
	}
	
	/**
	 * 
	 * @return the gameId's for all available games
	 */
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


