package com.grsoft.database;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.grsoft.dataobjects.CommandArgs;
import com.grsoft.dataobjects.ReqServerData;
import com.grsoft.dataobjects.ReqServerExch;
import com.grsoft.network.ObjectListener;
import com.grsoft.network.RawObject;
import com.grsoft.network.SocketConnection;
import com.grsoft.network.exception.RuntimeException;

public class ServerReqDataHitching implements CommandArgs, ObjectListener {

	ServerDemoDataSendHitching demoData;
	ServerLicenseDataSendHitching licenseData;
	ServerLicenseTypeSendHitchiing licenseType;
	
	public ServerReqDataHitching(ServerDemoDataSendHitching dd, ServerLicenseDataSendHitching ld, ServerLicenseTypeSendHitchiing lt) {
		demoData = dd;
		licenseData = ld;
		licenseType = lt;
	}
	
	@Override public void onStart() {}
	@Override public void onSave() {}
	@Override public void onEnd() { }
	@Override public String getObjectName() { return "ReqServerData"; }
	@Override public String getCommand() { return "SELECT"; }
	@Override public String getParams() throws RuntimeException { return getObjectName();	}

	@Override
	public void onRead(RawObject rawObject) throws RuntimeException {
		ReqServerData req = (ReqServerData) rawObject.createDataObject(ReqServerData.class);
		if(req != null) {
			ReqServerExch rse = new ReqServerExch();
			rse.ip = SocketConnection.WinAdr == null ? "" : SocketConnection.WinAdr;
			rse.port = SocketConnection.WinPort;
			rse.name = req.name;
			rse.log = req.data;
			rse.tz = req.tz;
			
			Gson json = new Gson();
			String data = json.toJson(rse);
			try {
				String spec = "https://grsoft.ru//int_cli_2/updrq.php";
				URL url = new URL(spec);
				HttpURLConnection  conn = (HttpURLConnection)url.openConnection();
				conn.setConnectTimeout(5000);
				try {
					conn.setDoOutput(true);
					conn.setChunkedStreamingMode(0);
					OutputStream out = new BufferedOutputStream(conn.getOutputStream());
					BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
					writer.write(data);

					data = "";
					InputStream in = new BufferedInputStream(conn.getInputStream());
					BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
					String line;
					while((line = reader.readLine()) != null) {
						data += line;
					}
					parseData(data);
				} catch(Exception e1) {
					e1.printStackTrace();
				} finally {
					conn.disconnect();
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	void parseData(String data) {
		JsonElement root = new JsonParser().parse(data);
		if(root == null)
			return;
		
		JsonArray objects = root.getAsJsonArray();
		for(int i = 0; i < objects.size(); i++) {
			JsonObject o = objects.get(i).getAsJsonObject();
			if(o == null)
				continue;
			JsonElement elName = o.get("name");
			JsonElement elData = o.get("data");
			if(elName == null || elData == null)
				continue;
			String name = elName.getAsString();
			if(name.equals("DemoData")) {
				demoData.add(elData);
			} else if( name.equals("LicenseTypeData")) {
				licenseType.add(elData);
			} else if(name.equals("LicenseData")) {
				licenseData.add(elData);
			}
		}
	}
}
