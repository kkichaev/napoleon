package com.grsoft.manager;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.FileProvider;
import androidx.fragment.app.DialogFragment;

import com.grsoft.dataobjects.AgentManagerMemo;
import com.grsoft.dataobjects.PicStoreSrc;
import com.grsoft.napoleon.util.Config;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.napoleon.util.debug.Path;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ManagerMemoDialog extends DialogFragment {
	Action handler;
	AgentManagerMemo data;
	String picSrc;
	
	public interface Action {
		void accept(AgentManagerMemo data, boolean accept);
	}
	
	public void setHandler(Action handler) { this.handler = handler; }
	
	public void setData(AgentManagerMemo data) {
		Bundle b = new Bundle();
		b.putParcelable("DATA", data);
		setArguments(b);
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		getDialog().setTitle(R.string.memo);
		data = getArguments().getParcelable("DATA");
		picSrc = PicStoreSrc.get(data.userid, data.created);
		
//		getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		
		View v = inflater.inflate(R.layout.memo_edit, container);

		if(picSrc != null) {
			Config config = ConfigManager.getConfig();
			String url = String.format("http://%s:%d/", config.address, config.port);
			url += picSrc;

			File f = new File(Path.getDataDir(), picSrc.replace('/', '_').replace('\\', '_'));
			ImageView iv = v.findViewById(R.id.image);
			new DownloadImageTask(iv, f).execute(url);

			iv.setOnClickListener(view -> {
				preview(f);
			});
		}

//		Map<String, ManagerAgent> agents = ManagerAgent.getAgents();
//		ManagerAgent a = agents.get(data.userid);
		
		TextView tv;
		
		tv = (TextView)v.findViewById(R.id.tvTopic);
		tv.setText(AgentMemo.getTopic(data.topic));
		
		tv = (TextView)v.findViewById(R.id.tvOrgName);
		tv.setText(data.orgName);
		tv.setTextColor(data.dogColor);

		String text = data.dogName + "/" + Integer.toString(data.dogDue) + "к/д /" + 
				Util.IntToScaleStr(data.dogLimit, Consts.SUM_SCALE, Util.DEC_DELIM, false);
		tv = (TextView)v.findViewById(R.id.tvDogovor);
		tv.setText(text);

		text = Util.IntToScaleStr(data.sumD, Consts.SUM_SCALE, Util.DEC_DELIM, false) + " / " + 
				Util.IntToScaleStr(data.overdueSum, Consts.SUM_SCALE, Util.DEC_DELIM, false) + " / " + 
				Integer.toString(data.overdue);
		tv = (TextView)v.findViewById(R.id.tvDolg);
		tv.setText(text);
		
		tv = (TextView)v.findViewById(R.id.tvMemo);
		tv.setText(data.remark);
		
		text = "Разблокировать до: " + Util.simpleDateFormat.format(data.till); 
		tv = (TextView)v.findViewById(R.id.tvUnlockTill);
		tv.setText(text);
		
		text = "Допустимая сумма: " + Util.IntToScaleStr(data.sum, Consts.SUM_SCALE, Util.DEC_DELIM, false); 
		tv = (TextView)v.findViewById(R.id.tvSum);
		tv.setText(text);
		
		final EditText ed = (EditText)v.findViewById(R.id.edComment);
		ed.setText(data.managerRemark);
		
		v.findViewById(R.id.btnDebt).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				DebetDialog dd = new DebetDialog();
				dd.setData(data);
				dd.show(getFragmentManager(), "");
			}
		});

//		InputMethodManager imm = (InputMethodManager)ed.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
//		if (imm.isActive())
//			imm.toggleSoftInput(0, InputMethodManager.HIDE_IMPLICIT_ONLY);
		
		View btnReject = v.findViewById(R.id.btnReject);
		View btnAccept = v.findViewById(R.id.btnAccept);
		if(data.isEditable()) {
			btnReject.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View arg0) {
					data.managerRemark = ed.getText().toString();
					if(handler != null)
						handler.accept(data, false);
					dismiss();
				}
			});
			
			btnAccept.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View arg0) {
					data.managerRemark = ed.getText().toString();
					if(handler != null)
						handler.accept(data, true);
					dismiss();
				}
			});
		} else {
			if(data.isAllowed()) {
				btnAccept.setEnabled(false);
				btnReject.setVisibility(View.INVISIBLE);
			} else {
				btnReject.setEnabled(false);
				btnAccept.setVisibility(View.INVISIBLE);
			}
		}
		
		return v;
	}

	private void preview(File f) {
		Intent i = new Intent();
		i.setAction(Intent.ACTION_VIEW);

		Uri uri = null;

		if (Build.VERSION.SDK_INT >= 24) {
			uri = FileProvider.getUriForFile(getContext(),getString(R.string.fileprovider_authorities), f);
		}else
			uri = Uri.parse("file://" + f.getAbsolutePath());

		i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
		i.setDataAndType(uri, "image/*");

		startActivity(i);
	}

	private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
		ImageView bmImage;
		File wr;

		public DownloadImageTask(ImageView bmImage, File wr) {
			this.bmImage = bmImage;
			this.wr = wr;
		}

		protected Bitmap doInBackground(String... urls) {
			String urldisplay = urls[0];
			Bitmap mIcon11 = null;
			try {
				InputStream in = new java.net.URL(urldisplay).openStream();
				mIcon11 = BitmapFactory.decodeStream(in);

				try (FileOutputStream out = new FileOutputStream(wr)) {
					mIcon11.compress(Bitmap.CompressFormat.PNG, 100, out);
				} catch (IOException e) {
					e.printStackTrace();
				}
			} catch (Exception e) {
				Log.e("Error", e.getMessage());
				e.printStackTrace();
			}
			return mIcon11;
		}

		protected void onPostExecute(Bitmap result) {
			bmImage.setImageBitmap(result);
			bmImage.setVisibility(View.VISIBLE);
		}
	}
}
