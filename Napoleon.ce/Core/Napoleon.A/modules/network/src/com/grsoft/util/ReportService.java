package com.grsoft.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.widget.RemoteViews;
import com.grsoft.napoleon.R;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.napoleon.util.debug.Path;

@SuppressLint("SimpleDateFormat")
public class ReportService extends Service {
	private static final String HTTP_GRSOFT_RU_UPGRADE_GETCURIP_PHP = "http://grsoft.ru/upgrade/getcurip.php";
	private static final String NAPOLEON = "Napoleon";
	private static final String NAPOLEON_CRASH_LOG = "%s/crash.log";
	private static final String NAPOLEON_CRASH_ZIP = "%s/crash.zip";
	private static final String USERNAME = "anonymous";
	private static final String PASSWORD = "anonymous";
	private static final String PATH = "pub/crash";
	private static final String BASE = "base";
	private static final String FOLDER = "folder";
	private static final String COMMENT = "comment";
	private static final int PROCESS_MSG_ID = 1;
	private static final int STATUS_MSG_ID = 2;
	protected static final int PROCESS_ID = 1;

	public static void open(Context context, boolean base, String comment, String folder){
		Intent intent = new Intent(context, ReportService.class);
		intent.putExtra(BASE, base);
		intent.putExtra(COMMENT, comment);
		intent.putExtra(FOLDER, folder);
		context.startService(intent);
		
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@SuppressLint("HandlerLeak")
	@Override
	public void onStart(Intent intent, int startId) {
		boolean base = intent.getBooleanExtra(BASE, false);
		String folder = intent.getStringExtra(FOLDER);
		boolean result = false;
		FTPClient ftpClient = new FTPClient();
		String server = "";
		File zipFile = null;
		
		final NotificationManager manager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
		final Notification notification = new Notification(R.drawable.message, 
					getString(R.string.report_sending), System.currentTimeMillis());
		notification.flags |= Notification.FLAG_INSISTENT | Notification.FLAG_AUTO_CANCEL;;
		final RemoteViews contentView = new RemoteViews(getPackageName(), 
				R.layout.notify_progress);
		
		contentView.setTextViewText(R.id.text, getString(R.string.progress));       
		notification.contentView = contentView;
//		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, null, 0);
		Intent crsh_i = new Intent("com.grsoft.napoleon.crash_send");
		PendingIntent contentIntent = PendingIntent.getBroadcast(this, PROCESS_MSG_ID, crsh_i, PendingIntent.FLAG_UPDATE_CURRENT);
		notification.contentIntent = contentIntent;
		manager.notify(PROCESS_MSG_ID, notification);
			
		try {
			HttpClient httpclient = new DefaultHttpClient();
		    HttpResponse response = httpclient.execute(new HttpGet(HTTP_GRSOFT_RU_UPGRADE_GETCURIP_PHP));
		    StatusLine statusLine = response.getStatusLine();
		    if(statusLine.getStatusCode() == HttpStatus.SC_OK){
		        ByteArrayOutputStream out = new ByteArrayOutputStream();
		        response.getEntity().writeTo(out);
		        out.close();
		        server = out.toString();
		        result = true;
		    }else
		    	response.getEntity().getContent().close();
		   
		    if(result){
			    ftpClient.connect(InetAddress.getByName(server));
			    ftpClient.login(USERNAME, PASSWORD);
			    ftpClient.changeWorkingDirectory(PATH);
			 
			    if (ftpClient.getReplyString().contains("250")) {
			        ftpClient.setFileType(org.apache.commons.net.ftp.FTP.BINARY_FILE_TYPE);
			        
			        OutputStream os = new BufferedOutputStream(new FileOutputStream(new File(
			        				Environment.getExternalStorageDirectory(), 
			        				String.format(NAPOLEON_CRASH_ZIP, folder))));
			        
			        File file = new File(
	        				Environment.getExternalStorageDirectory(),
	        				String.format(NAPOLEON_CRASH_LOG, folder));
			        
	        		ZipOutputStream zos = new ZipOutputStream(new BufferedOutputStream(os));
	        		BufferedInputStream origin = null; 
	        		final int BUFFER = 2048;
	        		byte data[] = new byte[BUFFER]; 
	        		
	        		try {
	        			FileInputStream fi = new FileInputStream(file); 
	        			origin = new BufferedInputStream(fi, BUFFER); 
	    		        String filename = file.getName();
	
	    		        ZipEntry entry = new ZipEntry(filename);
	    		        zos.putNextEntry(entry);
	    		        int count; 
	    		        
	    		        while ((count = origin.read(data, 0, BUFFER)) != -1) { 
	    		        	zos.write(data, 0, count); 
	    		        } 
	    		        
	    		        origin.close();
	    		        
	    		        if(intent != null){
	    		        	String comment = intent.getStringExtra(COMMENT);
	    		        	
	    		        	if(comment != null && comment.length() > 0){
			    		        entry = new ZipEntry("comment");
			    		        zos.putNextEntry(entry);
			    		        
			    		        byte[] cmt_array = comment.getBytes();
			    		        zos.write(cmt_array, 0, cmt_array.length);
	    		        	}
	    		        }
	    		        
	    		        com.grsoft.napoleon.util.Config cfg = ConfigManager.getConfig();
	    		        StringBuilder privateInfo = new StringBuilder();
	    		        privateInfo.append("ip1=").append(cfg.address).append('\n');
	    		        privateInfo.append("ip2=").append(cfg.address2).append('\n');
	    		        privateInfo.append("port=").append(cfg.port).append('\n');
	    		        privateInfo.append("port2=").append(cfg.port2).append('\n');
	    		        privateInfo.append("login=").append(cfg.login).append('\n');
	    		        privateInfo.append("passw=").append(cfg.passw).append('\n');
	    		        
	    		        entry = new ZipEntry("privateInfo");
	    		        zos.putNextEntry(entry);
	    		        
	    		        byte[] pi = privateInfo.toString().getBytes();
	    		        zos.write(pi, 0, pi.length);
	    		        
	    		        if(base){
							File src = new File(Path.getDataBasePath());
							File sdcard = Environment.getExternalStorageDirectory();
							File dist = new File(new File(sdcard, NAPOLEON), Path.BASE_NAME);
							Util.copy(src,dist);
							
							filename = dist.getName();
							fi = new FileInputStream(dist); 
			    		    origin = new BufferedInputStream(fi, BUFFER);
		    		        entry = new ZipEntry(filename);
		    		        zos.putNextEntry(entry);
		    		        count = 0;
	
		    		        while ((count = origin.read(data, 0, BUFFER)) != -1) { 
		    		        	zos.write(data, 0, count); 
		    		        } 
		    		        
		    		        origin.close();
	    		        }
	    		        
	    		        zos.closeEntry();
	    		        
	        		} finally {
	        			 zos.flush();
	        		     zos.close();
	        		}
			        		 
			        BufferedInputStream buffIn = null;
			        String prj = getResources().getString(R.string.project);
			        Calendar calendar = Calendar.getInstance();
			        SimpleDateFormat sdf =  new SimpleDateFormat("yyMMddHHmmss");
			        String time = sdf.format(calendar.getTime());
			        
			        zipFile = new File(
	        				Environment.getExternalStorageDirectory(),
	        				String.format(NAPOLEON_CRASH_ZIP, folder)); 
			        buffIn = new BufferedInputStream(
			        		new FileInputStream(zipFile));
			        ftpClient.enterLocalPassiveMode();
			        
			        final int len = (int)zipFile.length();
					notification.contentView.setProgressBar(R.id.progressBar, len, 0, false);
					manager.notify(PROCESS_MSG_ID, notification);
					
			        ProgressInputStream progressInput = new ProgressInputStream(buffIn, new Handler(){
			        	@Override
			        	public void handleMessage(Message msg) {
			        		if(msg.what == PROCESS_ID){
				        		long p  = msg.getData().getLong(ProgressInputStream.PROGRESS_UPDATE);
				        		notification.contentView.setProgressBar(R.id.progressBar, len, (int)p, false);
				        		manager.notify(PROCESS_MSG_ID, notification);
			        		}
			        	}
			        });
			        
			        result = ftpClient.storeFile(time+"."+prj, progressInput);
			        
			        buffIn.close();
			        ftpClient.logout();
			        ftpClient.disconnect();
			        
			        
			    }
			    
			    if(zipFile != null)
			    	zipFile.delete();
		    }
		} catch (Exception e) {
		    e.printStackTrace();
		}
		
		int pic = R.drawable.message;
        String tickerText = getString(R.string.sent_succs);
        String contentTitle = getString(R.string.report_sending);
        String contentText = getString(R.string.sent_succs);
        
        if(!result){
        	tickerText = getString(R.string.send_error);
	        contentTitle = getString(R.string.report_sending);
	        contentText = getString(R.string.send_error_try_later);
        }
        
    	manager.cancelAll();
    	Notification state = new Notification(pic, tickerText, System.currentTimeMillis());
    	state.flags |= Notification.FLAG_AUTO_CANCEL;
//    	PendingIntent i = PendingIntent.getActivity(this, 0, null, 0);
//    	state.setLatestEventInfo(this, contentTitle, contentText, i);
    	state.setLatestEventInfo(this, contentTitle, contentText, contentIntent);
    	manager.notify(STATUS_MSG_ID, state);
    	
		stopSelf();
	}
}

class ProgressInputStream extends InputStream {
    public static final String PROGRESS_UPDATE = "progress_update";
    private static final int TEN_KILOBYTES = 1024 * 10;
 
    private InputStream inputStream;
    private Handler handler;
 
    private long progress;
    private long lastUpdate;
 
    private boolean closed;
 
    public ProgressInputStream(InputStream inputStream, Handler handler) {
        this.inputStream = inputStream;
        this.handler = handler;
 
        this.progress = 0;
        this.lastUpdate = 0;
 
        this.closed = false;
    }
 
    @Override
        public int read() throws IOException {
            int count = inputStream.read();
            return incrementCounterAndUpdateDisplay(count);
        }
 
    @Override
        public int read(byte[] b, int off, int len) throws IOException {
            int count = inputStream.read(b, off, len);
            return incrementCounterAndUpdateDisplay(count);
        }
 
    @Override
        public void close() throws IOException {
            super.close();
            if (closed)
                throw new IOException("already closed");
            closed = true;
        }
 
    private int incrementCounterAndUpdateDisplay(int count) {
        if (count > 0)
            progress += count;
        lastUpdate = maybeUpdateDisplay(progress, lastUpdate);
        return count;
    }
 
    private long maybeUpdateDisplay(long progress, long lastUpdate) {
        if (progress - lastUpdate > TEN_KILOBYTES) {
            lastUpdate = progress;
            sendLong(PROGRESS_UPDATE, progress);
        }
        return lastUpdate;
    }
 
    public void sendLong(String key, long value) {
        Bundle data = new Bundle();
        data.putLong(key, value);
 
        Message message = Message.obtain();
        message.what = ReportService.PROCESS_ID;
        message.setData(data);
        handler.sendMessage(message);
    }
}