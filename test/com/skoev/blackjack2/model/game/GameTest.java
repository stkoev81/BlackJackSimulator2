package com.skoev.blackjack2.model.game;

import static org.junit.Assert.*;

import java.math.BigDecimal;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class GameTest {
	public static final BigDecimal MONEY_11 = BigDecimal.valueOf(11);
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

	
	/**
	 * Tests that the game starts and stops properly given the number of rounds requested and money available
	 */
	@Test
	public void testGameCycle() {
		//test number of rounds played (1 round)
		PlayingStrategy playingStrategy = new PlayingStrategyPredictable(Round.Offer.STAND, MONEY_1, false);
		Game game = new Game(playingStrategy, 1, MONEY_10);
		game.play();
		assertEquals(game.getNumRoundsPlayed(), 1);
		assertEquals(game.getNumRoundsToPlay(), 1); 
		assertEquals(game.getPastRounds().size(), 1); 
		assertEquals(game.isUserInputNeeded(), false);
		assertEquals(game.isFinished(), true);
		assertNull(game.getCurrentRound());
		Round round = game.getLastRound();
		assertEquals(round.getRoundStatus(), Round.RoundStatus.ROUND_FINISHED);
		assertNull(round.getCurrentHand());
		assertEquals(round.getHands().size(), 1);
		
		//test number of rounds played (5 rounds)
		playingStrategy = new PlayingStrategyPredictable(Round.Offer.STAND, MONEY_1, false);
		game = new Game(playingStrategy, 5, MONEY_10);
		game.play();
		assertEquals(game.getNumRoundsPlayed(), 5);
		assertEquals(game.getNumRoundsToPlay(), 5); 
		assertEquals(game.getPastRounds().size(), 5); 
		assertEquals(game.isUserInputNeeded(), false);
		assertEquals(game.isFinished(), true);
		assertNull(game.getCurrentRound());
		round = game.getLastRound();
		assertEquals(round.getRoundStatus(), Round.RoundStatus.ROUND_FINISHED);
		assertNull(round.getCurrentHand());
		assertEquals(round.getHands().size(), 1);
		
		//test number of rounds played (money runs out)
		playingStrategy = new PlayingStrategyPredictable(Round.Offer.HIT, MONEY_1, false);
		game = new Game(playingStrategy, 20, MONEY_10);
		game.setDeck(new MockDeckFixed(Rank.TEN)); // by getting only 10s and hitting each time will definitely lose
		game.play();
		assertEquals(game.getNumRoundsPlayed(), 10);
		assertEquals(game.getNumRoundsToPlay(), 20); 
		assertEquals(game.getPastRounds().size(), 10); 
		assertEquals(game.isUserInputNeeded(), false);
		assertEquals(game.isFinished(), true);
		assertNull(game.getCurrentRound());
		round = game.getLastRound();
		assertEquals(round.getRoundStatus(), Round.RoundStatus.ROUND_FINISHED);
		assertNull(round.getCurrentHand());
		assertEquals(round.getHands().size(), 1);
	}
	@Test
	public void testGameWin(){
		PlayingStrategy playingStrategy = new PlayingStrategyPredictable(Round.Offer.STAND, MONEY_1, false);
		Game game = new Game(playingStrategy, 1, MONEY_10);
		Deck deck = new MockDeckVaried(Rank.TWO, Rank.TEN, Rank.NINE, Rank.SIX, Rank.NINE); 
		game.setDeck(deck); 
		game.play();
		Round round = game.getLastRound();
		Hand hand = round.getHand(1);
		Hand dealerHand = round.getDealerHand();
		
		//dealing of cards
		assertEquals(hand, HandTest.toHand(Rank.TWO, Rank.TEN));
		assertEquals(dealerHand, HandTest.toHand(Rank.NINE, Rank.SIX, Rank.NINE));
		
		//calculation of points
		assertEquals(hand.getFinalPoints().intValue(), 12);
		assertEquals(dealerHand.getFinalPoints().intValue(), 0);
		assertEquals(dealerHand.getCurrentPoints(), 24);
		
		//status changes
		assertEquals(hand.getHandOutcome(), Hand.HAND_OUTCOME.WIN);
		assertEquals(hand.getInsuranceOutcome(), Hand.INSURANCE_OUTCOME.NOT_OFFERED);
		
		//money adjustment
		assertEquals(round.getMoneyStart(), MONEY_10); 
		assertEquals(round.getMoneyEnd(), MONEY_11);
		assertEquals(game.getMoneyStart(), MONEY_10);
		assertEquals(game.getMoneyCurrent(), MONEY_11);
	}
	
	@Test
	public void testGameLose(){
		PlayingStrategy playingStrategy = new PlayingStrategyPredictable(Round.Offer.STAND, MONEY_1, false);
		Game game = new Game(playingStrategy, 1, MONEY_10);
		Deck deck = new MockDeckVaried(Rank.THREE, Rank.TEN, Rank.NINE, Rank.EIGHT); 
		game.setDeck(deck); 
		game.play();
		Round round = game.getLastRound();
		Hand hand = round.getHand(1);
		Hand dealerHand = round.getDealerHand();
		
		//dealing of cards
		assertEquals(hand, HandTest.toHand(Rank.THREE, Rank.TEN));
		assertEquals(dealerHand, HandTest.toHand(Rank.NINE, Rank.EIGHT));
		
		//calculation of points
		assertEquals(hand.getFinalPoints().intValue(), 13);
		assertEquals(dealerHand.getFinalPoints().intValue(), 17);
		assertEquals(dealerHand.getCurrentPoints(), 17);
		
		//status changes
		assertEquals(hand.getHandOutcome(), Hand.HAND_OUTCOME.LOSS);
		assertEquals(hand.getInsuranceOutcome(), Hand.INSURANCE_OUTCOME.NOT_OFFERED);
		
		//money adjustment
		assertEquals(round.getMoneyStart(), MONEY_10); 
		assertEquals(round.getMoneyEnd(), MONEY_9);
		assertEquals(game.getMoneyStart(), MONEY_10);
		assertEquals(game.getMoneyCurrent(), MONEY_9);
	}
	
	
	
	@Test
	public void testGamePush(){
		PlayingStrategy playingStrategy = new PlayingStrategyPredictable(Round.Offer.STAND, MONEY_1, false);
		Game game = new Game(playingStrategy, 1, MONEY_10);
		Deck deck = new MockDeckVaried(Rank.ACE, Rank.TEN, Rank.ACE, Rank.JACK); 
		game.setDeck(deck); 
		game.play();
		Round round = game.getLastRound();
		Hand hand = round.getHand(1);
		Hand dealerHand = round.getDealerHand();
		
		//dealing of cards
		assertEquals(hand, HandTest.toHand(Rank.ACE, Rank.TEN));
		assertEquals(dealerHand, HandTest.toHand(Rank.ACE, Rank.JACK));
		
		//calculation of points
		assertEquals(hand.getFinalPoints().intValue(), 21);
		assertEquals(dealerHand.getFinalPoints().intValue(), 21);
		assertEquals(dealerHand.getCurrentPoints(), 21);
		
		//status changes
		assertEquals(hand.getHandOutcome(), Hand.HAND_OUTCOME.PUSH);
		assertEquals(hand.getInsuranceOutcome(), Hand.INSURANCE_OUTCOME.DECLINED);
		
		//money adjustment
		assertEquals(round.getMoneyStart(), MONEY_10); 
		assertEquals(round.getMoneyEnd(), MONEY_10);
		assertEquals(game.getMoneyStart(), MONEY_10);
		assertEquals(game.getMoneyCurrent(), MONEY_10);
	}

	@Test
	public void testGameInteractive(){
		
	}
	
}
