package com.ksoft.ftpwriter;

import java.io.ByteArrayInputStream;
import java.net.InetAddress;

import org.apache.commons.net.ftp.FTPClient;

import android.os.AsyncTask;

public class FtpSender extends AsyncTask<Object, Void, Boolean> {

	@Override
	protected Boolean doInBackground(Object... params) {
		boolean result = false;
		
		if(params.length > 0){
			FtpData d = (FtpData)params[0];
			String file = d.file;
			String server = d.server;
			String user = d.user;
			String password = d.password;
			String path = d.path;
			String text = d.text;
			
			try {
				FTPClient ftpClient = new FTPClient();
			    ftpClient.connect(InetAddress.getByName(server));
			    ftpClient.login(user, password);
			    ftpClient.changeWorkingDirectory(path);
			 
			    if (ftpClient.getReplyString().contains("250")) {
			        ftpClient.setFileType(org.apache.commons.net.ftp.FTP.BINARY_FILE_TYPE);
			        ftpClient.enterLocalPassiveMode();
			        result = ftpClient.storeFile(file, new ByteArrayInputStream(text.getBytes("UTF-8")));
			        ftpClient.logout();
			        ftpClient.disconnect();
			    }
				    
			} catch (Exception e) {
			    e.printStackTrace();
			}
		}
	
		return result;
	}

}
