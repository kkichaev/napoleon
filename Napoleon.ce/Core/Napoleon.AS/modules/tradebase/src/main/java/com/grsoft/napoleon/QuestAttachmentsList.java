package com.grsoft.napoleon;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.grsoft.dataobjects.impl.AttachmentImpl;
import com.grsoft.napoleon.util.Config;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.network.UpdateProcess;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import androidx.core.content.FileProvider;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class QuestAttachmentsList extends Activity implements OnItemClickListener {
	private ListView list;
	private final static String ATTACHES = "attaches";

	public static void open(Context context, List<QuestionAttachInfo> attaches) {
		Intent i = new Intent(context, QuestAttachmentsList.class);
		i.putParcelableArrayListExtra(ATTACHES, (ArrayList<? extends Parcelable>) attaches);
		context.startActivity(i);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.taskattachment);
		
		list = (ListView) findViewById(R.id.list);
		list.setAdapter(new QuestAttachmentAdapter(this, 
				getIntent().getParcelableArrayListExtra(ATTACHES)));
		list.setOnItemClickListener(this);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		final QuestionAttachInfo i = (QuestionAttachInfo) parent.getItemAtPosition(position);
		final AttachmentImpl impl = new AttachmentImpl();

		if (!impl.read("id", i.id)) 
			loadAttachment(i, impl);
		else
			preview(impl.getData().path);
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		if (id == R.id.wait_dlg)
			return createWaitDlg();
		return super.onCreateDialog(id);
	}

	private Dialog createWaitDlg() {
		ProgressDialog dlg = new ProgressDialog(this);
		dlg.setMessage(getString(R.string.please_wait));
		return dlg;
	}

	protected void loadAttachment(final QuestionAttachInfo i, final AttachmentImpl impl) {
		UpdateProcess p = new UpdateProcess(this) {
			@Override
			protected void onPreExecute() {
				showDialog(R.id.wait_dlg);
			}
			
			@Override
			protected void onPostExecute(Boolean result) {
				if (result) {
					dismissDialog(R.id.wait_dlg);
					if (impl.read("id", i.id)) 
						preview(impl.getData().path);
				}
			}
		};
		
		Config cfg = ConfigManager.getConfig();
		UpdateProcess.Params arg = new UpdateProcess.Params();
		arg.login = cfg.login;
		arg.pass = cfg.passw;
		arg.ip1 = cfg.address;
		arg.ip2 = cfg.address2;
		arg.port1 = cfg.port;
		
		arg.indata.add(new AttachmentHitching(i.id));
		p.execute(arg);
	}
	
	private void preview(String file) {
		try {
			Intent i = new Intent();
			i.setAction(Intent.ACTION_VIEW);
			String mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(file));
			
			Uri uri = null;
			
			if (Build.VERSION.SDK_INT >= 24) {
				uri = FileProvider.getUriForFile(this,getString(R.string.fileprovider_authorities), new File(file));
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
