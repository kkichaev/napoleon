/*
 * Copyright (C), 2010, Гильдия Разработчиков
 *
 * Тест ByteStream
 *
 * kki   14/09/2010   creating
 */

package com.grsoft.tests.network;


import junit.framework.TestCase;

import com.grsoft.network.ByteStream;
import com.grsoft.network.util.UnicodUtils;

import android.content.Context;

public class ByteStreamTestCase extends TestCase
{
	public void testToString(Context context)
	{
		try
		{
			final String TEST_STRING = "ServerAnswer[message:s,response:n][\"Сейчас войти не удастся. Превышено число подключений оговоренных в лицензионном соглашении\",0]";
			final byte[] TEST_ARRAY = UnicodUtils.toBytes(TEST_STRING);
		
			ByteStream stream = new ByteStream(TEST_ARRAY, context);
			assertEquals(TEST_STRING, stream.toString());
		}
		catch(Exception exception)
		{
			fail(exception.getMessage());
		}
		
	}
}
