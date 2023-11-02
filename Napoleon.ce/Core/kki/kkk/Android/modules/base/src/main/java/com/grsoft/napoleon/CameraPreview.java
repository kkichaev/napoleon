/*
 * Copyright (C), 2011, Гильдия Разработчиков
 *
 * Форма для фотографий
 *
 * kki   04/03/2011   creating
 */

package com.grsoft.napoleon;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.List;

import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.PhotoDocument;
import com.grsoft.napoleon.documents.VisitDoc;
import com.grsoft.napoleon.util.CfgNplW;
import com.grsoft.napoleon.util.Config;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.napoleon.util.PhotoClickHandler;
import com.grsoft.napoleon.util.debug.Path;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.Size;
import com.grsoft.util.SrcDataCounter;
import com.grsoft.util.view.ViewUtil;
import com.grsoft.view.SimpleMessageBox;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.core.content.FileProvider;

public class CameraPreview extends Activity {
	public static final String PHOTO_PATH = "photo_path";
	public static  int CAMERA_PREVIEW_ACTIVITY = 0x1814;
	static final String PHOTO_FILE = "PhotoFile";
	public static final int WAIT_DLG = 0;
	public static Class<? extends Activity> activity = CameraPreview.class;
	long docRowId = ExtrasConst.INVALID_ROWID;
	String docType = null;
	Preview mPreview;

	String photoFileName = null;
	int photoRotation = 0;

	static public void open(Activity context, PhotoDocument doc) {
		Intent i = new Intent(context, CameraPreview.class);
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());
		context.startActivityForResult(i, CAMERA_PREVIEW_ACTIVITY);
	}

	static public void open(Activity context, PhotoDocument doc, DocType docType) {
		Intent i = new Intent(context, activity);
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());
		if (docType != null)
			i.putExtra(ExtrasConst.DOC_TYPE, docType.getObjectName());
		context.startActivityForResult(i, CAMERA_PREVIEW_ACTIVITY);
	}

	static public void takePhoto(Activity context, String fileName, int reqCode) {
		Intent i = new Intent(context, activity);
		i.putExtra(PHOTO_FILE, fileName);
		context.startActivityForResult(i, reqCode);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);

		mPreview = new Preview();

		Bundle extras = (savedInstanceState == null) ? getIntent().getExtras() : savedInstanceState;

		if (extras != null) {
			docRowId = extras.getLong(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ID);
			docType = extras.getString(ExtrasConst.DOC_TYPE);
			photoFileName = extras.getString(PHOTO_FILE);
		}

		// this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		setContentView(mPreview);
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case WAIT_DLG:
			return createWaitDlgDialog();
		default:
			return super.onCreateDialog(id);
		}
	}

	private Dialog createWaitDlgDialog() {
		ProgressDialog result = new ProgressDialog(this);
		result.setMessage(getString(R.string.please_wait));
		result.setCancelable(false);

		return result;
	}

	protected void save() {
		mPreview.save();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putLong(ExtrasConst.DOC_ROW_ID_STR, docRowId);
		if (docType != null)
			outState.putString(ExtrasConst.DOC_TYPE, docType);
		super.onSaveInstanceState(outState);
	}

	class Preview extends RelativeLayout
			implements SurfaceHolder.Callback, OnClickListener, Camera.AutoFocusCallback, Camera.PictureCallback {
		SurfaceHolder holder;
		Camera camera;
		ImageView ivPhoto;

		Preview() {
			super(CameraPreview.this);

			SurfaceView surfaceView = new SurfaceView(CameraPreview.this);
			addView(surfaceView);

			ivPhoto = new ImageView(CameraPreview.this);
			ivPhoto.setImageResource(R.drawable.takephoto);
			ivPhoto.setOnClickListener(this);
			final int SZ = 150;
			int dpsz = (int) ViewUtil.spToPixel(this.getContext(), SZ);
			RelativeLayout.LayoutParams lp = new LayoutParams(dpsz, dpsz);
			lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
			lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			ivPhoto.setLayoutParams(lp);
			final int PD = 20;
			dpsz = (int) ViewUtil.spToPixel(this.getContext(), PD);
			ivPhoto.setPadding(0, 0, dpsz, dpsz);
			addView(ivPhoto);

			holder = surfaceView.getHolder();
			holder.addCallback(this);
			holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}
		

		void checkRotation() {
			android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
			android.hardware.Camera.getCameraInfo(CameraInfo.CAMERA_FACING_BACK, info);

			int rotation = getWindowManager().getDefaultDisplay().getRotation();
			int degrees = 0;

			switch (rotation) {
			case Surface.ROTATION_0:
				degrees = 0;
				break;
			case Surface.ROTATION_90:
				degrees = 90;
				break;
			case Surface.ROTATION_180:
				degrees = 180;
				break;
			case Surface.ROTATION_270:
				degrees = 270;
				break;
			}

//			if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
//				result = (info.orientation + degrees) % 360;
//				result = (360 - result) % 360; // compensate the mirror
//			} else { // back-facing
			photoRotation = (info.orientation - degrees + 360) % 360;
//			}
			
			if (camera != null)
				camera.setDisplayOrientation(photoRotation);
		}
		
		@Override
		protected void onConfigurationChanged(Configuration newConfig) {
			super.onConfigurationChanged(newConfig);
			checkRotation();
		}

		public void surfaceCreated(SurfaceHolder holder) {
			try {
				camera = Camera.open();
				camera.setPreviewDisplay(holder);

				checkRotation();
			} catch (Exception exception) {
				if (camera != null) {
					camera.release();
					camera = null;
				}
			}
		}

		public void surfaceDestroyed(SurfaceHolder holder) {
			if (camera != null) {
				camera.stopPreview();
				camera.release();
				camera = null;
			}
		}

		private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h) {
			Log.d(Consts.D_TAG, "surfaceChanged() Looking for camera size w=" + Integer.toString(w) + ", h="
					+ Integer.toString(h) + '\n');
			final double ASPECT_TOLERANCE = 0.05;
			double targetRatio = (double) w / h;
			if (sizes == null) {
				Log.d(Consts.D_TAG, "Camera has no supported sizes.\n");
				return null;
			}

			Camera.Size optimalSize = null;
			double minDiff = Double.MAX_VALUE;

			int targetHeight = h;

			// Try to find an size match aspect ratio and size
			for (Camera.Size size : sizes) {
				double ratio = (double) size.width / size.height;
				if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE)
					continue;
				if (Math.abs(size.height - targetHeight) < minDiff) {
					optimalSize = size;
					minDiff = Math.abs(size.height - targetHeight);
				}
			}

			// Cannot find the one match the aspect ratio, ignore the
			// requirement
			if (optimalSize == null) {
				Log.d(Consts.D_TAG, "Cannot find the match for the aspect ratio.\n");
				minDiff = Double.MAX_VALUE;
				for (Camera.Size size : sizes) {
					if (Math.abs(size.height - targetHeight) < minDiff) {
						optimalSize = size;
						minDiff = Math.abs(size.height - targetHeight);
					}
				}
			}
			Log.d(Consts.D_TAG, "Found camera size: w=" + Integer.toString(optimalSize.width) + ", h="
					+ Integer.toString(optimalSize.height) + '\n');
			return optimalSize;
		}

		public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
			try {
				if (camera != null) {
					Camera.Parameters parameters = camera.getParameters();
					List<Camera.Size> sizes = getSupportedPreviewSizes();
					Camera.Size optimalSize = getOptimalPreviewSize(sizes, w, h);
					parameters.setPreviewSize(optimalSize.width, optimalSize.height);
					setFlashMode();
					Config config = ConfigManager.getConfig();
					Size setttingCameraSize = new Size(config.cameraWidth, config.cameraHeight);
					parameters.setPictureSize(setttingCameraSize.width, setttingCameraSize.hight);

					camera.setParameters(parameters);
					camera.startPreview();
				} else {
					SimpleMessageBox meb = new SimpleMessageBox(getContext().getString(R.string.init_camera_error),
							this.getContext());

					meb.setButton(AlertDialog.BUTTON_NEUTRAL, getContext().getString(R.string.close),
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog, int which) {
									dialog.dismiss();
									CameraPreview.this.finish();
								}
							});
					meb.show();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		@SuppressWarnings("unchecked")
		private List<Camera.Size> getSupportedPreviewSizes() {

			List<Camera.Size> result = null;
			try {
				Method invoker = Camera.Parameters.class.getMethod("getSupportedPreviewSizes", (Class[]) null);
				result = (List<Camera.Size>) invoker.invoke(camera.getParameters(), (Object[]) null);

				return result;
			} catch (Exception e) {
				e.printStackTrace();
			}

			return result;
		}

		private void setFlashMode() {
			try {
				Method invoker = Camera.Parameters.class.getMethod("setFlashMode", String.class);
				invoker.invoke(camera.getParameters(), "torch");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		public void save() {
			if (camera != null) {
				ivPhoto.setEnabled(false);

				if (((CfgNplW) ConfigManager.getConfig()).useAutoFocus)
					camera.autoFocus(this);
				else
					camera.takePicture(null, null, this);
			}
		}

		@Override
		public void onClick(View view) {
			try {
				ScaleAnimation anim = new ScaleAnimation(1f, 0.5f, 1f, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f,
						Animation.RELATIVE_TO_SELF, 0.5f);
				anim.setFillAfter(true);
				anim.setDuration(1000);
				view.startAnimation(anim);
				save();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		@Override
		public void onAutoFocus(boolean success, Camera camera) {
			try {
				camera.takePicture(null, null, this);
			} catch (Exception e) {
			}
		}

		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			camera.stopPreview();

			int length = (null == data) ? 0 : data.length;
			Log.d(Consts.D_TAG, "onPictureTaken dataSize: " + Integer.toString(length));
			if (0 != length)
				new SavePictureAsync() {
					protected void onPreExecute() {
						showDialog(WAIT_DLG);
					};

					protected void onPostExecute(String result) {
						try {
							dismissDialog(WAIT_DLG);
						} catch (Exception e) {
							e.printStackTrace();
						}
					};
				}.execute(data);
		}

		class SavePictureAsync extends AsyncTask<byte[], String, String> {
			@Override
			protected String doInBackground(byte[]... params) {
				try {
					if (photoFileName != null) {
						File file = new File(photoFileName);
						OutputStream outStream = new FileOutputStream(file);
						Bitmap bitmap = BitmapFactory.decodeByteArray(params[0], 0, params[0].length);
						if(photoRotation != 0) {
							Matrix m = new Matrix();
							m.postRotate(photoRotation);
							bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
						}
						bitmap.compress(Bitmap.CompressFormat.JPEG, 30, outStream);
						outStream.flush();
						outStream.close();

						Intent data = new Intent();
						data.putExtra(PHOTO_PATH, file.getAbsolutePath());
						setResult(Activity.RESULT_OK, data);

						Thread.sleep(3000);
					} else {
						DocType dt = (DocType) ((docType != null) ? DocType.getDocType(docType) : DocType.getCurDoc());
						Document<?> doc = dt.create();
						if (!(doc instanceof PhotoDocument))
							doc = VisitDoc.instance().create();

						PhotoDocument document = (PhotoDocument) doc; // photoDocType.newInstance();

						if (docRowId != ExtrasConst.INVALID_ROWID)
							document.read(docRowId);

						File file = new File(Path.getDataDir(), String.format("%d.jpg",SrcDataCounter.getValue()));
						File path = new File(Path.getDataDir());
						path.mkdirs();
						OutputStream outStream = new FileOutputStream(file);
						Bitmap bitmap = BitmapFactory.decodeByteArray(params[0], 0, params[0].length);
						if(photoRotation != 0) {
							Matrix m = new Matrix();
							m.postRotate(photoRotation);
							bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
						}
						bitmap.compress(Bitmap.CompressFormat.JPEG, 30, outStream);
						outStream.flush();
						outStream.close();

						String f = file.getAbsolutePath().toString();

						if (docRowId != ExtrasConst.INVALID_ROWID)
							document.addPhoto(f.getBytes());

						if(Features.SHARED_PICTURES) {
							MediaScannerConnection.scanFile(CameraPreview.this, new String[] {file.toString()}, null, null);
//							Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
//							Uri contentUri = FileProvider.getUriForFile(CameraPreview.this, getString(R.string.fileprovider_authorities), file);
////							Uri contentUri = Uri.fromFile(file);
//							mediaScanIntent.setData(contentUri);
//							CameraPreview.this.sendBroadcast(mediaScanIntent);
						}

						Intent data = new Intent();
						data.putExtra(PHOTO_PATH, f);
						setResult(Activity.RESULT_OK, data);

						Thread.sleep(3000);
					}
				} catch (Exception e1) {
					e1.printStackTrace();
				}

				((Activity) Preview.this.getContext()).finish();
				return null;
			}

		}
	}

}
