package com.skoev.blackjack2.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * 
 * - todo normal: add the methods to hand 
 * @author stefan.t.koev
 *
 */
public class Hand {
	public BigDecimal amountBet;
	public List<HandHistory> handHistory = Collections.EMPTY_LIST;
	public List<Card> cards = Collections.EMPTY_LIST;
	public Integer finalPoints;

	private HAND_OUTCOME handOutcome = null; 
	
	
	public enum HAND_OUTCOME{WIN, LOSS, PUSH}
	
	public Hand(BigDecimal amountBet, Card card1, Card card2){
		this.amountBet = amountBet;
		cards = new ArrayList<Card>();
		cards.add(card1);
		cards.add(card2);
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
	 * Modifies the original hand and returns new hand.
	 * @return
	 */
	public void split(Card card1, Card card2){
		if(cards.size() > 2){
			throw new RuntimeException("Split only allowed whene there are two cards");
		}
		
		Hand hand1 = this; 
		Hand hand2 = new Hand(amountBet, card1, card2);
		Card hand1Card2 = hand1.cards.get(1);
		// hand 1 is card one from original hand with a new card
		hand1.cards.set(1, hand2.cards.get(0));
		// hand 2 is card 2 from the original hand with a new card
		hand2.cards.set(0, hand1Card2);
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

	// todo normal check how to do the big decimal math
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
	
	//todon basic check if big decimal is immutable
	public BigDecimal getInsuranceAmountWon(){
		BigDecimal result = getInsureanceAmountBet().multiply(BigDecimal.valueOf(2));
		result = result.add(getInsureanceAmountBet());
		return result;
	}
	
	@Override
	public String toString() {
		String result = "";
		for(Card card: cards){
			result = result + card;
		}
		return result;
	}
	

	public HAND_OUTCOME getHandOutcome() {
		return handOutcome;
	}

	public void setHandOutcome(HAND_OUTCOME handOutcome) {
		this.handOutcome = handOutcome;
	}

	
	
}

