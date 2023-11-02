package com.ksoft.dms;

import android.Manifest;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.ksoft.dms.database.DBHelper;
import com.ksoft.dms.database.controller.IDNumberController;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.UUID;

public class NoteItemEdit extends AppCompatActivity {
    public static final String ITEM_ID = "item_id";
    private static final int PERMISSION_REQUEST = 2;
    private static final int GET_IMAGE_SOURCE_REQUEST = 3;
    private static final int CALCULATOR_REQUEST = 4;
    private static final String IMAGE_TAG = "[image]";
    private static final String THUMB_SFX = "_thumb";
    ;

    RichEdit edNote;
    String itemid = "";

    MaterialButton bold;
    MaterialButton italic;
    MaterialButton strike;
    MaterialButton underline;

    int textColor = Color.BLACK;
    private File photoFile;
    private int curSelection = -1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.note_item_edit);

        bold = findViewById(R.id.bold);
        italic = findViewById(R.id.italic);
        strike = findViewById(R.id.strike);
        underline = findViewById(R.id.underline);

        edNote = findViewById(R.id.edNote);

        itemid = getIntent().getStringExtra(ITEM_ID);

        ((MaterialToolbar) findViewById(R.id.topAppBar)).setOnMenuItemClickListener(menuitem ->
        {
            if (menuitem.getItemId() == R.id.calc) {
                startActivityForResult(new Intent(this, Calculator.class), CALCULATOR_REQUEST);
            }else if (menuitem.getItemId() == R.id.share){
                Intent intent = new Intent(android.content.Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.extraShareMessage));
                intent.putExtra(android.content.Intent.EXTRA_TEXT, getPlainNoteText(false));
                startActivity(Intent.createChooser(intent, getString(R.string.share)));
            }else if (menuitem.getItemId() == R.id.notify){
                AlarmDlg dlg = new AlarmDlg();
                dlg.show(getSupportFragmentManager(), dlg.getTag());
                dlg.setIAlarmDlg((d)->{
                    Intent intent = new Intent(this, AlarmReciever.class);
                    intent.putExtra(AlarmDlg.MESSAGE, d.edText.getText().toString().trim());
                    intent.putExtra(NoteItemEdit.ITEM_ID, itemid);
                    int  id = new IDNumberController().generateID(this);
                    PendingIntent oper = PendingIntent.getBroadcast(this, id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, d.dateNotify.getTime(), oper);
                });
            }

            return true;
        });

        findViewById(R.id.back).setOnClickListener((x) -> {
            supportFinishAfterTransition();
        });

        findViewById(R.id.palette).setOnClickListener((x) -> {
            selectColorDlg();
        });

        edNote.addTextChangedListener(textWatcher);
        findViewById(R.id.camera).setOnClickListener((x) -> checkCameraPermission());

        edNote.setImageClickListener((v, path) -> {
            if (path.contains(THUMB_SFX)) {
                path = path.replace(THUMB_SFX, "");
            }

            preview(path);
        });
    }

    private void readNote() {
        SQLiteDatabase db = new DBHelper(this).getReadableDatabase();
        Cursor c = db.query("notes_items", new String[]{"note", "note_html"}, "id=?", new String[]{itemid}, null, null, null);

        if (c.moveToFirst()) {
            String html = c.getString(c.getColumnIndex("note_html"));

            if (html != null && html.length() > 0) {

                edNote.removeTextChangedListener(textWatcher);
                Spanned s = Html.fromHtml(html, 0, new Html.ImageGetter() {
                    @Override
                    public Drawable getDrawable(String source) {
                        try {
                            source = getThumbString(source);
                            Uri uri = createUri(source);
                            InputStream inputStream = getContentResolver().openInputStream(uri);
                            Drawable img = Drawable.createFromStream(inputStream, source);
                            fitToWidth(img);

                            return img;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        return null;
                    }
                }, null);

                edNote.setText(s);
                edNote.addTextChangedListener(textWatcher);
            }
        }

        c.close();
    }

    @Override
    protected void onResume() {
        super.onResume();

        readNote();

        edNote.postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(edNote, InputMethodManager.SHOW_IMPLICIT);
            }
        }, 500);

        if (curSelection != -1)
            edNote.setSelection(curSelection);
    }

    private Uri createUri(String source) {
        Uri uri = null;

        if (Build.VERSION.SDK_INT >= 24) {
            uri = FileProvider.getUriForFile(NoteItemEdit.this, String.format("%s.provider", BuildConfig.APPLICATION_ID), new File(source));
        } else
            uri = Uri.fromFile(new File(source));
        return uri;
    }

    private void selectColorDlg() {
        ViewGroup view = (ViewGroup) View.inflate(this, R.layout.select_color_dialog, null);

        final Dialog dlg = new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.select_color)
                .setView(view)
                .create();

        for (int i = 0; i < view.getChildCount(); i++)
            view.getChildAt(i).setOnClickListener(x -> {
                Drawable background = x.getBackground();
                if (background instanceof ColorDrawable) {
                    textColor = ((ColorDrawable) background).getColor();
                    findViewById(R.id.colorView).setBackgroundColor(textColor);
                    dlg.dismiss();
                }
            });

        dlg.show();
    }

    TextWatcher textWatcher = new TextWatcher() {
        Object span;
        int start;
        int end;
        StyleSpan ss = null;
        SpannableString spannableString = new SpannableString("");

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence cs, int start, int before, int count) {
            this.start = start + before;
            this.end = start + count;
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (start >= end || s.toString().charAt(end-1) == '\n')
                return;

            int spanStart = start;

            int style = Typeface.NORMAL;

            if (bold.isChecked())
                style |= Typeface.BOLD;

            if (italic.isChecked())
                style |= Typeface.ITALIC;

            if (style != Typeface.NORMAL) {
                Object[] arr = s.getSpans(start - 1, end, StyleSpan.class);

                for(Object o : arr){
                    StyleSpan sp = (StyleSpan)o;
                    if (sp.getStyle() == style) {
                        if (s.getSpanStart(o) < spanStart)
                            spanStart = s.getSpanStart(o);

                        s.removeSpan(o);
                    }
                }

                span = new StyleSpan(style);
                s.setSpan(span, spanStart, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }

            spanStart = start;

            if (strike.isChecked()) {
                Object[] arr = s.getSpans(start - 1, end, StrikethroughSpan.class);

                for(Object o : arr){
                    if (s.getSpanStart(o) < spanStart)
                        spanStart = s.getSpanStart(o);

                    s.removeSpan(o);
                }

                span = new StrikethroughSpan();
                s.setSpan(span, spanStart, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }

            spanStart = start;

            if (underline.isChecked()) {
                Object[] arr = s.getSpans(start - 1, end, UnderlineSpan.class);

                for(Object o : arr){
                    if (s.getSpanStart(o) < spanStart)
                        spanStart = s.getSpanStart(o);

                    s.removeSpan(o);
                }

                span = new UnderlineSpan();
                s.setSpan(span, spanStart, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }

            spanStart = start;

            if (textColor != getResources().getColor(R.color.black)) {
                Object[] arr = s.getSpans(start - 1, end, ForegroundColorSpan.class);


                for(Object o : arr){
                    ForegroundColorSpan sp = (ForegroundColorSpan)o;

                    if (sp.getForegroundColor() == textColor) {
                        if (s.getSpanStart(o) < spanStart)
                            spanStart = s.getSpanStart(o);

                        s.removeSpan(o);
                    }
                }

                span = new ForegroundColorSpan(textColor);
                s.setSpan(span, spanStart, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
    };

    private void checkCameraPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        PERMISSION_REQUEST);
            else
                cameraOpen();
        }
    }

    @Override
    public void onRequestPermissionsResult(int rc, String[] permissions, int[] result) {
        if (rc == PERMISSION_REQUEST) {
            for (int i = 0; i < result.length; i++) {
                if (result[i] != PackageManager.PERMISSION_GRANTED && permissions[i].equals(Manifest.permission.CAMERA)) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST);
                    return;
                } else if (result[i] != PackageManager.PERMISSION_GRANTED && permissions[i].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST);
                    return;
                }
            }

            cameraOpen();
        }
    }

    private void cameraOpen() {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        photoFile = new File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), getPicFileName());

        Uri uri = createUri(photoFile.getAbsolutePath());

        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);

        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
        galleryIntent.setType("image/*");

        Intent chooser = Intent.createChooser(galleryIntent, getString(R.string.select_photo_app));
        chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{cameraIntent});
        startActivityForResult(chooser, GET_IMAGE_SOURCE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GET_IMAGE_SOURCE_REQUEST && resultCode == RESULT_OK) {
            String tag = IMAGE_TAG;
            SpannableString ss = new SpannableString(tag);

            File source = photoFile;

            if (data != null && data.getData() != null) {
                Uri uri = data.getData();
                try {
                    File file = FileUtil.copyToShare(getApplicationContext(), uri, getPicFileName());
                    FileUtil.copy(getApplicationContext(), uri, file);
                    source = file;

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (source != null) {
                try {
                    String thumbName = getThumbString(source.getAbsolutePath());
                    BitmapDrawable img = createThumb(source, thumbName);

                    //Uri uri = createUri(thumbName);

                    ImageSpan span = new ImageSpan(img, thumbName, ImageSpan.ALIGN_BASELINE);
                    ss.setSpan(span, 0, tag.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);

                    final String srcPath = source.getAbsolutePath();
                    ClickableSpan span2 = new ClickableSpan() {
                        @Override
                        public void onClick(@NonNull View widget) {
                            preview(srcPath);
                        }
                    };

                    ss.setSpan(span2, 0, tag.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            photoFile = null;
            edNote.removeTextChangedListener(textWatcher);
            edNote.append("\n");
            edNote.append(ss);
            edNote.setSelection(edNote.getText().length());
            edNote.append("\n");
            edNote.requestFocus();
            edNote.addTextChangedListener(textWatcher);

            saveChanges();
        } else if (resultCode == RESULT_OK && requestCode == CALCULATOR_REQUEST) {
            edNote.append(data.getStringExtra(Calculator.DIGIT_RESULT));
        }
    }

    private void preview(String path) {
        Intent i = new Intent();
        i.setAction(Intent.ACTION_VIEW);

        Uri uri = createUri(path);

        i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        i.setDataAndType(uri, "image/*");

        startActivity(i);
    }


    private BitmapDrawable createThumb(File source, String dist) {
        BitmapDrawable img = null;

        try {
            final int THUMBNAIL_SIZE = 256;

            FileInputStream fis = new FileInputStream(source);
            Bitmap imageBitmap = BitmapFactory.decodeStream(fis);

            ExifInterface exif = new ExifInterface(source.getAbsolutePath());
            int rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            int rotationInDegrees = exifToDegrees(rotation);

            Matrix matrix = new Matrix();
            if (rotation != 0) {
                matrix.preRotate(rotationInDegrees);
                imageBitmap = Bitmap.createBitmap(imageBitmap, 0, 0, imageBitmap.getWidth(), imageBitmap.getHeight(), matrix, true);
            }

            FileOutputStream baos = new FileOutputStream(source);
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            baos.flush();

            imageBitmap = Bitmap.createScaledBitmap(imageBitmap, THUMBNAIL_SIZE, THUMBNAIL_SIZE * imageBitmap.getHeight() / imageBitmap.getWidth(), false);
            source = new File(dist);

            baos = new FileOutputStream(source);
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            baos.flush();

            img = new BitmapDrawable(getResources(), imageBitmap);
            fitToWidth(img);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return img;
    }

    private String getThumbString(String source) {


        if (source.indexOf(THUMB_SFX) == -1) {
            String sp = source;
            int idx = sp.lastIndexOf(".");
            final String ext = sp.substring(idx);
            return String.format("%s%s%s", sp.substring(0, idx), THUMB_SFX, ext);
        } else
            return source;
    }

    private static int exifToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        }
        return 0;
    }

    private String getPicFileName() {
        return String.format("%s.png", UUID.randomUUID().toString());
    }

    void fitToWidth(Drawable drawable) {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int widthD = (int) ((double) size.x / 3);

        drawable.setBounds(size.x / 2 - widthD / 2, 0, widthD + size.x / 2 - widthD / 2, (widthD * drawable.getIntrinsicHeight() / drawable.getIntrinsicWidth()));
        //drawable.setBounds(0, 0, widthD, (widthD * drawable.getIntrinsicHeight() / drawable.getIntrinsicWidth()));
    }

    @Override
    protected void onPause() {
        super.onPause();

        saveChanges();

        curSelection = edNote.getSelectionStart();

        if (isFinishing()) {
            Intent i = new Intent(NoteEdit.UPDATE_ACTION_ROW);
            i.putExtra(ITEM_ID, itemid);
            sendBroadcast(i);
        }
    }

    private void saveChanges() {
        ContentValues cv = new ContentValues();
        cv.put("note_html", Html.toHtml(edNote.getText(), 0));
        cv.put("note", getPlainNoteText(true));

        SQLiteDatabase db = new DBHelper(this).getWritableDatabase();
        db.update("notes_items", cv, "id=?", new String[]{itemid});
    }

    private String getPlainNoteText(boolean first) {
        String multiLines = edNote.getText().toString();
        String delimiter = "\n";
        String[] lines = multiLines.split(delimiter);
        String note = "";

        for (String line : lines) {
            if (line.trim().equals("[image]") || line.trim().equals("￼") || line.trim().length() == 0)
                continue;

            if (note.trim().length() > 0)
                note += "\n";

            note += line;

            if (first)
                break;
        }

        return note;
    }
}
