package com.grsoft.napoleon;

import android.util.Xml;

import com.grsoft.database.DbWriter;
import com.grsoft.dataobjects.Present;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.impl.PriceImpl;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class PresentationUpdater {

    static final String ns = null;
    static String folder = null;

    static PriceImpl pi = new PriceImpl();

    static boolean update(File src) {
        InputStream in = null;
        try {
            folder = src.getParent() + '/';
            in = new FileInputStream(src);
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            return readData(parser);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null)
                    in.close();
                pi.close();
            } catch(Exception e) {

            }
        }

        return false;
    }

    static boolean readData(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "data");
        DbWriter db = null;
        while (parser.next() != XmlPullParser.END_TAG || !"data".equals(parser.getName())) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // Starts by looking for the entry tag.
            if (name.equals("item")) {
                Present p = readEntry(parser);
                if(p != null) {
                    if(db == null) {
                        DbWriter.dropTable(p.getTableName());
                        DbWriter.checkDBTable(p.getClass());
                        db = new DbWriter();
                    }
                    db.insertRecord(p);
                }
                while(parser.next() != XmlPullParser.END_TAG)
                    ;
            } else {
                skip(parser);
            }
        }
        if(db != null) {
            db.close();
        }
        return db!= null;
    }

    static Present readEntry(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "item");
        String id = null;
        String path = null;
        for(int i=parser.getAttributeCount()-1; i >= 0; i--) {
            String an = parser.getAttributeName(i);
            if("name".equals(an)) {
                path = folder + parser.getAttributeValue(i);
            } else if("id".equals(an)) {
                id = parser.getAttributeValue(i);
            }
        }
        if(path == null || id == null) {
            return null;
        }
        PriceEx p = (PriceEx) pi.getData();
        p.id = id;
        if(!pi.read()) {
            return null;
        }
        Present ret = new Present();
        ret.id = id;
        ret.photoPath = path;
        ret.folderId = p.fid;
        return ret;
    }

    static void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }

}
