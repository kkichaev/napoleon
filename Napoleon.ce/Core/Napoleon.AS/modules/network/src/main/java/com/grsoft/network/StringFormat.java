package com.grsoft.network;

public class StringFormat extends MemberFormat
	implements StringFormatValue
{
	public StringFormat(String name)
	{
		super(name, String.class, ":s");
	}

	@Override
	public String valueToFormatString(Object value)
	{
		if( value == null )
			return "\"\"";
		
		StringBuilder result = new StringBuilder();
		addQuotedString(result, value.toString());
		return result.toString();
	}
	
	private void addQuotedString(StringBuilder dest, String src)
	{
		char[] srcSym = src.toCharArray();

        dest.append('"');
        for (char sym : srcSym)
        {
           switch (sym)
           {
              case '\\': dest.append("\\\\"); break;
              case '/': dest.append("\\/"); break;
              case '"': dest.append("\\\""); break;
              case '\b': dest.append("\\b"); break;
              case '\f': dest.append("\\f"); break;
              case '\n': dest.append("\\n"); break;
              case '\r': dest.append("\\r"); break;
              case '\t': dest.append("\\t"); break;
              default: dest.append(sym); break;
           }
        }
        dest.append('"');
		
	}

	@Override
	public boolean readMember(Member m, ByteStream stream)
	{
		StringBuilder str = new StringBuilder();

        if (readString(str, stream))
        {
           m.setValue(str.toString());
           return true;
        }
        return false;
	}

	private boolean readString(StringBuilder dest, ByteStream stream)
	{
		try
		{
			dest.append(stream.readString());
			return true;
		}
		catch(Exception exception)
		{
			return false;
		}
	}
}
