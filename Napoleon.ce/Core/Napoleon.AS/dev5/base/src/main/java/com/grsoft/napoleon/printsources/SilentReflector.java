package com.grsoft.napoleon.printsources;
import com.grsoft.aceteam.R;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.grsoft.types.DateFormat;
import com.grsoft.types.Scale;
import com.grsoft.util.Util;

public class SilentReflector {
	public static boolean getFieldValue(StringBuilder value, String name, Object object, String format){
		if (value != null && name != null){
			value.setLength(0);
			
			Field fields[] = object.getClass().getFields();
//			Field fields[] = object.getClass().getDeclaredFields();
			
			Field finded = null;
			
			for(Field f : fields){
				PrintInfo printInfo = f.getAnnotation(PrintInfo.class);
				if (printInfo != null && 
						printInfo.name().equals(name)){
					try{
						finded = f;
						break;
//						value.append(f.get(object));
//						return true;
					}catch(Exception e){
						e.printStackTrace();
						return false;
					}
				}
			}
			
			try {
//				Field fld = object.getClass().getDeclaredField(name);
				Field fld = (finded != null) ? finded : object.getClass().getField(name);
				
				if (fld == null)
					return false;
				
				fld.setAccessible(true);
				
				Class<?> fldType = fld.getType(); 
				if (fldType == String.class)
				{ 
					value.append((String)fld.get(object));
					return true;
				} else if (fldType == int.class){
					int val = (Integer)fld.get(object);
					Scale s = fld.getAnnotation(Scale.class);
					if( s == null )
						value.append(val);
					else {
						String str = Util.IntToScaleStr(val, s.value(), Util.DEC_DELIM, s.hideRest());
						value.append(str);
					}
					return true;
				} else if (fldType == long.class){
					long val = (Long)fld.get(object);
					Scale s = fld.getAnnotation(Scale.class);
					if( s == null )
						value.append(val);
					else {
						String str = Util.IntToScaleStr(val, s.value(), Util.DEC_DELIM, s.hideRest());
						value.append(str);
					}
					return true;
				} else if (fldType == double.class){
					value.append((Double)fld.get(object));
					return true;
				} else if (fldType == Date.class){
					if(format == null) {
						DateFormat df = fld.getAnnotation(DateFormat.class);
						if(df != null)
							format = df.format();
					}
					SimpleDateFormat sdf = ( format == null ) ? Util.simpleDateFormat : new SimpleDateFormat(format, Locale.getDefault());
					value.append(sdf.format((Date)fld.get(object)));
					return true;
				}else
					return false;
			} catch (Exception e) {	return false; }
		}else
			return false;
	}
}
