package com.skoev.blackjack2.model.game;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.io.Serializable;

import com.skoev.blackjack2.common.ValueObject;

/**
 * Models a standard deck of cards that is used to deal cards in blackjack game. The deck consists of the normal 52 cards, and the card to be dealt is chosen
 * using a random number generator. When a card is dealt, it is not removed from the deck, so the probability from withdrawing a card stays constant over time. 
 * In a physical blackjack game, normally multiple decks of cards are mixed together to form a very large deck so the probability doesn't change much over time. 
 *
 */
class Deck implements ValueObject{
	private Random ng; 
	protected List<Card> cards;
	
	public Deck(){
		this.ng = new Random();
		cards = getStandardDeckCards();
	}
	
	/**
	 * Deals a card randomly chosen from the 52 cards
	 * @return
	 */
	public Card nextCard(){
		int nextInt = ng.nextInt(cards.size());
		return cards.get(nextInt);
	}
	private final List<Card>  getStandardDeckCards(){
		List<Card> result = new ArrayList<Card>();
		for(Rank rank : Rank.values()){
			for(Suit suit : Suit.values()){
				result.add(new Card(suit, rank));
			}
		}
		return result;
	}
	@Override
	public String toString() {
		StringBuilder result = new StringBuilder("Deck");
		
		for (Card card : cards){
			result.append(card.toString());
		}
		return result.toString();
	}
	
	
}




