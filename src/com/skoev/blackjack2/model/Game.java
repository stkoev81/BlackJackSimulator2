package com.skoev.blackjack2.model;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
/**
 *  
 * - todo normal: add constructors
 * - todo basic: check numround before playing
 * - todo basic: implement lose money
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
		
	public boolean playGame(){
		if (currentRound == null){
			currentRound = new Round(this);
		}
		if(currentRound.playRound(playingStrategy)){
			numRoundsPlayed ++;
			pastRounds.add(currentRound);
			currentRound = null; //round is finished 
			return true;
		}
		else {
			return false;
		}
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
