package com.grsoft.napoleon;

import java.io.File;
import java.util.ArrayList;

import com.grsoft.dataobjects.Visit;
import com.grsoft.dataobjects.VisitItem;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.dataobjects.impl.VisitImpl;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.DocTypeBase;
import com.grsoft.napoleon.documents.DocumentSender;
import com.grsoft.napoleon.documents.PhotoDocument;
import com.grsoft.napoleon.documents.SendResultListener;
import com.grsoft.napoleon.documents.VisitDoc;
import com.grsoft.napoleon.util.CfgNplW;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.napoleon.util.ImagesAdapter;
import com.grsoft.napoleon.util.PhotoClickHandler;
import com.grsoft.script.ScriptActivity;
import com.grsoft.script.ScriptHelper;
import com.grsoft.script.dataobjects.impl.ScriptImpl;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.OnClickListenerToNotify;
import com.grsoft.util.RuntimeEnv;
import com.grsoft.util.view.dialog_helper.DialogHelper;
import com.grsoft.view.RegDurationActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.core.content.FileProvider;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.Spinner;

public class VisitEditNew extends RegDurationActivity implements SendResultListener, PhotoClickHandler.EventHandler,
		ScriptActivity {
	public static Class<? extends Activity> activity = VisitEdit.class;

	protected CreatableDocument<? extends Visit> visit = createDocument();
	protected EditText edNotes;
	protected ImageButton btnSend;
	private ImageButton btnPhoto;
	protected ImagesAdapter adapter;
	protected String picPath;
	protected GridView grid;

	ArrayList<CharSequence> visitCause = new ArrayList<CharSequence>();
	final static int ASK_TO_DEL_VISIT_MSG = R.id.ask_to_del_visit_msg;

	protected static final String PIC_PATH = "pic_path";

	@SuppressWarnings("unchecked")
	protected CreatableDocument<? extends Visit> createDocument() {
		return (CreatableDocument<? extends Visit>) VisitDoc.instance().create();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (!visit.isEditable()) {
				finish();
				return true;
			}

			if (Features.DEL_VISIT_WITHOUT_PHOTO && visit.isEmpty())
				showDialog(ASK_TO_DEL_VISIT_MSG);
			else {
				if (!saveVisit())
					visit.delete();

				finish();
			}

			return true;
		} else
			return super.onKeyDown(keyCode, event);
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		if (id == ASK_TO_DEL_VISIT_MSG)
			return createAskToDelDlg();
		else
			return super.onCreateDialog(id);
	}

	private Dialog createAskToDelDlg() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.alert);
		builder.setMessage(R.string.askToDelVisitMsg);
		builder.setPositiveButton(R.string.ok, new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				visit.delete();
				finish();
			}
		});

		builder.setNegativeButton(R.string.cancel, null);

		return builder.create();
	}

	protected boolean saveVisit() {
		Visit v = visit.getData();
		v.remark = edNotes.getText().toString();

		boolean canDelete = (v.remark.trim().length() == 0 && v.items.size() == 0);

		if (Features.HAVE_VISIT_CAUSE) {
			Spinner s = (Spinner) findViewById(R.id.spVisitCause);
			String val = (String) s.getSelectedItem();
			if (val != null) {
				v.cause = val;
				canDelete = false;
			}
		}

		visit.write();
		visit.close();

		refreshDocSum(v);
		return !canDelete;
	}

	protected void refreshDocSum(Visit v) {
		DocType.getCurDoc().refreshDocSum(v.id);
	}

	boolean cache = true;

	@Override
	protected void onResume() {
		super.onResume();
		visit.read(visit.getRowid(), cache);
		adapter.notifyDataSetChanged();

		cache = false;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(getContentView());

		init(savedInstanceState);

		if (Features.START_VISIT_OPEN_CAMERA && needOpenCamera())
			btnPhoto.performClick();
	}

	private boolean needOpenCamera() {
		return visit.isEditable() && visit.getData().items.size() == 0;
	}

	protected int getContentView() {
		return R.layout.visiteditnew;
	}

	static public void open(Context context, VisitImpl visit) {
		Intent i = new Intent(context, activity);
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, visit.getRowid());
		context.startActivity(i);
	}

	protected View.OnClickListener createPhotoClickHandler() {
		return new PhotoClickHandler((PhotoDocument) visit, this, null);
	}

	protected void init(Bundle savedInstanceState) {
		edNotes = (EditText) findViewById(R.id.edNotes);
		// edNotes.setInputType(InputType.TYPE_NULL);
		this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		long visitRowId = ExtrasConst.INVALID_ID;

		if (savedInstanceState == null) {
			visitRowId = getIntent().getLongExtra(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ID);
		} else {
			visitRowId = savedInstanceState.getLong(ExtrasConst.DOC_ROW_ID_STR);
		}

		if (visitRowId != ExtrasConst.INVALID_ID) {
			visit.read(visitRowId);
			edNotes.setText(visit.getData().remark);
		}

		btnPhoto = (ImageButton) findViewById(R.id.btnPhoto);
		btnPhoto.setOnClickListener(createPhotoClickHandler());

		adapter = createImageAdapter();

		grid = (GridView) findViewById(R.id.grid);
		grid.setAdapter(adapter);

		CfgNplW cfg = (CfgNplW) ConfigManager.getConfig();
		if (!RuntimeEnv.isPhotoSupported() || cfg.cameraWidth == 0 || cfg.cameraHeight == 0) {
			btnPhoto.setVisibility(View.GONE);
			grid.setVisibility(View.GONE);
		}

		btnSend = (ImageButton) findViewById(R.id.btnSend);
		btnSend.setOnClickListener(new OnClickListenerToNotify() {
			@Override
			public void onClick(View v) {
				super.onClick(v);
				if (saveVisit())
					send();
			}
		});

		ScriptHelper.initView(this, DocType.getCurDoc().getObjectName(), visit.getData().created, visit.getId());

		int v = View.GONE;
		if (Features.HAVE_VISIT_CAUSE) {
			v = View.VISIBLE;
			ConfigImpl config = new ConfigImpl();

			Spinner s = (Spinner) findViewById(R.id.spVisitCause);
			DialogHelper.loadSpinnerFromConfig(config, "ПричиныВизита", visitCause, s, visit.getData().cause);

			config.close();
		}
		findViewById(R.id.llVisitCause).setVisibility(v);

		setEditableControl(!visit.isExported());
	}

	protected ImagesAdapter createImageAdapter() {
		return new ImagesAdapter(this, visit);
	}

	protected void send() {
		new DocumentSender(this, btnSend, VisitDoc.OBJ_NAME, visit, visit.getRowid(), this)
				.execute((Void[]) null);
	}

	protected void setEditableControl(boolean isEditable) {
		edNotes.setEnabled(isEditable);
		btnPhoto.setEnabled(isEditable);

		setContextMenu(isEditable);
	}

	protected void setContextMenu(boolean isEditable) {
		registerForContextMenu(grid);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		saveVisit();
		outState.putLong(ExtrasConst.DOC_ROW_ID_STR, visit.getRowid());
		outState.putString(PIC_PATH, picPath);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		picPath = savedInstanceState.getString(PIC_PATH);
	}

	@Override
	public void prepareBoforeClick() {
		saveVisit();
	}

	@Override
	public void makePhotoFile(File newFile) {
		picPath = newFile.getAbsolutePath();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		

		if (requestCode == PhotoClickHandler.CAMERA_ACTIVITY && resultCode == RESULT_OK) {
			if (picPath != null && picPath.trim().length() > 0)
				((PhotoDocument) visit).addPhoto(picPath.getBytes());
		}
		
		if (requestCode == CameraPreview.CAMERA_PREVIEW_ACTIVITY && resultCode == RESULT_OK) {
			if(replaced != null) {
				visit.read(visit.getRowid(), cache);
				
				for (VisitItem i : visit.getData().items) {
					if (i.getImageFileName().equals(replaced.getImageFileName())){
						visit.getData().items.remove(i);
						visit.write();
						visit.close();
						break;
					}
				}
				
				replaced = null;
			}
		}
		
		((BaseAdapter) grid.getAdapter()).notifyDataSetChanged();
	}

	@Override
	public void postSendExecute(boolean result) {
		visit.read(visit.getRowid(), false);
		setEditableControl(!visit.isExported());
	}

	protected int getContextMenuID() {
		return R.menu.visit_context_menu_new;
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		Log.d(Consts.D_TAG, "VisitEdit.onCreateContextMenu");

		getMenuInflater().inflate(getContextMenuID(), menu);

		if (!visit.isEditable()) {
			MenuItem mi = menu.findItem(R.id.itDelete);
			if (mi != null)
				mi.setVisible(false);
			
			mi = menu.findItem(R.id.itReplace);
			if (mi != null)
				mi.setVisible(false);
		}

		super.onCreateContextMenu(menu, v, menuInfo);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		Log.d(Consts.D_TAG, "VisitEdit.onContextItemSelected");

		AdapterView.AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo) item.getMenuInfo();

		VisitItem visitItem = (VisitItem) adapter.getItem(menuInfo.position);
		String picPath = visitItem.getImageFileName();
		if (item.getItemId() == R.id.itDelete) {
			removeItem(visitItem, picPath);
		} else if (item.getItemId() == R.id.itShow) {
			preview(picPath);
		} else if (item.getItemId() == R.id.itReplace) {
			replace(visitItem);
		}
		return super.onContextItemSelected(item);
	}
	
	private VisitItem replaced = null;

	private void replace(VisitItem item) {
		replaced = item;
		btnPhoto.performClick();
	}

	private void removeItem(VisitItem visitItem, String picPath) {
		File file = new File(picPath);
		file.delete();
		Visit v = visit.getData();
		v.items.remove(visitItem);
		v.sendedPhotos = 0;

		visit.write();
		adapter.notifyDataSetChanged();
	}

	private void preview(String path) {
		Intent i = new Intent();
		i.setAction(Intent.ACTION_VIEW);
		
		Uri uri = null;
		
		if (Build.VERSION.SDK_INT >= 24) {
			uri = FileProvider.getUriForFile(this,getString(R.string.fileprovider_authorities), new File(path));
		}else
			uri = Uri.parse("file://" + path);
		
		i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
		i.setDataAndType(uri, "image/*");
		
		startActivity(i);

	}

	@Override
	public boolean closeDocument() {
		boolean ret = saveVisit();

		if (!ret){
			visit.delete();
			visit.close();
		}

		return ret;
	}
}