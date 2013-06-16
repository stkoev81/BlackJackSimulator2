package com.skoev.blackjack2.model.game;

/**
 * Always deals the same card passed in the constructor. 
 * 
 *
 */
public class MockDeckFixed extends Deck {
	private Card card;
	public MockDeckFixed(Rank rank) {
		card = new Card(Suit.HEARTS, rank);
	}

	@Override
	public Card nextCard() {
		return card;
	}

}
