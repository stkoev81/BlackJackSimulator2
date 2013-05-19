package com.skoev.blackjack2.model;

import java.util.Collection;

public class Util {
	public static void notImpl() {
		throw new RuntimeException("not Implemented");
	}
	public static<T> String toCollString(String prepend, Collection<T> coll, String append){
		StringBuilder result = new StringBuilder();
		
		for (T obj : coll){
			result.append(prepend);
			result.append(obj.toString());
			result.append(append);
		}
		return result.toString();
	}

}
