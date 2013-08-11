package com.skoev.blackjack2.presentation;

import com.skoev.blackjack2.model.game.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

/**
 * Contains methods to interact with the user about specific items. 
 */
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
		String status; 
		if(game.isFinished()){
			status = "complete";  
		}
		else{
			status = "incomplete";
		}
		display("======================= Game " + game.getGameID() + " =======================");
		display("status="  + status +  ", moneyStart="+ game.getMoneyStart() + ", moneyCurrent=" + game.getMoneyCurrent() + ", strategy=" + game.getPlayingStrategy());
		display("======================================================"); 
	}
	
	public static void displayGameDetails(Game game){
		displayGameSummary(game);
		display("Game beginning.");
		for(Round round : game.getPastRounds()){
			displayRoundDetails(round);
		}
		if(game.isFinished()){
			display("Game end. numRoundsPlayed=" + game.getNumRoundsPlayed() +  ", moneyCurrent=" + game.getMoneyCurrent());
		}
	}
	
	public static void displayRoundDetails(Round round){
		display("------------------ Round "+ round.getRoundNumber() + " results -------------------");
		display("moneyStart=" + round.getMoneyStart() + ", moneyEnd=" + round.getMoneyEnd()
		+ ", roundStatus=" + round.getRoundStatus());
		for(Hand hand : round.getHands()){
			display(hand);
		}
		display(round.getDealerHand());
		display("------------------------------------------------------");
	}
		

	public static Round.Offer getResponseToOffer(List<Round.Offer> availableOffers, Hand dealerHand, Hand currentHand){
		String message = "User input needed for the following hand: \n";
		message += "\t" + currentHand + "\n";
		message += "\t" + dealerHand;
		return getOption(availableOffers.toArray(new Round.Offer[0]), message); 
	}
	
	public static BigDecimal getAmountBet(int gameNumber, int roundNumber){
		String message = "Starting game " + gameNumber + ", round " + roundNumber + ". Enter bet amount:";
		return getAmount(message);
	}
	
}
