/*
 * Copyright (C), 2010, Гильдия Разработчиков
 *
 * Различные утилиты
 *
 * kki   04/11/2010   creating
 */
package com.grsoft.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.graphics.Color;

/**
 * Общие функции
 * @author kki
 *
 */
public class Util
{
	public static final int INT_SIZE = 4;
	public static final int LONG_SIZE = 8;
	public static final String DEC_DELIM = ",";
	
	static ByteBuffer intBuffer = ByteBuffer.allocate(INT_SIZE);
	static ByteBuffer longBuffer = ByteBuffer.allocate(LONG_SIZE);
	
	public static final SimpleDateFormat simpleDateFormat =  new SimpleDateFormat("dd.MM.yy");
	
	/**
	 * Конвертирует целое число в массив байт
	 * 
	 * @param val число
	 * @return массив байт
	 */
	public static byte[] intToBytes(int val)
	{
		intBuffer.clear();
		intBuffer.putInt(val);
		
		return intBuffer.array();
	}
	
	/**
	 * Переводит массив байт в целое число типа <b>int</b>
	 * 
	 * @param buf массив байт
	 * @param pos смещение
	 * @return число
	 */
	public static int bytesToInt(byte[] buf, int pos)
	{
		ByteBuffer bb = ByteBuffer.wrap(buf, pos, INT_SIZE);
		return bb.getInt();
	}
	
	/**
	 * Переводит массив байт в число типа <b>long</b>
	 *  
	 * @param buf массив байт
	 * @param pos смещение
	 * @return число
	 */
	public static long bytesToLong(byte[] buf, int pos)
	{
		ByteBuffer bb = ByteBuffer.wrap(buf, pos, LONG_SIZE);
		return bb.getLong();
	}

	/**
	 * Конвертирует целое число типа long в массив байт
	 * 
	 * @param val число
	 * @return массив байт
	 */
	public static byte[] longToBytes(long val)
	{
		longBuffer.clear();
		longBuffer.putLong(val);
		
		return longBuffer.array();
	}
	
	/**
	 * Конвертирует число с масштабом в строку
	 * с разделителем
	 * 
	 * например: число 14130 и масштаб 2
	 * будет сконвертировано в строку: 141,30 
	 * @param val число
	 * @param scale масштаб
	 * @return сконвертированная строка
	 */
	public static String IntToScaleStr(int val, int scale)
	{
		return IntToScaleStr(val, scale, DEC_DELIM);
	}
	
	/**
	 * Конвертирует число с масштабом в строку
	 * с разделителем
	 * 
	 * например: число 14130 и масштаб 2
	 * будет сконвертировано в строку: 141,30 
	 * @param val число
	 * @param scale масштаб
	 * @param delimeter разделитель десятичной части
	 * @return сконвертированная строка
	 */
	public static String IntToScaleStr(int val, int scale, String delimeter)
	{
		return IntToScaleStr(val, scale, delimeter, true);
	}
	
	/**
	 * отделяем пробелом группы
	 * @param number
	 */
	public static String spacingDigitGroup(String number) {
		StringBuilder res = new StringBuilder();
		boolean haveDivider = (number.lastIndexOf(',') != -1);

		int i = number.length() - 1;
		int dig = 2;
		for( ; i>=0; i --) {
			char sym = number.charAt(i);
			res.insert(0, sym);
			if( haveDivider ) {
				if( sym == ',')
					haveDivider = false;
			} else {
				if( dig == 0 ) {
					res.insert(0, ' ');
					dig = 2;
				} else
					dig--;
			}
		}
		return res.toString();
	}
	
	public static String IntToScaleStr(int val, int scale, String delimeter, boolean hideRest)
	{
		if (scale == 0)
			scale = 1;
		
		StringBuilder result = new StringBuilder();
		if( val < 0 ) {
			result.append('-');
			val = - val;
		}
		String valStr = Integer.toString(val);
		int width = getWidthScale(scale);
		
		if (valStr.length() - width <= 0)
			result.append("0");
		else
			result.append(valStr.substring(0, valStr.length() - width));
		
		if ((val % scale) != 0) {
			result.append(delimeter);
			int vl = valStr.length();
			while( vl < width ) {
				result.append("0");
				width--;
			}
			result.append(valStr.substring(vl - width));
		} else if(!hideRest)
		{
			result.append(delimeter);
			int v = 10;
			while( v <= scale )
			{
				result.append("0");
				v *= 10;
			}
		}
		
		return result.toString();
	}

	private static int getWidthScale(int scale)
	{
		int result = 0;
		
		while((scale /= 10) > 0)
			result++;
		
		return result;
	}
	
	/**
	 * Конвертирует число с масштабом в строку
	 * с разедлителем(см. IntToScaleStr), обрезает 
	 * выходную строк до <b>widtch</b> символов после
	 * запятой
	 * @param val число
	 * @param scale масштаб
	 * @param width число символов после запятой
	 * @return сконвертированная строка
	 */
	public static String IntToScaleWStr(int val, int scale, int width)
	{
		return IntToScaleWStr(val, scale, width, true);
	}
	
	public static String IntToScaleWStr(int val, int scale, int width, boolean hideRest)
	{
		String result = IntToScaleStr(val, scale, DEC_DELIM, hideRest);
		int delimPos = result.indexOf(DEC_DELIM);
		
		if (delimPos != -1)
		{
			result = result.substring(0, delimPos + width + 1);
		}
		
		return result;
	}

	public static StringBuilder IntToStrLeadingZero(int val, StringBuilder sb)
	{
		if (val < 10)
			sb.append(0);
		
		sb.append(val);
		return sb;
	}
	
	public static int StrToScale(String str, int scale)
	{
		int result = 0;
		
		int dec_pos = str.replace(".", DEC_DELIM).indexOf(DEC_DELIM);
		
		try {
		if (dec_pos != -1)
		{
			String dec_str = str.substring(0, dec_pos);
			
			if (dec_str.length() == 0)
				dec_str = "0";
			
			boolean blw0 = dec_str.startsWith("-");
			if( blw0 )
				dec_str = dec_str.substring(1);
			
			int dec = Integer.parseInt(dec_str);
			
			dec *= scale;
			result = dec + getFrac(str.substring(dec_pos+1), scale);
			if( blw0 ) result = -result;
		}
		else
			result = Integer.parseInt(str) * scale;
		} catch(Exception e) {
			e.printStackTrace();
		}
		 
		return result;
	}
	
	private static int getFrac(String str, int scale)
	{
		int result = 0;
		for(char sym : str.toCharArray()) {
			if( scale == 1 ) break;
			scale /= 10;
			result += Integer.parseInt(Character.toString(sym)) * scale;
		}
		
		return result;
		
//		str = trimZeroEnds(str);
//		int widthScale = getWidthScale(scale);
//		if (str.length() > widthScale)
//			str = str.substring(0, widthScale);
//		
//		if (str.startsWith("0"))
//		{
//			int mul = scale / 10;
//			
//			for (; mul > 1; mul /= 10  )
//			{
//				if (!str.startsWith("0"))
//					break;
//				else
//					str = str.substring(1);
//			}
//			result = Integer.parseInt(str) * mul;
//		}
//		else
//		{
//			if (str.length() < widthScale)
//			{
//				StringBuilder sb = new StringBuilder(str);
//				
//				for(int i = str.length(); i < widthScale; i++)
//					sb.append("0");
//				
//				str = sb.toString();
//				
//			}
//				result = Integer.parseInt(str);
//		}
//			
//		return result;
	}
	
//	private static String trimZeroEnds(String str)
//	{
//		String result = str;
//		while(result.endsWith("0"))
//			result = result.substring(0,result.length()-1);
//			
//		return result;
//	}
	
	/**
	 * Конвертирует объект коллекцию массива байт
	 * в один массив
	 * @param arrayList
	 * @return
	 */
	static public byte[] ArrayListToBytes(ArrayList<byte[]> arrayList)
	{
		ByteArrayOutputStream result = new ByteArrayOutputStream();
		
		for (byte[] b: arrayList)
			result.write(b, 0, b.length);
		
		return result.toByteArray();
	}
	
	/**
	 * 
	 * @return дата без времени
	 */
	static public Date getDate() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		
		return calendar.getTime();
	}
	
	/**
	 * Обязательно использовать в создании документов иначе статус в обработке не будет получаться
	 * @return дата /время без миллисекунд
	 */
	static public Date getDateTime() {
		long date = (Calendar.getInstance().getTime().getTime() / 1000) * 1000;
		return new Date(date);
	}
	
	/**
	 * Ковертирует цвет, который используется в базе в системный цвет Android
	 * @param color
	 * @return
	 */
	static public int GrServerColorToSystem(int color) {
		return Color.rgb( color & 0x000000ff,
				(color & 0x0000ff00) >> 8, 
				(color & 0x00ff0000) >> 16);
	}
	
	/***
	 * Номер API SDK Android
	 * @return
	 */
	public static int getPlatformVersion() {
	    try {
	        Field verField = Class.forName("android.os.Build$VERSION").getField("SDK_INT");
	        int ver = verField.getInt(verField);
	        return ver;
	    } catch (Exception e) {
	        try {
	            Field verField = Class.forName("android.os.Build$VERSION").getField("SDK");
	            String verString = (String) verField.get(verField);
	            return Integer.parseInt(verString);
	        } catch(Exception except) {
	            return -1;
	        }
	    }
	}
	
	/***
	 * Копирование файла
	 * @param src файл источник
	 * @param dst файл назначение
	 * @throws IOException
	 */
	public static void copy(File src, File dst) throws IOException {
	    InputStream in = new BufferedInputStream(new FileInputStream(src));
	    OutputStream out = new BufferedOutputStream(new FileOutputStream(dst));
	    final int CPY_BUF_SIZE = 1024;

	    byte[] buf = new byte[CPY_BUF_SIZE];
	    int len;
	    while ((len = in.read(buf)) > 0) {
	        out.write(buf, 0, len);
	    }
	    in.close();
	    out.close();
	}
	
}
