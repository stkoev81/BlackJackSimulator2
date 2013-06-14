package com.skoev.blackjack2.ui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.skoev.blackjack2.model.game.Round;

/**
 * Contains general methods to interact with the user. 
 */

public class ViewGeneral {
	protected static BufferedReader in; 
	protected static PrintWriter out;
	public static final String FOOTER = "######################################################";
	public static final char FOOTER_CHAR = '#';
	
	static {
		//It's OK to leave these streams open because the underlying streams, System.in and System.out, are always open anyway.
		in = new BufferedReader(new InputStreamReader(System.in));
		out = new PrintWriter(new OutputStreamWriter(System.out), true);
	}
	
	public static BigDecimal getAmount(String message){
		out.println(message);
		double result = 0;
		boolean success = false;
		while(!success){
			try{
				String response = in.readLine();
				checkActionCanceled(response);
				result = Double.parseDouble(response);
				if (result <= 0){
					throw new IllegalArgumentException();
				}
				success = true;
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
		return getPositiveInteger(null, message);
	}
	
	public static int getPositiveInteger(Collection<Integer> allowedValues, String message){
		out.println(message);
		int result = 0;
		boolean success = false;
		while(!success){
			try{
				String response = in.readLine();
				checkActionCanceled(response);
				result = Integer.parseInt(response);
				if (result <= 0){
					throw new IllegalArgumentException();
				}
				if(allowedValues != null && !allowedValues.contains(result)){
					throw new IllegalArgumentException();
				}
				success = true;
			}
			catch(IOException e){
				out.println("Error! : there was an unknown problem reading response. If problem persists, contact support. ");
			}
			catch(NumberFormatException e){
				out.println("Error! : the response you entered was not formatted correclty. Must be an integer. Try again. ");
			}
			catch(IllegalArgumentException e){
				out.println("Error! : the response you entered is invalid. Must be > 0. ");
				if(allowedValues != null){
					out.println("Must be within the follwoing values: " + allowedValues);	
				}
				
			}
		}
		
		return result;
	}
	
	public static <T> T getOption(T[] options, String message){
		out.println(message);
		out.println("Choose one of the following options by entering the number next to it:");
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
			displayFooter();
			throw new UserCanceledActionException();
		}
		
	}
	
	public static void display(Object toDisplay){
		out.println(toDisplay.toString());
	}
	
	public static void displayFooter(){
//		out.println("##############################");
		out.println(FOOTER);
	}
	public static void displayHeader(String title){
		if (title == null){
			title = "";
		}
		if(!title.equals("")){
			title = " " + title + " ";
		}
		int paddingWidth = FOOTER.length() - title.length();
		int paddingLeft, paddingRight;
		if(paddingWidth < 0 ){
			paddingWidth = paddingLeft = paddingRight = 0; 
		}
		else{
			paddingLeft = paddingWidth/2;
			paddingRight = FOOTER.length() - paddingLeft - title.length();
		}
		out.println(createString(FOOTER_CHAR, paddingLeft) + title + createString(FOOTER_CHAR, paddingRight));
	}
	private static String createString(char c, int repetitons){
		StringBuilder sb = new StringBuilder(repetitons);
		for(int i=0; i<repetitons; i++){
			sb.append(c);
		}
		return sb.toString();
	}
	

}
