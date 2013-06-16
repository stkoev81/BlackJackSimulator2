package com.skoev.blackjack2.model.game;

import static org.junit.Assert.*;

import java.math.BigDecimal;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.skoev.blackjack2.model.game.Round.Offer;

public class GameTest {
	public static final BigDecimal MONEY_11 = BigDecimal.valueOf(11.0);
	public static final BigDecimal MONEY_10 = BigDecimal.valueOf(10.0);
	public static final BigDecimal MONEY_9 = BigDecimal.valueOf(9.0);
	public static final BigDecimal MONEY_8p5 = BigDecimal.valueOf(8.5);
	public static final BigDecimal MONEY_8 = BigDecimal.valueOf(8.0);
	public static final BigDecimal MONEY_1 = BigDecimal.valueOf(1.0);

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
	public void testNumRounds() {
		//test number of rounds played (1 round)
		PlayingStrategy playingStrategy = new PlayingStrategyFixed(Round.Offer.STAND, MONEY_1, false);
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
		playingStrategy = new PlayingStrategyFixed(Round.Offer.STAND, MONEY_1, false);
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
		playingStrategy = new PlayingStrategyFixed(Round.Offer.HIT, MONEY_1, false);
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
	
	/**
	 * Tests that the interactive strategy properly stops and waits for input and then continues
	 */
	@Test
	public void testGameInteractive(){	
		//test number of rounds played (1 round)
		PlayingStrategy playingStrategy = new PlayingStrategyInteractive();
		Game game = new Game(playingStrategy, 1, MONEY_10);
		game.play();
		game.setDeck(new MockDeckFixed(Rank.TEN)); // deterministic deck needed here; with random may sometimes deal an ACE to dealer, which will cause insurance offer to be made
		assertEquals(game.isFinished(), false);
		assertEquals(game.isUserInputNeeded(), true);
		Round round = game.getCurrentRound();
		assertEquals(Round.RoundStatus.HAND_BEING_DEALT, round.getRoundStatus());
		
		playingStrategy.setAmountBet(MONEY_1);
		game.play();

		assertEquals(game.isFinished(), false);
		assertEquals(game.isUserInputNeeded(), true);
		round = game.getCurrentRound();
		assertEquals(Round.RoundStatus.HANDS_BEING_PLAYED_OUT, round.getRoundStatus());
		
		playingStrategy.setResponseToOffer(Offer.STAND);
		game.play();
		
		assertEquals(game.isFinished(), true);
		assertEquals(game.isUserInputNeeded(), false);
		assertNull(game.getCurrentRound());
		round = game.getLastRound();
		assertEquals(round.getRoundStatus(), Round.RoundStatus.ROUND_FINISHED);
		assertNull(round.getCurrentHand());
		assertEquals(round.getHands().size(), 1);
		assertEquals(game.getNumRoundsPlayed(), 1);
		assertEquals(game.getNumRoundsToPlay(), 1); 
		assertEquals(game.getPastRounds().size(), 1); 
	}
	
	@Test
	public void testHandWin(){
		PlayingStrategy playingStrategy = new PlayingStrategyFixed(Round.Offer.STAND, MONEY_1, false);
		Game game = new Game(playingStrategy, 1, MONEY_10);
		Deck deck = new MockDeck(Rank.TWO, Rank.TEN, Rank.NINE, Rank.SIX, Rank.NINE); 
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
		assertEquals(dealerHand.calculateCurrentPoints(), 24);
		
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
	public void testHandLose(){
		PlayingStrategy playingStrategy = new PlayingStrategyFixed(Round.Offer.STAND, MONEY_1, false);
		Game game = new Game(playingStrategy, 1, MONEY_10);
		Deck deck = new MockDeck(Rank.THREE, Rank.TEN, Rank.NINE, Rank.EIGHT); 
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
		assertEquals(dealerHand.calculateCurrentPoints(), 17);
		
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
	public void testHandPush(){
		PlayingStrategy playingStrategy = new PlayingStrategyFixed(Round.Offer.STAND, MONEY_1, false);
		Game game = new Game(playingStrategy, 1, MONEY_10);
		Deck deck = new MockDeck(Rank.ACE, Rank.TEN, Rank.ACE, Rank.JACK); 
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
		assertEquals(dealerHand.calculateCurrentPoints(), 21);
		
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
	public void testInsuranceWinGameLose(){
		PlayingStrategy playingStrategy = new PlayingStrategyFixed(Round.Offer.STAND, MONEY_1, true);
		Game game = new Game(playingStrategy, 1, MONEY_10);
		Deck deck = new MockDeck(Rank.THREE, Rank.TEN, Rank.ACE, Rank.JACK); 
		game.setDeck(deck); 
		game.play();
		Round round = game.getLastRound();
		Hand hand = round.getHand(1);
		Hand dealerHand = round.getDealerHand();
		
		//dealing of cards
		assertEquals(hand, HandTest.toHand(Rank.THREE, Rank.TEN));
		assertEquals(dealerHand, HandTest.toHand(Rank.ACE, Rank.JACK));
		
		//calculation of points
		assertEquals(hand.getFinalPoints().intValue(), 13);
		assertEquals(dealerHand.getFinalPoints().intValue(), 21);
		
		
		//status changes
		assertEquals(hand.getHandOutcome(), Hand.HAND_OUTCOME.LOSS);
		assertEquals(hand.getInsuranceOutcome(), Hand.INSURANCE_OUTCOME.WIN);
		
		//money adjustment
		assertEquals(round.getMoneyStart(), MONEY_10); 
		assertEquals(round.getMoneyEnd(), MONEY_10);
		assertEquals(game.getMoneyStart(), MONEY_10);
		assertEquals(game.getMoneyCurrent(), MONEY_10);
	}
	
	@Test
	public void testInsuranceLoseGameLose(){
		PlayingStrategy playingStrategy = new PlayingStrategyFixed(Round.Offer.STAND, MONEY_1, true);
		Game game = new Game(playingStrategy, 1, MONEY_10);
		Deck deck = new MockDeck(Rank.THREE, Rank.TEN, Rank.ACE, Rank.EIGHT); 
		game.setDeck(deck); 
		game.play();
		Round round = game.getLastRound();
		Hand hand = round.getHand(1);
		Hand dealerHand = round.getDealerHand();
		
		//dealing of cards
		assertEquals(hand, HandTest.toHand(Rank.THREE, Rank.TEN));
		assertEquals(dealerHand, HandTest.toHand(Rank.ACE, Rank.EIGHT));
		
		//calculation of points
		assertEquals(hand.getFinalPoints().intValue(), 13);
		assertEquals(dealerHand.getFinalPoints().intValue(), 19);
		
		
		//status changes
		assertEquals(hand.getHandOutcome(), Hand.HAND_OUTCOME.LOSS);
		assertEquals(hand.getInsuranceOutcome(), Hand.INSURANCE_OUTCOME.LOSS);
		
		//money adjustment
		assertEquals(round.getMoneyStart(), MONEY_10); 
		assertEquals(round.getMoneyEnd(), MONEY_8p5);
		assertEquals(game.getMoneyStart(), MONEY_10);
		assertEquals(game.getMoneyCurrent(), MONEY_8p5);
	}

	@Test
	public void testHandDouble(){
		PlayingStrategy playingStrategy = new PlayingStrategyFixed(Round.Offer.DOUBLE, MONEY_1, false);
		Game game = new Game(playingStrategy, 1, MONEY_10);
		Deck deck = new MockDeck(Rank.THREE, Rank.TEN, Rank.TEN, Rank.TWO, Rank.EIGHT); 
		game.setDeck(deck); 
		game.play();
		Round round = game.getLastRound();
		Hand hand = round.getHand(1);
		Hand dealerHand = round.getDealerHand();
		
		//dealing of cards
		assertEquals(hand, HandTest.toHand(Rank.THREE, Rank.TEN, Rank.TWO));
		assertEquals(dealerHand, HandTest.toHand(Rank.TEN, Rank.EIGHT));
		
		//calculation of points
		assertEquals(hand.getFinalPoints().intValue(), 15);
		assertEquals(dealerHand.getFinalPoints().intValue(), 18);
		
		
		//status changes
		assertEquals(hand.getHandOutcome(), Hand.HAND_OUTCOME.LOSS);
		assertEquals(hand.getInsuranceOutcome(), Hand.INSURANCE_OUTCOME.NOT_OFFERED);
		
		//money adjustment
		assertEquals(round.getMoneyStart(), MONEY_10); 
		assertEquals(round.getMoneyEnd(), MONEY_8);
		assertEquals(game.getMoneyStart(), MONEY_10);
		assertEquals(game.getMoneyCurrent(), MONEY_8);
	}
	
	@Test
	public void testHandSplit(){
		PlayingStrategy playingStrategy = new PlayingStrategyInteractive();
		Game game = new Game(playingStrategy, 1, MONEY_10);
		Deck deck = new MockDeck(Rank.THREE, Rank.THREE, Rank.TEN, Rank.TWO, Rank.EIGHT, Rank.SEVEN); 
		game.setDeck(deck); 
		
		game.play();
		playingStrategy.setAmountBet(MONEY_1);
		game.play();
		playingStrategy.setResponseToOffer(Offer.SPLIT);
		game.play();
		playingStrategy.setResponseToOffer(Offer.STAND);
		game.play();
		playingStrategy.setResponseToOffer(Offer.STAND);
		
		
		Round round = game.getLastRound();
		Hand hand1 = round.getHand(1);
		Hand hand2 = round.getHand(2);
		Hand dealerHand = round.getDealerHand();
		
		//dealing of cards
		assertEquals(hand1, HandTest.toHand(Rank.THREE, Rank.TWO));
		assertEquals(hand2, HandTest.toHand(Rank.THREE, Rank.EIGHT));
		assertEquals(dealerHand, HandTest.toHand(Rank.TEN, Rank.SEVEN));
		
		
		//status changes
		assertEquals(hand1.getHandOutcome(), Hand.HAND_OUTCOME.LOSS);
		assertEquals(hand2.getHandOutcome(), Hand.HAND_OUTCOME.LOSS);
		
		//money adjustment
		assertEquals(round.getMoneyStart(), MONEY_10); 
		assertEquals(round.getMoneyEnd(), MONEY_8);
		assertEquals(game.getMoneyStart(), MONEY_10);
		assertEquals(game.getMoneyCurrent(), MONEY_8);
	}
	
	
	
}
