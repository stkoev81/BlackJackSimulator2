package com.skoev.blackjack2.model.game;

import java.math.BigDecimal;
import java.util.Collection;

import com.skoev.blackjack2.model.game.Round.Offer;

public class PlayingStrategyThreshold extends PlayingStrategy {
	private BigDecimal defaultBet;
	private int threshold;
	
	public PlayingStrategyThreshold(int threshold, BigDecimal defaultBet){
		this.threshold = threshold;
		this.defaultBet = defaultBet;
	}
	
	@Override
	public BigDecimal respondToAmountBet() {
		return defaultBet;
	}

	@Override
	public Round.Offer respondToOffer(Collection<Round.Offer> offers, Hand hand, Hand dealerHand) {
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
	public boolean isInteractive() {
		return false;
	}

	@Override
	public String toString() {
		return "PlayingStrategyThreshold [threshold=" + threshold
				+ ", defaultBet=" + defaultBet + "]";
	}
	
	

}
