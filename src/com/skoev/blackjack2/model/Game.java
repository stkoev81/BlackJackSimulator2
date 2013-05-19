package com.skoev.blackjack2.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
/**
 *  
 * 
 * todo normal - fix the game id (global) and round ids (local, within each game).
 * @author stefan.t.koev
 *
 */
public class Game {
	public int gameID = 1; 
	public int numRoundsPlayed = 0;
	public int numRoundsToPlay = 5;
	public BigDecimal moneyStart = BigDecimal.valueOf(100);
	public BigDecimal moneyCurrent = BigDecimal.valueOf(100);
	public Round currentRound;
	public List<Round> pastRounds = new ArrayList();
	public PlayingStrategy playingStrategy;
	public Deck deck = new Deck();
	public boolean userInputNeeded = false;
	
	public Game(PlayingStrategy playingStrategy, Deck deck){
		this.playingStrategy = playingStrategy;
		this.deck = deck;
	}
	
	public void playGame(){
		if (isFinished()){
			throw new RuntimeException("this game is already finished. Cannot continue playing");
		}
		userInputNeeded = false;
		while (!isFinished() && !userInputNeeded){
			if (currentRound == null){
				currentRound = new Round(this);
				currentRound.roundNumber = numRoundsPlayed +1;
			}
			currentRound.playRound(playingStrategy);
			if(!userInputNeeded){
				numRoundsPlayed ++;
				pastRounds.add(currentRound);
				
				currentRound = null; //round is finished
				
				
			}
		}
	}
	
	public boolean isFinished(){
		return numRoundsPlayed >= numRoundsToPlay || moneyCurrent.doubleValue() <= 0;
	}
	
	public void addMoney(BigDecimal money) {
		moneyCurrent = moneyCurrent.add(money);
	}
	public void subtractMoney(BigDecimal money){
		moneyCurrent = moneyCurrent.subtract(money);
	}
	
	public Card dealCard(){
		return deck.nextCard(); 
	}

	//todo normal: if the game is finished vs. if the game is not finished, display differently.
	@Override
	public String toString() {
		return "--Game " + gameID + "\n" 
				+ "Game settings: playingStrategy=" + playingStrategy +  ", moneyStart=" + moneyStart + "\n\n"
				 
				+ Util.toCollString("", pastRounds, "\n") 
				 
				+ "Game ends. numRoundsPlayed=" + numRoundsPlayed	+  ", moneyCurrent=" + moneyCurrent + "\n" ;
				 
				
	}
	
	

}
