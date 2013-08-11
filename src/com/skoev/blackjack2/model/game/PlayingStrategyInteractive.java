package com.skoev.blackjack2.model.game;

import java.math.BigDecimal;
import java.util.Collection;

import com.skoev.blackjack2.common.Util;
import com.skoev.blackjack2.model.game.Round.Offer;

/**
 * Implements and interactive playing strategy. No automated choices are made by the player. The choices must be obtained from user input and set in this class before 
 * this class can be used to make player choices. 
 */
public class PlayingStrategyInteractive extends PlayingStrategy {
	private BigDecimal amountBet;
	private Round.Offer responseToOffer;
	private boolean userInputNeeded = false; 
	
	
	/**
	 * Returns the value previously set with {@link #respondToAmountBet()}, null if not set. Subsequent calls will return null until a value is set 
	 * again. 
	 */
	@Override
	public BigDecimal respondToAmountBet(){
		BigDecimal result = amountBet;
		amountBet = null;
		if(result == null){
			userInputNeeded = true;
		}
		return result;
	}
	/**
	 * Returns the value previously set with {@link #respondToOffer(Collection, Hand, Hand)}, null if not set. Subsequent calls will return null until a value is set 
	 * again. 
	 */
	@Override
	public Round.Offer respondToOffer(Collection<Round.Offer> offers, Hand hand, Hand dealerHand){
		Round.Offer result = responseToOffer;
		responseToOffer = null;
		if(result == null){
			userInputNeeded = true;
		}
		return result;
	}
	
	/**
	 * @return true 
	 */
	@Override
	public boolean isInteractive() {
		return true;
	}
	
	
	@Override
	public String toString() {
		return "PlayingStrategyInteractive";
	}
	/**
	 * Sets the value that the next call to respondToAmountBet() will return . 
	 */
	@Override
	public void setAmountBet(BigDecimal amountBet) {
		this.amountBet = amountBet;
		if(amountBet != null){
			userInputNeeded = false;
		}
	}
	
	/**
	 * Sets the value that the next call to respondToOffer() will return 
	 */
	@Override
	public void setResponseToOffer(Offer responseToOffer) {
		this.responseToOffer = responseToOffer;
		if(responseToOffer != null){
			userInputNeeded  = false;
		}
	}
	
	/**
	 * Returns true if the next call to {@link #respondToAmountBet()} or {@link #respondToOffer(Collection, Hand, Hand)} will return null, i.e. if the
	 * player choices need to be set before those methods can be used. 
	 */
	@Override
	public boolean isUserInputNeeded() {
		return userInputNeeded;
	}
	
	@Override
	void setIsUserInputNeeded(boolean userInputNeeded){
		this.userInputNeeded = userInputNeeded;
	 }
	
}
