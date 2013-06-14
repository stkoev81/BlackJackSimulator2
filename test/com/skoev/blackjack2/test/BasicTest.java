package com.skoev.blackjack2.test;
import static org.junit.Assert.*;

import org.junit.Test;

import com.skoev.blackjack2.model.game.*;

import java.math.BigDecimal;
import java.util.Random;



public class BasicTest {
	@Test
	public void test() {
		Rank[] testCards = new Rank[]{Rank.ACE, Rank.ACE};
		//Deck testDeck = new TestDeck(testCards);
		Deck testDeck = new Deck(
				new Random(){
					public int nextInt() {
						return 1;
				}});
		Game game = new Game(new PlayingStrategyInteractive(), 5, BigDecimal.valueOf(10));
		do {
			game.play();
		}
		while(game.isUserInputNeeded());
	}

}
