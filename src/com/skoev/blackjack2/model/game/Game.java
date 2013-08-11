package com.skoev.blackjack2.model.game;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.io.Serializable;

import com.skoev.blackjack2.common.Entity;
import com.skoev.blackjack2.common.Util;
/**
 * The main class in the BlackJack model. Provides functionality for playing a single blackjack game. A game contains multiple rounds: the round that
 * is currently being played and any past rounds. A game is also associated with a playing strategy. This strategy is consulted to determine how much
 * money the player bets and and which offer the player chooses.    
 *
 */
public class Game implements Entity{
	private int gameID = 1; 
	private int numRoundsPlayed; 
	private final int numRoundsToPlay; 
	private BigDecimal moneyStart;
	private BigDecimal moneyCurrent;
	private Round currentRound;
	private List<Round> pastRounds = new ArrayList();
	private PlayingStrategy playingStrategy;
	private Deck deck = new Deck();
	
	/**
	 * @return true if the associated playing strategy is interactive; false otherwise   
	 */
	public boolean isInteractive(){
		return playingStrategy.isInteractive();
	}
	
	/**
	 * @param playingStrategy The playing strategy that models the player behavior.
	 * @param numRoundsToPlay Maximum number of rounds that will be played. 
	 * @param moneyStart The amount of money the user has at the beginning of the game.  
	 */
	public Game(PlayingStrategy playingStrategy, int numRoundsToPlay, BigDecimal moneyStart){
		Util.assertNotNull(playingStrategy);
		Util.assertNotNull(moneyStart);
		Util.assertTrue(moneyStart.compareTo(BigDecimal.valueOf(0)) > 0);
		Util.assertTrue(numRoundsToPlay > 0);
		
		this.moneyStart = moneyStart;
		this.moneyCurrent = moneyStart;
		this.playingStrategy = playingStrategy;
		this.numRoundsToPlay = numRoundsToPlay;
	}
	
	/**
	 * Plays this game using the associated playing strategy. When a round is finished, another one is automatically started and so on until the game is finished.  
	 * When this method returns, one of the following is true: 1)The game is finished ({@link #isFinished()} returns true.) 2) User input is needed (
	 * {@link #isUserInputNeeded()}) returns true). In the second case, the game is essentially paused and this method should be called again to continue it
	 * once user input has been supplied to the associated playing strategy. 
	 * @throws InsufficientMoneyException If the playing strategy tries to make a bet that exceeds the current money of the game.
	 * @throws IllegalStateException if this game is already finished 
	 */
	public void play() throws InsufficientMoneyException{
		if (isFinished()){
			throw new IllegalStateException();
		}
		while (!isFinished() && !isUserInputNeeded()){
			if (currentRound == null){
				currentRound = new Round(this);
				currentRound.setRoundNumber(numRoundsPlayed + 1);
			}
			try{
				currentRound.play(playingStrategy);
			}
			catch(InsufficientMoneyException e){
				if(playingStrategy.isInteractive()){
					playingStrategy.setIsUserInputNeeded(true);
				}
				throw e;
			}
			if(!isUserInputNeeded()){
				numRoundsPlayed ++;
				pastRounds.add(currentRound);
				currentRound = null; //round is finished
			}
		}
	}
	/**
	 * @return true if the currentMoney is depleted to 0 or the number of rounds to play has been reached and there is no no current round; false otherwise
	 */
	public boolean isFinished(){
		return ((numRoundsPlayed >= numRoundsToPlay) || (moneyCurrent.compareTo(BigDecimal.ZERO) <= 0)) && currentRound == null;
	}
	
	/**
	 * Adds money to the game, e.g. due to a win. 
	 * @param money
	 */
	void addMoney(BigDecimal money) {
		moneyCurrent = moneyCurrent.add(money);
	}
	/**
	 * Subtracts money from the game, e.g. to place a bet or to pay for insurance. 
	 * @param money
	 * @throws InsufficientMoneyException if the money we are trying to subtract exceed the current money. 
	 */
	void subtractMoney(BigDecimal money) throws InsufficientMoneyException{
		if (moneyCurrent.compareTo(money) < 0){
			throw new InsufficientMoneyException();
		} 
		moneyCurrent = moneyCurrent.subtract(money);
	}
	
	/**
	 * Deal a new card from the deck. 
	 * @return
	 */
	Card dealCard(){
		return deck.nextCard(); 
	}
	
	/**
	 * Retrieve the last completed round. 
	 * @return
	 */
	public Round getLastRound(){
		int length = pastRounds.size();
		if(length == 0){
			return null;
		}
		Round round = pastRounds.get(length - 1);
		return round;
	}
	
	public int getGameID() {
		return gameID;
	}

	public void setGameID(int gameID) {
		this.gameID = gameID;
	}

	public int getNumRoundsPlayed() {
		return numRoundsPlayed;
	}

	public int getNumRoundsToPlay() {
		return numRoundsToPlay;
	}

	public BigDecimal getMoneyStart() {
		return moneyStart;
	}

	public BigDecimal getMoneyCurrent() {
		return moneyCurrent;
	}

	/**
	 * Tells us if user input is needed in order to continue the game. Calls {@link PlayingStrategy#isUserInputNeeded()} for the associated playing strategy.  
	 */
	public boolean isUserInputNeeded() {
		return playingStrategy.isUserInputNeeded();
	}

	public Round getCurrentRound() {
		return currentRound;
	}

	public List<Round> getPastRounds() {
		return pastRounds;
	}

	public PlayingStrategy getPlayingStrategy() {
		return playingStrategy;
	}

	/**
	 * Method to supply different deck implementations for testing purposes. 
	 * @param deck
	 */
	void setDeck(Deck deck) {
		this.deck = deck;
	}
	
	
	

}
