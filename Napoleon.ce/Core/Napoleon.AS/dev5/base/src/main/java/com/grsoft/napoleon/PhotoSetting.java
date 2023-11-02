/*
 * Copyright (C), 2011, Гильдия Разработчиков
 *
 * kki   21/04/2011   creating
 */
package com.grsoft.napoleon;
import com.grsoft.aceteam.R;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.hardware.Camera;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.Toast;

import com.grsoft.napoleon.util.CfgNplW;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.util.CameraHelper;
import com.grsoft.util.SettingActivity;
import com.grsoft.util.Size;

public class PhotoSetting extends SettingActivity{
	public static int MIN_CAMERA_RESOLUTION = 600;
	public static int MAX_CAMERA_RESOLUTION = 4000;

	private Spinner spPicSize;
	private CheckBox cbAutoFocus;
	private CheckBox cbAndroidPhoto;
	
	/***
	 * Флаг успешного завершения инициализации камеры
	 * если иницализация не прошла, то мы не будем сохранять установки
	 */
	boolean inited = false;
	boolean no_camera = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(getContentViewID());
		spPicSize = (Spinner) findViewById(R.id.spPicSize);
		cbAutoFocus = (CheckBox) findViewById(R.id.cbAutofocus);
		cbAndroidPhoto = (CheckBox) findViewById(R.id.cbAndroidPhoto);
		
		initChildView();

		if(Features.ANDROID_CAMERA_PROHIBIT) {
			if(cbAndroidPhoto != null) {
				cbAndroidPhoto.setVisibility(View.GONE);
				cbAndroidPhoto = null;
			}
		}

		if (cbAndroidPhoto != null){
			cbAndroidPhoto.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					if(!no_camera) {
						spPicSize.setEnabled(!isChecked && picSizeEnabled());
						cbAutoFocus.setEnabled(!isChecked);
					}
				}
			});
		}
		
		init();
	}

	protected boolean picSizeEnabled() { return true; }

	protected void initChildView() {}

	protected int getContentViewID() {
		return R.layout.photo_setting;
	}

	protected void init() {
		inited = false;
		CfgNplW config = (CfgNplW) ConfigManager.getConfig();
		
		Camera camera = null;
		
		try{
			camera = Camera.open();
		}catch(Exception e){
			e.printStackTrace();
		}
		
		if (camera == null){
			no_camera = true;
			
			Toast.makeText(this, R.string.init_camera_error, Toast.LENGTH_LONG).show();
			
			spPicSize.setEnabled(false);
			cbAutoFocus.setEnabled(false);
		}else{
			spPicSize.setEnabled(picSizeEnabled());
			try{
				Camera.Parameters cameraParameters = camera.getParameters();
				List<CameraSize>  cameraSizes = new ArrayList<>();
				for(Camera.Size sz : CameraHelper.getSupportedPictureSizes(cameraParameters, true)) {
					if (sz.width > MIN_CAMERA_RESOLUTION && sz.height > MIN_CAMERA_RESOLUTION &&
					    sz.width < MAX_CAMERA_RESOLUTION && sz.height < MAX_CAMERA_RESOLUTION)
						cameraSizes.add(new CameraSize(sz));
				}

				Collections.sort(cameraSizes);
				
				ArrayAdapter<CameraSize> adapter = new ArrayAdapter<CameraSize>(this,
		                android.R.layout.simple_spinner_item, 
		                cameraSizes);
		        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		        spPicSize.setAdapter(adapter);
		        
		        Size sz = new Size(config.cameraWidth, config.cameraHeight);
		        
		        CameraSize selectedSize = null;
		        
		        if(sz != null)
			        for(CameraSize cSize: cameraSizes){
			        	selectedSize = cSize;
			        	
			        	if (selectedSize.cameraSize.height == sz.hight && 
			        			selectedSize.cameraSize.width == sz.width)
			        		break;
			        }
		        
		        if (selectedSize != null)
		        	spPicSize.setSelection(cameraSizes.indexOf(selectedSize));
		        
		        cbAutoFocus.setChecked(config.useAutoFocus);
		        
		        postCameraViewInit(config);
		        
		        inited = true;
			}
			catch(Exception e){
				e.printStackTrace();
			}
			
			if (camera != null)
				camera.release();
		}
		
		if(cbAndroidPhoto != null){
			cbAndroidPhoto.setChecked(((CfgNplW)config).androidPhoto);
			cbAndroidPhoto.setEnabled(true);
		}
	}
	
	protected void postCameraViewInit(CfgNplW config) {}

	@Override
	public void save() {
		if (inited){
			CfgNplW config = (CfgNplW) ConfigManager.getConfig();
			
			/*Разрешения камеры*/
			Spinner spPicSize = (Spinner) findViewById(R.id.spPicSize);
			CameraSize cs = (CameraSize) spPicSize.getSelectedItem();
			if( cs != null ){
				Size size = cs.getSize();
				config.cameraWidth = size.width;
				config.cameraHeight = size.hight;
			}
			
			/*Автофокус*/
			CheckBox cbAutofocus = (CheckBox) findViewById(R.id.cbAutofocus);
			config.useAutoFocus = cbAutofocus.isChecked();
			
			/*Фотографировать внутренним приложением*/
			if(cbAndroidPhoto != null)
				((CfgNplW)config).androidPhoto = cbAndroidPhoto.isChecked();
			else
				((CfgNplW)config).androidPhoto = false;

			postSave(config);
			
			ConfigManager.save();
		}
	}
	
	protected void postSave(CfgNplW config) {}

//	private List<CameraSize> adjustCameraSizes(List<Camera.Size> picSizes){
//		ArrayList<CameraSize> result = new ArrayList<CameraSize>();
//
//		for(Camera.Size cs: picSizes){
//			if (isAvailResolution(cs))
//				result.add(new CameraSize(cs));
//		}
//
//		return result;
//	}
//
//	protected boolean isAvailResolution(Camera.Size cameraSize){
//		return cameraSize.width <= Features.MAX_FOTO_WIDTH && cameraSize.height <= Features.MAX_FOTO_HEIGHT &&
//				cameraSize.width >= Features.MAX_FOTO_WIDTH && cameraSize.height >= Features.MIN_FOTO_HEIGHT;
//	}

	class CameraSize implements Comparable<CameraSize>{
		private Camera.Size cameraSize;
		
		public CameraSize(Camera.Size cameraSize) {
			this.cameraSize = cameraSize;
		}
		
		@Override
		public String toString() {
			StringBuilder result = new StringBuilder();
			result.append(cameraSize.height).
				append(" X ").
				append(cameraSize.width);
			
			return result.toString();
		}
		
		public Size getSize(){
			return new Size(cameraSize.width, cameraSize.height);
		}

		@Override
		public int compareTo(CameraSize o) {
			int cmp = cameraSize.width - o.cameraSize.width;
			return cmp != 0 ? cmp : cameraSize.height - o.cameraSize.height;
		}
	}

	@Override
	public void update() {
		init();
	}

	@Override
	public int getName() {
		return R.string.photo;
	}

	@Override
	public int getIcon() {
		return R.drawable.setting_photo;
	}
}
