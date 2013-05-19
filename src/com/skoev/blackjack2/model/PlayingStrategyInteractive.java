package com.skoev.blackjack2.model;

import java.math.BigDecimal;
import java.util.Collection;

/**
 * todo normal - create a hierarchy of this forseveral types of palying strategies
 * 
 * @author stefan.t.koev
 *
 */
public class PlayingStrategyInteractive implements PlayingStrategy {
	public BigDecimal amountBet;
	public Round.Offer responseToOffer;
	
	@Override
	public BigDecimal respondToAmountBet(){
		BigDecimal result = amountBet;
		amountBet = null;
		return result;
	}
	@Override
	public Round.Offer respondToOffer(Collection<Round.Offer> offers, Hand hand, Hand dealerHand){
		Round.Offer result = responseToOffer;
    responseToOffer = null;
		return result;
	}
	@Override
	public boolean isInteractive() {
		return true;
	}
	@Override
	public String toString() {
		return "PlayingStrategyInteractive";
	}
	
	
}
