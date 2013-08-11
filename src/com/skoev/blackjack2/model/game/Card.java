package com.skoev.blackjack2.model.game;

import java.io.Serializable;
/**
 * A playing card. Although in blackjack only the rank matters and not the suit, the card is given both rank and suit so that it can 
 * resemble a real card. 
 */
public class Card implements Serializable{
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

	@Override
	public int hashCode() {
		return 0;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Card other = (Card) obj;
		if (rank != other.rank)
			return false;
		if (suit != other.suit)
			return false;
		return true;
	}
	
	
	
	
	
	
	
	
	
}
