package com.skoev.blackjack2.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Stack;

import com.skoev.blackjack.model.GameState;
import com.skoev.blackjack.model.Hand;
import com.skoev.blackjack.model.Offer;


/**
 * todo basic: finish the methods already started
 * todo next: copy exitisting functionality over from other game that can be copied. 
 * @author stefan.t.koev
 *
 */
public class Round {
	public BigDecimal moneyStart = BigDecimal.valueOf(100);
	public BigDecimal moneyEnd = BigDecimal.valueOf(10);
	public RoundStatus roundStatus = RoundStatus.HAND_BEING_DEALT;
	public Game game;
	public List<Hand> hands = new ArrayList<Hand>(); 
	public Hand dealerHand;
	public Stack<Hand> handsToResolve = new Stack<Hand>();
	public Hand handBeingResolved = null;
	
		
	/**
	 * @param game
	 */
	public Round(Game game) {
		this.game = game;
	}
	public enum RoundStatus{
		HAND_BEING_DEALT, HAND_BEING_INSURED, HAND_BEING_RESOVED, ROUND_FINISHED;
	}
	public enum Offer{
		HIT, STAND, DOUBLE, SPLIT, ACCEPT_INSURANCE, DECLINE_INSURANCE
	}
	
	public boolean playRound(PlayingStrategy playingStrategy){
		switch(roundStatus){
		case HAND_BEING_DEALT: 
			BigDecimal betAmount = playingStrategy.respondToAmountBet();
			if(betAmount == null){
				return false;
			}
			hands.add(new Hand(betAmount, game.dealCard(), game.dealCard()));
			dealerHand = new Hand(null, game.dealCard(), game.dealCard());
			roundStatus = RoundStatus.HAND_BEING_INSURED;
		case HAND_BEING_INSURED: 
			Offer playerResponse = null;
			Collection<Offer> offers = getAvailableOffers(hands.get(0), true);
			if(offers.size() > 0 ){
				playerResponse = playingStrategy.respondToOffer();
				if(playerResponse == null){
					return false;
				}
			}
			applyOffer(playerResponse);	
			roundStatus = RoundStatus.HAND_BEING_RESOVED;
			handsToResolve.addAll(hands);
			handBeingResolved = null;
			
		case HAND_BEING_RESOVED:
			do {
				if(handBeingResolved == null && handsToResolve.size()>0){
					handBeingResolved = handsToResolve.pop();
				}
				playerResponse = null;
				playerResponse = playingStrategy.respondToOffer();
				if(playerResponse == null){
					return false;
				}
				applyOffer(playerResponse);//in case of split, the handBeingResolved and handsToResolve will be changed
			}	
			while(handsToResolve.size()>0 || handBeingResolved != null);
			roundStatus = RoundStatus.ROUND_FINISHED;
		}
		return true;
	}
	
	
	
	private void applyOffer(Offer offer){
		switch(offer){
		//todo basic: what happens if dealer has 21? Player still plays in the hopes that he will get a 21 and at leas have a push? What happens if player has 21? Dealer still plays out his card? What if player has 21 immediately - is he offered insuance still?  
		case ACCEPT_INSURANCE:
			Hand hand = hands.get(0);
			game.subtractMoney(hand.getInsureanceAmountBet());
			boolean insuranceWon = false;
			if(dealerHand.getCurrentPoints() == 21){
				insuranceWon = true;
			}
			if(insuranceWon){
				game.addMoney(hand.getInsuranceAmountWon());
				// do nothing -- keep playing because you still have a chance of having a push
				//now display the dealer's 2nd card
			}
			if(!insuranceWon){
				// insurance already deducted from stake at this point, no need to adjust stake
				//do nothing - dealer's card is still a secret
				// keep playing to resolve the hand. 
			}
			
			
			break;
		case DECLINE_INSURANCE:
			//do nothing
			break;
		case STAND:

			int handValue = hand.getValue();
			Hand.HAND_OUTCOME winLoseOutcome = null;  
			if(handValue > 21){
				//automatic loss, do nothing, value is already removed from stake
				winLoseOutcome = Hand.HAND_OUTCOME.LOSS;
			}
			else {
				//compare to dealer's hand
				int dealerValue = game.getDealersHandValue();
				if (handValue < dealerValue){
					//loss, do nothing, valuei is alreayd removed from stake
					winLoseOutcome = Hand.HAND_OUTCOME.LOSS;
				}
				else if(handValue == dealerValue){
					//return to the stake what was already taken form it; essentially a push
					player.addToStake(hand.getAmountBet());
					winLoseOutcome = Hand.HAND_OUTCOME.PUSH;
				}
				else {
					//win (original amount + win)
					player.addToStake(hand.getAmountWon());
					winLoseOutcome = Hand.HAND_OUTCOME.WIN;
				}
			}
			hand.setHandResolutionStatus(Hand.HAND_RESOLUTION_STATUS.HAND_RESOLVED);
			hand.setHandOutcome(winLoseOutcome);
		
			
			break;
		case HIT:

			hand.addCard();
			int handValue = hand.getValue(); 
			
			if(handValue > 21){
				//automatic loss, do nothing, value is already removed from stake
				hand.setHandResolutionStatus(Hand.HAND_RESOLUTION_STATUS.HAND_RESOLVED);
				hand.setHandOutcome(Hand.HAND_OUTCOME.LOSS);
			}
			else if(handValue == 21){
				//either win or push, compare to dealer
				int dealerValue = game.getDealersHandValue();
				if(dealerValue == 21){ //push
					player.addToStake(hand.getAmountBet());
					hand.setHandResolutionStatus(Hand.HAND_RESOLUTION_STATUS.HAND_RESOLVED);
					hand.setHandOutcome(Hand.HAND_OUTCOME.PUSH);

					
				}
				else{ //win
					player.addToStake(hand.getAmountWon());
					hand.setHandResolutionStatus(Hand.HAND_RESOLUTION_STATUS.HAND_RESOLVED);
					hand.setHandOutcome(Hand.HAND_OUTCOME.WIN);
				}
				
			}
			else {
				//keep going
				hand.setHandResolutionStatus(Hand.HAND_RESOLUTION_STATUS.HAND_NOT_RESOLVED);
			}
		
			break;
		case DOUBLE:
			int amountBet = hand.getAmountBet();
			hand.setAmountBet(2*amountBet);
			player.addToStake(-amountBet);
			hand.addCard();
			Offer.STAND.execute(hand, player, game);
			break;
		
		case SPLIT:

			hand.split();
			hand.setHandResolutionStatus(Hand.HAND_RESOLUTION_STATUS.HAND_NOT_RESOLVED_AND_SPLIT);
			player.addToStake(hand.getHandAddedBySplit().getAmountBet());
		
		}	
		
	}
	
	
	private Collection<Offer> getAvailableOffers(Hand hand, boolean insuranceOffers){
		Collection<Offer> offers = new LinkedHashSet<Offer>();
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
		if(dealerHand.finalPoints != null){ 
			// dealer's value already revealed
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
	
	/*
	 * 
	SPLIT {
		public void execute(Hand hand, Player player, BlackJackGameSession game){
			hand.split();
			hand.setHandResolutionStatus(Hand.HAND_RESOLUTION_STATUS.HAND_NOT_RESOLVED_AND_SPLIT);
			player.addToStake(hand.getHandAddedBySplit().getAmountBet());
		}
	},
	STAND {
		public void execute(Hand hand, Player player, BlackJackGameSession game){
			int handValue = hand.getValue();
			Hand.HAND_OUTCOME winLoseOutcome = null;  
			if(handValue > 21){
				//automatic loss, do nothing, value is already removed from stake
				winLoseOutcome = Hand.HAND_OUTCOME.LOSS;
			}
			else {
				//compare to dealer's hand
				int dealerValue = game.getDealersHandValue();
				if (handValue < dealerValue){
					//loss, do nothing, valuei is alreayd removed from stake
					winLoseOutcome = Hand.HAND_OUTCOME.LOSS;
				}
				else if(handValue == dealerValue){
					//return to the stake what was already taken form it; essentially a push
					player.addToStake(hand.getAmountBet());
					winLoseOutcome = Hand.HAND_OUTCOME.PUSH;
				}
				else {
					//win (original amount + win)
					player.addToStake(hand.getAmountWon());
					winLoseOutcome = Hand.HAND_OUTCOME.WIN;
				}
			}
			hand.setHandResolutionStatus(Hand.HAND_RESOLUTION_STATUS.HAND_RESOLVED);
			hand.setHandOutcome(winLoseOutcome);
		}
	}
 
	, HIT {
		public void execute(Hand hand, Player player, BlackJackGameSession game){
			hand.addCard();
			int handValue = hand.getValue(); 
			
			if(handValue > 21){
				//automatic loss, do nothing, value is already removed from stake
				hand.setHandResolutionStatus(Hand.HAND_RESOLUTION_STATUS.HAND_RESOLVED);
				hand.setHandOutcome(Hand.HAND_OUTCOME.LOSS);
			}
			else if(handValue == 21){
				//either win or push, compare to dealer
				int dealerValue = game.getDealersHandValue();
				if(dealerValue == 21){ //push
					player.addToStake(hand.getAmountBet());
					hand.setHandResolutionStatus(Hand.HAND_RESOLUTION_STATUS.HAND_RESOLVED);
					hand.setHandOutcome(Hand.HAND_OUTCOME.PUSH);

					
				}
				else{ //win
					player.addToStake(hand.getAmountWon());
					hand.setHandResolutionStatus(Hand.HAND_RESOLUTION_STATUS.HAND_RESOLVED);
					hand.setHandOutcome(Hand.HAND_OUTCOME.WIN);
				}
				
			}
			else {
				//keep going
				hand.setHandResolutionStatus(Hand.HAND_RESOLUTION_STATUS.HAND_NOT_RESOLVED);
			}
		}
	}
	 
	, DOUBLE {
		public void execute(Hand hand, Player player, BlackJackGameSession game){
			int amountBet = hand.getAmountBet();
			hand.setAmountBet(2*amountBet);
			player.addToStake(-amountBet);
			hand.addCard();
			Offer.STAND.execute(hand, player, game);
		}
	}
	 
	, ACCEPT_INSURANCE {
		public void execute(Hand hand, Player player, BlackJackGameSession game){
			player.addToStake(-hand.getInsuranceAmountBet());
			hand.setInsured(true);
			hand.setHandResolutionStatus(Hand.HAND_RESOLUTION_STATUS.HAND_NOT_RESOLVED);
		}
	} 
	
	, DECLINE_INSURANCE{
		public void execute(Hand hand, Player player, BlackJackGameSession game){
			// do nothing
		}
	}
	
	
	;
	
	
//todo basic: automatic win when the hand is 21 - don't ask for player's response. 	
		
	public abstract void execute(Hand hand, Player player, BlackJackGameSession game);
		
	

	 */
	 

}
