package com.skoev.blackjack2.model.game;

import static org.junit.Assert.*;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class HandTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testPointCalculation() {
		//assertEquals(11, (new Hand(null, 0, toCards(Rank.ACE)).getCurrentPoints()));   this one is not necessary - never a single card in a hand
//		assertEquals(12, (new Hand(null, 0, toCards(Rank.ACE, Rank.ACE)).getCurrentPoints()));
		assertEquals(21, (new Hand(null, 0, toCards(Rank.ACE, Rank.JACK)).getCurrentPoints()));
		assertEquals(13, (new Hand(null, 0, toCards(Rank.ACE, Rank.JACK, Rank.TWO)).getCurrentPoints()));
	}
	
	public static Card[] toCards(Rank ... ranks){
		Card[] cards = new Card[ranks.length];
		for(int i = 0; i<ranks.length; i++){
			cards[i] = new Card(Suit.HEARTS, ranks[i]);
		}
		return cards;
	}
	
	public static Hand toHand(Rank ... ranks){
		return new Hand(null, 0, toCards(ranks)); 
	}

}
