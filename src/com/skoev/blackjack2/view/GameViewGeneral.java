package com.skoev.blackjack2.view;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

import com.skoev.blackjack2.controller.UserCanceledActionException;
import com.skoev.blackjack2.model.game.Round;

public class GameViewGeneral {
	protected static BufferedReader in; 
	protected static PrintWriter out; 
	
	static {
		//It's OK to leave these streams open because the underlying streams, System.in and System.out, are always open anyway.
		in = new BufferedReader(new InputStreamReader(System.in));
		out = new PrintWriter(new OutputStreamWriter(System.out), true);
	}
	
	public static BigDecimal getAmount(String message){
		out.println(message);
		double result = 0;
		while(result == 0){
			try{
				String response = in.readLine();
				checkActionCanceled(response);
				result = Double.parseDouble(response);
				if (result <= 0){
					throw new IllegalArgumentException();
				}
			}
			catch(IOException e){
				System.out.println("Error! : there was an unknown problem reading response. If problem persists, contact support. ");
			}
			catch(NumberFormatException e){
				out.println("Error! : the response you entered was not formatted correclty. Must be a number. Try again. ");
			}
			catch(IllegalArgumentException e){
				System.out.println("Error! : the response you entered is invalid. Must be > 0"); 
			}
		}
		return BigDecimal.valueOf(result);
	}
	
	public static int getPositiveInteger(String message){
		out.println(message);
		int result = 0;
		while(result == 0){
			try{
				String response = in.readLine();
				checkActionCanceled(response);
				result = Integer.parseInt(response);
				if (result <= 0){
					throw new IllegalArgumentException();
				}
			}
			catch(IOException e){
				out.println("Error! : there was an unknown problem reading response. If problem persists, contact support. ");
			}
			catch(NumberFormatException e){
				out.println("Error! : the response you entered was not formatted correclty. Must be an integer. Try again. ");
			}
			catch(IllegalArgumentException e){
				out.println("Error! : the response you entered is invalid. Must be > 0"); 
			}
		}
		
		return result;
	}
	
	public static <T> T getOption(T[] options, String message){
		out.println(message);
		out.println("Please choose one of the following options by entering the number next to it:");
		int i = 1; 
		
		for(T option : options){
			out.println("\t" + "(" + i + ")" + " " + option);
			i++;
		}
		
		int optionNum = 0;
		T result = null;
		while(result == null){
			try{
				String response = in.readLine();
				checkActionCanceled(response);
				optionNum = Integer.parseInt(response);
				result = options[optionNum - 1];
			}
			catch(IOException e){
				out.println("Error! : there was an unknown problem reading response. If problem persists, contact support. ");
			}
			catch(NumberFormatException e){
				out.println("Error! : the response you entered was not formatted correclty. Must be an integer. Try again. ");
			}
			catch(java.lang.IndexOutOfBoundsException e ){
				out.println("Error! : the number you entered is not a valid option number. Try again. ");
			}
		}
		return result;
		
	}
	
	public static String getInput(String message){
		out.println(message);
		String result = null;
		try{
			result = in.readLine();
			checkActionCanceled(result);
		}
		catch(IOException e){
			out.println("Error! : there was an unknown problem reading response. If problem persists, contact support. ");
		}
		return result;
	}
	
	private static void checkActionCanceled(String s) {
		if(s == null || s.length() == 0){
			out.println("User canceled action by entering empty input. Returning to previous screen.");
			throw new UserCanceledActionException();
		}
		
	}
	
	public static void display(Object toDisplay){
		out.println(toDisplay.toString());
	}
	

}
