package com.skoev.blackjack2.presentation;
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
	private User user; 
	
	public static void main(String[] args){
		new Controller().startApplication();
	}
	
	private static enum Option{LOGIN, HELP, CREATE_ACCOUNT, START_GAME, EXIT, VIEW_DETAILS, START_NEW, LOG_OUT, DELETE_GAME, CONTINUE_GAME, HOME_SCREEN}
	
	private static enum Strategy{INTERACTIVE, FIXED, THRESHOLD, TEXTBOOK}
	
	public void startApplication(){
		boolean end = false; 
		while(!end){
			try{
				ViewGeneral.displayHeader("Main screen"); 
				String message = "This is a blackjack simulation game.";
				Option[] options = {Option.HELP, Option.LOGIN, Option.CREATE_ACCOUNT, Option.EXIT};
				Option option = ViewGeneral.getOption(options, message);
				ViewGeneral.displayFooter();
				switch (option){
				case LOGIN : 
					logIn();
					break;
				case HELP : 
					showHelp();
					break;
				case CREATE_ACCOUNT :
					createAccount();
					break;
				case EXIT :
					end = true;
					break;
				}
			}
			catch (UserCanceledActionException e){
				//stay on login screen
			}
		}
	}
	
	private void showHelp(){
		ViewGeneral.displayHeader("Help screen");
		// todo: add to the help message
		String message = "This is a blackjack game help"; 
		ViewGeneral.display(message);
		ViewGeneral.displayFooter();
	}
 
	private void logIn(){
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
	
	private void createAccount(){
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
	
	private void goToHomeScreen(){
		boolean end = false;
		while (!end){
			Option option = null; 
			try{
				ViewGeneral.displayHeader("User's home screen");
				View.displayGameSummary(GameService.getGames(user));
				String message = "You can view (and continue if inccomplete) an existing game or start a new one.";
				Option[] options = {Option.LOG_OUT, Option.VIEW_DETAILS, Option.START_NEW};
				option = ViewGeneral.getOption(options, message);
				switch(option){
					case LOG_OUT:
						end = true;
						user = null;
						ViewGeneral.displayFooter();
						break;
					case VIEW_DETAILS:
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
					case START_NEW:
						ViewGeneral.displayFooter();
						startNewGame();
						break;
				}
			}
			catch (UserCanceledActionException e){
			}
		}
	} 
	
	private void viewGameDetails(int gameId){
		View.displayHeader("Game details screen");
		Game game = GameService.getGame(gameId, user);
		View.displayGameDetails(game);
		String message = "You can delete or continue (if incomplete) the game";
		Option[] options = {Option.HOME_SCREEN, Option.DELETE_GAME, Option.CONTINUE_GAME};
		Option option = ViewGeneral.getOption(options, message);
		switch(option){
			case HOME_SCREEN: 
				ViewGeneral.displayFooter();
				break;
			case DELETE_GAME:
				GameService.deleteGame(gameId, user);
				ViewGeneral.display("Deleted game " + gameId);
				ViewGeneral.displayFooter();
				break;
			case CONTINUE_GAME:
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
	
	private void startNewGame(){
		Strategy[] options = {Strategy.INTERACTIVE, Strategy.FIXED, Strategy.THRESHOLD, Strategy.TEXTBOOK};
		String message = "You must choose a game type.";
		ViewGeneral.displayHeader("Start new game screen");
		Strategy option = ViewGeneral.getOption(options, message);
		
		PlayingStrategy playingStrategy = null;
		BigDecimal money = ViewGeneral.getAmount("Enter starting money:");
		int numRounds = ViewGeneral.getPositiveInteger("Enter number of rounds to play:");
		
		if(option.equals(Strategy.INTERACTIVE)){
			playingStrategy = new PlayingStrategyInteractive();
		} 
		
		if(option.equals(Strategy.FIXED)){
			message = "Accept insurance?";
			Boolean acceptInsurance =  ViewGeneral.getOption(new Boolean[]{Boolean.TRUE, Boolean.FALSE}, message);
			message = "Default response to offer?";
			Round.Offer defaultOffer = ViewGeneral.getOption(new Round.Offer[]{Round.Offer.STAND, Round.Offer.HIT, Round.Offer.DOUBLE }, message);
			message = "Default amount of bet?"; 
			BigDecimal defaultBet = ViewGeneral.getAmount(message);
			playingStrategy = new PlayingStrategyFixed(defaultOffer, defaultBet, acceptInsurance);
		}
		
		if(option.equals(Strategy.THRESHOLD)){
			message = "Threshold?";
			int threshold =  ViewGeneral.getPositiveInteger(message);
			message = "Amount of bet?"; 
			BigDecimal defaultBet = ViewGeneral.getAmount(message);
			playingStrategy = new PlayingStrategyThreshold(threshold, defaultBet);
		}
		
		if(option.equals(Strategy.TEXTBOOK)){
			message = "Amount of bet?"; 
			BigDecimal defaultBet = ViewGeneral.getAmount(message);
			playingStrategy = new PlayingStrategyTextbook(defaultBet);
		} 
		
		Game game = new Game(playingStrategy, numRounds, money);
		GameService.addNewGame(game, user);
		ViewGeneral.displayFooter();
		playGame(game);
	}
	
	private void playGame(Game game){
		do {
			try{
				GameService.playGame(user, game);
			}
			catch(InsufficientMoneyException e){
				ViewGeneral.display("Error! Player tried to make a bet that exceeds money available. Cannot continue");
			}
			//you end up here only if game is finished(any strategy) or if input is needed (interactive strategy only) or if insufficient money for bet
			
			//	interactive case, game not finished
			if(game.isInteractive() && !game.isFinished() && game.isUserInputNeeded()){
				PlayingStrategy strategy = game.getPlayingStrategy();
				Round round = game.getCurrentRound();
				if(Round.RoundStatus.HAND_BEING_DEALT.equals(round.getRoundStatus())){
					int n = game.getPastRounds().size();
					if(n == 0){ // if this is the first round of the game
						ViewGeneral.displayHeader("Interactive game screen");
					}
					else{
						View.displayRoundDetails(game.getLastRound());
					}
					strategy.setAmountBet(View.getAmountBet(game.getGameID(), round.getRoundNumber()));
					ViewGeneral.display("Round started.");
				}
				else{
					strategy.setResponseToOffer(View.getResponseToOffer(round.getAvailableOffers(), round.getDealerHand(), round.getCurrentHand()));
				}
			}
			// interactive case, game finished
			else if(game.isInteractive() && game.isFinished()){
				View.displayRoundDetails(game.getLastRound());
				ViewGeneral.displayFooter();
			}
			//	non-interactive case, game finished
			else if(!game.isInteractive()){
				ViewGeneral.displayHeader("Non-interactive game screen");
				View.displayGameDetails(game);
				ViewGeneral.displayFooter();
			}
		}
		while(game.isInteractive() && !game.isFinished());
	} 
}
