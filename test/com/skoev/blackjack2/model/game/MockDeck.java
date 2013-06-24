package com.skoev.blackjack2.model.game;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import com.skoev.blackjack2.model.game.*;


/**
 * Deck that is easy to use for testing. Deals the cards passed in the constructor. In using this, we must keep in mind the sequence in which the cards are dealt.
 * First two are dealt to the player. Next one to the dealer. Next cards are dealt to the player until he stands. If player doesn't go over 21, cards are dealt to the
 * dealer again. 
 * @author stefan.t.koev
 *
 */
class MockDeck extends Deck {
	private Iterator<Card> it;
	
	public MockDeck(Rank ... ranks){
		if (ranks == null) {
			cards = Collections.EMPTY_LIST;
		}
		else {
			cards = new LinkedList<Card>();
			for(Rank rank : ranks){
				if (rank == null){
					continue;
				}
				cards.add(new Card(Suit.HEARTS, rank));
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
	
	

	