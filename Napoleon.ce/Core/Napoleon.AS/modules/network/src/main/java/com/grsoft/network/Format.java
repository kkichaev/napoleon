/*
 * Copyright (C), 2010, Гильдия Разработчиков
 *
 * Формат объекта, что служит запросом к базе данных
 *
 * kki   14/09/2010   creating
 */

package com.grsoft.network;


import java.util.ArrayList;
import java.util.List;

import com.grsoft.napoleon.util.ConvertConstants;
import com.grsoft.network.exception.EndOfStream;
import com.grsoft.network.exception.RuntimeException;

/**
 * Формат объекта, что служит запросом к базе данны,
 * передается - принимается от сервера
 * 
 * @author kki
 *
 */
@SuppressWarnings("serial")
public class Format extends ArrayList<MemberFormat>
{
	private String name;
	private static ArrayList<Format> formats = new ArrayList<Format>(); 

	public Format(String name) 
	{ 
		this.name = name;
	}
	
	public static Format createFormat(ByteStream stream) 
		throws RuntimeException
	{
		String name = stream.readStringTillChar(ConvertConstants.LEFT_BRACKET);
		
		if (!stream.moveNext())
			throw new RuntimeException(new EndOfStream());
		
    	Format result = new Format(name);
    	result.addMembers(stream);
    	add(result);
    	
    	return result;
	}

	public synchronized static void clearFormats() {
		formats.clear();
	}

	public static void add(Format format) throws RuntimeException {
		if (format != null) {
			for (Format f : formats) {
				if (f != null && f.name.equals(format.name)) {
					formats.remove(f);
					break;
				}
			}

			formats.add(format);
		}
	}
	
	public MemberFormat get(String name) {
		for(MemberFormat mf : this)
			if( mf.getName().compareTo(name) == 0)
				return mf;
		
		return null;
	}

	public void addMembers(ByteStream stream) throws RuntimeException
	{
		StringBuilder memberName = new StringBuilder();
        MemberFormat memberFormat = null;
        ReadMemberContext context = new ReadMemberContext(this, stream);
        
        while (!stream.isEOS())
        {
           char sym = stream.current();
           
           if(sym == ConvertConstants.COMMA)
           {
        	   	addNewMember(memberFormat, memberName);
           }
           else if (sym == ConvertConstants.RIGHT_BRACKET)
           {
        	    /*Конец чтения*/
        	   	addNewMember(memberFormat, memberName);
       			stream.moveNext();
       			break;
           }
           else if (sym == ConvertConstants.COLON)
           {
        	    /*Имя поля в memberName, создание формата*/
        	   	context.setMemberName(memberName.toString());
        	   	memberFormat = MemberFormat.createFormat(context);
           }
           else if (sym == ConvertConstants.LEFT_BRACKET)
           {
        	    /*Чтение внутреннего объекта*/
        	   	context.setMemberName(memberName.toString());
        	   	// при чтении формата поток продвигается на первый символ после ]
        	   	// здесь надо проверить на ] - если внутренний объект последний в объекте
        	   	memberFormat = MemberFormat.createFormat(context);
        	   	addNewMember(memberFormat, memberName);
        	   	if( stream.current() == ConvertConstants.RIGHT_BRACKET ) {
        	   		stream.moveNext();
        	   		break;
        	   	}
           }
           else
           {
        	   memberName.append(sym);
           }

           stream.moveNext();
        }
	}

	private void addNewMember(MemberFormat member, StringBuilder name)
	{
		add(member);
		name.setLength(0);
	}

	public static String membersToString(List<MemberFormat> formats) throws RuntimeException
	{
		StringBuilder result = new StringBuilder(); 
		result.append(ConvertConstants.LEFT_BRACKET);
        
        for (MemberFormat mf : formats)
        {
           result.append(mf.getName());
           result.append(mf.toFormatString());
           result.append(ConvertConstants.COMMA);
        }
        
        if (result.charAt(result.length()-1) == ConvertConstants.COMMA)
        	result.deleteCharAt(result.length()-1);
        
        result.append(ConvertConstants.RIGHT_BRACKET);
        
        return result.toString();
	}

	public String getName()
	{
		return name;
	}

	public static Format find(String name) throws RuntimeException
	{
		for (Format format : formats)
        {
           if (format.name.equals(name))
              return format;
        }
        
		return null;
	}
	
	@Override
	public String toString()
	{
		try
		{
			return membersToString(this);
		}
		catch(Exception exception)
		{
			return "Can't representation Format as String. Error: " + exception.getMessage();
		}
	}
}
