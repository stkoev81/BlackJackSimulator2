package com.skoev.blackjack2.controller;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Map;
import java.util.Observable;

import com.skoev.blackjack2.model.*;
import com.skoev.blackjack2.view.*;


public class GameController {
	
	public static void main(String[] args){
		new GameController().playSingleGame();
	}
		
	public void playSingleGame(){
//		PlayingStrategy playingStrategy = new PlayingStrategyPredictable(Round.Offer.STAND, BigDecimal.valueOf(1), false);
		PlayingStrategy playingStrategy = new PlayingStrategyInteractive();
		Game game = new Game(playingStrategy, new Deck());
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
