package com.skoev.blackjack2.model.game;

import static org.junit.Assert.*;

import java.math.BigDecimal;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class GameTest {
	public static final BigDecimal MONEY_10 = BigDecimal.valueOf(10);
	public static final BigDecimal MONEY_9 = BigDecimal.valueOf(9);
	public static final BigDecimal MONEY_1 = BigDecimal.valueOf(1);

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void test() {
		//test the game ending; 
		PlayingStrategy playingStrategy = new PlayingStrategyPredictable(Round.Offer.STAND, MONEY_1, true);
		Game game = new Game(playingStrategy, 1, MONEY_10);
		game.play();
		assertEquals(game.getNumRoundsPlayed(), 1);
		assertEquals(game.getNumRoundsToPlay(), 1); 
		assertEquals(game.getPastRounds().size(), 1); 
		assertEquals(game.isUserInputNeeded(), false);
		assertEquals(game.isFinished(), true);
//		assertEquals(game.moneyStart, MONEY_10);
//		assertEquals(game.moneyCurrent, MONEY_9);
		assertNull(game.getCurrentRound());
		
		Round round = game.getPastRounds().get(0);
//		assertEquals(round.moneyStart, MONEY_10);
//		assertEquals(round.moneyEnd, MONEY_9);
		assertEquals(round.getRoundStatus(), Round.RoundStatus.ROUND_FINISHED);
		assertNull(round.getCurrentHand());
		assertEquals(round.getHands().size(), 1);
		
		
		//test the winning and losing using the pre-made deck;
		
	}

}
