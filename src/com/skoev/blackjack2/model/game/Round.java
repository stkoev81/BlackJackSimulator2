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

import com.skoev.blackjack2.common.Entity;
import com.skoev.blackjack2.common.Util;
import com.skoev.blackjack2.common.ValueObject;
import com.skoev.blackjack2.model.game.Hand.HAND_OUTCOME;
import java.io.Serializable;


/**
 * Represents a round of the blackjack game. The round object should be mutated only by its owning game object. <br/>A round begins by the player making a bet. Then, the he is dealt hands. Then he is given various
 * offers from which he chooses. The round ends when the player's cards go over 21 or when his cards are compared to the dealer's hand. Each round is 
 * associated with one dealer hand and with one or more player hands. The player starts with one hand, but if he chooses a split offer at any time, 
 * two hands are formed (and they can be split further).  
 * @author stefan.t.koev
 *
 */
public class Round implements Entity{
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
	
		
	Round(Game game) {
		Util.assertNotNull(game);
		Util.assertNotNull(game.getMoneyCurrent());
		this.game = game;
		this.moneyStart = game.getMoneyCurrent();
	}
	/**
	 * Represents the states through which a standard blackjack round goes.   
	 */
	public enum RoundStatus{
		HAND_BEING_DEALT, HAND_BEING_INSURED, HANDS_BEING_PLAYED_OUT, HANDS_BEING_COMPARED_TO_DEALERS_HAND, ROUND_FINISHED;
	}
	/**
	 * Represents an offer that is made to the player. These meaning of these offers is part of the standard Blackjack vocabulary. 
	 */
	public enum Offer{
		HIT, STAND, DOUBLE, SPLIT, ACCEPT_INSURANCE, DECLINE_INSURANCE
	}
	
	/**
	 * Starts or continues playing the round with the given playingStrategy. The action taken to play the round depend on the current round
	 * status. 
	 */
	void play(PlayingStrategy playingStrategy) throws InsufficientMoneyException{
		Util.assertNotNull(playingStrategy);
		switch(roundStatus){
		case HAND_BEING_DEALT: 
			BigDecimal betAmount = playingStrategy.respondToAmountBet();
			if(betAmount == null){
				return;
			}
			game.subtractMoney(betAmount);
			hands.add(new Hand(betAmount, 1, game.dealCard(), game.dealCard()));
			dealerHand = new Hand(null, 0, game.dealCard());
			roundStatus = RoundStatus.HAND_BEING_INSURED;
		case HAND_BEING_INSURED: 
			Offer playerResponse = null;
			currentHand = hands.get(0);
			availableOffers = getAvailableOffers();
			if(availableOffers.size() > 0 ){
				playerResponse = playingStrategy.respondToOffer(availableOffers, currentHand, dealerHand);
				if(playerResponse == null){
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
				availableOffers = getAvailableOffers();
				playerResponse = playingStrategy.respondToOffer(availableOffers, currentHand, dealerHand);
				if(playerResponse == null){
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
	}
	
	private void applyOffer(Offer offer) throws InsufficientMoneyException{
		Util.assertNotNull(offer);
		if(!availableOffers.contains(offer)){
			throw new IllegalStateException();
		}
		
		switch(offer){
			case ACCEPT_INSURANCE:
				
				game.subtractMoney(currentHand.getInsuranceAmountBet());
				dealerHand.addCard(game.dealCard());
				
				if(dealerHand.calculateCurrentPoints() == 21){ // insurance won
					game.addMoney(currentHand.getInsuranceAmountToBeWon());
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
				break;
			case HIT:
				currentHand.addCard(game.dealCard());
				if(currentHand.calculateCurrentPoints() > 21 || currentHand.calculateCurrentPoints() == 21){
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
	/**
	 * Returns the available offers, which depend on the status of the blackjack round, the player's hand, and the dealer's hand. If the 
	 * hand has just been dealt and the dealer's hand contains an Ace, the offers are to accept or decline insurance. If the round is past the 
	 * insurance stage, the available offers are hit, stand, and double (and also split if the hand contains two cards of the same rank). 
	 */
	public List<Offer> getAvailableOffers(){
		List<Offer> offers = new LinkedList<Offer>();
		if(RoundStatus.HAND_BEING_INSURED.equals(roundStatus) && dealerHand != null){
			if (dealerHand.firstCardIsAce() ){
				offers.add(Offer.ACCEPT_INSURANCE);
				offers.add(Offer.DECLINE_INSURANCE);
			}
		}
		else if(RoundStatus.HANDS_BEING_PLAYED_OUT.equals(roundStatus) && currentHand != null){
			offers.add(Offer.HIT);
			offers.add(Offer.STAND);
			offers.add(Offer.DOUBLE);
			if(currentHand.isEligibleForSplit()){
				offers.add(Offer.SPLIT);
			}
		}
		return offers;
	}
	/**
	 * Calculates the dealer's hand points, which might involve dealing more cards to the dealer. The dealer receives more cards(i.e. "hits") if the hand value is 
	 * less than or equal to 16; otherwise he keeps current hand (i.e. "stands"). If the dealer's hand has already been calculated, this method uses the 
	 * old value and doesn't deal more cards (which would be incorrect).   
	 */
	private int calculateDealersHandPoints(){
		if(dealerHand.getCards().size() == 1){ //dealer might have already received 2nd card if there was insurance but otherwise only has 1 still
			dealerHand.addCard(game.dealCard());
		}
		
		if(dealerHand.getFinalPoints() != null){ 
			// dealer's value already revealed, so do nothing
		}
		else if(dealerHand.calculateCurrentPoints() > 16){
			dealerHand.setFinalPoints(dealerHand.calculateCurrentPoints());
		}
		else{
			while (dealerHand.calculateCurrentPoints() <= 16){ // dealer takes hit and stands as prescribed by casino rules: Hit on 16, stand on 17
				dealerHand.addCard(game.dealCard());
				if(dealerHand.calculateCurrentPoints() > 21){ //dealer busts
					dealerHand.setFinalPoints(0);
					break;
				}
				else {
					dealerHand.setFinalPoints(dealerHand.calculateCurrentPoints());
				}
			}
		}
		return dealerHand.getFinalPoints();
		
	}
	private void compareToDealerHandAndAdjustMoney(Hand hand){
		Util.assertNotNull(hand);
		if(hand.calculateCurrentPoints() > 21){ //automatic loss
			// mark as lost, 0 points
			hand.setHandOutcome(Hand.HAND_OUTCOME.LOSS);
			hand.setFinalPoints(0);
		}
		
		
		// compare to dealer's hand, mark as won or push, adjust money
		else {//compare to dealer's hand
			if (hand.calculateCurrentPoints() < calculateDealersHandPoints()){
				//loss, do nothing, value is alredy removed from stake
				hand.setHandOutcome(Hand.HAND_OUTCOME.LOSS);
				hand.setFinalPoints(hand.calculateCurrentPoints());
			}
			else if(hand.calculateCurrentPoints() == calculateDealersHandPoints()){
				//return to the stake what was already taken form it; essentially a push
				hand.setHandOutcome(Hand.HAND_OUTCOME.PUSH);
				hand.setFinalPoints(hand.calculateCurrentPoints());
				game.addMoney(hand.getAmountBet());
			}
			else {
				//win (original amount + win)
				hand.setHandOutcome(Hand.HAND_OUTCOME.WIN);
				hand.setFinalPoints(hand.calculateCurrentPoints());
				game.addMoney(hand.getAmountToBeWon());
			}
		}
		
	}
	/**
	 * Retrieve the player hand for this handNumber. A player starts out with one hand but may eventually have more than one due to splitting. Hand numbers start with 1.  		
	 */
	public Hand getHand(int handNumber){
		Util.assertTrue(!(handNumber < 1 || handNumber > hands.size()));
		return hands.get(handNumber - 1); 
	}
	/**
	 * Returns the number of this round. Rounds start with 1. 
	 */
	public int getRoundNumber() {
		return roundNumber;
	}
	/**
	 * Returns the player's starting money. 
	 */
	public BigDecimal getMoneyStart() {
		return moneyStart;
	}
	/**
	 * Returns the player's ending money. The difference between ending and starting money is the amount the player won. 
	 */
	public BigDecimal getMoneyEnd() {
		return moneyEnd;
	}
	
	public RoundStatus getRoundStatus() {
		return roundStatus;
	}
	
	/**
	 * Returns all of the player's hands. 
	 */
	public List<Hand> getHands() {
		return hands;
	}

	/**
	 * Returns the dealer's hand. The dealer always has only one hand. 
	 * @return
	 */
	public Hand getDealerHand() {
		return dealerHand;
	}
	
	/**
	 * Returns the player's hand that is currently being played. The player may have more than one hands, but they are played one at a time.   
	 * @return
	 */
	public Hand getCurrentHand() {
		return currentHand;
	}
	
	void setRoundNumber(int roundNumber) {
		this.roundNumber = roundNumber;
	}
	
}
		
	
		
	

	 
	 


