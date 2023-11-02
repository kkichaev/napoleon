package com.grsoft.napoleon.modules.print;

import java.io.UnsupportedEncodingException;

import com.grsoft.dataobjects.impl.PrintFormImpl;

public class StdFormLoader implements TextPrinter.FormLoader {

	@Override
	public String getForm(String formName) {
		String ret = null;
		
		PrintFormImpl printFormImpl = new PrintFormImpl();
		printFormImpl.getData().name = formName;
		if( printFormImpl.read() ) {
			try {
				ret = new String(printFormImpl.getData().form, TextPrinter.FILE_ENCODE);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		printFormImpl.close();
		
		return ret;
	}
	
}
