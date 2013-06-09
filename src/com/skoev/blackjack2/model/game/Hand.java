package com.skoev.blackjack2.model.game;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * 
 * todo basic: add filelds to show whether there was any doubling involved (e.g. initial bet, final bet). Also add fields to keep track of whether the hand is insured and whether the insurance is lost or won.  
 * 
 * @author stefan.t.koev
 *
 */
public class Hand {
	public int handNumber;
	public BigDecimal amountBet;
	public List<Card> cards = Collections.EMPTY_LIST;
	public Integer finalPoints;

	private HAND_OUTCOME handOutcome = null; 
	
	public enum HAND_OUTCOME{WIN, LOSS, PUSH}
	
	public Hand(BigDecimal amountBet, int handNumber, Card ...cardsToAdd){
		this.amountBet = amountBet;
		this.handNumber = handNumber;
		cards = new ArrayList<Card>();
		for(Card card : cardsToAdd){
			cards.add(card);
		}
	}
	
	//todo normal: handle the cases where there are multiple aces 
	public int getCurrentPoints(){
		int value = 0; 
		for(Card card : cards){
			if(card.getValue() != null){
				value = card.getValue() + value;
			}
			else {
				value = value + 11; 
			}
		}
		if(value<=21){
			return value;
		}
		value = 0;
		for(Card card : cards){
			if(card.getValue() != null){
				value = card.getValue() + value;
			}
			else {
				value = value + 1; 
			}
		}
		
		return value; 
	}
	
	public Card getFirstCard(){
		return cards.get(0);
	}
	
	public boolean firstCardIsAce(){
		return getFirstCard().getRank().equals(Rank.ACE);
	}
	
	public boolean isEligibleForSplit(){
		if(cards.size() == 2 && (cards.get(0).getRank().equals(cards.get(1).getRank()))){
			return true;
		}
		return false; 
	}
	
	/**
	 * Modifies this hand and returns new hand. The cards parameters passed in are used to complete two resulting hands. 
	 * @return
	 */
	public Hand split(Card card1, Card card2, int handNumber){
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
	
	public void addCard(Card card){
		cards.add(card);
	}

	public BigDecimal getAmountBet(){
		return amountBet;
	}
	
	public void setAmountBet(BigDecimal amountBet) {
		this.amountBet = amountBet;
	}

	public BigDecimal getAmountToBeWon(){
		double amountBetDouble = amountBet.doubleValue();
		BigDecimal result; 
		if(getCurrentPoints() == 21){
			//won 1.5*amountBet + amountBet
			result = amountBet.multiply(BigDecimal.valueOf(2.5));
		}
		else{
			//won 1* amountBet + amountBet
			result = amountBet.multiply(BigDecimal.valueOf(2));
		} 
		return result;
			
	}

	public BigDecimal getInsureanceAmountBet(){
		return amountBet.divide(BigDecimal.valueOf(2));
	}
	
	public BigDecimal getInsuranceAmountWon(){
		BigDecimal result = getInsureanceAmountBet().multiply(BigDecimal.valueOf(2));
		result = result.add(getInsureanceAmountBet());
		return result;
	}

	public HAND_OUTCOME getHandOutcome() {
		return handOutcome;
	}

	
	// todo basic: if handOutcome or amount bet or final points is null (e.g. dealer's hand), dont print it). 
	@Override
	public String toString() {
		String cardsString = "";
		for(Card card: cards){
			cardsString = cardsString + card;
		}
	
		
		
		
		if(handNumber != 0){		
			return "Player hand " + handNumber + ": amountBet=" + amountBet 
				+ ", cards=" + cardsString
				+ ", finalPoints=" + finalPoints
				+ ", handOutcome=" + handOutcome + "\n";
		}
		else{
			if(finalPoints != null){
				return "Dealer hand:"  
						+ " cards=" + cardsString
						+ ", finalPoints=" + finalPoints + "\n";
			}
			else{
				return "Dealer hand:"  
						+ " cards=" + cardsString
						+ "\n";
			}
			
		}
		
		
		
	}

	public void setHandOutcome(HAND_OUTCOME handOutcome) {
		this.handOutcome = handOutcome;
	}

	
	
}

