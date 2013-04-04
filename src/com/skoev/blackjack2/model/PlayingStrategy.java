package com.skoev.blackjack2.model;

import java.math.BigDecimal;

/**
 * todo normal - create a hierarchy of this forseveral types of palying strategies
 * 
 * @author stefan.t.koev
 *
 */
public class PlayingStrategy {
	public BigDecimal amountBet;
	public Round.Offer responseToOffer;
	
	public BigDecimal respondToAmountBet(){
		return amountBet;
	}
	public Round.Offer respondToOffer(){
		return responseToOffer;
	}
}
