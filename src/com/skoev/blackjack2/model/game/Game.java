package com.skoev.blackjack2.model.game;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
/**
 * The main class in the BlackJack model. Provides functionality for playing a single blackjack game. 
 * 
 * @author stefan.t.koev
 *
 */
public class Game {
	private int gameID = 1; 
	private int numRoundsPlayed; 
	private final int numRoundsToPlay; 
	private BigDecimal moneyStart;
	private BigDecimal moneyCurrent;
	private Round currentRound;
	private List<Round> pastRounds = new ArrayList();
	private PlayingStrategy playingStrategy;
	private Deck deck = new Deck();
	private boolean userInputNeeded = false;
	
	public boolean isInteractive(){
		return playingStrategy.isInteractive();
	}
	
	public Game(PlayingStrategy playingStrategy, int numRoundsToPlay, BigDecimal moneyStart){
		this.moneyStart = moneyStart;
		this.moneyCurrent = moneyStart;
		this.playingStrategy = playingStrategy;
		this.numRoundsToPlay = numRoundsToPlay;
	}
	
	
	//todo normal: comment
	//todo normal: add more tests
	//todo normal: verify model validations (e.g. not playing if not enough money). 
	public void play(){
		if (isFinished()){
			throw new RuntimeException("this game is already finished. Cannot continue playing");
		}
		userInputNeeded = false;
		while (!isFinished() && !userInputNeeded){
			if (currentRound == null){
				currentRound = new Round(this);
				currentRound.setRoundNumber(numRoundsPlayed + 1);
			}
			currentRound.play(playingStrategy);
			if(!userInputNeeded){
				numRoundsPlayed ++;
				pastRounds.add(currentRound);
				
				currentRound = null; //round is finished
				
				
			}
		}
	}
	
	public boolean isFinished(){
		return numRoundsPlayed >= numRoundsToPlay || moneyCurrent.doubleValue() <= 0;
	}
	
	void addMoney(BigDecimal money) {
		moneyCurrent = moneyCurrent.add(money);
	}
	void subtractMoney(BigDecimal money){
		moneyCurrent = moneyCurrent.subtract(money);
	}
	
	Card dealCard(){
		return deck.nextCard(); 
	}
	
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

	public boolean isUserInputNeeded() {
		return userInputNeeded;
	}

	void setUserInputNeeded(boolean userInputNeeded) {
		this.userInputNeeded = userInputNeeded;
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

	void setDeck(Deck deck) {
		this.deck = deck;
	}
	
	
	

}
