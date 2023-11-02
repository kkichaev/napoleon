package com.grsoft.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import com.grsoft.dataobjects.Agent;
import com.grsoft.napoleon.util.Config;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.napoleon.util.debug.Path;

public class AgentInfoHelper {
	private static Map<String, Agent> agents = new HashMap<String, Agent>();

	public static Map<String, Agent> getAgents() {
		File file = Path.getAgentInfo();

		if (agents.size() == 0 && file.exists()) {
			try {
				FileReader fr = new FileReader(file);
				StringWriter sw = new StringWriter();
				final int BUF_SZ = 256;
				char[] buf = new char[BUF_SZ];
				
				int sz = -1;
				
				while((sz = fr.read(buf)) != -1) 
					sw.write(buf, 0, sz);
				
				fr.close();
				sw.close();
				
				Config cfg = ConfigManager.getConfig();
				String text = sw.toString();
				text = Crypto.decrypt(cfg.passw, text);
				
				XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
				factory.setNamespaceAware(false);
				XmlPullParser xpp = factory.newPullParser();
				InputStream is = new ByteArrayInputStream(text.getBytes());
				xpp.setInput(is, "UTF-8");

				int eventType = xpp.getEventType();

				while (eventType != XmlPullParser.END_DOCUMENT) {
					switch (xpp.getEventType()) {
					case XmlPullParser.START_TAG:
						if (xpp.getName().equals("item")) {
							Agent a = new Agent();
							a.id = xpp.getAttributeValue("", "id");
							a.login = xpp.getAttributeValue("", "login");
							a.password = xpp.getAttributeValue("", "password");
							a.name = xpp.getAttributeValue("", "name");
							agents.put(a.id, a);
						}

					default:
						break;
					}
					eventType = xpp.next();
				}

				is.close();
			} catch (Exception e) {

			}

		}

		return agents;
	}
}
