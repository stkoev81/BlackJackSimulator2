package com.skoev.blackjack2.model;

import com.skoev.blackjack.model.Rank;
import com.skoev.blackjack.model.Suit;

/**
 * 
 * -todo normal: add the cards attributes, methods to get value
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
