/*
 * Copyright (C), 2010, Гильдия Разработчиков
 *
 * Формат данных числового типа
 *
 * kki   10/10/2010   creating
 */

package com.grsoft.network;

import java.lang.reflect.Field;

import com.grsoft.dataobjects.DataObject;
import com.grsoft.types.Scale;
import com.grsoft.util.Util;

public class NumberFormat extends MemberFormat
	implements StringFormatValue
{
	private short fraction = 0;
	private int scale = 0;
	private ScaleSetter setter = null;
	
	class ScaleSetter {
		private int fieldScale;
		
		public ScaleSetter(Field f) {
			Scale s = f.getAnnotation(Scale.class);
			fieldScale = (s==null) ? 1 : s.value();
			if( scale == 0 )
				scale = 1;
		}
		void set(Field f, Member m, DataObject object) throws Exception {
			Object v = m.getValue();
			if( scale == fieldScale ) {
				f.set(object, v);
			} else {
				if( v instanceof Long ) {
					long fval = (Long)v * fieldScale / scale;
					f.set(object, (Long)fval);
				} else {
					long fval = (long)((Integer)v) * fieldScale / scale;
					f.set(object, (Integer)((int)fval));
				}
			}				
		}
	}
	
	public NumberFormat(String name)
	{
		super(name, int.class, "");
	}

	public NumberFormat(String name, short fraction)
	{
		super(name, int.class, "");
		this.fraction = fraction;
	}
	
	public NumberFormat(String name, int scale)
	{
		super(name, int.class, "");
		this.scale = scale;
		this.fraction = convertScaleToFraction();
	}
	
	@Override
	public void setField(Field f, Member m, Class<? extends DataObject> dataObjectClass, 
			DataObject object) throws Exception {
		if( setter == null )
			setter = new ScaleSetter(f);
		setter.set(f, m, object);
	}
	
	public int getScale() { return scale; }

	@Override
	public boolean read(ByteStream stream)
	{
		if (stream.next() == '(')
        {
           StringBuilder val = new StringBuilder();
           stream.moveNext(); // eat 'n'
           stream.moveNext(); // eat '('
           if (!stream.copyUntill(val, ')'))
              return false;

           fraction = (short)Integer.parseInt(val.toString());
        }
        else
           fraction = 0;
		
		scale = fractionToScale(fraction);
        return true;
	}

	private int fractionToScale(short value) {
		int v = 1;
		while( value-- > 0 )
			v *= 10;
		return v;
	}

	@Override
	public boolean readMember(Member m, ByteStream stream)
	{
		String sym = "0123456789.eE-+";
        StringBuilder dest = new StringBuilder();

        while (!stream.isEOS())
        {
           char cur = stream.current();
           if (sym.indexOf(cur) < 0) break;

           dest.append(cur);
           stream.moveNext();
        }

        int val = 0;
        
		if (fraction == 0)
			val = Integer.parseInt(dest.toString());
		else
		{
			String num_str = dest.toString();
			boolean blw0 = (num_str.charAt(0) == '-');
			if( blw0 )
				num_str = num_str.substring(1);

			int pointPos = num_str.indexOf(".");
			String intPart = num_str.substring(0,pointPos); 
			String fracPart = num_str.substring(pointPos + 1);
			
			val = Integer.parseInt(intPart) * decPow(fraction) + Integer.parseInt(fracPart);
			if( blw0 )
				val = -val;
		}
			
        
        m.setValue(val);
        return true;
    }

	private int decPow(int pow)
	{
		final int DEC = 10;
		
		int result = 1;
		
		if(pow > 0)
			result = decPow(--pow) * DEC;
		
		return result;
		
	}
	private short convertScaleToFraction()
	{
		short result = 0;
		int s = scale;
		
		while((s /= 10) > 0)
			result++;
		
		return result;
	}
	
	@Override
	public String toFormatString()
	{
		String result = ":n";
		
        if (fraction != 0)
           result += "(" + Integer.toString(fraction) + ")";
        
        return result;
	}

	@Override
	public String valueToFormatString(Object value)
	{
		if( value == null )
			value = 0;
		
		final String DEC_DELIM = ".";
		StringBuilder result = new StringBuilder(Util.IntToScaleStr((Integer)value, scale, DEC_DELIM));
		
		if (fraction > 0)
		{
			int dec_width = 0;
			int delim_pos = result.indexOf(DEC_DELIM);
			
			if ( delim_pos < 0)
				result.append(DEC_DELIM);
			else
				dec_width = result.length() - delim_pos + 1; 
			
			while(dec_width < fraction)
			{
				result.append("0");
				dec_width++;
			}
		}
		
		return result.toString();
	}
}
