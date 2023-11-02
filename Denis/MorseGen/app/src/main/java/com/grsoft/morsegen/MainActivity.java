package com.grsoft.morsegen;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Bundle;
import android.text.Spannable;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.TextView;

import com.google.android.material.slider.Slider;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    static final int MAX_TEXT = 40;

    AudioTrack audioTrack;
    String morseText;
    String custom = null;
    Spannable drawingText;

    String getMorseText(int length, String morseSet) {
        List<Character> chars = new ArrayList();
        if(morseSet == null)
            chars.addAll(MorseAudio.morseMap.keySet());
        else {
            for(char sym : morseSet.toCharArray())
                chars.add(sym);
        }

        String text = "";
        for(int i=0; i<length; i++) {
            char sym = chars.get((int)(Math.random() * chars.size()));
            text += sym;
        }
        return text;
    }

    void drawMorseText() {
        String textOut = "";
        int ctr = 1;
        for(char sym : morseText.toCharArray()) {
            textOut += sym;
            if((ctr % 25) == 0) {
                textOut += "\n";
            } else if((ctr % 5) == 0) {
                textOut += " ";
            }
            ctr++;
        }
        TextView tv = (TextView)findViewById(R.id.tvMorseText);
        tv.setText(textOut, TextView.BufferType.SPANNABLE);
        drawingText = (Spannable) tv.getText();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MorseAudio.InitMap();

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            audioTrack = new AudioTrack.Builder()
                    .setAudioAttributes(new AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .build())
                    .setAudioFormat(new AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(MorseAudio.RATE)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build())
                    .setBufferSizeInBytes(MorseAudio.RATE * 2 * 2) // 2 sec 16 bites
                    .setTransferMode(AudioTrack.MODE_STREAM)
                    .build();
        } else {
            audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                    MorseAudio.RATE, AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_16BIT, MorseAudio.RATE * 2 * 2,
                    AudioTrack.MODE_STREAM);

        }

        morseText = getMorseText(MAX_TEXT, MorseAudio.CHAR_SET);
        drawMorseText();

        findViewById(R.id.btnRefresh).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateMorseText();
            }
        });

        findViewById(R.id.rbCustom).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                SelectChars.open(MainActivity.this, custom);
            }
        });

        findViewById(R.id.btnStop).setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                if(audioTrack.getPlayState() == AudioTrack.PLAYSTATE_PLAYING) {
                    audioTrack.pause();
                    audioTrack.flush();
                    audioTrack.stop();
                }

                drawingText.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.black)), 0, drawingText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        });

        findViewById(R.id.btnStart).setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Slider s = findViewById(R.id.slider);

                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if(audioTrack.getPlayState() != AudioTrack.PLAYSTATE_PLAYING)
                            audioTrack.play();
                        double dot = MorseAudio.CalcDotTiming(morseText, (int) s.getValue());
                        MorseAudio.Play(morseText, dot, audioTrack, new MorseAudio.Working() {
                            @Override
                            public void sendChar(int index) {
                                drawingText.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.red)), 0, (index - 1) + (index) / 5, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            }
                        });
                    }
                });
                t.start();
            }
        });
    }

    void updateMorseText() {
        Slider s = findViewById(R.id.slider);
        String cs = null;
        if(((RadioButton)findViewById(R.id.rbCustom)).isChecked()) cs = custom;
        else if(((RadioButton)findViewById(R.id.rbA)).isChecked()) cs = MorseAudio.CHAR_SET;
        else if(((RadioButton)findViewById(R.id.rbDig)).isChecked()) cs = MorseAudio.DIGIT_SET;

        morseText = getMorseText((int) s.getValue(), cs);
        drawMorseText();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == SelectChars.SEL_CHARS_ID && resultCode == Activity.RESULT_OK) {
            String sel = data.getStringExtra(SelectChars.SEL_CHAR_TAG);
            custom = sel;
            updateMorseText();
        }
    }
}