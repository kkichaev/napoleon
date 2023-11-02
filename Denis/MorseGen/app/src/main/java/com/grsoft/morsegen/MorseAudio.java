package com.grsoft.morsegen;

import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.content.Context;

import androidx.core.content.ContextCompat;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MorseAudio {
    interface Working {
        void sendChar(int index);
    }

    static int MAX_LEN = 10;

    // frames per seconds
    static int RATE = 24000;

    static int SND_FREQ = 600;
    static Map<Character, String> morseMap = new HashMap<>();

    static final String CHAR_SET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    static final String DIGIT_SET = "0123456789";

    static void InitMap() {
        morseMap.put('A', ".-");
        morseMap.put('B', "-...");
        morseMap.put('C', "-.-.");
        morseMap.put('D', "-..");
        morseMap.put('E', ".");
        morseMap.put('F', "..-.");
        morseMap.put('G', "--.");
        morseMap.put('H', "....");
        morseMap.put('I', "..");
        morseMap.put('J', ".---");
        morseMap.put('K', "-.-");
        morseMap.put('L', ".-..");
        morseMap.put('M', "--");
        morseMap.put('N', "-.");
        morseMap.put('O', "---");
        morseMap.put('P', ".--.");
        morseMap.put('Q', "--.-");
        morseMap.put('R', ".-.");
        morseMap.put('S', "...");
        morseMap.put('T', "-");
        morseMap.put('U', "..-");
        morseMap.put('V', "...-");
        morseMap.put('W', ".--");
        morseMap.put('X', "-..-");
        morseMap.put('Y', "-.--");
        morseMap.put('Z', "--..");

        morseMap.put('0', "-----");
        morseMap.put('1', ".----");
        morseMap.put('2', "..---");
        morseMap.put('3', "...--");
        morseMap.put('4', "....-");
        morseMap.put('5', ".....");
        morseMap.put('6', "-....");
        morseMap.put('7', "--...");
        morseMap.put('8', "---..");
        morseMap.put('9', "----.");

        morseMap.put('?', "..--..");
        morseMap.put('/', "-..-.");
        morseMap.put('=', "-...-");
        morseMap.put('.', ".-.-.-");
        morseMap.put(',', "--..--");
    }

    // time in seconds
    static void GenTone(List<Byte> dest, double time) {
        int count = (int)(time * RATE + 0.5);

        int decrAmp = (count * 12 - count) / 12;
        for (int i = 0; i < count; ++i) {
            short amp = 0x6FFF;
            if(i >= decrAmp) {
                amp = (short)((double)amp * (count - i) * 12 / count);
            }
            short val = (short)((Math.sin(2 * Math.PI * i / (RATE/SND_FREQ))) * amp);
            dest.add((byte)(val & 0xFF));
            dest.add((byte)((val & 0xFF00) >> 8));
        }
    }

    // time in seconds
    static void GenMute(List<Byte> dest, double time) {
        int count = (int)(time * RATE + 0.5);

        for (int i = 0; i < count; ++i) {
            dest.add((byte)0);
            dest.add((byte)0);
        }
    }

    static int CharToDots(char sym) {
        int res = 0;
        String morse = morseMap.get(sym);
        if(morse != null) {
            res = morse.length() - 1; // pauses
            for(char csym : morse.toCharArray()) {
                if(csym == '-') res += 3;
                else if(csym == '.') res ++;
            }
        }
        return res;
    }

    static int CountGroupDots(String s) {
        int res = 0;
        for(char sym : s.toCharArray())
            res += CharToDots(sym);

        // puase between sym 3 dots, between groups 7
        return res + (s.length() * 3) + (s.length() / 5 * 4) - 3;
    }

    static byte[] Make(String data, int speed) {
        int  dot = speed;
        int dash = 3 * dot;
        int dotPause = dot;

        List<Byte> buf = new ArrayList<Byte>();

        int idx = 0;
        for(char sym : data.toCharArray()) {
            String morse = morseMap.get(sym);
            if(morse != null) {
                for(char m : morse.toCharArray()) {
                    GenTone(buf, (m == '.') ? dot : dash);
                    GenMute(buf, dotPause);
                }
                GenMute(buf, dotPause * 2);
            }
            idx++;
            if(idx == 5) {
                GenMute(buf, dotPause);
                idx = 0;
            }
        }

        byte[] ret = new byte[buf.size()];
        int i = 0;
        for(Byte b : buf) {
            ret[i++] = b;
        }
        return ret;
    }

    static double CalcDotTiming(String data, int speed) {
        int cdots = CountGroupDots(data);
        double coef = (double)data.length() / speed;
        double  dot = (coef * 60) / cdots;
        return dot;
    }

    static void Play(String data, double dot, AudioTrack audioTrack, Working working) {
        double dash = 3 * dot;
        double dotPause = dot;

        int idx = 0;
        for(char sym : data.toCharArray()) {
            List<Byte> buf = new ArrayList<Byte>();

            String morse = morseMap.get(sym);
            if(morse != null) {
                for(char m : morse.toCharArray()) {
                    GenTone(buf, (m == '.') ? dot : dash);
                    GenMute(buf, dotPause);
                }
                GenMute(buf, dotPause * 3);
            }

            idx++;
            if((idx % 5) == 0) {
                GenMute(buf, dotPause * 4);
            }

            byte[] ret = new byte[buf.size()];
            int i = 0;
            for(Byte b : buf) {
                ret[i++] = b;
            }

            audioTrack.write(ret, 0, ret.length);

            if(working != null)
                working.sendChar(idx);
            if(audioTrack.getPlayState() != AudioTrack.PLAYSTATE_PLAYING)
                break;
        }
    }
}
