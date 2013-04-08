package com.skoev.blackjack2.test;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import com.skoev.blackjack2.model.*;


/**
 * Deck that is easy to use for testing. 
 * @author stefan.t.koev
 *
 */
public class TestDeck extends Deck {
	private List<Card> cards;
	private Iterator<Card> it;
	
	public TestDeck(Rank[] ranks){
		if (ranks == null) {
			cards = Collections.EMPTY_LIST;
		}
		else {
			cards = new LinkedList<Card>();
			for(Rank rank : ranks){
				if (rank == null){
					continue;
				}
				cards.add(new Card(Suit.CLUBS, rank));
			}
		}
		it = cards.iterator(); 
	}
	
	
	class NotEnoughTestCards extends RuntimeException{} 
	
	@Override
	public Card nextCard() {
		if(it.hasNext()){
			return it.next();
		}
		else{
			throw new NotEnoughTestCards(){};
		}
	}
	
}
	
	

	