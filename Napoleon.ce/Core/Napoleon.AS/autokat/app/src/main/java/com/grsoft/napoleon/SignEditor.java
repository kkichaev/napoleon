package com.grsoft.napoleon;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.grsoft.napoleon.main.SignHelper;

import java.io.File;
import java.io.FileOutputStream;

public class SignEditor extends BaseFragment implements View.OnTouchListener {
    public static final String READ_ONLY = "readonly";
    public static String KEY = "SignEditor";
    public static final String FILE_NAME = "file_name";
    Paint paint = new Paint();
    ImageView signature;
    Canvas canvas;
    int eraseColor;
    float downx, downy = 0.0f;
    private Bitmap bitmap;
    File file;
    String signPath;
    boolean changed = false;

    @Override
    protected int getLayoutID() {
        return R.layout.signature_edit;
    }

    @Override
    public String TAG() {
        return null;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        signature = view.findViewById(R.id.signature);
        paint.setStrokeWidth(10);
        paint.setColor(getResources().getColor(R.color.sign_stroke));

        eraseColor = Color.TRANSPARENT;
        signPath = getArguments().getString(FILE_NAME);
        file = new File(signPath);
        bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());

        if (bitmap == null) {
            initClearBitmap();
        }

        bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        signature.setImageBitmap(bitmap);
        canvas = new Canvas(bitmap);

        signature.setOnTouchListener(this);

        if (!getArguments().getBoolean(READ_ONLY)) {
            view.findViewById(R.id.btnClear).setOnClickListener(v -> eraseImage());
            view.findViewById(R.id.btnOK).setOnClickListener(v -> doOK());
        }else {
            view.findViewById(R.id.btnClear).setVisibility(View.INVISIBLE);
            view.findViewById(R.id.btnOK).setVisibility(View.INVISIBLE);
        }


        return view;
    }

    private void initClearBitmap() {
        bitmap = Bitmap.createBitmap(
                (int) getResources().getDimension(R.dimen.sign_width),
                (int) getResources().getDimension(R.dimen.sign_height),
                Bitmap.Config.ARGB_8888);
        bitmap.eraseColor(eraseColor);
    }

    private void doOK() {
        if (changed) {
            saveImage();
            Bundle res = new Bundle();
            res.putString(FILE_NAME, file.getAbsolutePath());
            getParentFragmentManager().setFragmentResult(KEY, res);
        }

        getParentFragmentManager().popBackStack();
    }

    @Override
    public String getTitle() {
        return getString(R.string.sign_edit_title);
    }

    private void saveImage() {
        try {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, new FileOutputStream(file));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void eraseImage(){
        changed = false;
        file.delete();
        initClearBitmap();
        bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        signature.setImageBitmap(bitmap);
        canvas = new Canvas(bitmap);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        float bw = getResources().getDimension(R.dimen.sign_width);
        float bh = getResources().getDimension(R.dimen.sign_height);
        float iw = signature.getWidth();
        float ih = signature.getHeight();

        float sw = bw / iw;
        float sh = bh / ih;

        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                downx = event.getX();
                downy = event.getY();
                downx *= sw;
                downy *= sh;
                break;
            case MotionEvent.ACTION_MOVE:
                float upx = event.getX();
                float upy = event.getY();
                upx *= sw;
                upy *= sh;
                canvas.drawLine(downx, downy, upx, upy, paint);
                signature.invalidate();
                downy = upy;
                downx = upx;
                changed = true;
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
            default:
                break;
        }
        return true;
    }
}
