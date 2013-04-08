package com.skoev.blackjack2.model;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
/**
 *  
 * - todo normal: add constructors
 * - todo basic: check numround before playing
 * @author stefan.t.koev
 *
 */
public class Game {

	public int numRoundsPlayed = 0;
	public int numRoundsToPlay = 100; 
	public BigDecimal moneyCurrent = BigDecimal.valueOf(100);
	public Round currentRound;
	public Collection<Round> pastRounds = Collections.EMPTY_LIST;
	public PlayingStrategy playingStrategy = new PlayingStrategy();
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
		while (!isFinished() && !userInputNeeded){
			if (currentRound == null){
				currentRound = new Round(this);
			}
			currentRound.playRound(playingStrategy);
			if(!userInputNeeded){
				currentRound = null; //round is finished
				numRoundsPlayed ++;
				pastRounds.add(currentRound);
			}
		}
	}
	
	public boolean isFinished(){
		return (numRoundsPlayed >= numRoundsToPlay) && moneyCurrent.doubleValue() <= 0;
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
