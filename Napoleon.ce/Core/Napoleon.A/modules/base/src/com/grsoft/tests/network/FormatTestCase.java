/*
 * Copyright (C), 2010, Гильдия Разработчиков
 *
 * Тест Format
 *
 * kki   14/09/2010   creating
 */
package com.grsoft.tests.network;

import junit.framework.TestCase;

import com.grsoft.network.ByteStream;
import com.grsoft.network.Format;
import com.grsoft.network.MemberFormat;
import com.grsoft.network.NumberFormat;
import com.grsoft.network.ObjectFormat;
import com.grsoft.network.StringFormat;
import com.grsoft.network.util.UnicodUtils;

import android.content.Context;

public class FormatTestCase extends TestCase
{
	private final String SERVER_RESPOND_AS_FAIL = "ServerAnswer[message:s,response:n][\"Сейчас войти не удастся. Превышено число подключений оговоренных в лицензионном соглашении\",0]";
//	private final String SERVER_RESPOND_AS_GOOD = "ServerAnswer[message:s,response:n][\"4A2DCE\",1]";
	private final String SERVER_RESPOND_SELECT_DIVISION = "Division[agents[id:s],cheif:s,description:s,id:n,name:s,parent:n][,\"0000089\",\"все компании\",1,\"ВЕК\",0][,\"\",\"\",2,\"МК\",1][,\"0000072\",\"\",3,\"Океан\",1][,\"0000058\",\"\",4,\"Заморозка\",1][,\"0000078\",\"Данон, Вимбильдан\",5,\"МК 1\",2][,\"0000077\",\"\",6,\"МК 2\",2][[\"0000005\"][\"0000080\"][\"0000086\"][\"0000068\"][\"0000084\"][\"0000088\"][\"0000081\"][\"0000083\"][\"0000066\"][\"0000087\"][\"0000082\"][\"0000085\"],\"\",\"\",7,\"Общий прайс\",4][[\"0000060\"][\"\"][\"0000051\"][\"0000052\"][\"0000059\"][\"0000050\"][\"0000053\"][\"0000057\"],\"\",\"\",8,\"Данон\",5][[\"0000047\"][\"0000049\"][\"0000048\"][\"0000021\"],\"\",\"\",9,\"Вимбильдан\",5][[\"0000045\"][\"0000046\"][\"0000055\"][\"0000056\"][\"0000054\"],\"\",\"\",10,\"НМЖК\",6][[\"0000042\"][\"0000028\"][\"0000038\"][\"0000029\"][\"0000043\"],\"\",\"\",11,\"Юнимилк\",6][[\"0000024\"][\"0000025\"][\"0000026\"],\"\",\"\",12,\"Общий прайс\",6][[\"0000064\"][\"0000065\"][\"0000079\"][\"0000062\"],\"0000093\",\"\",13,\"Город\",3][[\"0000031\"][\"0000032\"][\"0000033\"][\"0000034\"][\"0000035\"][\"0000036\"][\"0000037\"][\"0000039\"][\"0000040\"][\"0000041\"],\"0000072\",\"Продажи Океан в регионе\",14,\"Океан область\",3]";
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}
	
	public void testFormat()
	{
		String TEST_NAME = "test_name";
		
		Format format = new Format(TEST_NAME);
		assertEquals(TEST_NAME, format.getName());
	}
	
	public void testCreateFormatFromStream(Context context)
	{
		try
		{
			final byte[] TEST_ARRAY = UnicodUtils.toBytes(SERVER_RESPOND_AS_FAIL);
			ByteStream stream = new ByteStream(TEST_ARRAY, context);
			
			Format format = Format.createFormat(stream);
			final String EXPECTED_FORMAT_SERVER_RESPOND_STRING = "[message:s,response:n]";
			final int FORMAT_SIZE = 2;
			
			assertEquals(EXPECTED_FORMAT_SERVER_RESPOND_STRING, format.toString());
			assertEquals(FORMAT_SIZE, format.size());
			
			final String MEMBER_1 = "message:s";
			MemberFormat memberFormat = format.get(0);
			assertEquals(MEMBER_1, memberFormat.toString());
			assertEquals(StringFormat.class, memberFormat.getClass());
			
			ByteStream divisionStream = new ByteStream(UnicodUtils.toBytes(SERVER_RESPOND_SELECT_DIVISION), context);
			Format divisionFormat = Format.createFormat(divisionStream);
			final String EXPECTED_FORMAT_DIVISION_STRING = "[agents[id:s],cheif:s,description:s,id:n,name:s,parent:n]";
			final int FORMAT_DIVISION_SIZE = 6;
			
			assertEquals(EXPECTED_FORMAT_DIVISION_STRING, divisionFormat.toString());
			assertEquals(FORMAT_DIVISION_SIZE, divisionFormat.size());
			
			assertEquals(ObjectFormat.class, divisionFormat.get(0).getClass());
			assertEquals(StringFormat.class, divisionFormat.get(1).getClass());
			assertEquals(StringFormat.class, divisionFormat.get(2).getClass());
			assertEquals(NumberFormat.class, divisionFormat.get(3).getClass());
			assertEquals(StringFormat.class, divisionFormat.get(4).getClass());
			assertEquals(NumberFormat.class, divisionFormat.get(5).getClass());
		}
		catch(Exception exception)
		{
			fail(exception.getMessage());
		}
	}
	
	public void testToString(Context context)
	{
		try
		{
			final byte[] SIMPLE_SERVER_RESPOND = UnicodUtils.toBytes(SERVER_RESPOND_AS_FAIL);
			final byte[] DIVISION_RESPOND = UnicodUtils.toBytes(SERVER_RESPOND_SELECT_DIVISION);
			final String EXCEPTED_SIMPLE_SERVER_RESPOND = "[message:s,response:n]";
			final String EXPECTED_DIVISION_RESPOND = "[agents[id:s],cheif:s,description:s,id:n,name:s,parent:n]";
			
			ByteStream stream_1 = new ByteStream(SIMPLE_SERVER_RESPOND, context);
			Format format_1 = Format.createFormat(stream_1);
			assertEquals(EXCEPTED_SIMPLE_SERVER_RESPOND, format_1.toString());
			
			ByteStream stream_2 = new ByteStream(DIVISION_RESPOND, context);
			Format format_2 = Format.createFormat(stream_2);
			assertEquals(EXPECTED_DIVISION_RESPOND, format_2.toString());
		}
		catch(Exception exception)
		{
			fail(exception.getMessage());
		}
	}
}
