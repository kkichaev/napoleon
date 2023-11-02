/*
 * Copyright (C), 2010, Гильдия Разработчиков
 *
 * Тест StringFormat
 *
 * kki   14/09/2010   creating
 */

package com.grsoft.tests.network;

import junit.framework.TestCase;

import com.grsoft.network.ByteStream;
import com.grsoft.network.StringFormat;
import com.grsoft.network.StringMember;
import com.grsoft.network.util.UnicodUtils;

import android.content.Context;

public class StringFormatTestCase extends TestCase
{
	private final String MEMBER_NAME = "test_string_member";
	
	public void testGetMemberType()
	{
		final Class<String> EXPECTED_RESULT = String.class;
		
		StringFormat stringFormat = new StringFormat(MEMBER_NAME);
		
		assertEquals(EXPECTED_RESULT, stringFormat.getMemberType());
	}
	
	public void testToFormatString()
	{
		final String EXPECTED_RESULT = ":s";

		StringFormat stringFormat = new StringFormat(MEMBER_NAME);
		
		assertEquals(EXPECTED_RESULT, stringFormat.toFormatString());
	}
	
	public void testToString()
	{
		final String EXPECTED_RESULT = "test_string_member:s";
		
		StringFormat stringFormat = new StringFormat(MEMBER_NAME);
		
		assertEquals(EXPECTED_RESULT, stringFormat.toString());
	}
	
	public void testReadMember(Context context)
	{
		try
		{
			final String TEST_STRING_STREAM = "\"test\\\\_\\/_\\\"_\\b_\\f_\\n_\\r_\\t_\"";
			final String EXPECTED_RESULT = "test\\_/_\"_\b_\f_\n_\r_\t_";
			
			StringMember stringMember = new StringMember();
			
			ByteStream stream = new ByteStream(UnicodUtils.toBytes(TEST_STRING_STREAM), context);
			StringFormat stringFormat = new StringFormat(MEMBER_NAME);
			stringFormat.readMember(stringMember, stream);
			assertEquals(EXPECTED_RESULT, (String)stringMember.getValue());
		}
		catch(Exception exception)
		{
			fail(exception.getMessage());
		}
	}
}
