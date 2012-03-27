package com.compiler;

import java.io.IOException;
import java.util.Scanner;

public class Utils {
	
	static char look; //look ahead
	static int lCount;
	
	public static void printError(String msg){
		System.out.println(msg);
	}
	
	public static void getChar(){
		try {
			look=(char)System.in.read();
		} catch (IOException e) {
			e.printStackTrace();
			abort("Error reading character");
		}
	}
	
	public static void abort(String msg){
		printError(msg);
		System.exit(1);
	}
	
	public static void expected(String msg){
		abort(msg+" Expected");
	}
	
	public static void matchChar(char x){
		if(look==x){
			getChar();
			skipWhite();
		}
		else
			expected("'"+x+"'");
	}
	
	public static boolean isAlpha(char x){
		char x1=Character.toUpperCase(x);
		if(x1<='Z' && x1>='A')
			return true;
		return false;
	}
	
	public static boolean isDigit(char x){
		if(x<='9' && x>='0')
			return true;
		return false;
	}
	
	public static String getName(){
		String token="";
		
		if(!isAlpha(look))
			expected("Name");
		while(isAlpha(look)){
			token+=Character.toUpperCase(look);
			getChar();
		}
		skipWhite();
		return token;
	}
	
	public static String getNum(){
		String value="";
		if(!isDigit(look))
			expected("Integer");
		while(isDigit(look)){
			value+=look;
			getChar();
		}
		skipWhite();
		return value;
	}
	
	public static String newLabel(){
		String s;
		s="L"+lCount;
		lCount++;
		return s;
	}
	
	public static void postLabel(String label){
		System.out.println(label+":");
	}
	
	public static boolean isWhite(char c){
		if(c==' ' || c=='\t')
			return true;
		return false;
	}
	
	public static void skipWhite(){
		while(isWhite(look))
			getChar();
	}
	
	public static void emit(String s){
		System.out.print("\t"+s);
	}
	
	public static void emitLn(String s){
		emit(s);
		System.out.println("");
	}
	
	public static void init(){
		lCount=0;
		getChar();
		skipWhite();
	}
	
}