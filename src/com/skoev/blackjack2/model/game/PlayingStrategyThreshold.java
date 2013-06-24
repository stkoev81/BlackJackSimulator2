package com.skoev.blackjack2.model.game;

import java.math.BigDecimal;
import java.util.Collection;

import com.skoev.blackjack2.model.game.Round.Offer;
import com.skoev.blackjack2.util.Util;

/**
 * Automated strategy that is based on a threshold. If the hand's value is less than or equal to the threshold, the player chooses HIT; otherwise the 
 * player chooses STAND. Insurance is always declined. This strategy is similar to the dealer's strategy (the dealer uses a threshold of 16).   
 * @author stefan.t.koev
 *
 */

public class PlayingStrategyThreshold extends PlayingStrategy {
	private BigDecimal defaultBet;
	private int threshold;
	
	/**
	 * 
	 * @param threshold The point value threshold for hitting or standing. 
	 * @param defaultBet The bet that will always be made
	 */
	public PlayingStrategyThreshold(int threshold, BigDecimal defaultBet){
		Util.assertTrue(threshold > 0);
		Util.assertNotNull(defaultBet);
		this.threshold = threshold;
		this.defaultBet = defaultBet;
	}
	
	@Override
	public BigDecimal respondToAmountBet() {
		return defaultBet;
	}

	@Override
	public Round.Offer respondToOffer(Collection<Round.Offer> offers, Hand hand, Hand dealerHand) {
		Util.assertNotNull(offers);
		Util.assertNotNull(hand);
		
		Round.Offer response = null;
		if (offers.contains(Round.Offer.DECLINE_INSURANCE)){
			response = Offer.DECLINE_INSURANCE;
		}
		else if (hand.calculateCurrentPoints() > threshold){
			response = Offer.STAND;
		}
		else { 
			response = Offer.HIT;
		}
		return response;
	}


	@Override
	public String toString() {
		return "PlayingStrategyThreshold [threshold=" + threshold
				+ ", defaultBet=" + defaultBet + "]";
	}
	
	

}
