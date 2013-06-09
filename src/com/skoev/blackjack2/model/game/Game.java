package com.skoev.blackjack2.model.game;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
/**
 *  
 * 
 * @author stefan.t.koev
 *
 */
public class Game {
	public int gameID = 1; 
	public int numRoundsPlayed; 
	public int numRoundsToPlay; 
	public BigDecimal moneyStart;
	public BigDecimal moneyCurrent;
	public Round currentRound;
	public List<Round> pastRounds = new ArrayList();
	public PlayingStrategy playingStrategy;
	public Deck deck = new Deck();
	public boolean userInputNeeded = false;
	
	public boolean isInteractive(){
		return playingStrategy.isInteractive();
	}
	
	public Game(PlayingStrategy playingStrategy, int numRoundsToPlay, BigDecimal moneyStart){
		this.moneyStart = moneyStart;
		this.moneyCurrent = moneyStart;
		this.playingStrategy = playingStrategy;
		this.numRoundsToPlay = numRoundsToPlay;
	}
	
	
	//todo normal: add getters and setters and set proper visibility
	//todo normal: comment
	//todo normal: add more tests
	//todo normal: verify model validations (e.g. not playing if not enough money). 
	public void play(){
		if (isFinished()){
			throw new RuntimeException("this game is already finished. Cannot continue playing");
		}
		userInputNeeded = false;
		while (!isFinished() && !userInputNeeded){
			if (currentRound == null){
				currentRound = new Round(this);
				currentRound.roundNumber = numRoundsPlayed +1;
			}
			currentRound.play(playingStrategy);
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

}
