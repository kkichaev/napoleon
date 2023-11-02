package com.grsoft.napoleon;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import com.grsoft.dataobjects.impl.FolderImpl;
import com.grsoft.dataobjects.impl.FolderPresentImpl;
import com.grsoft.util.view.ViewUtil;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


public class FoldersViewAdapter extends BaseAdapter {
	public static class FData{
		public int id = -1;
		public String path = "";
		public String color = "";
		public int size;
	}
	
	private Activity context;
	private List<FData> data = new ArrayList<FData>();
	private FolderImpl folder = new FolderImpl();
	private Map<String, WeakReference<Bitmap>> pics = new WeakHashMap<String, WeakReference<Bitmap>>();
	private Drawable defPic;
	
	int picSize = 170;
	
	public FoldersViewAdapter(Activity context, int imageWidth){
		this.context = context;
		defPic = context.getResources().getDrawable(R.drawable.folder_pic);
		
		reload(imageWidth);
	}
	
	@Override public int getCount() { return data.size(); }
	@Override public Object getItem(int position) { return data.get(position); }
	@Override public long getItemId(int position) { return 0; }

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		if (view == null)
			view = View.inflate(context, R.layout.folderpresent, null);
		
		FData f = (FData) getItem(position);
		folder.read("id", f.id);
		
		TextView tv = (TextView) view.findViewById(R.id.text);
		tv.setText(folder.getData().name);
		
		int c = context.getResources().getColor(R.color.black);
		
		try{
			c = Color.parseColor(f.color);
		}catch (Exception e){
			e.printStackTrace();
		}
		
		tv.setTextColor(c);
		
		if (f.size <= 0)
			f.size = 18;
		
		tv.setTextSize(ViewUtil.spToPixel(context, f.size));
		tv.getLayoutParams().width = picSize;
		
		ImageView iv = ((ImageView) view.findViewById(R.id.image));
		
		try {
			WeakReference<Bitmap> weak = (WeakReference<Bitmap>) pics.get(f.path);
			
			if(weak == null  || weak.get() == null){
				BitmapFactory.Options opt = new BitmapFactory.Options();
				Bitmap bitmap = BitmapFactory.decodeFile(f.path, opt);
				pics.put(f.path, new WeakReference<Bitmap>(bitmap));
			}

			iv.getLayoutParams().height = picSize;
			iv.getLayoutParams().width = picSize;

			Bitmap b = pics.get(f.path).get();
			
			if(b == null)
				iv.setImageDrawable(defPic);
			else
				iv.setImageBitmap(b);
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return view;
	}

	@SuppressLint("UseSparseArrays")
	public void reload(int imageWidth) {
		data.clear();
		
		picSize = imageWidth; 

		PresentationFolderW.items.clear();
		PresentationFolderW.items.fill(false);
		PresentationList list = PresentationFolderW.items;
		
		FolderImpl f = new FolderImpl();
		FolderPresentImpl z = new FolderPresentImpl();
		
		Map<Integer, FData> hash = new HashMap<Integer, FData>();
		
		for(PresentationData pd : list){
			if(!hash.containsKey(pd.folder)){
				f.read("id", pd.folder);
				
				boolean bz = z.read("id", f.getData().fid);
				
				FData y = new FData();
				y.id = pd.folder;
				
				if(bz){
					y.path = z.getData().path;
					y.color = z.getData().color;
					y.size = z.getData().tsz;
				}
				
				hash.put(pd.folder, y);
			}
		}
		
		data.addAll(hash.values());
	}
}
