package com.skoev.blackjack2.controller;
import java.util.Arrays;
import java.util.Map;
import java.util.Observable;

import com.skoev.blackjack2.model.*;



public class GameController {
	
	public static void main(String[] args){
		new GameController().playSingleGame();
	}
		
	public void playSingleGame(){
		
		Game game = new Game(new PlayingStrategy(), new Deck());
		do {
			game.playGame();
		}
		while(game.userInputNeeded);
	}
			
}
