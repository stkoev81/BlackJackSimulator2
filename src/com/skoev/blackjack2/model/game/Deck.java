package com.skoev.blackjack2.model.game;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;


/**
 * 
 * @author stefan.t.koev
 *
 */
public class Deck {
	private Random ng; 
	protected List<Card> cards;
	public Deck(){
		this.ng = new Random();
		cards = getStandardDeckCards();
	}
	public Deck(Random ng){
		this.ng = ng;
		cards = getStandardDeckCards();
	}
	
	public Deck(Random ng, List<Card> cards){
		this.ng = ng;
		this.cards = cards;
	}
	
	public Deck(List<Card> cards){
		this.ng = new Random();
		this.cards = cards;
	}
	
	public Card nextCard(){
		// todo normal:  check the bounds on the next int method, may have a one-off error here
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




