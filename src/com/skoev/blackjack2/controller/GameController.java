package com.skoev.blackjack2.controller;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Map;
import java.util.Observable;

import com.skoev.blackjack2.model.*;



public class GameController {
	
	public static void main(String[] args){
		new GameController().playSingleGame();
	}
		
	public void playSingleGame(){
		PlayingStrategy playingStrategy = new PlayingStrategyPredictable(Round.Offer.STAND, BigDecimal.valueOf(1), true);
		Game game = new Game(playingStrategy, new Deck());
		do {
			game.playGame();
			//e.g. previous Hands, previous results. 
			
			//if user input Neede (or if playing strategy is intractive), print message prompting to enter input (how do you know what type of input is needed? -- see round state )
			// if it is a bet amound needed, include in prompt the current stake, etc.; but first, show the conclusion of the previous round, if any. When do you show the 
			// outcome of the last round? - no user input is neede there, so how would you know if it's a case of interactive or automated? Maybe if PlayingStrategy.isInteractive?
			// if it is an insurance needed, include in the prompt the dealer's card and current hand
			// if it is an offer reposnse needed, include the current hand dealt and the possible options. 
			//read in that input
			//set the response in the playing strategy
			
			
			//if playing strategy is not interactive and uesr input is needed, throw exception.
			
			//if user input is not needed, this implies that the simulation is finished. Print everthing to date -- all rounds, all hands, all hands history.
			// todo next: do this part because it seem simpler; for the user input needed case, you need to keep track of what you showed. Will need to write a predictable player for this one first so it can buidl up some history.
			// need to add constructors everywhere to initizlize things properly, populate the historic datastructures properly, and then build to string methods or view method to display them. 
		}
		while(game.userInputNeeded);
	}
			
}
