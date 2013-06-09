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



/**
 * @author stefan.t.koev
 *
 */
public class Round {
	public int roundNumber;
	public BigDecimal moneyStart = BigDecimal.valueOf(100);
	public BigDecimal moneyEnd = BigDecimal.valueOf(10);
	public RoundStatus roundStatus = RoundStatus.HAND_BEING_DEALT;
	public Game game;
	public List<Hand> hands = new ArrayList<Hand>(); 
	public Hand dealerHand;
	private Queue<Hand> handsToProcess = new LinkedList<Hand>();
	public Hand currentHand = null;
	public List<Offer> availableOffers = Collections.EMPTY_LIST;
	
		
	/**
	 * @param game
	 */
	public Round(Game game) {
		this.game = game;
		this.moneyStart = game.moneyCurrent;
	}
	public enum RoundStatus{
		HAND_BEING_DEALT, HAND_BEING_INSURED, HANDS_BEING_PLAYED_OUT, HANDS_BEING_COMPARED_TO_DEALERS_HAND, ROUND_FINISHED;
	}
	public enum Offer{
		HIT, STAND, DOUBLE, SPLIT, ACCEPT_INSURANCE, DECLINE_INSURANCE
	}
	public void play(PlayingStrategy playingStrategy){
		switch(roundStatus){
		case HAND_BEING_DEALT: 
			BigDecimal betAmount = playingStrategy.respondToAmountBet();
			if(betAmount == null){
				game.userInputNeeded = true;
				return;
			}
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
					game.userInputNeeded = true;
					return;
				}
			}
			
			applyOffer(playerResponse);	
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
					game.userInputNeeded = true;
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
			moneyEnd = game.moneyCurrent;
		}
		game.userInputNeeded = false;
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
			}
			else { // insurance lost
				// do nothing - insurance already subtracted form money, no need to subtract more money here
			}
			break;
		case DECLINE_INSURANCE:
			//do nothing
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
		if(dealerHand.cards.size() == 1){ //dealer might have already received 2nd card if there was insurance but otherwise only has 1 still
			dealerHand.addCard(game.dealCard());
		}
		
		if(dealerHand.finalPoints != null){ 
			// dealer's value already revealed, so do nothing
		}
		else if(dealerHand.getCurrentPoints() > 16){
			dealerHand.finalPoints = dealerHand.getCurrentPoints();
		}
		else{
			while (dealerHand.getCurrentPoints() <= 16){ // dealer takes hit and stands as prescribed by casino rules: Hit on 16, stand on 17
				dealerHand.addCard(game.dealCard());
				if(dealerHand.getCurrentPoints() > 21){ //dealer busts
					dealerHand.finalPoints = 0;
					break;
				}
				else {
					dealerHand.finalPoints = dealerHand.getCurrentPoints();
				}
			}
		}
		return dealerHand.finalPoints;
		
	}
	private void compareToDealerHandAndAdjustMoney(Hand hand){
		// 

		if(hand.getCurrentPoints() > 21){ //automatic loss
			// mark as lost, 0 points
			hand.setHandOutcome(Hand.HAND_OUTCOME.LOSS);
			hand.finalPoints = 0;
		}
		
		
		// compare to dealer's hand, mark as won or push, adjust money
		else {//compare to dealer's hand
			if (hand.getCurrentPoints() < calculateDealersHandPoints()){
				//loss, do nothing, value is alredy removed from stake
				hand.setHandOutcome(Hand.HAND_OUTCOME.LOSS);
				hand.finalPoints = 0;
			}
			else if(hand.getCurrentPoints() == calculateDealersHandPoints()){
				//return to the stake what was already taken form it; essentially a push
				hand.setHandOutcome(Hand.HAND_OUTCOME.PUSH);
				hand.finalPoints = hand.getCurrentPoints();
				game.addMoney(hand.getAmountBet());
			}
			else {
				//win (original amount + win)
				hand.setHandOutcome(Hand.HAND_OUTCOME.WIN);
				hand.finalPoints = hand.getCurrentPoints();
				game.addMoney(hand.getAmountToBeWon());
			}
		}
		
	}
	@Override
	public String toString() {
		return "-----Round "+ roundNumber+ " results-----\nmoneyStart=" + moneyStart + ", moneyEnd=" + moneyEnd
				+ ", roundStatus=" + roundStatus + "\n" + Util.toCollString("", hands, "") + dealerHand + "------------";
	}
	
	
		
	
	
	
}
//todo normal: automatic win when the hand is 21 - don't ask for player's response. 	
		
	
		
	

	 
	 


