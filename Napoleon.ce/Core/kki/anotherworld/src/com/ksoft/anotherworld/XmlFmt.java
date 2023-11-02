package com.ksoft.anotherworld;

import java.io.StringReader;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

public class XmlFmt {
	public static String getValue(String src, String tag) {
		try {
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(false);
			XmlPullParser xpp = factory.newPullParser();
			xpp.setInput(new StringReader(src));
			
			int eventType = xpp.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT) {
				switch (xpp.getEventType()) {
				case XmlPullParser.START_TAG:
					if (xpp.getName().equals(tag))
						return xpp.nextText();

				default:
					break;
				}
				eventType = xpp.next();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
}
