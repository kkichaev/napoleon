package com.grsoft.napoleon;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Html;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;

import com.grsoft.dataobjects.InvFrg;
import com.grsoft.dataobjects.InvFrgItem;
import com.grsoft.dataobjects.Visit;
import com.grsoft.dataobjects.VisitItemEx;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.dataobjects.impl.InvFrgImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.VisitImplEx;
import com.grsoft.napoleon.documents.AuditEquipDoc;
import com.grsoft.napoleon.documents.DocSendListner;
import com.grsoft.napoleon.documents.DocumentSender;
import com.grsoft.napoleon.documents.InvFrgDoc;
import com.grsoft.napoleon.documents.SendResultListener;
import com.grsoft.napoleon.documents.VisitDoc;
import com.grsoft.napoleon.util.VisitPhotoHandler;
import com.grsoft.network.DocExportListener;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.GpsCoord;
import com.grsoft.util.view.dialog_helper.DialogHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class InvFrgEdit extends FragmentActivity implements SendResultListener {
	private InvFrgImpl doc = new InvFrgImpl();
	Adapter adapter;
	VisitImplEx refVisit = new VisitImplEx();
	VisitPhotoHandler photoHandler;
	ItemDialog itemDialog;

	public static void open(Context context, long rowid) {
		Intent i = new Intent(context, InvFrgEdit.class);
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, rowid);
		context.startActivity(i);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.invfrgedit);

		Bundle b = savedInstanceState == null ? getIntent().getExtras() : savedInstanceState;
		doc.read(b.getLong(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ROWID));
		InvFrg d = doc.getData();

		Visit v = refVisit.getData();
		v.created = doc.getData().visitDoc;
		if(refVisit.read() == false) {
			refVisit.init(this, d.id, new GpsCoord(d.latitude, d.longitude, d.stltime));
			doc.getData().visitDoc = refVisit.getData().created;
			doc.write();
		}
		photoHandler = new VisitPhotoHandler(refVisit);

		OrgImpl org = new OrgImpl();
		org.read("id", d.id);

		TextView tv = (TextView) findViewById(R.id.tvOrg);
		tv.setText(org.getData().name);

		adapter = new Adapter();
		ListView list = (ListView) findViewById(R.id.list);
		list.setAdapter(adapter);
		registerForContextMenu(list);
		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				InvFrgItem item = (InvFrgItem) adapter.getItem(position);
				itemDialog = new ItemDialog(InvFrgEdit.this, item, refVisit, photoHandler);
				itemDialog.show(getSupportFragmentManager(), "");
			}
		});

		findViewById(R.id.btnAddItems).setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {
				InvFrgItem item = new InvFrgItem();
				item.id = UUID.randomUUID().toString().replace("-", "");
				item.newItem = 1;
				itemDialog = new ItemDialog(InvFrgEdit.this, item, refVisit, photoHandler);
				itemDialog.show(getSupportFragmentManager(), "");
			}
		});

		findViewById(R.id.btnSend).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(!doc.isEmpty()) {
					if(doc.checkPhoto(refVisit) == false) {
						Toast.makeText(InvFrgEdit.this, R.string.no_rfrg_photo, Toast.LENGTH_LONG).show();
						return;
					}
					List<DocExportListener> sendDocs = new ArrayList<>();
					sendDocs.add(new DocSendListner(InvFrgDoc.instance().getObjectName(), doc));
					if(!refVisit.isEmpty()) {
						sendDocs.add(new DocSendListner(VisitDoc.instance().getObjectName(), refVisit));
					}
					new DocumentSender(InvFrgEdit.this, findViewById(R.id.btnSend), sendDocs, InvFrgEdit.this).execute((Void[]) null);
				}
			}
		});
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
		getMenuInflater().inflate(R.menu.inv_frg_context, menu);
	}

	@Override
	public boolean onContextItemSelected(@NonNull MenuItem item) {
		AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
		InvFrgItem i = (InvFrgItem) adapter.getItem(menuInfo.position);
		if(i.newItem > 0) {
			doc.getData().items.remove(i);
		} else {
			i.inputNumber = "";
		}

		refVisit.removePhoto(i.id);
		doc.write();

		return super.onContextItemSelected(item);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putLong(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());
		photoHandler.storeData(outState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		photoHandler.restoreData(savedInstanceState);
	}

	@Override
	protected void onStop() {
		super.onStop();
		doc.close();
		refVisit.close();
	}

	@Override
	public void onBackPressed() {
		if(doc.isEditable() && doc.checkPhoto(refVisit) == false) {
			Toast.makeText(this, R.string.no_rfrg_photo, Toast.LENGTH_LONG).show();
			return;
		}

		super.onBackPressed();
		if(refVisit.isEmpty()) {
			refVisit.delete();
		}
		if(doc.isEditable() && doc.isEmpty()) {
			doc.delete();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(photoHandler.onActivityResult(requestCode, resultCode, data) && itemDialog != null) {
			itemDialog.updatePhoto();
		}
	}

	@Override
	public void postSendExecute(boolean result) {
		if(result)
			doc.read(doc.getRowid(), false);
	}

	public static class ItemDialog extends DialogFragment {
		InvFrgEdit owner;
		InvFrgItem item;
		ImageView imageView;
		VisitImplEx visit;
		View.OnClickListener photoHandler;

		public ItemDialog(InvFrgEdit owner, InvFrgItem item, VisitImplEx visit, View.OnClickListener photoHandler) {
			this.owner = owner;
			this.item = item;
			this.visit = visit;
			this.photoHandler = photoHandler;
		}

		@NonNull
		@Override
		public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
			AlertDialog.Builder builder = new AlertDialog.Builder(owner);
			builder.setTitle(R.string.new_frg_item);
			View v = View.inflate(owner, R.layout.frgitemedit, null);

			imageView = v.findViewById(R.id.ivPhoto);
			updatePhoto();

			final EditText ed = v.findViewById(R.id.edNumber);
			ed.setText(item.inputNumber);

			final Spinner sp = v.findViewById(R.id.spVolume);
			ConfigImpl ci = new ConfigImpl();
			DialogHelper.loadSpinnerFromConfig(ci, "ќбъем’олодильника", new ArrayList<CharSequence>(), sp, item.volume);

			v.findViewById(R.id.btnAddPhoto).setOnClickListener(photoClick);

			v.findViewById(R.id.btnDelPhoto).setOnClickListener(new View.OnClickListener(){
				@Override
				public void onClick(View v) {
					visit.removePhoto(item.id);
					updatePhoto();
				}
			});

			builder.setView(v);
			builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					item.inputNumber = ed.getText().toString();
					item.volume = (String) sp.getSelectedItem();
					owner.updateItem(item);
				}
			});

			builder.setNegativeButton(R.string.cancel, null);

			return builder.create();
		}

		View.OnClickListener photoClick = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				visit.setPhotoTag(item.id);
				photoHandler.onClick(v);
			}
		};

		public void updatePhoto() {
			Bitmap img = null;
			VisitItemEx vi = visit.findPhoto(item.id);
			if(vi != null) {
				img = vi.getImage();
			}
			if(img != null)
				imageView.setImageBitmap(img);
			else
				imageView.setImageResource(R.drawable.camera);
		}
	}

	public void updateItem(InvFrgItem item) {
		InvFrg d = doc.getData();
		if(d.items.indexOf(item) < 0) {
			d.items.add(item);
		}
		doc.write();
		adapter.notifyDataSetChanged();
	}

	private class Adapter extends BaseAdapter {

		public Adapter() {
		}

		@Override
		public int getCount() {
			return doc.getData().items.size();
		}

		@Override
		public Object getItem(int position) {
			return doc.getData().items.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View view, ViewGroup parent) {
			if (view == null)
				view = View.inflate(InvFrgEdit.this, android.R.layout.simple_list_item_checked, null);

			InvFrgItem i = (InvFrgItem) getItem(position);

			CheckedTextView tv = (CheckedTextView) view;
			String text = i.getText();
			tv.setText(Html.fromHtml(text));
			tv.setChecked(refVisit.findPhoto(i.id) != null);

			view.setBackgroundDrawable(getResources().getDrawable(
					i.newItem > 0 ? R.drawable.list_red_selector : R.drawable.list_selector));

			return view;
		}
	}
}
