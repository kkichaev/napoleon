package com.grsoft.camera;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.util.Size;
import android.util.SizeF;
import android.view.OrientationEventListener;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.TorchState;
import androidx.camera.view.CameraController;
import androidx.camera.view.LifecycleCameraController;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CameraActivity extends AppCompatActivity {

    static public final int REQ_CODE = CameraActivity.class.hashCode();
    static public final String BARCODE_TAG = "bc";
    static BarcodeHandler barcodeHandler = null;

    public interface BarcodeValidator {
        boolean validate(String bc);
    }

    int VERSION = 4;

    OrientationEventListener orientationListener = null;

    static void setBarcodeHandler(BarcodeHandler bh) {
        barcodeHandler = bh;
    }

    public static void openBCScanner(Activity context) {
        openBCScanner(context, null);
    }

    public static void openBCScanner(Activity context, BarcodeValidator validator) {
        Intent i = new Intent(context, CameraActivity.class);
        setBarcodeHandler(new BarcodeHandler() {
            @Override
            public boolean onReadBarcode(Activity owner, String barcode, int type, long elapsesMs) {
                if (validator == null || validator.validate(barcode)) {
                    Intent i = new Intent();
                    i.putExtra(BARCODE_TAG, barcode);
                    owner.setResult(RESULT_OK, i);
                    owner.finish();
                }

                return false;
            }

            @Override
            public void initActivity(Activity owner) {
            }
        });

        context.startActivityForResult(i, REQ_CODE);
    }

    private static final String TAG = "CameraXApp";
    private final int REQUEST_CODE_PERMISSIONS = 10;

    String[] REQUIRED_PERMISSIONS = new String[]{
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };
    ExecutorService cameraExecutor;

    LifecycleCameraController cameraController;
    PreviewView previewView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.bc_layout);
        TextView tvVer = findViewById(R.id.tvVersion);
        tvVer.setText(String.format("Версия %d", VERSION));

        if (barcodeHandler != null)
            barcodeHandler.initActivity(this);

        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        }

        View v = findViewById(R.id.btnTorch);
        if (v != null)
            v.setOnClickListener(view -> {
                if (cameraController != null)
                    cameraController.enableTorch(cameraController.getTorchState().getValue() == TorchState.OFF);
            });

        Log.d(TAG, "create success! " + new Date().toString());
        cameraExecutor = Executors.newSingleThreadExecutor();
    }

    public static String bcTypeToString(int format) {
        switch (format) {
            case Barcode.FORMAT_EAN_8:
                return "EAN-8";
            case Barcode.FORMAT_EAN_13:
                return "EAN-13";
            case Barcode.FORMAT_QR_CODE:
                return "QRCode";
            case Barcode.FORMAT_DATA_MATRIX:
                return "DataMatrix";
            case Barcode.FORMAT_CODE_128:
                return "CODE-128";
            case Barcode.FORMAT_CODE_39:
                return "CODE-39";
            case Barcode.FORMAT_CODE_93:
                return "CODE-93";
        }
        return "?";
    }

    void startCamera() {
        try {
            cameraController = new LifecycleCameraController(this);
            cameraController.bindToLifecycle(CameraActivity.this);
            cameraController.setEnabledUseCases(CameraController.IMAGE_ANALYSIS | CameraController.IMAGE_CAPTURE);
            //cameraController.getCameraControl().cancelFocusAndMetering();

            BarcodeView bv = findViewById(R.id.bvView);
            BCAnalyzer bca = new BCAnalyzer(cameraExecutor, bv, this);

            cameraController.setImageAnalysisAnalyzer(cameraExecutor, bca);
            cameraController.setImageAnalysisTargetSize(new CameraController.OutputSize(new Size(1100, 1100)));

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
        startCamera();
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
            matrixInvert.set(new float[]{
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

        void onRead(List<Barcode> barcodes, long elapsedMs) {
            if (barcodeHandler == null)
                return;

            Log.d(TAG, "onRead barcodes count: " + barcodes.size());
            for (Barcode b : barcodes) {
//                bcValue = b.getDisplayValue();
                bcValue = b.getRawValue();
                if (barcodeHandler.onReadBarcode(CameraActivity.this, bcValue, b.getFormat(), elapsedMs)) {
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

                SizeF coef = bv.getHoleCoef();

                InputImage ii;
                Bitmap src = BitmapUtils.getBitmap(imageProxy);
                Bitmap b;

                int cx = src.getWidth() / 2;
                int cy = src.getHeight() / 2;

                int sw = (int) (src.getWidth() * coef.getWidth());
                int sh = (int) (src.getHeight() * coef.getHeight());

                b = Bitmap.createBitmap(src, cx - sw / 2, cy - sh / 2, sw, sh);
                ii = InputImage.fromBitmap(b, rotDeg);

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
            } catch (Exception e) {
                e.printStackTrace();
                Log.d(TAG, "Exception! " + e.getMessage() + "" + new Date().toString());
            }

            Log.d(TAG, "analyze success! " + new Date().toString());
        }
    }

    private Boolean allPermissionsGranted() {
        for (String p : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, p) != PackageManager.PERMISSION_GRANTED)
                return false;
        }
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (orientationListener != null)
            orientationListener.enable();
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (orientationListener != null)
            orientationListener.disable();

        if (cameraController != null)
            cameraController.unbind();
    }

//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//    }
}
