package com.grsoft.camera;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.util.Size;
import android.util.SizeF;
import android.view.OrientationEventListener;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.TorchState;
import androidx.camera.view.CameraController;
import androidx.camera.view.LifecycleCameraController;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;
import com.grsoft.napoleon.Features;
import com.grsoft.napoleon.util.Config;
import com.grsoft.napoleon.util.ConfigManager;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CameraActivity extends AppCompatActivity  {

    static final String BC_MODE_TAG = "barcode-mode";
    static BarcodeHandler barcodeHandler = null;
    static TakePhotoHandler takePhotoHandler = null;

    int VERSION = 4;

    Boolean barcodeMode;
    OrientationEventListener orientationListener = null;

    static void setBarcodeHandler(BarcodeHandler bh) {
        barcodeHandler = bh;
    }

    static void setTakePhotoHandler(TakePhotoHandler th) {
        takePhotoHandler = th;
    }

    public static void openBCScanner(Context context, BarcodeHandler handler) {
        Intent i = new Intent(context, CameraActivity.class);
        i.putExtra(BC_MODE_TAG, true);
        setBarcodeHandler(handler);

        context.startActivity(i);
    }

    public static void openCamera(Context context, TakePhotoHandler handler) {
        Intent i = new Intent(context, CameraActivity.class);
        i.putExtra(BC_MODE_TAG, false);
        setTakePhotoHandler(handler);
        context.startActivity(i);
    }

    private static final String TAG = "CameraXApp";
    private final int REQUEST_CODE_PERMISSIONS = 10;

    String[] REQUIRED_PERMISSIONS = new String[] {
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };
    ExecutorService cameraExecutor;

    LifecycleCameraController cameraController;
    PreviewView previewView;

    private ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    initCamera();
                } else {
                    showExplainDialog();
                }
            });

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        barcodeMode = getIntent().getBooleanExtra(BC_MODE_TAG, false);
        if(barcodeMode) {
            setContentView(R.layout.bc_layout);
            findViewById(R.id.tvInput).setOnClickListener(this::inputBarcode);
            checkNeededPermissions();
//            TextView tvVer = findViewById(R.id.tvVersion);
//            tvVer.setText(String.format("Версия %d", VERSION));
//
//            if (barcodeHandler != null)
//                barcodeHandler.initActivity(this);
        } else {
//            setContentView(R.layout.camera_main);
//
//            orientationListener = new OrientationEventListener(this) {
//                @Override
//                public void onOrientationChanged(int i) {
//                    if(i == OrientationEventListener.ORIENTATION_UNKNOWN)
//                        return;
//
//                    int rot = (i >= 45 && i < 135) ? 270:
//                                (i>=135 && i < 225) ? 180 :
//                                (i >= 225 && i < 315) ? 90:
//                                0;
//
//                    ImageView iv = findViewById(R.id.ivTakePhoto);
//                    iv.setRotation(rot);
//                }
//            };
        }
//
//        if (!allPermissionsGranted()) {
//            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
//        }
//
//        // Request camera permissions
//        //if (allPermissionsGranted()) {
//            //startCamera();
//        //} else {
//        //    ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
//        //}
//
//        // Set up the listeners for take photo and video capture buttons
//        View v = findViewById(R.id.ivTakePhoto);
//        if(v != null)
//            v.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                takePhoto();
//            }
//        });
//
//        v = findViewById(R.id.btnTorch);
//        if(v != null)
//            v.setOnClickListener(view -> {
//                if (cameraController != null)
//                    cameraController.enableTorch(cameraController.getTorchState().getValue() == TorchState.OFF );
//            });
//
//        Log.d(TAG, "create success! " + new Date().toString());
//        cameraExecutor = Executors.newSingleThreadExecutor();
////        startCamera();
    }

    private void inputBarcode(View view) {
        if (cameraController != null)
            cameraController.unbind();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.barcode_input_prompt)
                .setTitle(R.string.barcode_title);
        builder.setPositiveButton(R.string.ok, this::okBarcode);
        builder.setNegativeButton(R.string.cancel, (d,i)->finish());
        AlertDialog dialog = builder.create();
        dialog.setCancelable(true);
        dialog.setOnCancelListener((d)->finish());
        dialog.setOnShowListener( new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg0) {
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getApplicationContext().getResources().getColor(R.color.blue));
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getApplicationContext().getResources().getColor(R.color.blue));
            }
        });
        View ed = new EditText(this);
        ed.setId(R.id.edInput);
        dialog.setView(ed);
        dialog.show();
    }

    private void okBarcode(DialogInterface dialogInterface, int i) {
        finish();

        if (barcodeHandler != null){
            EditText ed = ((AlertDialog)dialogInterface).findViewById(R.id.edInput);
            barcodeHandler.onReadBarcode(this, ed.getText().toString().trim(), -1 ,-1 );
        }
    }

    private void checkNeededPermissions() {
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED) {
            // You can use the API that requires the permission.
            initCamera();
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                // In an educational UI, explain to the user why your app requires this
                // permission for a specific feature to behave as expected, and what
                // features are disabled if it's declined. In this UI, include a
                // "cancel" or "no thanks" button that lets the user continue
                // using your app without granting the permission.
                showExplainDialog();
            } else {
                // You can directly ask for the permission.
                // The registered ActivityResultCallback gets the result of this request.
                requestPermissionLauncher.launch(
                        Manifest.permission.CAMERA);
            }
        }
    }

    void showExplainDialog() {
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setMessage("Для распознавания штрихкодов приграмме необходимо использовать камеру телефона. Разрешая использовать камеру Вы позволите программе сканировать штрихкоды на товарах");
        b.setPositiveButton(android.R.string.ok, (dialogInterface, i) -> dialogInterface.dismiss());
        b.create().show();
    }

    private void takePhoto() {
        if(takePhotoHandler == null)
            return;

        final File file = takePhotoHandler.getPhotoFile();
        ImageCapture.OutputFileOptions of = (new ImageCapture.OutputFileOptions.Builder(file)).build();

//        ContentValues cv = new ContentValues();
//        cv.put(MediaStore.MediaColumns.DISPLAY_NAME, name);
//        cv.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
//        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
//            cv.put(MediaStore.Images.Media.RELATIVE_PATH, "Photos");
//        }
//
//        ImageCapture.OutputFileOptions of = (new ImageCapture.OutputFileOptions.Builder(
//                getContentResolver(),
//                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
//                cv
//        )).build();

        if (cameraController != null)
            cameraController.takePicture(
                    of,
                    ContextCompat.getMainExecutor(CameraActivity.this),
                    new ImageCapture.OnImageSavedCallback() {
                        @Override
                        public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                            if(takePhotoHandler.photoSaved(file, outputFileResults.getSavedUri()))
                                finish();
//                            Toast.makeText(CameraActivity.this, "Take photo", Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onError(@NonNull ImageCaptureException exception) {
//                            Toast.makeText(CameraActivity.this, exception.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
            );
    }

    public static String bcTypeToString(int format) {
        switch (format) {
            case Barcode.FORMAT_EAN_8: return "EAN-8";
            case Barcode.FORMAT_EAN_13: return "EAN-13";
            case Barcode.FORMAT_QR_CODE: return "QRCode";
            case Barcode.FORMAT_DATA_MATRIX: return "DataMatrix";
            case Barcode.FORMAT_CODE_128: return "CODE-128";
            case Barcode.FORMAT_CODE_39:  return "CODE-39";
            case Barcode.FORMAT_CODE_93:  return "CODE-93";
        }
        return "?";
    }

    void initCamera() {
        try {
            cameraController = new LifecycleCameraController(this);
            cameraController.bindToLifecycle(CameraActivity.this);
            cameraController.setEnabledUseCases(CameraController.IMAGE_ANALYSIS|CameraController.IMAGE_CAPTURE);
            //cameraController.getCameraControl().cancelFocusAndMetering();

            if(barcodeMode) {
                BarcodeView bv = findViewById(R.id.bvView);
                BCAnalyzer bca = new BCAnalyzer(cameraExecutor, bv, this);

//            bca.setEventHandler((bc, format, elapses) -> runOnUiThread(() -> {
//                if (bc.length() > 0) {
//                    String text = bc + "\n" + bcTypeToString(format) + "\ndetecting: " + Long.toString(elapses) + " ms";
//                    TextView tv = findViewById(R.id.tvBC);
//                    tv.setText(text);
//                }
//            }));

                cameraController.setImageAnalysisAnalyzer(cameraExecutor, bca);
                cameraController.setImageAnalysisTargetSize(new CameraController.OutputSize(new Size(1100, 1100)));
            } else {
                Config c = ConfigManager.getConfig();
                cameraController.setImageCaptureTargetSize(new CameraController.OutputSize(new Size(c.cameraWidth, c.cameraHeight)));
            }

            //cameraController.setTapToFocusEnabled(true);
            cameraController.setPinchToZoomEnabled(true);

            previewView = findViewById(R.id.viewFinder);
            previewView.setController(cameraController);
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }

        Log.d(TAG, "start camera success! " + new Date().toString());
    }

    @Override
    protected void onResume() {
        super.onResume();
        initCamera();
    }

    public class BCAnalyzer implements ImageAnalysis.Analyzer {

        ExecutorService executor;
        String bcValue = null;
        BarcodeView bv;
        Activity owner;
        boolean finish = false;

        BarcodeScanner scanner;
        public BCAnalyzer(ExecutorService executor, BarcodeView bv, Activity owner) {
            this.executor = executor;
//            BarcodeScannerOptions.Builder b = new BarcodeScannerOptions.Builder();
//            b.setBarcodeFormats(
//                    Barcode.FORMAT_EAN_8,
//                    Barcode.FORMAT_EAN_13,
//                    Barcode.FORMAT_QR_CODE,
//                    Barcode.FORMAT_DATA_MATRIX,
//                    Barcode.FORMAT_CODE_128,
//                    Barcode.FORMAT_CODE_39,
//                    Barcode.FORMAT_CODE_93
//                    );
            scanner = BarcodeScanning.getClient();
            this.bv = bv;
            this.owner = owner;

            Log.d(TAG, "BCAnalyzer created! " + new Date().toString());
        }

        Bitmap invert(Bitmap src) {
            int height = src.getHeight();
            int width = src.getWidth();

            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            Paint paint = new Paint();

            ColorMatrix matrixGrayscale = new ColorMatrix();
            matrixGrayscale.setSaturation(0);

            ColorMatrix matrixInvert = new ColorMatrix();
            matrixInvert.set(new float[] {
                            -1.0f, 0.0f, 0.0f, 0.0f, 255.0f,
                            0.0f, -1.0f, 0.0f, 0.0f, 255.0f,
                            0.0f, 0.0f, -1.0f, 0.0f, 255.0f,
                            0.0f, 0.0f, 0.0f, 1.0f, 0.0f
                    });
            matrixInvert.preConcat(matrixGrayscale);

            ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrixInvert);
            paint.setColorFilter(filter);

            canvas.drawBitmap(src, 0, 0, paint);
            return bitmap;
        }

        void onRead(List<Barcode> barcodes, long elapsedMs){
            if(barcodeHandler == null)
                return;

            for(Barcode b : barcodes) {
//                bcValue = b.getDisplayValue();
                bcValue = b.getRawValue();
                Log.d(TAG, "Red bc " + bcValue);
                if(barcodeHandler.onReadBarcode(CameraActivity.this, bcValue, b.getFormat(), elapsedMs)) {
                    finish = true;
                    owner.finish();
                }
            }
        }

        @SuppressLint("UnsafeOptInUsageError")
        @Override
        public void analyze(@NonNull ImageProxy imageProxy) {
            if (finish)
                return;

            Log.d(CameraActivity.TAG, String.format("analyze %d ^ %d", imageProxy.getWidth(), imageProxy.getHeight()));

            try {
                long frameStartMs = SystemClock.elapsedRealtime();
                int rotDeg = imageProxy.getImageInfo().getRotationDegrees();
                final boolean[] scanned = {false};

//                Bitmap b = BitmapUtils.getBitmap(imageProxy);

                View v = findViewById(R.id.viewFinder);
                SizeF parent = new SizeF(v.getWidth(), v.getHeight());
                SizeF coef = bv.getHoleCoef(parent);

                InputImage ii;
                Bitmap src = BitmapUtils.getBitmap(imageProxy);
                Bitmap b;
//                if(Features.USING_SCAN_FRAME) {
                    int cx = src.getWidth() / 2;
                    int cy = src.getHeight() / 2;

                    int sw = (int) (src.getWidth() * coef.getWidth());
                    int sh = (int) (src.getHeight() * coef.getHeight());

                    b = Bitmap.createBitmap(src, cx - sw / 2, cy - sh / 2, sw, sh);
                    ii = InputImage.fromBitmap(b, rotDeg);
//                } else {
//                    b = src;
//                    ii = InputImage.fromMediaImage(imageProxy.getImage(), rotDeg);
//                }

                scanner.process(ii)
                        .addOnSuccessListener(executor, barcodes -> {
                            long endMs = SystemClock.elapsedRealtime();
                            scanned[0] = barcodes.size() > 0;

                            onRead(barcodes, endMs - frameStartMs);
                        })
                        .addOnCompleteListener(r1 -> {
                            if (!scanned[0]) {
                                Bitmap nb = invert(b);
                                b.recycle();
                                InputImage nimage = InputImage.fromBitmap(nb, rotDeg);
                                scanner.process(nimage)
                                        .addOnSuccessListener(executor, bcs -> {
                                            long ems = SystemClock.elapsedRealtime();
                                            onRead(bcs, ems - frameStartMs);
                                        })
                                        .addOnCompleteListener(r2 -> {
                                            nb.recycle();
                                            imageProxy.close();
                                        })
                                ;
                            } else {
                                b.recycle();
                                imageProxy.close();
                            }
                        })
                ;
            }catch (Exception e){
                e.printStackTrace();
                Log.d(TAG, "Exception! " +  e.getMessage() + ""  + new Date().toString());
            }

            Log.d(TAG, "analyze success! " + new Date().toString());
        }
    }

    private Boolean allPermissionsGranted() {
        for(String p : REQUIRED_PERMISSIONS) {
            if(ContextCompat.checkSelfPermission(this, p) != PackageManager.PERMISSION_GRANTED)
                return false;
        }
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(orientationListener != null)
            orientationListener.enable();
    }

    @Override
    protected void onStop() {
        super.onStop();

        if(orientationListener != null)
            orientationListener.disable();

        if (cameraController != null)
            cameraController.unbind();
    }

//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//    }
}
