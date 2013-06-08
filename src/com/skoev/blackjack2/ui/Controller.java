package com.skoev.blackjack2.ui;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Observable;

import com.skoev.blackjack2.model.account.*;
import com.skoev.blackjack2.model.game.*;
import com.skoev.blackjack2.service.ApplicationService;


public class Controller {
	public User user; 
	
	public static void main(String[] args){
		//new GameController().playSingleGame(); 
		new Controller().startApplication();
	}
	
	private static enum Option{login, help, createAccount, startGame, exit, view_details, start_new, log_out, delete_game, continue_game, home_screen}
	
	private static enum Strategy{interactive, predictable}
	
	public void startApplication(){
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
			try{ 
				String message = "This is a blackjack simulation game.";
				Option[] options = {Option.help, Option.login, Option.createAccount, Option.exit};
				Option option = ViewGeneral.getOption(options, message);
				switch (option){
				case login : 
					logIn();
					break;
				case help : 
					showHelp();
					break;
				case createAccount :
					createAccount();
					break;
				case exit :
					end = true;
					break;
				}
			}
			catch (UserCanceledActionException e){
				//stay on login screen
			}
		}
	}
	
	public void showHelp(){
		String message = "This is a blackjack game help"; 
		ViewGeneral.display(message);
	}
 
	public void logIn(){
			ViewGeneral.display("Logging in with exising account. ");
			String username = ViewGeneral.getInput("Enter username: ");
			String password = ViewGeneral.getInput("Enter password: "); 
			user = ApplicationService.authenticateUser(username, password); 
			if(user != null){
				goToHomeScreen();
			}
			else{
				ViewGeneral.display("Invalid username/password.");
		}
	}
	
	public void createAccount(){
		String username = null; 
		String password = null; 
		String password2 = null;
		ViewGeneral.display("Creating a new account. ");
		while(password == null || !password.equals(password2)){
			username  =  ViewGeneral.getInput("Enter username: ");
			password = ViewGeneral.getInput("Enter password: "); 
			password2 = ViewGeneral.getInput("Re-enter password: ");
			if(!password.equals(password2)){
				ViewGeneral.display("Passwords don't match. Try again. "); 
			}
		}
		user = ApplicationService.createNewUser(username, password); 
		if(user != null){
			ViewGeneral.display("Account created");
			goToHomeScreen();
		}
		else{ ////todo basic: check for exceptions why the account could not created. Using business rules. Print out the rules for passwords from the business layer.  
			ViewGeneral.display("Account could not be created");
		}
		
	}
	
	public void goToHomeScreen(){
		//if log out, exit;
		//keep it an a loop so if these methods return you don't exit
		//if play new game, new game screen
		//if view game details, get which game & call view GAme details
		boolean end = false;
		while (!end){
			try{
				View.displayGameSummary(user.getGames());
				String message = "You can view (and continue if inccomplete) an existing game or start a new one.";
				Option[] options = {Option.log_out, Option.view_details, Option.start_new};
				Option option = ViewGeneral.getOption(options, message);
				switch(option){
					case log_out:
						end = true;
						user = null;
						break;
					case view_details:
						//todo next: change this call to get one of a group of integers; if there is only one integer, then get the same game. //also if no games don't go to view game details, simply state there are not games. 
						int whichGame = ViewGeneral.getPositiveInteger("Which game number?");
						viewGameDetails(whichGame);	
						break;
					case start_new:
						startNewGame();
						break;
				}
			}
			catch (UserCanceledActionException e){
				//stay on home screen 
			}
		}
	} 
	
	public void viewGameDetails(int gameId){
		Game game = ApplicationService.getGame(gameId, user);
		View.displayGameDetails(game);
		String message = "You can delete or continue (if incomplete) the game";
		
		//todo basic: check game for completenesss and don't allow to continue if it is complete
		Option[] options = {Option.home_screen, Option.delete_game, Option.continue_game};
		
		Option option = ViewGeneral.getOption(options, message);
		//if home screen, exit
		//if delete game, do so using service
		//if continue game, call playSingleGame
		switch(option){
			case home_screen: 
				break;
			case delete_game:
				ApplicationService.deleteGame(gameId, user);
				ViewGeneral.display("Deleted game " + gameId);
				break;
			case continue_game:
				ViewGeneral.display("Continuing game " + gameId);
				playGame(game); 
				break;
		}
	}
	
	public void startNewGame(){
		Strategy[] options = {Strategy.interactive, Strategy.predictable};
		String message = "Choose a game type";
		Strategy option = ViewGeneral.getOption(options, message);
		
		PlayingStrategy playingStrategy = null;
		BigDecimal money = ViewGeneral.getAmount("Enter starting money");
		int numRounds = ViewGeneral.getPositiveInteger("Enter number of rounds to play");
		
		if(option.equals(Strategy.interactive)){
			playingStrategy = new PlayingStrategyInteractive();
		} 
		
		if(option.equals(Strategy.predictable)){
			message = "Accept insurance?";
			Boolean acceptInsurance =  ViewGeneral.getOption(new Boolean[]{Boolean.TRUE, Boolean.FALSE}, message);
			message = "Default response to offer?";
			Round.Offer defaultOffer = ViewGeneral.getOption(new Round.Offer[]{Round.Offer.STAND, Round.Offer.HIT, Round.Offer.DOUBLE }, message);
			message = "Default amount of bet?"; 
			BigDecimal defaultBet = ViewGeneral.getAmount(message);
			playingStrategy = new PlayingStrategyPredictable(defaultOffer, defaultBet, acceptInsurance);
		}
		Game game = new Game(playingStrategy, numRounds, money);
		ApplicationService.addNewGame(game, user);
		playGame(game);
	}
	
	
	public void playGame(Game game){
//		PlayingStrategy playingStrategy = new PlayingStrategyPredictable(Round.Offer.STAND, BigDecimal.valueOf(1), false);
		//Game game = new Game(playingStrategy, new Deck());
		do {
			ApplicationService.playGame(user, game);
			//you end up here only if game is finished(any strategy) or if input is needed (interactive strategy only).
      
			//	interactive case, game not finished
			if(game.isInteractive() && game.userInputNeeded){
				PlayingStrategy strategy = game.playingStrategy;
				Round round = game.currentRound;
				if(Round.RoundStatus.HAND_BEING_DEALT.equals(round.roundStatus)){
					int n = game.pastRounds.size();
					if( n > 0){
						Round previousRound = game.pastRounds.get(n-1);
						View.displayRoundDetails(previousRound);
					}
					strategy.setAmountBet(View.getAmountBet(game.gameID, game.currentRound.roundNumber));
				}
				else{
					strategy.setResponseToOffer(View.getResponseToOffer(round.availableOffers, round.dealerHand, round.currentHand));
				}
			}
			
			// interactive case, game finished
			if(game.isInteractive() && !game.userInputNeeded){
				int n = game.pastRounds.size();
				View.displayRoundDetails(game.pastRounds.get(n-1));
			}
			
			//	non-interactive case, game finished
			if(!game.isInteractive()){
				View.displayGameDetails(game);
			}
			//	todo next: pretty up the outputs a little and verify they make sense (e.g. if it's a dealer hand, not need for amount bet because it's null).
			//todo after: create the views for the user returning to the game 
			// todo after: implement the other automated playing strategies; some tests
		}
		while(game.userInputNeeded);
		
		
	}
			
}
