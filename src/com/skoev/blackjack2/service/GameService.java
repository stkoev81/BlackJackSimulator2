package com.skoev.blackjack2.service;

import java.util.Collection;

import com.skoev.blackjack2.infrastructure.UserRepositoryImpl;
import com.skoev.blackjack2.model.account.User;
import com.skoev.blackjack2.model.game.Game;

/**
 *	Provides game playing functionality. Also starts new games and retrieves existing games. 
 */
public class GameService {
	private static UserRepository userRepository = UserRepositoryImpl.getInstance();

	public static void addNewGame(Game game, User user){
		user.addNewGame(game);
		userRepository.saveUser(user);
	}

	public static void deleteGame(int gameId, User user){
		user.deleteGame(gameId);
		userRepository.saveUser(user);
	}

	public static void playGame(User user, Game game){
		game.play();
		userRepository.saveUser(user);
	}

	public static Game getGame(int gameId, User user){
		return user.getGame(gameId);
	}

	public static Collection<Integer> getGameIds(User user){
		return user.getGameIds();
	}

	public static Collection<Game> getGames(User user){
		return user.getGames();
	} 
}
