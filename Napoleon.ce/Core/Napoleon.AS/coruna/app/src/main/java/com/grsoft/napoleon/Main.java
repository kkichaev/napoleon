package com.grsoft.napoleon;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.grsoft.camera.CameraActivity;

import java.util.HashSet;
import java.util.Set;

public class Main extends AppCompatActivity {

    static final int START_STATE = 1;
    static final int SCAN_STATE = 2;

    int state = START_STATE;
    MediaPlayer mediaPlayer = null;
    private Set<String> barcodes = new HashSet<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Config.load(this);

        findViewById(R.id.scanning).setOnClickListener(view -> {
            if(state == START_STATE) {
                CameraActivity.openBCScanner(this, this::barcodeValid);
            } else {
                ackClear();
            }
        });

        findViewById(R.id.check_scan).setOnClickListener(view -> {
            CameraActivity.openBCScanner(this);
        });

        TextInputLayout til = findViewById(R.id.scan_check);
        til.getEditText().setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(event != null && state != SCAN_STATE) {
                    state = SCAN_STATE;
                    updateLayout();
                }
                return false;
            }
        });

        til = findViewById(R.id.check_bc);
        til.getEditText().setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(event != null)
                    checkScan(((EditText) v).getText().toString());
                return false;
            }
        });

        updateLayout();
    }

    private boolean barcodeValid(String bc) {
        if (!barcodes.contains(bc)){
            barcodes.add(bc);
            return false;
        }

        barcodes.clear();
        return true;
    }

    void ackClear() {
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setTitle("Вопрос");
        b.setMessage("Очистить данные?");
        b.setPositiveButton(android.R.string.yes, (dialog, which) -> {
            TextInputLayout til = findViewById(R.id.check_bc);
            til.getEditText().setText("");

            til = findViewById(R.id.scan_check);
            til.getEditText().setText("");

            state = START_STATE;
            updateLayout();
        });

        b.setNegativeButton(android.R.string.no, null);
        b.create().show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CameraActivity.REQ_CODE && resultCode == RESULT_OK) {
            String bc = data.getExtras().getString(CameraActivity.BARCODE_TAG);
            if(state == START_STATE) {
                TextInputLayout til = findViewById(R.id.scan_check);
                til.getEditText().setText(bc);
                state = SCAN_STATE;

                updateLayout();
            } else if(state == SCAN_STATE) {
                TextInputLayout til = findViewById(R.id.check_bc);
                til.getEditText().setText(bc);

                checkScan(bc);
            }
        }
    }

    private void checkScan(String bc) {
        TextInputLayout til = findViewById(R.id.scan_check);
        String refBC = til.getEditText().getText().toString();

        if(bc.equals(refBC)) {
            Toast.makeText(this, R.string.match, Toast.LENGTH_LONG).show();
            playSound(Config.goodUri);
        } else {
            playSound(Config.badUri);
            alertMismatch();
        }
    }

    private void alertMismatch() {
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setTitle("Ошибка");
        b.setMessage("Коды не совпадают");
        b.setNeutralButton(android.R.string.ok, null);
        b.create().show();
    }

    private void playSound(String uri) {
        if(uri == null || uri.length() == 0)
            return;

        Uri u = Uri.parse(uri);
        if(mediaPlayer != null) {
            mediaPlayer.stop();
        }

        mediaPlayer = MediaPlayer.create(this, u);
        mediaPlayer.setLooping(false);
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mediaPlayer = null;
                mp.reset();
                mp.stop();
            }
        });
        mediaPlayer.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mediaPlayer != null) {
            mediaPlayer.stop();
        }
    }

    void updateLayout() {
        int vsbl = View.INVISIBLE;
        if(state == SCAN_STATE) {
            vsbl = View.VISIBLE;

            TextInputLayout til = findViewById(R.id.check_bc);
            til.getEditText().requestFocus();
        } else {
            TextInputLayout til = findViewById(R.id.scan_check);
            til.getEditText().requestFocus();
        }
        findViewById(R.id.scan_layout).setVisibility(vsbl);

        ((Button)findViewById(R.id.scanning)).setText(state == START_STATE ? R.string.scanning : R.string.clearing);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.exit) {
            finish();
            return true;
        }
        if(id == R.id.settings) {
            Settings.open(Main.this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
