package com.skoev.blackjack2.model.game;

import java.io.Serializable;
/**
 * Rank of a card.  
 */
public enum Rank implements Serializable{
	TWO(2), THREE(3), FOUR(4), FIVE(5), SIX(6), SEVEN(7), EIGHT(8), NINE(9), TEN(10), ACE(null), JACK(10), KING(10), QUEEN(10);
	private final Integer value;
	Rank(Integer value){
		this.value = value;
	}
	/**
	 * Returns the points value of this rank. All the ranks except Ace have well defined values. Returns null for an ace since its value is 
	 * undefined (it could be either 1 or 11 depending on the hand).  
	 */
	public Integer getValue(){
		return this.value;
	}
}
