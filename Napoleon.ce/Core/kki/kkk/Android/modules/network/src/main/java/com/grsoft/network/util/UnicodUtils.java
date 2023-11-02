/*
 * Copyright (C), 2010, Гильдия Разработчиков
 *
 * Утилиты для работы с Unicode
 *
 * kki   8/10/2010   creating
 */

package com.grsoft.network.util;

import static com.grsoft.util.Debug.dbgPrint;

public class UnicodUtils
{
	public static byte[] toBytes(String str)  
	{
//		try
//		{
//			return str.getBytes(ConvertConstants.UTF_16LE);
//		}
//		catch(Exception exception)
//		{
//			throw new RuntimeException(exception);
//		}
		
		//Эта новая версия, которая должна бы увеличивать
		//производительность, надо проверять!
		char[] buffer = str.toCharArray();
		int buf_len = buffer.length;
		byte[] b = new byte[buf_len << 1];
		
		for(int i = 0; i < buf_len; i++) 
		{
			int bpos = i << 1;
			
			b[bpos + 1] = (byte) ((buffer[i]&0xFF00)>>8);
			b[bpos] = (byte) (buffer[i]&0x00FF);
		}
		
		return b;
	}

	public static char readChar(byte[] bytes, int pos, char defChar) {
		try {
			byte high = bytes[pos + 1];
			byte low = bytes[pos];
			char ch = (char)(high << 8 | (low & 0xFF));
			
			return ch;
			//return new String(bytes, pos, BYTES_TO_READ,"UTF-16LE").charAt(0);
		}
		catch(Exception e) {
			dbgPrint(e.getMessage());
			return defChar;
		}
	}
}
