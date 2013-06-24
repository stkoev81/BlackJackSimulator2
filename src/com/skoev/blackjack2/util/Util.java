package com.skoev.blackjack2.util;

/**
 * Various utility methods. 
 *
 */
public class Util {
	/**
	 * 
	 * @param arg
	 * @return true if arg is null or empty string, false otherwise. 
	 */
	public static boolean isEmpty(String arg){
		if(arg == null || arg.isEmpty()){
			return true;
		}
		else{
			return false;
		}
	}
	/**
	 * 
	 * @param arg
	 * @throws IllegalArgumentException if arg is null or empty string
	 */
	public static void assertNotEmpty(String arg){
		if(isEmpty(arg)){
			throw new IllegalArgumentException();
		}
	}
	
	/**
	 * 
	 * @param arg
	 * @throws IllegalArgumentException if arg is null
	 */
	public static void assertNotNull(Object arg){
		if(arg == null){
			throw new IllegalArgumentException();
		}
	}
	
	/**
	 * 
	 * @param arg
	 * @throws IllegalArgumentException if arg is false
	 */
	public static void assertTrue(boolean arg){
		if (!arg){
			throw new IllegalArgumentException();
		}
	}
	
	
}
