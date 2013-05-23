package com.skoev.blackjack2.controller;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Map;
import java.util.Observable;

import com.skoev.blackjack2.model.account.*;
import com.skoev.blackjack2.model.game.*;
import com.skoev.blackjack2.view.*;


public class GameController {
	public User user; //todo next: save the user object upon authentication or creation of a new account
	
	public static void main(String[] args){
		//new GameController().playSingleGame();
		new GameController().start();
		
	}
	
	public static enum Option{login, createAccount, startGame, exit, view_details, start_new, log_out, delete_game, continue_game, home_screen};
	
	
	public void start(){
		//display options to log in or create a new account
		//	if invalid option, show again same
		//	if choose to log in, present screen to log in or go back
		// 		if invalid login, show  same again
		//		if valid login, show games and options to continue playing or to start a new game
		// 			if start a game, choose game settings
		//			play a single game
		// 			show results
		
		// do this in a loop which is broken only by exit
		boolean end = false; 
		while(!end){
			Option option = GameView.getStartScreenInput();
			switch (option){
			case login : 
				logIn();
				break;
			case createAccount : 
				createAccount();
				break;
			case exit :
				end = true;
				break;
			}
		}
		
	}
	
	public void logIn(){
		String[] login = GameView.getLoginInput();
		if(AccountService.authenticateUser(login[0], login[1])){
			homeScreen();
		}
		else{
			GameView.printMessage("Invalid username/password.");
		}
			
		
		//attempt to log in
		//if invalid, print error message and exit; 
		// if valid, present the home screen 
	}
	public void createAccount(){
		String[] login = GameView.getCreateAccountInput();
		//attempt to create an account
		// if invalid, print error message and exit;
		// if valid, present "Success message"  and then the home screen
		if(AccountService.createNewUser(login[0], login[1])){
			GameView.printMessage("Account created");
			homeScreen();
		}
		else{
			GameView.printMessage("Account could not be created");
		}
		
	}
	
	public void homeScreen(){
		Option option = GameView.getHomeScreenInput();
		//if log out, exit;
		//keep it an a loop so if these methods return you don't exit
		//if play new game, new game screen
		//if view game details, get which game & call view GAme details
		boolean end = false;
		while (!end){
			switch(option){
				case exit:
					end = true;
					break;
				case view_details:
					int whichGame = GameView.getWhichGameInput();
					viewGameDetails(whichGame);
					break;
				case start_new:
					newGame();
					break;
			}
		}
		
	} 
	
	public void viewGameDetails(int gameId){
		Option option = GameView.getGameDetailsInput();
		//if home screen, exit
		//if delete game, do so using service
		//if continue game, call playSingleGame
		switch(option){
			case home_screen: 
				break;
			case delete_game:
				AccountService.deleteGame(null, 0);
				break;
			case continue_game:
				Game game = AccountService.getGame(user.username, gameId);
				playSingleGame(game); 
				break;
		}
	}
	
	public void newGame(){
		Game game = GameView.getNewGameInput();
		//todo next: construct a game based on user settings.
		playSingleGame(game);
	}
	
	
	public void playSingleGame(Game game){
//		PlayingStrategy playingStrategy = new PlayingStrategyPredictable(Round.Offer.STAND, BigDecimal.valueOf(1), false);
		PlayingStrategy playingStrategy = new PlayingStrategyInteractive();
		//Game game = new Game(playingStrategy, new Deck());
		do {
			game.playGame();
			//you end up here only if game is finished(any strategy) or if input is needed (interactive strategy only).
      
			//	interactive case, game not finished
			if(playingStrategy instanceof PlayingStrategyInteractive && game.userInputNeeded){
				PlayingStrategyInteractive interactive = (PlayingStrategyInteractive) playingStrategy;
				Round round = game.currentRound;
				if(Round.RoundStatus.HAND_BEING_DEALT.equals(round.roundStatus)){
					int n = game.pastRounds.size();
					if( n > 0){
						Round previousRound = game.pastRounds.get(n-1);
						GameView.printRound(previousRound);
					}
					interactive.amountBet = GameView.getAmountBet(game.gameID, game.currentRound.roundNumber);
				}
				else{
					interactive.responseToOffer = GameView.getResponseToOffer(round.availableOffers, round.dealerHand, round.currentHand);
				}
			}
			
			// interactive case, game finished
			if(playingStrategy instanceof PlayingStrategyInteractive && !game.userInputNeeded){
				int n = game.pastRounds.size();
				GameView.printRound(game.pastRounds.get(n-1));
			}
			
			//	non-interactive case, game finished
			if(!(playingStrategy instanceof PlayingStrategyInteractive)){
				GameView.printGame(game);
			}
			//	todo next: pretty up the outputs a little and verify they make sense (e.g. if it's a dealer hand, not need for amount bet because it's null).
			//todo after: create the views for the user returning to the game 
			// todo after: implement the other automated playing strategies; some tests
		}
		while(game.userInputNeeded);
		
		
	}
			
}
