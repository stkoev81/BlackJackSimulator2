package com.skoev.blackjack2.model.game;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;
import java.util.Collections;

import com.skoev.blackjack2.model.game.Hand.HAND_OUTCOME;



/**
 * @author stefan.t.koev
 *
 */
public class Round {
	private int roundNumber;
	private BigDecimal moneyStart;
	private BigDecimal moneyEnd;
	private RoundStatus roundStatus = RoundStatus.HAND_BEING_DEALT;
	private Game game; 
	private List<Hand> hands = new ArrayList<Hand>(); 
	private Hand dealerHand;
	private Queue<Hand> handsToProcess = new LinkedList<Hand>(); 
	private Hand currentHand = null;
	private List<Offer> availableOffers = Collections.EMPTY_LIST;
	
		
	/**
	 * @param game
	 */
	public Round(Game game) {
		this.game = game;
		this.moneyStart = game.getMoneyCurrent();
	}
	public enum RoundStatus{
		HAND_BEING_DEALT, HAND_BEING_INSURED, HANDS_BEING_PLAYED_OUT, HANDS_BEING_COMPARED_TO_DEALERS_HAND, ROUND_FINISHED;
	}
	public enum Offer{
		HIT, STAND, DOUBLE, SPLIT, ACCEPT_INSURANCE, DECLINE_INSURANCE
	}
	void play(PlayingStrategy playingStrategy){
		switch(roundStatus){
		case HAND_BEING_DEALT: 
			BigDecimal betAmount = playingStrategy.respondToAmountBet();
			if(betAmount == null){
				game.setUserInputNeeded(true);
				return;
			}
			game.subtractMoney(betAmount);
			hands.add(new Hand(betAmount, 1, game.dealCard(), game.dealCard()));
			dealerHand = new Hand(null, 0, game.dealCard());
			roundStatus = RoundStatus.HAND_BEING_INSURED;
		case HAND_BEING_INSURED: 
			Offer playerResponse = null;
			currentHand = hands.get(0);
			availableOffers = getAvailableOffers(currentHand, true);
			if(availableOffers.size() > 0 ){
				playerResponse = playingStrategy.respondToOffer(availableOffers, currentHand, dealerHand);
				if(playerResponse == null){
					game.setUserInputNeeded(true);
					return;
				}
				applyOffer(playerResponse);
			}
			else{
				currentHand.setInsuranceOutcome(Hand.INSURANCE_OUTCOME.NOT_OFFERED);
			}
			
				
			currentHand = null;
			handsToProcess.addAll(hands);
			roundStatus = RoundStatus.HANDS_BEING_PLAYED_OUT;
			
		case HANDS_BEING_PLAYED_OUT:  // keep getting player responses until player stands, busts, or gets 21
			do {
				if(currentHand == null && handsToProcess.size()>0){
					currentHand = handsToProcess.remove();
				}
				playerResponse = null;
				availableOffers = getAvailableOffers(currentHand, false);
				playerResponse = playingStrategy.respondToOffer(availableOffers, currentHand, dealerHand);
				if(playerResponse == null){
					game.setUserInputNeeded(true);
					return;
				}
				applyOffer(playerResponse); //in case of split, the handBeingResolved and handsToResolve will be changed
			}	
			while(handsToProcess.size() > 0 || currentHand != null);
			handsToProcess.addAll(hands);
			roundStatus = RoundStatus.HANDS_BEING_COMPARED_TO_DEALERS_HAND;
		case HANDS_BEING_COMPARED_TO_DEALERS_HAND: 
			for(Hand hand : handsToProcess){
				//compare to dealers hand, mark as won or lost if not already marked, adjust money in each case (the ones marked are not yet adjusted)
				compareToDealerHandAndAdjustMoney(hand);
			}
			roundStatus = RoundStatus.ROUND_FINISHED;
			moneyEnd = game.getMoneyCurrent();
		}
		game.setUserInputNeeded(false);
	}
	private void applyOffer(Offer offer){
		if(offer == null){
			return;
		}
		
		switch(offer){
		case ACCEPT_INSURANCE:
			
			game.subtractMoney(currentHand.getInsureanceAmountBet());
			dealerHand.addCard(game.dealCard());
			
			if(dealerHand.getCurrentPoints() == 21){ // insurance won
				game.addMoney(currentHand.getInsuranceAmountWon());
				currentHand.setInsuranceOutcome(Hand.INSURANCE_OUTCOME.WIN);
			}
			else { // insurance lost
				// do nothing - insurance already subtracted form money, no need to subtract more money here
				currentHand.setInsuranceOutcome(Hand.INSURANCE_OUTCOME.LOSS);
			}
			break;
		case DECLINE_INSURANCE:
			currentHand.setInsuranceOutcome(Hand.INSURANCE_OUTCOME.DECLINED);
			break;
		case STAND:
			currentHand = null; //we're done with playing this hand
			
//			
				
			break;
		case HIT:
			currentHand.addCard(game.dealCard());
			if(currentHand.getCurrentPoints() > 21 || currentHand.getCurrentPoints() == 21){
				currentHand = null; //we're done with playing this hand
			}
			else{
				// do nothing; leave currentHand != null, which means will keep playing this hand
			}
			

					
			break;
		case DOUBLE:
			// take just one more card and double the amount bet
			currentHand.addCard(game.dealCard());
			BigDecimal amountBet = currentHand.getAmountBet();
			currentHand.setAmountBet(amountBet.multiply(BigDecimal.valueOf(2)));
			game.subtractMoney(amountBet);
			currentHand = null; //we're done with playing this hand
			break;
		
		case SPLIT:
			Hand newHand = currentHand.split(game.dealCard(), game.dealCard(), hands.size() + 1);
			game.subtractMoney(newHand.getAmountBet());
			hands.add(newHand);						
		}	
		
	}
	private List<Offer> getAvailableOffers(Hand hand, boolean insuranceOffers){
		List<Offer> offers = new LinkedList<Offer>();
		if(insuranceOffers){
			if (dealerHand.firstCardIsAce() ){
				offers.add(Offer.ACCEPT_INSURANCE);
				offers.add(Offer.DECLINE_INSURANCE);
			}
		}
		else {
			offers.add(Offer.HIT);
			offers.add(Offer.STAND);
			offers.add(Offer.DOUBLE);
			if(hand.isEligibleForSplit()){
				offers.add(Offer.SPLIT);
			}
		}
		return offers;
	}
	private int calculateDealersHandPoints(){
		if(dealerHand.getCards().size() == 1){ //dealer might have already received 2nd card if there was insurance but otherwise only has 1 still
			dealerHand.addCard(game.dealCard());
		}
		
		if(dealerHand.getFinalPoints() != null){ 
			// dealer's value already revealed, so do nothing
		}
		else if(dealerHand.getCurrentPoints() > 16){
			dealerHand.setFinalPoints(dealerHand.getCurrentPoints());
		}
		else{
			while (dealerHand.getCurrentPoints() <= 16){ // dealer takes hit and stands as prescribed by casino rules: Hit on 16, stand on 17
				dealerHand.addCard(game.dealCard());
				if(dealerHand.getCurrentPoints() > 21){ //dealer busts
					dealerHand.setFinalPoints(0);
					break;
				}
				else {
					dealerHand.setFinalPoints(dealerHand.getCurrentPoints());
				}
			}
		}
		return dealerHand.getFinalPoints();
		
	}
	private void compareToDealerHandAndAdjustMoney(Hand hand){
		// 

		if(hand.getCurrentPoints() > 21){ //automatic loss
			// mark as lost, 0 points
			hand.setHandOutcome(Hand.HAND_OUTCOME.LOSS);
			hand.setFinalPoints(0);
		}
		
		
		// compare to dealer's hand, mark as won or push, adjust money
		else {//compare to dealer's hand
			if (hand.getCurrentPoints() < calculateDealersHandPoints()){
				//loss, do nothing, value is alredy removed from stake
				hand.setHandOutcome(Hand.HAND_OUTCOME.LOSS);
				hand.setFinalPoints(0);
			}
			else if(hand.getCurrentPoints() == calculateDealersHandPoints()){
				//return to the stake what was already taken form it; essentially a push
				hand.setHandOutcome(Hand.HAND_OUTCOME.PUSH);
				hand.setFinalPoints(hand.getCurrentPoints());
				game.addMoney(hand.getAmountBet());
			}
			else {
				//win (original amount + win)
				hand.setHandOutcome(Hand.HAND_OUTCOME.WIN);
				hand.setFinalPoints(hand.getCurrentPoints());
				game.addMoney(hand.getAmountToBeWon());
			}
		}
		
	}
	public int getRoundNumber() {
		return roundNumber;
	}
	
	public BigDecimal getMoneyStart() {
		return moneyStart;
	}
	public BigDecimal getMoneyEnd() {
		return moneyEnd;
	}
	public RoundStatus getRoundStatus() {
		return roundStatus;
	}
	public List<Hand> getHands() {
		return hands;
	}
	public Hand getDealerHand() {
		return dealerHand;
	}
	public Hand getCurrentHand() {
		return currentHand;
	}
	public List<Offer> getAvailableOffers() {
		return availableOffers;
	}
	void setRoundNumber(int roundNumber) {
		this.roundNumber = roundNumber;
	}
	
}
//todo normal: automatic win when the hand is 21 - don't ask for player's response. 	
		
	
		
	

	 
	 


