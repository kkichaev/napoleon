package com.grsoft.ads;

import java.io.File;

import com.grsoft.ads.dataobjects.TaskAttachmentHitching;
import com.grsoft.ads.dataobjects.TaskAttachmentInfo;
import com.grsoft.ads.dataobjects.impl.TaskAttcahmentImpl;
import com.grsoft.network.UpdateProcess;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.core.content.FileProvider;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class TaskAttachmentActivity extends SyncActivity implements OnItemClickListener {
	private ListView list;
	private final static String TASKID = "taskid";

	public static void open(Context context, String taskid) {
		Intent i = new Intent(context, TaskAttachmentActivity.class);
		i.putExtra(TASKID, taskid);
		context.startActivity(i);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.taskattachment);
		
		list = (ListView) findViewById(R.id.list);
		list.setAdapter(new TaskAttachmentAdapter(this, getIntent().getStringExtra(TASKID)));
		list.setOnItemClickListener(this);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		final TaskAttachmentInfo i = (TaskAttachmentInfo) parent.getItemAtPosition(position);
		final TaskAttcahmentImpl impl = new TaskAttcahmentImpl();

		if (!impl.read("id", i.id)) 
			loadAttachment(i, impl);
		else
			preview(impl.getData().path);
	}

	protected void loadAttachment(final TaskAttachmentInfo i, final TaskAttcahmentImpl impl) {
		UpdateProcess p = new UpdateProcess(this) {
			@Override
			protected void onPreExecute() {
				showProgress();
			}
			
			@Override
			protected void onPostExecute(Boolean result) {
				if (result) {
					hideProgress();
					if (impl.read("id", i.id)) 
						preview(impl.getData().path);
				}
			}
		};
		
		UpdateProcess.Params arg = new UpdateProcess.Params();
		service.setUserInfo(arg);
		arg.indata.add(new TaskAttachmentHitching(i.id));
		p.execute(arg);
	}
	
	private void preview(String file) {
		try {
			Intent i = new Intent();
			i.setAction(Intent.ACTION_VIEW);
			String mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(file));
			
			Uri uri = null;
			
			if (Build.VERSION.SDK_INT >= 24) {
				uri = FileProvider.getUriForFile(this,"com.grsoft.ads.fileprovider", new File(file)); 
			}else
				uri = Uri.fromFile(new File(file));
			
			i.setDataAndType(uri, mime);
			i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
			startActivity(i);
		} catch (Exception e) {
		    e.printStackTrace();
		}
	}
}
