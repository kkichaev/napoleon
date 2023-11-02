package com.grsoft.napoleon.modules.print.util;

public class Dig2Str {
	protected static String[] digSet = {" один", " два", " три", " четыре", " пять", " шесть", " семь", " восемь",
		  " девять", " десять", " одиннадцать", " двенадцать", " тринадцать",
		  " четырнадцать", " пятнадцать", " шестнадцать", " семнадцать",
		  " восемнадцать", " девятнадцать"};
	protected static String[] decDigSet = {" двадцать", " тридцать", " сорок", " пятьдесят", " шестьдесят",
		  " семьдесят", " восемьдесят", " девяносто"};
	protected static String[] hunDigSet =	{" сто", " двести", " триста", " четыреста", " пятьсот", " шестьсот",
		  " семьсот", " восемьсот", " девятьсот"};
	protected static String[] othDig = {"", " тысяч", " миллион", " миллиард"};
	protected static String[] shortOth = {"", " тыс", " млн", " млрд" };
	protected static String[] firstRest =	{"", "а", "и"};
	protected static String[] lastRest = {"ов", "", "а"};
	protected static String[] shortRest =	{".", ".", "." };


	private static String addRest( long val, String str, String base, String[] restSet)
	{
		  int rVal = (int)(val%100);
		  str += base;
		
		  if( rVal > 10 && rVal < 20 )
		  {
		     str += restSet[0];
		     return str;
		  }
		
		  rVal %= 10;
		  switch ( rVal )
		  {
		     case 1:
		        str += restSet[1];
		        return str;
		     case 2:
		     case 3:
		     case 4:
		        str += restSet[2];
		        return str;
		  }
		  str += restSet[0];
		  
		  return str;
	}


	private static String conv1000( int val, int step, String str) {
	  int rest = val % 100;
	  val /= 100;
	
	  if ( val > 0 ) 
		  str = hunDigSet[val-1];
	  else 
		  str = "";
	
	  if ( rest == 0 ) 
		  return str;
	  
	  if ( rest < 20 ) {
	     if ( step == 1 )
	        switch ( rest ) {
	           case 1:
	              str += " одна";
	              return str;
	           case 2:
	              str += " две";
	              return str;
	        }
	     str += digSet[rest-1];
	  } else {
	     str += decDigSet[(rest/10)-2];
	     if ( rest % 10  > 0) {
	        rest %= 10;
	        if ( step == 1 )
	           switch ( rest ) {
	              case 1:
	                 str += " одна";
	                 return str;
	              case 2:
	                 str += " две";
	              return str;
	           }
	        str += digSet[rest-1];
	    }
	  }
	  
	  return str;
	}

	public static String digToText(long dig)
	{
		int step = 0;
		long lastDig;
		String result = new String();
		
		if ( dig == 0 ) 
			return " ноль";
		
		do
		{
			int rest = (int)(dig%1000);
		    lastDig = dig;
		    dig /= 1000;
		    
		    if ( rest != 0 ) {
		    	String curDig = conv1000(rest, step, "");
		
		        if ( step != 0 )
		           curDig = addRest( lastDig, curDig, othDig[step], (step == 1) ? firstRest : lastRest );
		
		        curDig += result;
		        result = curDig;
		    }
		    
		    step++;
		} while ( dig > 0 );
		
		return result;
	} 
}
