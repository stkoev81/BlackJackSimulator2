package com.skoev.blackjack2.model.game;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * 
 * @author stefan.t.koev
 *
 */
public class Hand {
	private int handNumber;
	private BigDecimal amountBet;
	private List<Card> cards = Collections.EMPTY_LIST;
	private Integer finalPoints;

	private HAND_OUTCOME handOutcome = null; 
	private INSURANCE_OUTCOME insuranceOutcome = null; 
	
	public enum HAND_OUTCOME{WIN, LOSS, PUSH}
	public enum INSURANCE_OUTCOME{NOT_OFFERED, OFFERED, DECLINED, WIN, LOSS}
	
	public Hand(BigDecimal amountBet, int handNumber, Card ...cardsToAdd){
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
	
	boolean isEligibleForSplit(){
		if(cards.size() == 2 && (cards.get(0).getRank().equals(cards.get(1).getRank()))){
			return true;
		}
		return false; 
	}
	
	/**
	 * Modifies this hand and returns new hand. The cards parameters passed in are used to complete two resulting hands. 
	 * @return
	 */
	Hand split(Card card1, Card card2, int handNumber){
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
		cards.add(card);
	}

	public BigDecimal getAmountBet(){
		return amountBet;
	}
	
	void setAmountBet(BigDecimal amountBet) {
		this.amountBet = amountBet;
	}

	BigDecimal getAmountToBeWon(){
		double amountBetDouble = amountBet.doubleValue();
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
	
	BigDecimal getInsuranceAmountToBeWon(){
		BigDecimal result = getInsuranceAmountBet().multiply(BigDecimal.valueOf(2));
		result = result.add(getInsuranceAmountBet());
		return result;
	}

	void setInsuranceOutcome(INSURANCE_OUTCOME insuranceOutcome) {
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
		this.handOutcome = handOutcome;
	}
	

	Integer getFinalPoints() {
		return finalPoints;
	}

	void setFinalPoints(Integer finalPoints) {
		this.finalPoints = finalPoints;
	}

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

	
	
}

