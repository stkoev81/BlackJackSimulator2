package com.skoev.blackjack2.model.game;

import java.math.BigDecimal;
import java.util.Collection;

import com.skoev.blackjack2.model.game.Round.Offer;

/**
 * todo advanced - create a hierarchy of this forseveral types of palying strategies
 * 
 * @author stefan.t.koev
 *
 */
public class PlayingStrategyInteractive extends PlayingStrategy {
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
		return "PlayingStrategyFixed";
	}
	@Override
	public void setAmountBet(BigDecimal amountBet) {
		this.amountBet = amountBet;
	}
	@Override
	public void setResponseToOffer(Offer responseToOffer) {
		this.responseToOffer = responseToOffer;
	}
	
	
	
	
}
