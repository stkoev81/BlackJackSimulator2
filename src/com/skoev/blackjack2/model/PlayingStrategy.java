package com.skoev.blackjack2.model;

import java.math.BigDecimal;
import java.util.Collection;

public interface PlayingStrategy {

	 BigDecimal respondToAmountBet();

	 Round.Offer respondToOffer(Collection<Round.Offer> offers, Hand hand, Hand dealerHand);
	
	 boolean isInteractive();

}