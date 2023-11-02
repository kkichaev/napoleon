package com.grsoft.ads;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import com.grsoft.ads.database.TaskVisit;
import com.grsoft.ads.database.TaskVisitItem;
import com.grsoft.ads.dataobjects.TaskResponce;
import com.grsoft.ads.dataobjects.impl.CommentImpl;
import com.grsoft.ads.dataobjects.impl.PicStoreImpl;
import com.grsoft.ads.dataobjects.impl.TaskDocHelper;
import com.grsoft.ads.dataobjects.impl.TaskResponceImpl;
import com.grsoft.ads.dataobjects.impl.TaskVisitImpl;
import com.grsoft.napoleon.dataobjects.TaskQuery;
import com.grsoft.network.BaseFragmentActivity;
import com.grsoft.util.BitmapUtils;
import com.grsoft.util.gps.GPSUtilNew;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import androidx.core.content.FileProvider;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;


public class TaskEdit extends TaskPreview implements OnClickListener {
	public static final Class<? extends BaseFragmentActivity> activity = TaskEdit.class;
	public static final String DEL_PHOTO_ACTION = "com.grsoft.ads.TaskEdit.DEL_PHOTO_ACTION"; 
	
	private LinearLayout llAnswer;
	protected TextView tvRemark;
	protected View btnPhoto;
	private TaskVisitImpl visit = new TaskVisitImpl();
	private static final String COUNTER = "counter_str";
	protected static final int CAMERA_ACTIVITY = 1;
	private String storePath = new String();
	private LinearLayout preview;
	private CommentImpl comment = new CommentImpl();
	private ImageButton ivAttachment;

	public static void open(Context context, String taskid) {
		Intent intent = new Intent(context, activity);
		intent.putExtra(AdsConsts.TASKID, taskid);
		context.startActivity(intent);
	}
	
	@Override
	protected void initView() {
		super.initView();
		((TextView)btnOK).setText(task.getData().solution == TaskQuery.RESOLVED ? R.string.set_to_work : R.string.Finish );
		btnOK.setOnClickListener(finishClick());
		btnCancel.setVisibility(View.GONE);
		llAnswer.setVisibility(View.VISIBLE);
		
		comment.read("taskid", task.getData().taskid);
		String cmn = getString(R.string.Answer_task);
		
		if(comment.getData().text.length() > 0)
			cmn = comment.getData().text;

		tvRemark.setText(cmn);	
		tvRemark.setOnClickListener(remarkClick());
		btnPhoto.setOnClickListener(photoClick());
		btnNote.setVisibility(View.VISIBLE);
		
		findViewById(R.id.topPanel).setVisibility(View.GONE);
		ivAttachment.setVisibility(taskHasAttachments() ? View.VISIBLE : View.GONE);

		if (task.getData().solution == TaskQuery.RESOLVED)
			btnPhoto.setEnabled(false);
	}

	private OnClickListener remarkClick() {
		return new OnClickListener() {
			@Override
			public void onClick(View v) {
				RemarkDlg dlg = new RemarkDlg();
				dlg.show(getFragmentManager(), "");
			}
		};
	}

	@Override
	public void onClick(View view) {
		if (view.getId() == R.id.ivAttachment)
			TaskAttachmentActivity.open(this, task.getData().taskid);
	}

	public static class RemarkDlg extends DialogFragment{
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setTitle(R.string.answer_title);
			TaskEdit taskEdit = (TaskEdit) getActivity();
			EditText view = new EditText(taskEdit);
			view.setId(R.id.edComment);
			view.setHint(R.string.input_comment);
			view.setText(taskEdit.comment.getData().text);
			view.requestFocus();
			builder.setView(view);
			
			builder.setPositiveButton(R.string.ok, taskEdit.saveRemark());
			builder.setNegativeButton(R.string.cancel, null);
			
			return builder.create();
		}
		
		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
			super.onActivityCreated(savedInstanceState);
			getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
		}
	}

	BroadcastReceiver rcvPhoto = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String path = getPath(intent.getData(), context);
			addPhoto(path);
		}
	};

	public String getPath(Uri contentUri,Context context)
	{
		try{
			String[] proj = {MediaStore.Images.Media.DATA};

			Cursor cursor =  context.getContentResolver().query(contentUri, proj, null, null, null);
			int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			cursor.moveToFirst();
			return cursor.getString(column_index);
		}catch (Exception e){
			return contentUri.getPath();
		}
	}

	private OnClickListener photoClick() {
		return new OnClickListener() {
			@Override public void onClick(View v) { 
				try {
					if (Environment.getExternalStorageState().equals(
							Environment.MEDIA_MOUNTED)) {
						SharedPreferences pref = getPreferences(Context.MODE_PRIVATE);
						int cnt = pref.getInt(COUNTER, 1);
						File file = new File(getExternalFilesDir(null), Integer.toString(cnt) + ".jpg");
						storePath = file.getAbsolutePath();
						Editor ed = pref.edit();
						ed.putInt(COUNTER, ++cnt);
						ed.commit();

						Uri uri = null;

						if (Build.VERSION.SDK_INT >= 24) {
							uri = FileProvider.getUriForFile(TaskEdit.this, getString(R.string.ads_file_provider), file);
						}else
							uri = Uri.fromFile(file);


						Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
						intent.putExtra(MediaStore.EXTRA_OUTPUT,uri);
						intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

						startActivityForResult(intent, CAMERA_ACTIVITY);
					}
				} catch (Exception e) {
					e.printStackTrace();
				} 
			}
		};
	}

	public DialogInterface.OnClickListener saveRemark() {
		return new DialogInterface.OnClickListener(){

			@Override
			public void onClick(DialogInterface dialog, int which) {
				EditText ed = (EditText) ((Dialog)dialog).findViewById(R.id.edComment);
				
				if(ed != null){
					String txt = ed.getText().toString().trim();
					
					if(txt.length() > 0){
						comment.getData().taskid = task.getData().taskid;
						comment.getData().text = txt;
						comment.write();
						comment.close();
						
						tvRemark.setText(txt);
					}else
						tvRemark.setText(R.string.Answer_task);
				}
			}};
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void inflateView() {
		super.inflateView();
		
		View v = getLayoutInflater().inflate(R.layout.task_ready_action_bar, null);
		View btnOrder = v.findViewById(R.id.btnOrder);
		btnOrder.setOnClickListener(noteClick());

		ivAttachment = v.findViewById(R.id.ivAttachment);
		ivAttachment.setOnClickListener(this);
		
		ActionBar a = getActionBar();
        a.setCustomView(v);
        a.setDisplayShowCustomEnabled(true);

        llAnswer = (LinearLayout) findViewById(R.id.llAnswer);
		tvRemark = (TextView) findViewById(R.id.tvRemark);
		btnPhoto = findViewById(R.id.btnPhoto);
		
		preview = (LinearLayout) findViewById(R.id.preview);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		initPreview();
	}

	protected void initPreview() {
		int w = (int) getResources().getDimension(R.dimen.previewPhotoWidth);
		int h = (int) getResources().getDimension(R.dimen.previewPhotoHight);
		
		int space = (int) getResources().getDimension(R.dimen.previewPhotoSpace);
		
		preview.removeAllViews();
		
		TaskVisit v = visit.getData();
		
		for(int i = 0; i < v.items.size(); i++){
			String id  = v.items.get(i).id;
			PicStoreImpl picStore = new PicStoreImpl();
			
			if (picStore.read("id", id)) {
				String p = new String(picStore.getData().picture);
				TextView t = new TextView(this);
				t.setCompoundDrawablesWithIntrinsicBounds(null, BitmapUtils.createBitmap(this, p, w, h), null, null);
				t.setOnLongClickListener(managePhoto);
				t.setTag(id);
				LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
				lp.setMargins(0, 0, space, 0);
				lp.gravity = Gravity.CENTER_VERTICAL;
				t.setLayoutParams(lp);
				preview.addView(t);
			}
		}
	}
	
	OnLongClickListener managePhoto = new OnLongClickListener() {
		
		@Override
		public boolean onLongClick(View v) {
			ManagePhotoDlg dlg = new ManagePhotoDlg();
			Bundle args = new Bundle();
			args.putString(ManagePhotoDlg.PIC_ID, v.getTag().toString());
			dlg.setArguments(args);
			dlg.show(getFragmentManager(), dlg.getClass().getCanonicalName());
			return true;
		}
	};
	
	@Override
	protected void init() {
		super.init();
		TaskQuery t = task.getData();
		TaskVisit v = TaskDocHelper.getDoc(t.taskid, TaskVisit.class);
		
		if(v == null){
			visit.init(this, t.id, GPSUtilNew.getLastKnownLocation());
			visit.getData().taskid = t.taskid;
			visit.write();
		}else
			visit.read("created", v.created);
		
		visit.close();
	}
	
	private OnClickListener finishClick() {
		return new OnClickListener() {
			@Override
			public void onClick(View view) {
				Log.d("ADS", "TaskEdit.finish start");
				
				TaskVisit v = visit.getData();
				
				TaskResponceImpl impl = new TaskResponceImpl();
				impl.init(getContext(), task.getData());
				TaskResponce r = impl.getData();
				r.solution = task.getData().solution == TaskQuery.RESOLVED ? TaskQuery.INWORK : TaskQuery.RESOLVED;
				r.remark = comment.getData().text.trim();
				
				impl.write();
				impl.close();
				
				
				task.getData().solution = r.solution;
				task.write();
				task.close();

				if (r.solution == TaskQuery.RESOLVED) {
					v.done = r.created;
					v.readytosend = 1;
					v.params = 0;

					for (TaskVisitItem i : v.items) {
						PicStoreImpl picStore = new PicStoreImpl();

						if (picStore.read("id", i.id)) {
							picStore.getData().readytosend = 1;
							picStore.getData().params = 0;
							picStore.write();
							picStore.close();
						}
					}

					visit.write();
					visit.close();
				}
				
				sendBroadcast(new Intent(AdsService.SYNC_ACTION));
				finish();
				
				Log.d("ADS", "TaskEdit.finish finish");
			}
		};
	}
	
	@Override
	public void onStart() {
		super.onStart();
		registerReceiver(delphoto, new IntentFilter(DEL_PHOTO_ACTION));

		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			try {
				IntentFilter filter = new IntentFilter("com.android.camera.NEW_PICTURE", "image/*");
				registerReceiver(rcvPhoto, filter);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void onStop() {
		super.onStop();
		unregisterReceiver(delphoto);
	}

    @Override
    protected void onPause() {
        super.onPause();

        if (isFinishing()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                unregisterReceiver(rcvPhoto);
        }
    }

    @Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == CAMERA_ACTIVITY && resultCode == Activity.RESULT_OK
				&& storePath.trim().length() > 0) {

			addPhoto(storePath);

			storePath = "";
		}
	}

	private void addPhoto(String path) {
		PicStoreImpl picStore = new PicStoreImpl();
		picStore.getData().id = UUID.randomUUID().toString().replace("-", "");
		picStore.getData().picture = path.getBytes();
		Date date = Calendar.getInstance().getTime();
		picStore.getData().created = date;
		picStore.getData().date = date;

		picStore.write();
		picStore.close();

		TaskVisitItem item = new TaskVisitItem();
		item.id = picStore.getData().id;
		item.date = new Date();
		visit.getData().items.add(item);
		visit.write();
		visit.close();
	}

	BroadcastReceiver delphoto = new BroadcastReceiver(){

		@Override
		public void onReceive(Context context, Intent intent) {
			String p = intent.getStringExtra(ManagePhotoDlg.PIC_ID);
			
			TaskVisit v = visit.getData();
			for(TaskVisitItem i : v.items){
				String s = new String(i.id);
				
				if(s.equals(p)){
					v.items.remove(i);
					PicStoreImpl picStore = new PicStoreImpl();
					
					if (picStore.read("id",s)) {
						picStore.delete();
						picStore.close();
					}
					
					break;
				}
			}
			
			visit.write();
			visit.close();
			
			initPreview();
		}
	};
}
