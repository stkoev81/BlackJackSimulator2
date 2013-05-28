package com.skoev.blackjack2.model.game;

import java.math.BigDecimal;
import java.util.Collection;

public abstract class PlayingStrategy {

	 abstract BigDecimal respondToAmountBet();

	 abstract Round.Offer respondToOffer(Collection<Round.Offer> offers, Hand hand, Hand dealerHand);
	
	 abstract boolean isInteractive();
	 
	 public void setAmountBet(BigDecimal amountBet) {
		 throw new IllegalStateException();	 
	 }

	 public void setResponseToOffer(Round.Offer responseToOffer){
		 throw new IllegalStateException();	 
	 }
	 
	 

}