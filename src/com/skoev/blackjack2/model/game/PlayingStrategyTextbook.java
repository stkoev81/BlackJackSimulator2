package com.skoev.blackjack2.model.game;

import java.math.BigDecimal;
import java.util.Collection;

import com.skoev.blackjack2.model.game.Round.Offer;
import com.skoev.blackjack2.util.Util;

/**
 * Implements a blackjack playing strategy that is common in gambling texbooks and websites. This one is taken from 
 * http://www.online-casinos.com/blackjack/blackjack_chart.asp, where it was presented in the form of a matrix.   
 * 
 * @author stefan.t.koev
 *
 */
public class PlayingStrategyTextbook extends PlayingStrategy {
	
	public PlayingStrategyTextbook(BigDecimal defaultBet) {
		Util.assertNotNull(defaultBet);
		this.defaultBet = defaultBet;
	}

	private BigDecimal defaultBet;
	
	
	private static final Offer S = Offer.STAND;
	private static final Offer P = Offer.SPLIT;
	private static final Offer H = Offer.HIT;
	private static final Offer D = Offer.DOUBLE;
	
	
	/**
	 * Decision matrix created by 
	 */
	private static final Offer[][] DECISION_MATRIX = 
		{
	//minor index			 0, 1, 2, 3, 4, 5, 6, 7, 8,  9 
	//dealer card showing	 2, 3, 4, 5, 6, 7, 8, 9, 10, A 
	/* player hand	8 */	{H, H, H, H, H, H, H, H, H, H}, // major index 0
	/* player hand	9 */	{H, D, D, D, D, H, H, H, H, H}, //1
	/* player hand	10 */	{D, D, D, D, D, D, D, D, H, H}, //2
	/* player hand	11 */	{D, D, D, D, D, D, D, D, D, H}, //3
	/* player hand	12 */	{H, H, S, S, S, H, H, H, H, H}, //4
	/* player hand	13 */	{S, S, S, S, S, H, H, H, H, H}, //5
	/* player hand	14 */	{S, S, S, S, S, H, H, H, H, H}, //6
	/* player hand	15 */	{S, S, S, S, S, H, H, H, H, H}, //7
	/* player hand	16 */	{S, S, S, S, S, H, H, H, H, H}, //8 
	/* player hand	17 */	{S, S, S, S, S, S, S, S, S, S}, //9 
	/* player hand	A,2 */	{H, H, H, D, D, H, H, H, H, H}, //10
	/* player hand	A,3 */	{H, H, H, D, D, H, H, H, H, H}, //11
	/* player hand	A,4 */	{H, H, D, D, D, H, H, H, H, H}, //12
	/* player hand	A,5 */	{H, H, D, D, D, H, H, H, H, H}, //13
	/* player hand	A,6 */	{H, D, D, D, D, H, H, H, H, H}, //14
	/* player hand	A,7 */	{S, D, D, D, D, S, S, H, H, H}, //15
	/* player hand	A,8 */	{S, S, S, S, S, S, S, S, S, S}, //16
	/* player hand	A,9 */	{S, S, S, S, S, S, S, S, S, S}, //17
	/* player hand	2,2 */	{H, H, P, P, P, P, H, H, H, H}, //18
	/* player hand	3,3 */	{H, H, P, P, P, P, H, H, H, H}, //19
	/* player hand	4,4 */	{H, H, H, H, H, H, H, H, H, H}, //20
	/* player hand	5,5 */	{D, D, D, D, D, D, D, D, H, H}, //21
	/* player hand	6,6 */	{H, P, P, P, P, H, H, H, H, H}, //22
	/* player hand	7,7 */	{P, P, P, P, P, P, H, H, H, H}, //23
	/* player hand	8,8 */	{P, P, P, P, P, P, P, P, P, P}, //24
	/* player hand	9,9 */	{P, P, P, P, P, S, P, P, S, S}, //25
	/* player hand	10,10 */{S, S, S, S, S, S, S, S, S, S},	//26
	/* player hand	A,A */	{P, P, P, P, P, P, P, P, P, P}, //27
		}; 
	
	@Override
	public BigDecimal respondToAmountBet() {
		return defaultBet;
	}

	@Override
	public Offer respondToOffer(Collection<Offer> offers, Hand hand, Hand dealerHand) {
		if (offers.contains(Offer.DECLINE_INSURANCE)){
			return Offer.DECLINE_INSURANCE;
		}
		
		Offer response = null;
		Integer dealerCardValue = dealerHand.getFirstCard().getValue();
		int majorIndex = 0, minorIndex = 0;
						
		if(dealerCardValue == null){ // in case of an Ace
			minorIndex = 9; 
		}
		else{
			minorIndex = dealerCardValue - 2;
		}
		
		int playerPoints = hand.calculateCurrentPoints();
		// check if hand is the A,2 - A,9 segment of decision matrix
		if(hand.getHandSize() == 2 && hand.getCardWithValue(null) != null 
				&& !hand.isEligibleForSplit() && hand.getCardWithValue(10) == null){ 
			for(int i = 2; i<=9; i++){
				Card card = hand.getCardWithValue(i);
				if(card != null){
					majorIndex = i + 8; 
					break;
				}
				
			}
		}
		// check if hand is AA
		else if(hand.isEligibleForSplit() && hand.getCardWithValue(null) != null){
			majorIndex = 27; 
		}
		// check if hand is in the 2,2 - 10, 10 segment of decision matrix
		else if(hand.isEligibleForSplit()){
			for(int i = 2; i<=10; i++){
				Card card = hand.getCardWithValue(i);
				if(card != null){
					majorIndex = i + 8; 
					break;
				}
			}
		}
		// outside of decision matrix -- use common sense response
		else if (playerPoints > 17){
			response = Offer.STAND;
		}
		// outside of decision matrix -- use common sense response
		else if (playerPoints < 8){
			response = Offer.HIT;
		}
		// last remaining segment of the decision matrix (value 8-17)
		else{
			majorIndex = playerPoints - 8; 
		}
	
		
		if(response != null){
			return response;
		}
		else{
			return DECISION_MATRIX[majorIndex][minorIndex];
		}
		
	}
	
	@Override
	public String toString() {
		return "PlayingStrategyTextbook";
	}

}
