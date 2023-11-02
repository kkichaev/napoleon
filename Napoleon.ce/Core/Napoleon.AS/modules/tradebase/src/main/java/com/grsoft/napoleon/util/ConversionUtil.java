package com.grsoft.napoleon.util;

import java.util.List;

public class ConversionUtil
{
	public static String makeStringWithDelimiter(List<String> strings)
	{
		return makeStringWithDelimiter(strings, ConvertConstants.COMMA);
	}
	
	public static String makeStringWithDelimiter(List<String> strings, char delimiter)
	{
		StringBuilder result = new StringBuilder();
		
		for(String str : strings)
		{
			result.append(str);
			result.append(delimiter);
		}
		
		result.deleteCharAt(result.length() - 1);
		return result.toString();
	}
	
	public static String makeStringCoverBrackets(String string)
	{
		return ConvertConstants.LEFT_BRACKET + string + ConvertConstants.RIGHT_BRACKET;
	}
}
