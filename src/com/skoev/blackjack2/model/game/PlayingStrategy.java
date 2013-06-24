package com.skoev.blackjack2.model.game;

import java.math.BigDecimal;
import java.util.Collection;

/**
 * Subclasses of this class are used to model the behavior of the blackjack player. Some subclasses can represent automated strategies, where the choices of the
 * player are automatically determined and no user input is needed. Others can represent interactive strategies, where user input is needed to determine the 
 * player's choices.   
 *
 */
public abstract class PlayingStrategy {

	/**
	 * Returns the amount bet on a blackjack round by the player. 
	 */
	 abstract BigDecimal respondToAmountBet();

	 /**
	  * Returns the offer chosen by the player for given hand
	  * @param offers The available offers that the player can choose from
	  * @param hand The player's hand for which the offers are made
	  * @param dealerHand The corresponding dealer's hand for that player's hand. 
	  */
	 abstract Round.Offer respondToOffer(Collection<Round.Offer> offers, Hand hand, Hand dealerHand);
	
	 /**
	  * Is the current strategy of the interactive or automated type
	  * @return always false; subclasses that implement interactive strategies must override this.
	  */
	 public boolean isInteractive(){
		 return false;
	 }
	 
	 /**
	  * Determines if user input is needed before calls can be made to {@link #respondToAmountBet()} and {@link #respondToOffer(Collection, Hand, Hand)} 
	  * @return always false; subclasses that implement interactive strategies must override this
	  */
	 public boolean isUserInputNeeded(){
		 return false;
	 }
	 
	 /**
	  * This method is valid only for interactive strategies and should be overrided by them. 
	  * @throws UnsupportedOperationException
	  */
	 public void setAmountBet(BigDecimal amountBet) {
		 throw new UnsupportedOperationException();	 
	 }

	 /**
	  * This method is valid only for interactive strategies and should be overrided by them. 
	  * @throws UnsupportedOperationException
	  */
	 public void setResponseToOffer(Round.Offer responseToOffer){
		 throw new UnsupportedOperationException();	 
	 }
	 /**
	  * This method is valid only for interactive strategies and should be overrided by them. 
	  * @throws UnsupportedOperationException
	  */
	 void setIsUserInputNeeded(boolean userInputNeeded){
		 throw new UnsupportedOperationException();	 
	 }
	 
	 
	 
	 

}