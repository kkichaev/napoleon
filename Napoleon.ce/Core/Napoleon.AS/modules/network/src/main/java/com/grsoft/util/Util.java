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
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.UUID;

import android.content.Context;
import android.graphics.Color;

import com.grsoft.network.encrypt.EncodableConnection;
import com.grsoft.network.encrypt.Encryptor;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

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
	
	public static final SimpleDateFormat simpleDateFormat =  new SimpleDateFormat("dd.MM.yy", Locale.getDefault());
	
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
	
	public static String IntToScaleStr(long val, int scale)
	{
		return IntToScaleStr(val, scale, DEC_DELIM, true);
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
		boolean haveDivider = (number.indexOf(',') != -1 || number.indexOf('.') != -1);

		int i = number.length() - 1;
		int dig = 2;
		for( ; i>=0; i --) {
			char sym = number.charAt(i);
			res.insert(0, sym);
			if( haveDivider ) {
				if( sym == ',' || sym == '.')
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
		return IntToScaleStr((long)val, scale, delimeter, hideRest);
	}
	
	public static String IntToScaleStr(long val, int scale, String delimeter, boolean hideRest)
	{
		if (scale == 0)
			scale = 1;
		
		StringBuilder result = new StringBuilder();
		if( val < 0 ) {
			result.append('-');
			val = - val;
		}

//		DecimalFormatSymbols formatSymbols = new DecimalFormatSymbols(Locale.ENGLISH);
//		formatSymbols.setDecimalSeparator('.');
//		formatSymbols.setGroupingSeparator(' ');
//
//		new DecimalFormat("###,###,#####.##", formatSymbols).format(val);

		String valStr = Long.toString(val);
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
		return IntToScaleWStr((long)val, scale, width, hideRest);
	}
	public static String IntToScaleWStr(long val, int scale, int width, boolean hideRest)
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
	
	public static long StrToScale(String str, int scale)
	{
		long result = 0;
		
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
			
			long dec = Long.parseLong(dec_str);
			
			dec *= scale;
			result = dec + getFrac(str.substring(dec_pos+1), scale);
			if( blw0 ) result = -result;
		}
		else
			result = Long.parseLong(str) * scale;
		} catch(Exception e) {
			e.printStackTrace();
		}
		 
		return result;
	}
	
	private static long getFrac(String str, int scale)
	{
		long result = 0;
		for(char sym : str.toCharArray()) {
			if( scale == 1 ) break;
			scale /= 10;
			result += Long.parseLong(Character.toString(sym)) * scale;
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
	
	static public Date getMonthStart(Date d) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(d);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		
		return calendar.getTime();
	}
	
	static public Date getMonthEnd(Date d){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(d);
		calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		
		return calendar.getTime();
	}
	
	static public Date getDayStart(Date d) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(d);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		
		return calendar.getTime();
	}

	static public Date getDayEnd(Date d) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(d);
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		calendar.set(Calendar.MILLISECOND, 999);
		
		return calendar.getTime();
	}
	
	static public Date getNextDay(Date d) {
		if(d == null)
			d = getDate();
		else
			d = getDayStart(d);
		return new Date(d.getTime() + 24 * 3600 * 1000);
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
    	try{
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

	public static File encodeFile(Context context, File src) {
		File ret = null;
		byte[] pubKey = new byte[] {
				(byte)0x30,(byte)0x82,(byte)0x1,(byte)0x22,(byte)0x30,(byte)0xd,(byte)0x6,(byte)0x9,(byte)0x2a,(byte)0x86,(byte)0x48,
				(byte)0x86,(byte)0xf7,(byte)0xd,(byte)0x1,(byte)0x1,(byte)0x1,(byte)0x5,(byte)0x0,(byte)0x3,(byte)0x82,
				(byte)0x1,(byte)0xf,(byte)0x0,(byte)0x30,(byte)0x82,(byte)0x1,(byte)0xa,(byte)0x2,(byte)0x82,(byte)0x1,
				(byte)0x1,(byte)0x0,(byte)0xc6,(byte)0xb7,(byte)0x5d,(byte)0xde,(byte)0xa5,(byte)0x23,(byte)0xa1,(byte)0x4b,
				(byte)0xb8,(byte)0xd9,(byte)0x3,(byte)0xf8,(byte)0xc9,(byte)0xbf,(byte)0x74,(byte)0xe7,(byte)0xf8,(byte)0x6c,
				(byte)0x17,(byte)0x5b,(byte)0xb5,(byte)0x26,(byte)0x6b,(byte)0xeb,(byte)0x8c,(byte)0x36,(byte)0xf4,(byte)0xce,
				(byte)0x60,(byte)0xbb,(byte)0x6b,(byte)0x62,(byte)0x1a,(byte)0xf4,(byte)0x21,(byte)0xba,(byte)0x5e,(byte)0xec,
				(byte)0x6a,(byte)0x85,(byte)0xc8,(byte)0xd0,(byte)0x9b,(byte)0x25,(byte)0x23,(byte)0x84,(byte)0xe8,(byte)0x9d,
				(byte)0x2e,(byte)0x9,(byte)0x1a,(byte)0x2,(byte)0xea,(byte)0xb4,(byte)0x61,(byte)0x18,(byte)0x10,(byte)0xd7,
				(byte)0x12,(byte)0xcf,(byte)0x7f,(byte)0xf2,(byte)0xef,(byte)0x12,(byte)0x77,(byte)0x8d,(byte)0xd1,(byte)0xbc,
				(byte)0xf2,(byte)0x4b,(byte)0xef,(byte)0x4,(byte)0x8a,(byte)0xb7,(byte)0x56,(byte)0x8b,(byte)0x90,(byte)0xfe,
				(byte)0xe3,(byte)0xcf,(byte)0xa8,(byte)0xbc,(byte)0x32,(byte)0x82,(byte)0xee,(byte)0x5,(byte)0xf9,(byte)0x2,
				(byte)0x79,(byte)0xa9,(byte)0x8,(byte)0x1d,(byte)0x89,(byte)0x9a,(byte)0xe1,(byte)0xaa,(byte)0x71,(byte)0x41,
				(byte)0x39,(byte)0xcd,(byte)0x3,(byte)0x20,(byte)0x96,(byte)0xb8,(byte)0x31,(byte)0x89,(byte)0xf9,(byte)0x13,
				(byte)0x50,(byte)0x3f,(byte)0x30,(byte)0x3d,(byte)0x1e,(byte)0x48,(byte)0x57,(byte)0x4e,(byte)0x3f,(byte)0xb8,
				(byte)0xd,(byte)0x95,(byte)0xfe,(byte)0x98,(byte)0x89,(byte)0x8b,(byte)0x2,(byte)0x9d,(byte)0x4d,(byte)0xa8,
				(byte)0xe9,(byte)0x7c,(byte)0x93,(byte)0x5c,(byte)0xda,(byte)0x9c,(byte)0x34,(byte)0xb7,(byte)0x3d,(byte)0xa3,
				(byte)0x97,(byte)0x41,(byte)0x67,(byte)0x8,(byte)0x1b,(byte)0x1f,(byte)0xf8,(byte)0x6,(byte)0x0,(byte)0xe0,
				(byte)0x56,(byte)0x38,(byte)0xd2,(byte)0xe8,(byte)0x73,(byte)0x1c,(byte)0x49,(byte)0x2a,(byte)0x6b,(byte)0xa1,
				(byte)0x14,(byte)0x4e,(byte)0xf9,(byte)0x47,(byte)0x72,(byte)0x87,(byte)0x84,(byte)0xe5,(byte)0x36,(byte)0xaf,
				(byte)0x95,(byte)0x21,(byte)0xec,(byte)0xfc,(byte)0xda,(byte)0xd6,(byte)0xd0,(byte)0x27,(byte)0x48,(byte)0xb8,
				(byte)0x44,(byte)0x50,(byte)0x7a,(byte)0x45,(byte)0x70,(byte)0xe7,(byte)0x9e,(byte)0xfe,(byte)0x19,(byte)0x6,
				(byte)0x9d,(byte)0xb,(byte)0x7f,(byte)0x2,(byte)0xa7,(byte)0xf1,(byte)0x5,(byte)0xf8,(byte)0xe1,(byte)0x75,
				(byte)0xf5,(byte)0x1,(byte)0xe0,(byte)0x8e,(byte)0xb8,(byte)0xd9,(byte)0x31,(byte)0xdb,(byte)0x33,(byte)0x36,
				(byte)0xfb,(byte)0xb2,(byte)0x2,(byte)0xef,(byte)0x13,(byte)0x98,(byte)0x3c,(byte)0x90,(byte)0x39,(byte)0x5e,
				(byte)0xb5,(byte)0xfa,(byte)0xe5,(byte)0xbb,(byte)0xf7,(byte)0x64,(byte)0x1f,(byte)0x71,(byte)0xd8,(byte)0x42,
				(byte)0x9e,(byte)0x5e,(byte)0x45,(byte)0xa4,(byte)0xe8,(byte)0x12,(byte)0x6c,(byte)0xf0,(byte)0xd7,(byte)0x8a,
				(byte)0xba,(byte)0xc,(byte)0x58,(byte)0xb,(byte)0x17,(byte)0xca,(byte)0x74,(byte)0x91,(byte)0x18,(byte)0xb1,
				(byte)0x12,(byte)0xf0,(byte)0xdc,(byte)0x55,(byte)0xd3,(byte)0x4a,(byte)0xed,(byte)0x55,(byte)0x2,(byte)0x3,
				(byte)0x1,(byte)0x0,(byte)0x1,};

		try {
			ret = new File(context.getExternalCacheDir(), String.format("napoleon%d.db", new Date().getTime()));
			FileOutputStream fos = new FileOutputStream(ret);

			PublicKey pk = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(pubKey));
			Cipher cipher = Cipher.getInstance(Encryptor.RSA_PRV);
			cipher.init(Cipher.ENCRYPT_MODE, pk);

			Random r = new Random();

			byte[] ak = new byte[32];
			byte[] ivData = new byte[16];

			r.nextBytes(ak);
			r.nextBytes(ivData);

			byte[] tot = new byte[ak.length + ivData.length];
			System.arraycopy(ivData, 0, tot, 0, ivData.length);
			System.arraycopy(ak, 0, tot, ivData.length, ak.length);
			byte[] keyData = cipher.doFinal(tot);

			String head = String.format("GRDB%08d", keyData.length);
			fos.write(head.getBytes());
			fos.write(keyData);

			SecretKeySpec key = new SecretKeySpec(ak, "AES");
			IvParameterSpec iv = new IvParameterSpec(ivData);
			Cipher encrypt = Cipher.getInstance(EncodableConnection.AES_PRV);
			encrypt.init(Cipher.ENCRYPT_MODE, key, iv);

			FileInputStream fis = new FileInputStream(src);
			byte[] buffer = new byte[1024 * 10];

			int bytesRead;
			while ((bytesRead = fis.read(buffer)) != -1) {
				byte[] output = encrypt.update(buffer, 0, bytesRead);
				if (output != null && output.length > 0) {
					fos.write(output);
				}
			}
			byte[] outputBytes = encrypt.doFinal();
			if (outputBytes != null) {
				fos.write(outputBytes);
			}
			fis.close();
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
			ret = null;
		}

		return ret;
	}


	public static boolean isToday(Date date){
		Calendar cal = Calendar.getInstance();
		
		return (cal.get(Calendar.YEAR) == (date.getYear() + 1900)) && 
				(cal.get(Calendar.MONTH) == date.getMonth()) && 
				(cal.get(Calendar.DAY_OF_MONTH) == date.getDate());
	}
	
	/***
	 * Сбрасывает у даты время в 0(часы, минуты, секунды, милисекунды)
	 * @param date
	 * @return
	 */
	public static Date resetTime(Date date){
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		
		return cal.getTime();
	}

	public static String genUUID(){
		return UUID.randomUUID().toString().replace("-","");
	}
}
