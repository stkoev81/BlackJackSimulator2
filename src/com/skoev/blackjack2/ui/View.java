package com.skoev.blackjack2.ui;

import com.skoev.blackjack2.model.game.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
public class View extends ViewGeneral{
	
	
	
	
	
	public static void displayGameSummary(Collection<Game> games){
		display("These are your games: ");
		if(games.isEmpty()){
			display("\tNo games yet");
		}
		for(Game game : games){
			displayGameSummary(game);
		}
	}
	
	public static void displayGameSummary(Game game){
		//todo basic : display the game summary such as: "game number, status - finished, not finished, waiting for user input, strategy. If finished, won or lost"
		display(game.gameID);
	}
	
	public static void displayGameDetails(Game game){
		display(game);
	}
	
	public static void displayRoundDetails(Round round){
		display(round);
	}
		

	public static Round.Offer getResponseToOffer(List<Round.Offer> availableOffers, Hand dealerHand, Hand currentHand){
		String message = "User input needed for the following hand: \n";
		message += "\t" + currentHand + "\n";
		message += "\t" + dealerHand + "\n";
		return getOption(availableOffers.toArray(new Round.Offer[0]), message); 
	}
	
	public static BigDecimal getAmountBet(int gameNumber, int roundNumber){
		String message = "Starting game " + gameNumber + ", round " + roundNumber + ". Please enter bet amount." + "\n";
		return getAmount(message);
	}
	
}
