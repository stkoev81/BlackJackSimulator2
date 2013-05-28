package com.skoev.blackjack2.view;

import com.skoev.blackjack2.controller.GameController;
import com.skoev.blackjack2.model.game.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
//todo normal: refactor all the methods getting user response to use common code 
public class GameView extends GameViewGeneral{
	
	
	
	
	
	public static void displayGameSummary(Collection<Game> games){
		display("These are your games: ");
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
