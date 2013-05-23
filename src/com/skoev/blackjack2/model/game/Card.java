package com.skoev.blackjack2.model.game;


/**
 * 
 * 
 * 
 * @author stefan.t.koev
 *
 */
public class Card {
	private Suit suit;
	private Rank rank;
	
	public Integer getValue(){
		return rank.getValue();
	}

	public Card(Suit suit, Rank rank) {
		this.suit = suit;
		this.rank = rank;
	}

	public Suit getSuit() {
		return suit;
	}

	public Rank getRank() {
		return rank;
	}

	@Override
	public String toString() {
		return "[" + rank + " OF " + suit + "]";
	}
	
	
	
	
	
	
	
}
