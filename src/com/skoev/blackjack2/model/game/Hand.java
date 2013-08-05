package com.skoev.blackjack2.model.game;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.skoev.blackjack2.util.Util;

/**
 * A hand in the blackjack game. Both players and dealers have hands. A player's hand consists of two or more cards. A dealer's hand 
 * consists of one or more. A player's hand is associated with an amount of money bet. 
 *
 */
public class Hand {
	private int handNumber; // dealer hands will have number 0; player hands will have numbers 1 and up
	private BigDecimal amountBet;
	private List<Card> cards = Collections.EMPTY_LIST;
	private Integer finalPoints;

	private HAND_OUTCOME handOutcome = null; 
	private INSURANCE_OUTCOME insuranceOutcome = null; 
	
	/**
	 * Represents the result of playing the hand  
	 */
	public enum HAND_OUTCOME{
		/**
		 * Player won.
		 */
		WIN,
		/**
		 * Player lost.
		 */
		LOSS,
		/**
		 * Player's value was equal to the dealer's value, so no one won or lost. 
		 */
		PUSH}
	/**
	 * Represents the insurance status of a hand. It is not always offered, but when it is offered it can be declined; if accepted it can be won or lost. 
	 */
	public enum INSURANCE_OUTCOME{NOT_OFFERED, OFFERED, DECLINED, WIN, LOSS}
	
	
	Hand(BigDecimal amountBet, int handNumber, Card ...cardsToAdd){
		if(handNumber < 0 || (handNumber > 1 && amountBet == null) || cardsToAdd == null || cardsToAdd.length == 0){
			throw new IllegalArgumentException();
		}
		this.amountBet = amountBet;
		this.handNumber = handNumber;
		cards = new ArrayList<Card>();
		for(Card card : cardsToAdd){
			cards.add(card);
		}
	}
	
	/**
	 * Calculates points of a hand according to the rules of blackjack. Each numbered card has that number of points, each face card 
	 * has 10 points, and an ACE can have either 1 or 10 points (whichever gives the hand more points without pushing it over 21).   
	 * @return
	 */
	public int calculateCurrentPoints(){
		int valueNonAces = 0; 
		int numAces = 0;
		int tempValue = 0;
		int value = 0;
		for(Card card : cards){
			if(!Rank.ACE.equals(card.getRank())){
				valueNonAces = card.getValue() + valueNonAces;
			}
			else {
				numAces++; 
			}
		}
		
		for(int i=0; i<=numAces; i++){
			tempValue = i*11 + (numAces-i)*1 + valueNonAces;
			if(tempValue <= 21 || i == 0){
				value = tempValue;
			}
			if(tempValue > 21){
				break;
			}
		}
		return value; 
	}
	
	Card getFirstCard(){
		return cards.get(0);
	}
	
	boolean firstCardIsAce(){
		return getFirstCard().getRank().equals(Rank.ACE);
	}
	/**
	 * A hand can be split if it has two cards and they both have the same rank. 
	 */
	boolean isEligibleForSplit(){
		if(cards.size() == 2 && (cards.get(0).getRank().equals(cards.get(1).getRank()))){
			return true;
		}
		return false; 
	}
	
	/**
	 * Splits a hand to create two hands. Modifies this hand and returns new hand. This hand's card cards parameters passed in are used to complete two resulting hands. 
	 * @param card1 The second card of the current hand after the split
	 * @param card2 The second card of the new new hand after the split. 
	 * @param handNumber The hand number of the new hand
	 * @return the new hand. 
	 */
	Hand split(Card card1, Card card2, int handNumber){
		if(card1 == null || card2 == null || handNumber < 1 || handNumber == this.handNumber){
			throw new IllegalArgumentException();
		}
		if(!isEligibleForSplit()){
			throw new RuntimeException("Splitting is not allowed for this hand");
		}
		
		Hand hand1 = this; 
		Hand hand2 = new Hand(amountBet, handNumber, card1, card2);
		Card hand1Card2 = hand1.cards.get(1);
		// hand 1 is card one from original hand with a new card
		hand1.cards.set(1, hand2.cards.get(0));
		// hand 2 is card 2 from the original hand with a new card
		hand2.cards.set(0, hand1Card2);
		return hand2;
	}
	
	void addCard(Card card){
		Util.assertNotNull(card);
		cards.add(card);
	}

	public BigDecimal getAmountBet(){
		return amountBet;
	}
	
	void setAmountBet(BigDecimal amountBet) {
		Util.assertNotNull(amountBet);
		this.amountBet = amountBet;
	}

	/**
	 * Return the amount to be won. This includes the amount bet plus the payoff of the bet.   
	 */
	BigDecimal getAmountToBeWon(){
		BigDecimal result; 
		if(calculateCurrentPoints() == 21){
			//won 1.5*amountBet + amountBet
			result = amountBet.multiply(BigDecimal.valueOf(2.5));
		}
		else{
			//won 1* amountBet + amountBet
			result = amountBet.multiply(BigDecimal.valueOf(2));
		} 
		return result;
			
	}

	BigDecimal getInsuranceAmountBet(){
		return amountBet.divide(BigDecimal.valueOf(2));
	}
	/**
	 * Amount to be won if insurance is won. This include the amount paid for insurance plus the payoff of the insurance.  
	 * 
	 */
	BigDecimal getInsuranceAmountToBeWon(){
		BigDecimal result = getInsuranceAmountBet().multiply(BigDecimal.valueOf(2));
		result = result.add(getInsuranceAmountBet());
		return result;
	}

	void setInsuranceOutcome(INSURANCE_OUTCOME insuranceOutcome) {
		Util.assertNotNull(insuranceOutcome);
		this.insuranceOutcome = insuranceOutcome;
	}

	@Override
	public String toString() {
		String cardsString = "";
		String handString = "";
		for(Card card: cards){
			cardsString = cardsString + card;
		}
	
		if(handNumber != 0){
			handString = "Player hand " + handNumber + ": amountBet=" + amountBet + ", cards=" + cardsString;
			if(finalPoints != null && handOutcome != null){
				handString += ", finalPoints=" + finalPoints + ", handOutcome=" + handOutcome; 
			}
			if(insuranceOutcome != null){
				handString += ", insurance=" + insuranceOutcome;
			}
		}
		else{
			handString = "Dealer hand:" + " cards=" + cardsString;
			if(finalPoints != null && handOutcome != null){
				handString += ", finalPoints=" + finalPoints;
			}
			if(insuranceOutcome != null){
				handString += ", insurance=" + insuranceOutcome;
			}
		}
		return handString;
		
		
	}

	void setHandOutcome(HAND_OUTCOME handOutcome) {
		Util.assertNotNull(handOutcome);
		this.handOutcome = handOutcome;
	}
	
	/**
	 * Returns the final points of the hand. This is different from the current points. The current points are always available, even for hands
	 * that are not finished. The final points are only available for finished hands and null otherwise. Also, the final points for a busted 
	 * hand (current points > 21) are 0.  
	 *  
	 */
	Integer getFinalPoints() {
		return finalPoints;
	}

	void setFinalPoints(Integer finalPoints) {
		Util.assertNotNull(finalPoints);
		this.finalPoints = finalPoints;
	}

	/**
	 * Returns all the cards in the hand.  
	 */
	public List<Card> getCards() {
		return cards;
	}
	
	@Override
	public int hashCode() {
		return 0;
	}

	
	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Hand other = (Hand) obj;
		if (cards == null) {
			if (other.cards != null)
				return false;
		} else if (!cards.equals(other.cards))
			return false;
		return true;
	}

	public HAND_OUTCOME getHandOutcome() {
		return handOutcome;
	}

	public INSURANCE_OUTCOME getInsuranceOutcome() {
		return insuranceOutcome;
	}

	/**
	 * Returns the card with value equal to that of the parameter or null if such card doesn't exist
	 * @param value
	 * @return
	 */
	Card getCardWithValue(Integer value){
		for(Card card : cards){
			if (card.getRank().getValue() == value){
				return card;
			}
		}
		return null;
	}
	
	/**
	 * Returns the number of cards in the hand.
	 * @return
	 */
	public int getHandSize(){
		return cards.size();
	} 
	
	
}

