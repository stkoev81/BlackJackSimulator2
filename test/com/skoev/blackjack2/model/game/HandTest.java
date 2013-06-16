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
		assertEquals(20, (new Hand(null, 0, toCards(Rank.ACE, Rank.NINE)).calculateCurrentPoints()));
		assertEquals(21, (new Hand(null, 0, toCards(Rank.ACE, Rank.JACK)).calculateCurrentPoints()));
		assertEquals(12, (new Hand(null, 0, toCards(Rank.ACE, Rank.ACE)).calculateCurrentPoints()));
		assertEquals(13, (new Hand(null, 0, toCards(Rank.ACE, Rank.ACE, Rank.ACE)).calculateCurrentPoints()));
		assertEquals(14, (new Hand(null, 0, toCards(Rank.ACE, Rank.ACE, Rank.ACE, Rank.ACE)).calculateCurrentPoints()));
		assertEquals(12, (new Hand(null, 0, toCards(Rank.ACE, Rank.NINE, Rank.TWO)).calculateCurrentPoints()));
		assertEquals(21, (new Hand(null, 0, toCards(Rank.ACE, Rank.NINE, Rank.ACE)).calculateCurrentPoints()));
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
