package com.grsoft.napoleon;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import com.grsoft.dataobjects.AnswerItem;
import com.grsoft.dataobjects.QuestionAttach;
import com.grsoft.dataobjects.QuestionItem;
import com.grsoft.dataobjects.impl.AnswerImpl;
import com.grsoft.dataobjects.impl.PicStoreImpl;
import com.grsoft.dataobjects.impl.QuestionImpl;
import com.grsoft.napoleon.documents.QuestionDoc;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.napoleon.util.debug.Path;
import com.grsoft.script.ScriptActivity;
import com.grsoft.script.ScriptHelper;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.Util;
import com.grsoft.util.gps.GPSUtilNew;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class QuestEdit extends Activity implements OnClickListener, QuestPhoto, ScriptActivity{
	private static final String ANSWER_ROW_ID = "answerrowid";
	private View btnSave;
	private List<QuestControl> controls = new ArrayList<QuestControl>();
	
	private AnswerImpl answer = (AnswerImpl) QuestionDoc.instance().create();
	private QuestionImpl quest = new QuestionImpl();
	private String orgid = "";
	private static final String COUNTER = "counter";
	private String storePath = "";
	private static final int CAMERA_ACTIVITY = 0x181212; //1;
	private QuestImage image;
	private TextView tvName;
	private View btnAttach;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.quest_layout);

		btnSave = findViewById(R.id.btnSave);
		tvName = (TextView) findViewById(R.id.tvName);
		btnAttach = findViewById(R.id.btnAttach);

		btnSave.setOnClickListener(this);

		quest.read(getIntent().getLongExtra(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ID));
		quest.close();
		
		answer.read(getIntent().getLongExtra(ANSWER_ROW_ID, ExtrasConst.INVALID_ID));
		answer.close();
		
		Map<String, List<AnswerItem>> map = new HashMap<String, List<AnswerItem>>();
		
		for(AnswerItem i : answer.getData().items) {
			if (!map.containsKey(i.iditem))
				map.put(i.iditem, new ArrayList<AnswerItem>());
			
			map.get(i.iditem).add(i);
		}
		
		orgid  = getIntent().getStringExtra(ExtrasConst.ORG_ID_STR);

		LinearLayout holder = findViewById(R.id.holder);
		
		TextView tv = findViewById(R.id.tvDescr);
		tv.setText(quest.getData().text);

		QuestControlsFactory factory = QuestControlsFactory.getInstance();
		
		Collections.sort(quest.getData().items, new Comparator<QuestionItem>() {

			@Override
			public int compare(QuestionItem lhs, QuestionItem rhs) {
				return lhs.number - rhs.number;
			}
		});

		for (int pos = 0; pos < quest.getData().items.size(); pos++) {
			QuestionItem i = quest.getData().items.get(pos);
			QuestControl iv = factory.createItem(i);

			if (iv != null) {
				View v = iv.createView(this);
				v.setBackgroundResource(pos % 2 != 0 ? R.drawable.even_row_selector : R.drawable.list_selector);
				
				int c = i.optional == 0 ? getResources().getColor(R.color.red) : getResources().getColor(R.color.green);
				v.findViewById(R.id.status).setBackgroundColor(c);

				holder.addView(v);
				
				if (map.containsKey(i.iditem))
					iv.setValue(map.get(i.iditem));
				
				controls.add(iv);
			}
		}
		
		btnSave.setEnabled(answer.isEditable());
		tvName.setText(quest.getData().name);
		btnAttach.setOnClickListener(this);
		btnSave.setEnabled(answer.isEditable());

		ScriptHelper.initView(this, QuestionDoc.instance().getObjectName(), answer.getData().created, answer.getId() );
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btnSave) {
			if(save()) {
				finish();
			}
		}else if (v.getId() == R.id.btnAttach)
			showAttach();
	}

	private void showAttach() {
		List<QuestionAttachInfo> info = new ArrayList<>();
		
		for (QuestionAttach a : quest.getData().attach) {
			QuestionAttachInfo i = new QuestionAttachInfo();
			i.id = a.id;
			i.name = a.name;
			info.add(i);
		}
		
		QuestAttachmentsList.open(this, info);
	}

	protected boolean save() {
		boolean isOk = true;
		List<AnswerItem> items = new ArrayList<AnswerItem>();
		
		for(QuestControl c : controls){
			List<AnswerItem> val = c.getValue();
			
			
			if (c.item.optional == 0 && val.size() == 0) {
				isOk = false;
				break;
			}
			
			items.addAll(val);
		}
		
		if (!isOk)
			Toast.makeText(this, R.string.all_question_should_been_processed, 
					Toast.LENGTH_LONG).show();
		else {
			if (answer.getRowid() == ExtrasConst.INVALID_ID) {
				answer.init(this, orgid, GPSUtilNew.getLastKnownLocation());
				answer.getData().question = quest.getData().idquest;
				answer.getData().qname = quest.getData().name;
			}
			
			answer.getData().items = items;
			
			answer.write();
			answer.close();
		}

		return  isOk;
	}

	@Override
	public void doPhoto(QuestImage questImage) {
		try {
			if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
				File path = new File(Path.getDataDir());
				path.mkdir();
				SharedPreferences pref = getPreferences(Context.MODE_PRIVATE);
				int cnt = pref.getInt(COUNTER, 1);
				File file = new File(getExternalFilesDir(null), Integer.toString(cnt) + ".jpg");
				storePath = file.getAbsolutePath();
				Editor ed = pref.edit();
				ed.putInt(COUNTER, ++cnt);
				ed.commit();

				com.grsoft.napoleon.util.CfgNplW cfg = (com.grsoft.napoleon.util.CfgNplW) ConfigManager.getConfig();
				image = questImage;
				if (cfg.androidPhoto) {
					Uri uri = null;
					
					if (Build.VERSION.SDK_INT >= 24) {
						try {
							uri = FileProvider.getUriForFile(this,getString(R.string.fileprovider_authorities), file);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					if(uri == null)
						uri = Uri.fromFile(file);
					
					Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
					intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
					startActivityForResult(intent, CAMERA_ACTIVITY);
				} else {
					CameraPreview.takePhoto(this, storePath, CAMERA_ACTIVITY);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == CAMERA_ACTIVITY && resultCode == Activity.RESULT_OK && storePath.trim().length() > 0) {
			
			PicStoreImpl picStore = new PicStoreImpl();
			picStore.getData().id = UUID.randomUUID().toString().replace("-", "");
			picStore.getData().picture = storePath.getBytes();
			picStore.getData().date = answer.getData().created;
			picStore.getData().created = Util.getDateTime();
			picStore.write();
			picStore.close();
			
			image.addImage(this, picStore.getData().id);
			
			storePath = "";
		}
	}

	@Override
	public void longClick(QuestImage questImage, String id) {
		this.image = questImage; 
		ManagePhotoDlg dlg = new ManagePhotoDlg();
		Bundle args = new Bundle();
		args.putString(ManagePhotoDlg.PIC_ID, id);
		dlg.setArguments(args);
		dlg.show(getFragmentManager(), dlg.getClass().getCanonicalName());
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		
		registerReceiver(delphoto, new IntentFilter(ManagePhotoDlg.DEL_PHOTO_ACTION));
	}
	
	BroadcastReceiver delphoto = new BroadcastReceiver(){

		@Override
		public void onReceive(Context context, Intent intent) {
			String id = intent.getStringExtra(ManagePhotoDlg.PIC_ID);
			image.delImage(context, id);
		}
	};

	@Override
	protected void onPause() {
		super.onPause();

		if (isFinishing()){
			if (answer.isEditable() && answer.getData().items.size() == 0) {
				answer.delete();
				answer.close();
			}
		}
	}

	@Override
	public boolean closeDocument() {
		if(!answer.isEditable()) {
			return true;
		}
		boolean ret = save();
		if(!ret) {
			answer.delete();
		}
		return ret;
	}
}
