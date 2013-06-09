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
		String status; 
		if(game.isFinished()){
			status = "complete";  
		}
		else{
			status = "incomplete";
		}
		display("======= Game " + game.gameID + " =======");
		display("status="  + status +  ", moneyStart="+ game.moneyStart + ", moneyCurrent=" + game.moneyCurrent + ", strategy=" + game.playingStrategy);
		display("==================="); 
	}
	
	public static void displayGameDetails(Game game){
		displayGameSummary(game);
		display("Game beginning.");
		for(Round round : game.pastRounds){
			displayRoundDetails(round);
		}
		if(game.isFinished()){
			display("Game end. numRoundsPlayed=" + game.numRoundsPlayed +  ", moneyCurrent=" + game.moneyCurrent);
		}
	}
	
	public static void displayRoundDetails(Round round){
		display("---------Round "+ round.roundNumber + " results-----------");
		display("moneyStart=" + round.moneyStart + ", moneyEnd=" + round.moneyEnd
		+ ", roundStatus=" + round.roundStatus);
		for(Hand hand : round.hands){
			display(hand);
		}
		display(round.dealerHand);
		display("---------------------------------------------");
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
