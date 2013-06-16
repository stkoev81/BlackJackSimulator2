package com.skoev.blackjack2.model.game;

import java.math.BigDecimal;
import java.util.Collection;

import com.skoev.blackjack2.model.game.Round.Offer;

public class PlayingStrategyFixed extends PlayingStrategy {
	private Round.Offer defaultOffer;
	private BigDecimal defaultBet;
	private boolean acceptInsurace; 
	
	public PlayingStrategyFixed(Round.Offer defaultOffer, BigDecimal defaultBet, boolean acceptInsurace){
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
			throw new RuntimeException("The predictable strategy received an offer that it doesn't know what to do with");
		}
		return response;
	}

	@Override
	public boolean isInteractive() {
		return false;
	}

	@Override
	public String toString() {
		return "PlayingStrategyPredictable [defaultOffer=" + defaultOffer
				+ ", defaultBet=" + defaultBet + ", acceptInsurace="
				+ acceptInsurace + "]";
	}
	
	

}
