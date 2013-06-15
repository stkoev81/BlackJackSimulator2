package com.skoev.blackjack2.model.game;


public enum Rank {
	TWO(2), THREE(3), FOUR(4), FIVE(5), SIX(6), SEVEN(7), EIGHT(8), NINE(9), TEN(10), ACE(null), JACK(10), KING(10), QUEEN(10);
	private final Integer value;
	Rank(Integer value){
		this.value = value;
	}
	public Integer getValue(){
		return this.value;
	}
}
