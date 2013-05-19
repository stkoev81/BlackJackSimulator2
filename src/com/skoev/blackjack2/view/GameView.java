package com.skoev.blackjack2.view;

import com.skoev.blackjack2.model.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
//todo normal: refactor all the methods getting user response to use common code 
public class GameView {
	private static BufferedReader br; 
	
	static {
		//It's OK to leave this buffered reader open because the underlying stream, System.in, is always open anyway.
		br = new BufferedReader(new InputStreamReader(System.in));
	}
	
	public static PlayingStrategy playingStrategy = new PlayingStrategyPredictable(Round.Offer.HIT, BigDecimal.valueOf(1), false);

	public static Round.Offer getResponseToOffer(List<Round.Offer> availableOffers, Hand dealerHand, Hand currentHand){
		//todo basic: print the dealer's hand and the current hand;
		System.out.println("User input needed for the following hand: ");
		System.out.print("\t" + currentHand);
		System.out.print("\t" + dealerHand);
		System.out.println("Please choose one of the following offers by entering the number next to it:");
		int i = 1; 
		for(Round.Offer offer : availableOffers){
			System.out.println("\t" + "(" + i + ")" + " " + offer);
			i++;
		}
		
		int offerNum = 0;
		Round.Offer result = null;
		while(result == null){
			try{
				String response = br.readLine();
				if (isEmptyResponse(response)){
					break;
				}
				offerNum = Integer.parseInt(response);
				result = availableOffers.get(offerNum - 1);
			}
			catch(IOException e){
				System.out.println("Error! : there was an unknown problem reading response. If problem persists, contact support. ");
			}
			catch(NumberFormatException e){
				System.out.println("Error! : the response you entered was not formatted correclty. Must be an integer. Try again. ");
			}
			catch(java.lang.IndexOutOfBoundsException e ){
				System.out.println("Error! : the number you entered is not a valid offer number. Try again. ");
			}
		}
		return result;
		
	}
	//.responseToOffer = GameView.getResponseToOffer(round.availableOffers, round.dealerHand, round.currentHand);
	
	public static BigDecimal getAmountBet(int gameNumber, int roundNumber){
		//todo basic: get the amount bet interactively
		System.out.println("Starting game " + gameNumber + ", round " + roundNumber + ". Please enter bet amount.");
		double result = 0;
		while(result == 0){
			try{
				String response = br.readLine();
				if (isEmptyResponse(response)){
					break;
				}
				result = Double.parseDouble(response);
				if (result <= 0){
					throw new IllegalArgumentException();
				}
			}
			catch(IOException e){
				System.out.println("Error! : there was an unknown problem reading response. If problem persists, contact support. ");
			}
			catch(IllegalArgumentException e){
				System.out.println("Error! : the response you entered is invalid. Must be > 0"); 
			}
		}
		return BigDecimal.valueOf(result);
	}
	//interactive.amountBet = GameView.getAmountBet();
	
	public static void printGame(Game game){
		System.out.println(game);
	}
	
	public static void printRound(Round round){
		System.out.println(round);
	}
	
	private static boolean isEmptyResponse(String s){
		if(s == null || s.length() == 0){
			return true;
		}
		else{
			return false;
		}
	}
	
}
