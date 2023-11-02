package com.grsoft.manager;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.grsoft.dataobjects.AnswerItem;
import com.grsoft.dataobjects.ManagerAgent;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.QuestionAttach;
import com.grsoft.dataobjects.QuestionItem;
import com.grsoft.dataobjects.impl.MAnswerImpl;
import com.grsoft.dataobjects.impl.MOrgImpl;
import com.grsoft.dataobjects.impl.MQuestionImpl;
import com.grsoft.dataobjects.impl.ManagerAgentImpl;
import com.grsoft.dataobjects.impl.PicStoreImpl;
import com.grsoft.manager.SelectAgentHelper.AgentSelectedListener;
import com.grsoft.manager.SelectOrgHelper.OrgSelectedListener;
import com.grsoft.napoleon.QuestAttachmentsList;
import com.grsoft.napoleon.QuestControl;
import com.grsoft.napoleon.QuestControlsFactory;
import com.grsoft.napoleon.QuestImage;
import com.grsoft.napoleon.QuestPhoto;
import com.grsoft.napoleon.QuestionAttachInfo;
import com.grsoft.napoleon.util.debug.Path;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.Util;
import com.grsoft.util.gps.GPSUtilNew;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
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
import androidx.core.content.FileProvider;
import android.text.Html;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class AuditEdit extends Activity implements OnClickListener, AgentSelectedListener,
	QuestPhoto, OrgSelectedListener{
	public static Class<? extends Activity> activity = AuditEdit.class;
	protected MQuestionImpl quest = new MQuestionImpl();
	protected MAnswerImpl answer = new MAnswerImpl();
	private List<QuestControl> controls = new ArrayList<QuestControl>();
	private Button btnAgent;
	private SelectAgentHelper slAgentHelper;
	private TextView tvDescr;
	private View btnAttach;
	private static final String ANSWER_ROW_ID = "answerrowid";
	private View btnSave;
	private static final String COUNTER = "counter";
	private String storePath = "";
	private QuestImage image;
	private static final int CAMERA_ACTIVITY = 1;
	private TextView tvTitle;
	private SelectOrgHelper slOrgHelper;
	private Button btnOrg;
	private String selectedOrgID = "";

	public static void open(Context context, String idquest, long answer) {
		Intent intent = new Intent(context, activity);
		intent.putExtra(AuditQuests.ID_QUEST, idquest);
		intent.putExtra(ANSWER_ROW_ID, answer);
		
		context.startActivity(intent);
	}

	BroadcastReceiver delphoto = new BroadcastReceiver(){

		@Override
		public void onReceive(Context context, Intent intent) {
			String id = intent.getStringExtra(ManagePhotoDlg.PIC_ID);
			image.delImage(context, id);
		}
	};

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.auditedit);

		btnAgent = (Button) findViewById(R.id.btnAgent);
		tvDescr = (TextView) findViewById(R.id.tvDescr);
		btnAttach = findViewById(R.id.btnAttach);
		LinearLayout holder = (LinearLayout) findViewById(R.id.holder);
		btnSave = findViewById(R.id.btnSave);
		btnOrg = (Button)findViewById(R.id.btnOrg);
		
		initActionBar();

		quest.read("idquest", getIntent().getStringExtra(AuditQuests.ID_QUEST));
		answer.read(getIntent().getLongExtra(ANSWER_ROW_ID, ExtrasConst.INVALID_ID));
		answer.close();

		Map<String, List<AnswerItem>> map = new HashMap<String, List<AnswerItem>>();

		for (AnswerItem i : answer.getData().items) {
			if (!map.containsKey(i.iditem))
				map.put(i.iditem, new ArrayList<AnswerItem>());

			map.get(i.iditem).add(i);
		}

		QuestControlsFactory factory = QuestControlsFactory.getInstance();
		
		Collections.sort(quest.getData().items, new Comparator<QuestionItem>() {

			@Override
			public int compare(QuestionItem lhs, QuestionItem rhs) {
				return lhs.number - rhs.number;
			}
		});

		int idx = 0;
		for (int pos = 0; pos < quest.getData().items.size(); pos++) {
			QuestionItem i = quest.getData().items.get(pos);
			
			if (i.type == QuestionItem.DATASET)
				continue;
			
			QuestControl iv = factory.createItem(i);

			if (iv != null) {
				View v = iv.createView(this);
				v.setBackgroundResource(idx++ % 2 != 0 ? R.drawable.even_row_selector : R.drawable.list_selector);
				
				int c = i.optional == 0 ? getResources().getColor(R.color.red) : getResources().getColor(R.color.green);
				v.findViewById(R.id.status).setBackgroundColor(c);
				
				settingQuestControlView(v, i);
				
				holder.addView(v);

				if (map.containsKey(i.iditem))
					setValueQuestControl(iv, map.get(i.iditem));

				controls.add(iv);
			}
		}

		btnAgent.setOnClickListener(this);
		registerForContextMenu(btnAgent);
		
		btnSave.setOnClickListener(this);
		btnAttach.setOnClickListener(this);

		slAgentHelper = new SelectAgentHelper();
		slAgentHelper.init();
		slAgentHelper.setAgentSelectedListner(this);
		
		slOrgHelper = new SelectOrgHelper();
		slOrgHelper.init();
		slOrgHelper.setOrgSelectedListner(this);

		tvDescr.setText(quest.getData().name);
		
		if (answer.getData().agentid.trim().length() > 0) {
			ManagerAgentImpl a = new ManagerAgentImpl();
			if (a.read("id", answer.getData().agentid))
				btnAgent.setText(a.getData().name);
		}
		
		btnSave.setEnabled(answer.isEditable());
		
		tvTitle.setText(quest.getData().name);
		tvDescr.setText(quest.getData().text);
		
		btnOrg.setOnClickListener(this);
		
		if(answer.getId().trim().length() > 0) {
			MOrgImpl org = new MOrgImpl();
			if (org.read("id", answer.getId()))
				btnOrg.setText(Html.fromHtml("<b>" + 
						org.getData().name + "</b><br><i>" + 
						org.getData().address + "</i>"));
		}
	}
	
	protected void setValueQuestControl(QuestControl iv, List<AnswerItem> list) {
		iv.setValue(list);
	}

	protected void settingQuestControlView(View v, QuestionItem i) {
	}

	@Override
	protected void onStart() {
		super.onStart();
		
		registerReceiver(delphoto, new IntentFilter(ManagePhotoDlg.DEL_PHOTO_ACTION));
	}

	@Override
	protected void onPause() {
		super.onPause();
		
		if (isFinishing())
			unregisterReceiver(delphoto);
	}
	
	protected void initActionBar() {
		View v = getLayoutInflater().inflate(getActionBarLayoutID(), null);
        ActionBar ab = getActionBar();
        ab.setCustomView(v);
        ab.setDisplayShowCustomEnabled(true);
        ab.setDisplayShowTitleEnabled(false);
        
        tvTitle = (TextView) v.findViewById(R.id.tvTitle);
        
        adjustChildActionBar(v);
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		getMenuInflater().inflate(R.menu.auditquests_context_menu, menu);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.itDelete) {
			btnAgent.setText(R.string.select_agent);
			answer.getData().agentid = "";
			return true;
		}else
			return false;
	}

	protected void adjustChildActionBar(View v) {
	}

	protected int getActionBarLayoutID() {
		return R.layout.action_bar;
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btnAgent)
			showDialog(R.id.agent_dlg);
		else if (v.getId() == R.id.btnAttach)
			showAttach();
		else if (v.getId() == R.id.btnSave)
			save();
		else if (v.getId() == R.id.btnOrg)
			showDialog(R.id.org_dlg);
	}
	
	private void showAttach() {
		List<QuestionAttachInfo> info = new ArrayList<QuestionAttachInfo>();
		
		for (QuestionAttach a : quest.getData().attach) {
			QuestionAttachInfo i = new QuestionAttachInfo();
			i.id = a.id;
			i.name = a.name;
			info.add(i);
		}
		
		QuestAttachmentsList.open(this, info);
	}

	protected void save() {
		boolean isOk = true;
		List<AnswerItem> items = new ArrayList<AnswerItem>();
		
		for(QuestControl c : controls){
			List<AnswerItem> val = c.getValue();
			
			postProcess(val, c);
			
			if (c.item.optional == 0 && isNotComplete(val, c)) {
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
				answer.init(this, selectedOrgID, GPSUtilNew.getLastKnownLocation());
				answer.getData().question = quest.getData().idquest;
				answer.getData().qname = quest.getData().name;
			}
			
			answer.getData().items = items;
			
			answer.write();
			answer.close();
		
			finish();
		}
	}

	protected boolean isNotComplete(List<AnswerItem> val, QuestControl c) {
		return val.size() == 0;
	}


	protected void postProcess(List<AnswerItem> val, QuestControl c) {
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		if (id == R.id.agent_dlg)
			return slAgentHelper.createDialog(this);
		else if (id == R.id.org_dlg)
			return slOrgHelper.createDialog(this);
		else
			return super.onCreateDialog(id);
	}

	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		if (id == R.id.agent_dlg)
			slAgentHelper.prepareDialog(dialog);
		if (id == R.id.org_dlg)
			slOrgHelper.prepareDialog(dialog);
		else
			super.onPrepareDialog(id, dialog);
	}

	@Override
	public void onAgentSelected(ManagerAgent agent) {
		((Button) btnAgent).setText(agent.name);
		answer.getData().agentid = agent.id;
		slOrgHelper.clearCache();
		slOrgHelper.setUserID(agent.id);
	}

	@Override
	public void doPhoto(QuestImage questImage) {
		try {
			if (Environment.getExternalStorageState().equals(
					Environment.MEDIA_MOUNTED)) {
				File path = new File(Path.getDataDir());
				path.mkdir();
				SharedPreferences pref = getPreferences(Context.MODE_PRIVATE);
				int cnt = pref.getInt(COUNTER, 1);
				File file = new File(getExternalFilesDir(null), Integer.toString(cnt) + ".jpg");
				storePath = file.getAbsolutePath();
				Editor ed = pref.edit();
				ed.putInt(COUNTER, ++cnt);
				ed.commit();
				
				image = questImage;

				Uri uri = null;
				
				if (Build.VERSION.SDK_INT >= 24) {
					uri = FileProvider.getUriForFile(this,"com.grsoft.manager.fileprovider", file); 
				}else
					uri = Uri.fromFile(file);
				
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
				intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
				startActivityForResult(intent, CAMERA_ACTIVITY);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == CAMERA_ACTIVITY && resultCode == Activity.RESULT_OK
				&& storePath.trim().length() > 0) {
			
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
	public void onOrgSelected(Org org) {
		btnOrg.setText(Html.fromHtml("<b>" + 
				org.name + "</b><br><i>" + 
				org.address + "</i>"));
		selectedOrgID = org.id;
	}
}
