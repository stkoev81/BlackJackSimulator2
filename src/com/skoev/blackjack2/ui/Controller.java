package com.skoev.blackjack2.ui;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Observable;

import com.skoev.blackjack2.model.account.*;
import com.skoev.blackjack2.model.game.*;
import com.skoev.blackjack2.service.AccountService;
import com.skoev.blackjack2.service.GameService;

/**
 * Controls the state of the application. Uses the view to interact with the user. Maps user inputs to service and model method calls. Displays the results back to the user. 
 */
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
				ViewGeneral.displayHeader("Main screen"); 
				String message = "This is a blackjack simulation game.";
				Option[] options = {Option.help, Option.login, Option.createAccount, Option.exit};
				Option option = ViewGeneral.getOption(options, message);
				ViewGeneral.displayFooter();
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
		ViewGeneral.displayHeader("Help screen");
		String message = "This is a blackjack game help"; 
		ViewGeneral.display(message);
		ViewGeneral.displayFooter();
	}
 
	public void logIn(){
			ViewGeneral.displayHeader("Login screen");
			ViewGeneral.display("Logging in with exising account. ");
			String username = ViewGeneral.getInput("Enter username: ");
			String password = ViewGeneral.getInput("Enter password: "); 
			user = AccountService.authenticateUser(username, password); 
			if(user != null){
				ViewGeneral.displayFooter();
				goToHomeScreen();
			}
			else{
				ViewGeneral.display("Invalid username/password.");
				ViewGeneral.displayFooter();
		}
	}
	
	public void createAccount(){
		String username = null; 
		String password = null; 
		String password2 = null;
		ViewGeneral.displayHeader("Create account screen");
		ViewGeneral.display("Creating a new account. " + AccountService.RULES_MESSAGE);
		while(password == null || !password.equals(password2)){
			username  =  ViewGeneral.getInput("Enter username: ");
			if(!AccountService.checkUsernameAvailable(username)){
				ViewGeneral.display("Username is already taken. Try again. ");
				continue;
			}
			password = ViewGeneral.getInput("Enter password: ");
			if(!AccountService.checkUsernamePasswordRules(username, password)){
				ViewGeneral.display(AccountService.RULES_MESSAGE + " Try again.");
				continue;
			}
			password2 = ViewGeneral.getInput("Re-enter password: ");
			if(!password.equals(password2)){
				ViewGeneral.display("Passwords don't match. Try again. "); 
			}
			
		}
		user = AccountService.createNewUser(username, password); 
		if(user != null){
			ViewGeneral.display("Account created");
			ViewGeneral.displayFooter();
			goToHomeScreen();
		}
		else{   
			ViewGeneral.display("Account could not be created. Contact technical support.");
			ViewGeneral.displayFooter();
		}
		
	}
	
	public void goToHomeScreen(){
		//if log out, exit;
		//keep it an a loop so if these methods return you don't exit
		//if play new game, new game screen
		//if view game details, get which game & call view GAme details
		boolean end = false;
		while (!end){
			Option option = null; 
			try{
				ViewGeneral.displayHeader("User's home screen");
				View.displayGameSummary(GameService.getGames(user));
				String message = "You can view (and continue if inccomplete) an existing game or start a new one.";
				Option[] options = {Option.log_out, Option.view_details, Option.start_new};
				option = ViewGeneral.getOption(options, message);
				switch(option){
					case log_out:
						end = true;
						user = null;
						ViewGeneral.displayFooter();
						break;
					case view_details:
						Collection<Integer> gameIds = GameService.getGameIds(user);  
						if(gameIds.size()>0){
							int whichGame = ViewGeneral.getPositiveInteger(gameIds, "Which game number?");
							ViewGeneral.displayFooter();
							viewGameDetails(whichGame);	
						}
						else{
							ViewGeneral.display("No games to view!");
							ViewGeneral.displayFooter();
						}
						break;
					case start_new:
						ViewGeneral.displayFooter();
						startNewGame();
						break;
				}
			}
			catch (UserCanceledActionException e){
//				if(option == null){  // the action was canceled from the home screen. 
//					ViewGeneral.displayFooter();
//				}
			}
		}
	} 
	
	public void viewGameDetails(int gameId){
		View.displayHeader("Game details screen");
		Game game = GameService.getGame(gameId, user);
		View.displayGameDetails(game);
		String message = "You can delete or continue (if incomplete) the game";
		
		Option[] options = {Option.home_screen, Option.delete_game, Option.continue_game};
		
		Option option = ViewGeneral.getOption(options, message);
		//if home screen, exit
		//if delete game, do so using service
		//if continue game, call playSingleGame
		switch(option){
			case home_screen: 
				ViewGeneral.displayFooter();
				break;
			case delete_game:
				GameService.deleteGame(gameId, user);
				ViewGeneral.display("Deleted game " + gameId);
				ViewGeneral.displayFooter();
				break;
			case continue_game:
				ViewGeneral.display("Continuing game " + gameId);
				if(game.isFinished()){
					ViewGeneral.display("This game is finished! Cannot continue it.");
					ViewGeneral.displayFooter();
				}
				else{
					ViewGeneral.displayFooter();
					playGame(game);
				}
				break;
		}
	}
	
	public void startNewGame(){
		Strategy[] options = {Strategy.interactive, Strategy.predictable};
		String message = "You must choose a game type.";
		ViewGeneral.displayHeader("Start new game screen");
		Strategy option = ViewGeneral.getOption(options, message);
		
		PlayingStrategy playingStrategy = null;
		BigDecimal money = ViewGeneral.getAmount("Enter starting money:");
		int numRounds = ViewGeneral.getPositiveInteger("Enter number of rounds to play:");
		
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
		GameService.addNewGame(game, user);
		ViewGeneral.displayFooter();
		playGame(game);
	}
	
	
	public void playGame(Game game){
//		PlayingStrategy playingStrategy = new PlayingStrategyPredictable(Round.Offer.STAND, BigDecimal.valueOf(1), false);
		//Game game = new Game(playingStrategy, new Deck());
		do {
			GameService.playGame(user, game);
			//you end up here only if game is finished(any strategy) or if input is needed (interactive strategy only).
			
			//	interactive case, game not finished
			if(game.isInteractive() && game.userInputNeeded){
				PlayingStrategy strategy = game.playingStrategy;
				Round round = game.currentRound;
				if(Round.RoundStatus.HAND_BEING_DEALT.equals(round.roundStatus)){
					int n = game.pastRounds.size();
					if(n == 0){ // if this is the first round of the game
						ViewGeneral.displayHeader("Interactive game screen");
					}
					else{
						Round previousRound = game.pastRounds.get(n-1);
						View.displayRoundDetails(previousRound);
					}
					strategy.setAmountBet(View.getAmountBet(game.gameID, game.currentRound.roundNumber));
					ViewGeneral.display("Round started.");
				}
				else{
					strategy.setResponseToOffer(View.getResponseToOffer(round.availableOffers, round.dealerHand, round.currentHand));
				}
			}
			
			// interactive case, game finished
			if(game.isInteractive() && !game.userInputNeeded){
				int n = game.pastRounds.size();
				View.displayRoundDetails(game.pastRounds.get(n-1));
				ViewGeneral.displayFooter();
			}
			
			//	non-interactive case, game finished
			if(!game.isInteractive()){
				ViewGeneral.displayHeader("Non-interactive game screen");
				View.displayGameDetails(game);
				ViewGeneral.displayFooter();
			}
		}
		while(game.userInputNeeded);
		
		
	} 
}
