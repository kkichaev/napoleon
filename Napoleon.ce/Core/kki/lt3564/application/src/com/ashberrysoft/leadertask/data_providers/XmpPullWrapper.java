package com.ashberrysoft.leadertask.data_providers;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

/**
 * @author Vladimir Shcryabets <vshcryabets@gmail.com>
 */
public class XmpPullWrapper  {
    protected XmlPullParser mParser;

    public XmpPullWrapper(XmlPullParser parser) {
        mParser = parser;
    }

    public void run() throws XmlPullParserException, IOException {
        int eventType = mParser.getEventType();
        boolean work = true;
        String lastOpenedTag = null;
        while (eventType != XmlPullParser.END_DOCUMENT) {
            switch (eventType) {
                case XmlPullParser.START_TAG:
                    lastOpenedTag = mParser.getName();
                    work = onStartTag(lastOpenedTag);
                    break;
                case XmlPullParser.END_TAG:
                    String tagName = mParser.getName();
                    work = onEndTag(tagName);
                    break;
                case XmlPullParser.TEXT:
                    String text = mParser.getText();
                    work = onText(lastOpenedTag, text);
                    break;
            }
            if ( !work ) {
                break;
            }
            eventType = mParser.next();
        }
    }

    protected boolean onText(String name, String text) {
        return true;
    }

    protected boolean onEndTag(String name) {
        return true;
    }

    protected boolean onStartTag(String name) throws IOException, XmlPullParserException {
        return true;
    }
}
