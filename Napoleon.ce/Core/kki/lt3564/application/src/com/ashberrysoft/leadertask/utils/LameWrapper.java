package com.ashberrysoft.leadertask.utils;

import java.nio.ByteBuffer;

import android.text.TextUtils;

/**
 * <p>
 * Wrapper for JNI call to mp3lame library. This class implements singleton pattern, so you should use getInstance(); To
 * use it, first of all call init() method. After that you can encoding buffers on the fly using lameEncodeBuffer().
 * Don't forget to call finish() method after end of encoding buffers.
 * </p>
 * 
 * <p>
 * If you to encode or decode file, use lameEncodeFileNative or lameDecodeFileNative for that operations. You don't need
 * to use init() or finish methods in this case
 * </p>
 * 
 * @author A.Menyaylo <anton.menyaylo@gmail.com>
 * 
 */
public class LameWrapper {

    static {
        System.loadLibrary("lamewrapper_full");
    }

    public static native String getVersion();

    private static volatile LameWrapper sInstance;

    private String mFileName;
    private volatile boolean mInit;

    private LameWrapper() {
    }

    public static LameWrapper getInstance() {
        LameWrapper localInstance = sInstance;
        if (localInstance == null) {
            synchronized (LameWrapper.class) {
                localInstance = sInstance;
                if (localInstance == null) {
                    sInstance = localInstance = new LameWrapper();
                }
            }
        }
        return localInstance;
    }

    /**
     * Call this function before using lameEncodeBuffer().
     * 
     * @param filepath
     *            - path to file, which lame library will be used in lameEncodeBuffer()
     * @return
     */
    public int init(String filepath) {
        if (mInit) {
            finish();
        }
        if (!TextUtils.isEmpty(filepath)) {
            mFileName = filepath;
            int res = initNative(filepath);
            if (res != 0) {
                mInit = true;
            }
            return res;
        } else
            return -1;
    }

    /**
     * Used for encode buffer to mp3.
     * 
     * @param bufferL
     *            - left channel of raw data
     * @param bufferR
     *            - right channel of raw data
     * @param nSamples
     *            - samples count
     * @return
     */
    public int lameEncodeBuffer(ByteBuffer bufferL, ByteBuffer bufferR, int nSamples) {
        if (!mInit) {
            return -255;
        } else {
            synchronized (LameWrapper.class) {
                if (mInit) {
                    return LameWrapper.lameEncodeBufferNative(bufferL, bufferR, nSamples / 2);
                } else {
                    return -255;
                }
            }
        }
    }

    /**
     * Call this function after finishing used of lameEncodeBufferNative(), not after ever call of
     * lameEncodeBufferNative(), but after last call.
     * 
     * @return 0, if operation finished succussfull, error code otherwise
     */
    public int finish() {
        if (mInit == true) {
            synchronized (LameWrapper.class) {
                int res = finishNative();
                mInit = false;
                return res;
            }
        } else {
            return 255;
        }
    }

    private static native int initNative(String filepath);

    private static native int finishNative();

    private static native int lameEncodeBufferNative(ByteBuffer bufferL, ByteBuffer bufferR, int nSamples);

    /**
     * Encode raw pcm data to mp3.
     * 
     * @param inputFileName
     *            - full path to file, that contains raw pcm data.
     * @param outputFileName
     *            - full path to file, that will be contains encoded mp3.
     * @param sampleRate
     *            - sample rate of raw pcm data.
     * @return 0, if operation successfull, error code otherwise
     */
    public static native int lameEncodeFileNative(String inputFileName, String outputFileName, int sampleRate);

    /**
     * Decode mp3 to raw pcm data.
     * 
     * @param inputFileName
     *            - full path to file, that contains mp3.
     * @param outputFileName
     *            - full path to file, that will be contains raw pcm data.
     * @return 0, if operation successfull, error code otherwise
     */
    public static native int lameDecodeFileNative(String inputFileName, String outputFileName);
}