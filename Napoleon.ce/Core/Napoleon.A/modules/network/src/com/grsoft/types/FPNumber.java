/*
 * Copyright (C), 2010, Гильдия Разработчиков
 *
 * Число с фиксированной точкой (Fix point number)
 *
 * kki   09/11/2010   creating
 */
package com.grsoft.types;


//public class FPNumber
//{
//	private int val;
//	private int frac;
//	
//	public int toInteger()
//	{
//		return val;
//	}
//	
//	public int getFrac()
//	{
//		return frac;
//	}
//	
//	private FPNumber(int val, int frac)
//	{
//		this.val = val;
//		this.frac = frac;
//	}
//	
//	public static FPNumber create()
//	{
//		return new FPNumber(0,0);
//	}
//	
//	public static FPNumber parse(String str, int frac)
//	{
//		FPNumber result = FPNumber.create();
//		
//		result.setFrac(frac);
//		result.parseScale(str, frac);
//		
//		return result;
//	}
//	
//	public static FPNumber parse(int rawVal, Field info)
//	{
//		FPNumber result = new FPNumber(rawVal, 0);
//		result.setFrac(getFracFromFieldInfo(info));
//		
//		return result;
//	}
//	
//	private static int getFracFromFieldInfo(Field info)
//	{
//		Scale scale = info.getAnnotation(Scale.class);
//		
//		if (scale == null)
//			throw new RuntimeException(new Exception("Number should be scalable."));
//		
//		return scale.value();
//	}
//	
//	public boolean equals(int val)
//	{
//		return this.val == val; 
//	}
//	
//	private void setFrac(int val)
//	{
//		frac = val;
//	}
//	
//	private void parseScale(String str, int frac)
//	{
////		Scale scale = this.getClass().getAnnotation(Scale.class);
//		
////		if (scale == null)
////			throw new RuntimeException(new Exception("Number should be scalable."));
//		
//		if (frac == 0)
//			this.val = Integer.parseInt(str);
//		else
//		{
//			int pointPos = str.indexOf(".");
//			String intPart = str.substring(0,pointPos); 
//			String fracPart = str.substring(pointPos + 1);
//			
//			val = Integer.parseInt(intPart) * decPow(frac); 
//			val += Integer.parseInt(fracPart);
//		}
//	}
//	
//	private int decPow(int pow)
//	{
//		final int DEC = 10;
//		
//		int result = 1;
//		
//		if(pow > 0)
//			result = decPow(--pow) * DEC;
//		
//		return result;
//		
//	}
//	
//	@Override
//	public String toString()
//	{
//		return Integer.toString(val);
//	}
//
//	public Object mul(FPNumber num)
//	{
////		val *= num.val;
////		result.frac = result.frac  
//	
//		return null;
//	}
//}
