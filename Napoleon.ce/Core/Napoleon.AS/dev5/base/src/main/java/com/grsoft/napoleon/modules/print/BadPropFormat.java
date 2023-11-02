package com.grsoft.napoleon.modules.print;
import com.grsoft.aceteam.R;

/***
 * Исключение возникает когда свойство 
 * прочитанное из XML не может быть правильно
 * переведено в тип, ожитаемый объектом
 * @author kki
 *
 */
class BadPropFormat extends Exception{
	private static final long serialVersionUID = -7898721520056984708L;
    public BadPropFormat(String name){
    	super(String.format("Bad node format: %s", name));
    }
}