package com.skoev.blackjack2.model.game;

import java.math.BigDecimal;
import java.util.Collection;

import com.skoev.blackjack2.model.game.Round.Offer;
import com.skoev.blackjack2.util.Util;

/**
 * Implements a simple automated playing strategy. This strategy is fixed because it always returns the same responses (which are set in the constructor).  
 * 
 * */
public class PlayingStrategyFixed extends PlayingStrategy {
	private Round.Offer defaultOffer;
	private BigDecimal defaultBet;
	private boolean acceptInsurace; 
	
	/**
	 * @param defaultOffer The offer that will always be chosen. Must be one of the offers that are always available (HIT, STAND, DOUBLE)
	 * @param defaultBet The bet that will always be made
	 * @param acceptInsurace 
	 */
	public PlayingStrategyFixed(Round.Offer defaultOffer, BigDecimal defaultBet, boolean acceptInsurace){
		Util.assertNotNull(defaultOffer);
		Util.assertNotNull(defaultBet);
		Util.assertTrue(defaultOffer.equals(Offer.DOUBLE) || defaultOffer.equals(Offer.HIT) || defaultOffer.equals(Offer.STAND));
		this.defaultOffer = defaultOffer;
		this.defaultBet = defaultBet;
		this.acceptInsurace = acceptInsurace;
	}
	
	@Override
	public BigDecimal respondToAmountBet() {
		return defaultBet;
	}

	@Override
	public Round.Offer respondToOffer(Collection<Round.Offer> offers, Hand hand, Hand dealerHand) {
		Util.assertNotNull(offers);
		Round.Offer response = null;
		if (offers.contains(Round.Offer.ACCEPT_INSURANCE)){
			if(acceptInsurace){
				response = Offer.ACCEPT_INSURANCE;
			}
			else{
				response = Offer.DECLINE_INSURANCE;
			}
		}
		else if (offers.contains(defaultOffer)){
			response = defaultOffer ;
		}
		else { 
			throw new IllegalArgumentException();
		}
		return response;
	}
	
	@Override
	public String toString() {
		return "PlayingStrategyFixed [defaultOffer=" + defaultOffer
				+ ", defaultBet=" + defaultBet + ", acceptInsurace="
				+ acceptInsurace + "]";
	}
	
	

}
