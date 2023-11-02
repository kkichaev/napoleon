package com.grsoft.network.encrypt;

import static com.grsoft.network.encrypt.EncodableConnection.AES_PRV;

import android.content.Context;

import com.grsoft.network.ConnectionManager;
import com.grsoft.network.SocketConnection;
import com.grsoft.network.UserInfo;
import com.grsoft.network.util.UnicodUtils;
import com.grsoft.util.DataThread;
import com.grsoft.util.ThreadPool;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;

import android.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Encryptor {
    static String PRV_KEY = "data.skey";
    static String PUB_KEY = "data.pkey";

//    static String RSA_PRV = "RSA/ECB/OAEPWithSHA-1AndMGF1Padding";
//    static String RSA_PRV = "RSA/ECB/PKCS1Padding";
    public static String RSA_PRV = "RSA/ECB/OAEPWithSHA-256AndMGF1Padding";

    public SocketConnection startSession(Context context, UserInfo userInfo) {

        KeyPair keys = getKeyPair(context);
        if(keys == null)
            return null;

//        try {
////            byte[] test= new byte[]{(byte)0x06,(byte)0x70,(byte)0xd1,(byte)0x3d,(byte)0x0c,(byte)0xa4,(byte)0x06,(byte)0x04,(byte)0x2c,(byte)0x43,(byte)0x77,(byte)0x9d,(byte)0x16,(byte)0x15,(byte)0x0f,(byte)0x1d,(byte)0xac,(byte)0xf6,(byte)0x0a,(byte)0x3f,(byte)0x91,(byte)0xfa,(byte)0x9d,(byte)0xde,(byte)0x50,(byte)0x66,(byte)0xf1,(byte)0x37,(byte)0xa2,(byte)0x94,(byte)0x7f,(byte)0x27,};
////            byte[] code= new byte[]{(byte)0xeb,(byte)0x98,(byte)0xab,(byte)0x4a,(byte)0x74,(byte)0x64,(byte)0xbb,(byte)0x30,(byte)0xc6,(byte)0x92,(byte)0xc7,(byte)0x78,(byte)0x92,(byte)0x8e,(byte)0x1e,(byte)0x4e,(byte)0xc3,(byte)0xfb,(byte)0xa7,(byte)0x90,(byte)0x95,(byte)0x59,(byte)0x43,(byte)0x9d,(byte)0x95,(byte)0xbb,(byte)0x16,(byte)0x3d,(byte)0x88,(byte)0xe0,(byte)0x2f,(byte)0x42,(byte)0x5d,(byte)0xc8,(byte)0x61,(byte)0xc7,(byte)0xbb,(byte)0x33,(byte)0x4e,(byte)0xb0,(byte)0x43,(byte)0x5a,(byte)0xba,(byte)0xc2,(byte)0x6a,(byte)0xb3,(byte)0x73,(byte)0xf1,};
////
////            byte[] keyData = Arrays.copyOfRange(code, 0, 32);
////            byte[] ivData = Arrays.copyOfRange(code, 32, 48);
////
////            SecretKeySpec key = new SecretKeySpec(keyData, "AES");
////            IvParameterSpec iv = new IvParameterSpec(ivData);
////            Cipher decrypt = Cipher.getInstance(AES_PRV);
////            decrypt.init(Cipher.DECRYPT_MODE, key, iv);
////
////            byte[] res = decrypt.doFinal(test);
////            decrypt.doFinal();
//
////            byte data[] = new byte[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12};
////
////            Cipher c = Cipher.getInstance(RSA_PRV);
//////            c.init(Cipher.ENCRYPT_MODE, keys.getPublic());
//////            byte[] out = c.doFinal(data);
//////            c.init(Cipher.ENCRYPT_MODE, keys.getPublic());
//////            byte[] out2 = c.doFinal(data);
////
////            byte[] out1= new byte[]{(byte)0x81,(byte)0x0d,(byte)0x0f,(byte)0x72,(byte)0x62,(byte)0xba,(byte)0x9a,(byte)0xd8,(byte)0x01,(byte)0x7e,(byte)0x44,(byte)0xa5,(byte)0x70,(byte)0xea,(byte)0x26,(byte)0xdf,(byte)0xfd,(byte)0x25,(byte)0x20,(byte)0xca,(byte)0x47,(byte)0x5f,(byte)0xa9,(byte)0x82,(byte)0x07,(byte)0xe4,(byte)0xa2,(byte)0x5f,(byte)0xd4,(byte)0xfc,(byte)0x93,(byte)0x5b,(byte)0x96,(byte)0x6c,(byte)0x68,(byte)0x3c,(byte)0x24,(byte)0xdc,(byte)0x62,(byte)0xc9,(byte)0x67,(byte)0x0f,(byte)0xbd,(byte)0xaf,(byte)0xd0,(byte)0x97,(byte)0xe3,(byte)0xc5,(byte)0xc4,(byte)0x5a,(byte)0xc7,(byte)0x49,(byte)0xef,(byte)0x2a,(byte)0x17,(byte)0x3a,(byte)0xe1,(byte)0x05,(byte)0x7f,(byte)0x6a,(byte)0x1f,(byte)0x95,(byte)0xdb,(byte)0xfd,(byte)0x88,(byte)0xd2,(byte)0xc5,(byte)0xfd,(byte)0x49,(byte)0x00,(byte)0xd6,(byte)0x7f,(byte)0xdf,(byte)0x54,(byte)0xf4,(byte)0x82,(byte)0xb3,(byte)0xc2,(byte)0x4c,(byte)0x94,(byte)0x14,(byte)0xad,(byte)0x36,(byte)0xea,(byte)0x0e,(byte)0x23,(byte)0x81,(byte)0xd8,(byte)0x42,(byte)0x97,(byte)0x11,(byte)0x93,(byte)0xb5,(byte)0x3b,(byte)0x99,(byte)0xf0,(byte)0x29,(byte)0xeb,(byte)0x18,(byte)0xae,(byte)0x6d,(byte)0x42,(byte)0xf1,(byte)0x4a,(byte)0x3d,(byte)0x5e,(byte)0x65,(byte)0xe8,(byte)0x2d,(byte)0x3f,(byte)0x39,(byte)0x19,(byte)0x75,(byte)0x2a,(byte)0x8c,(byte)0xe2,(byte)0xde,(byte)0x47,(byte)0x07,(byte)0xea,(byte)0xe2,(byte)0x3b,(byte)0x1e,(byte)0x24,(byte)0xaf,(byte)0x44,(byte)0x8b,(byte)0xa0,(byte)0x82,(byte)0xd2,(byte)0x59,(byte)0x05,(byte)0x7b,(byte)0x14,(byte)0x8e,(byte)0xf8,(byte)0xad,(byte)0x49,(byte)0x84,(byte)0xad,(byte)0xcf,(byte)0x63,(byte)0x52,(byte)0xb3,(byte)0x2d,(byte)0xdf,(byte)0xe4,(byte)0x95,(byte)0x39,(byte)0x46,(byte)0x99,(byte)0x01,(byte)0x03,(byte)0xf5,(byte)0x95,(byte)0xb0,(byte)0xd8,(byte)0xc2,(byte)0xd7,(byte)0xc0,(byte)0xae,(byte)0xd0,(byte)0x3c,(byte)0x7d,(byte)0x69,(byte)0x12,(byte)0xab,(byte)0xe5,(byte)0xfa,(byte)0x51,(byte)0xb9,(byte)0x8c,(byte)0xfb,(byte)0x5d,(byte)0xc3,(byte)0xb4,(byte)0xac,(byte)0x6a,(byte)0xd3,(byte)0xf0,(byte)0xf1,(byte)0x8c,(byte)0xc1,(byte)0xab,(byte)0x55,(byte)0x20,(byte)0x75,(byte)0x08,(byte)0x45,(byte)0xf0,(byte)0x8e,(byte)0x95,(byte)0x62,(byte)0xde,(byte)0x2e,(byte)0xaa,(byte)0x8d,(byte)0x3c,(byte)0xc3,(byte)0xba,(byte)0x6f,(byte)0xac,(byte)0x5c,(byte)0xb9,(byte)0x04,(byte)0xca,(byte)0xf4,(byte)0x23,(byte)0xef,(byte)0x40,(byte)0x32,(byte)0xa8,(byte)0x49,(byte)0x7f,(byte)0xc6,(byte)0x40,(byte)0x20,(byte)0x05,(byte)0xd0,(byte)0xa0,(byte)0x8d,(byte)0xd3,(byte)0x9a,(byte)0xb0,(byte)0xae,(byte)0x61,(byte)0x57,(byte)0x62,(byte)0xcf,(byte)0x08,(byte)0x81,(byte)0xd5,(byte)0x98,(byte)0x82,(byte)0xf3,(byte)0xcc,(byte)0x3e,(byte)0xd4,(byte)0x37,(byte)0x86,(byte)0xdf,(byte)0xd0,(byte)0x0e,(byte)0x68,(byte)0x66,(byte)0xad,(byte)0x34,(byte)0x63,(byte)0x35,(byte)0x70,(byte)0x97,(byte)0xb3,(byte)0xf4,(byte)0x47,(byte)0x7d,(byte)0xec,};
////            c.init(Cipher.DECRYPT_MODE, keys.getPrivate());
////            byte[] data2 = c.doFinal(out1);
//////            c.init(Cipher.DECRYPT_MODE, keys.getPrivate());
//////            byte[] data3 = c.doFinal(out2);
////            String testD = new String(data2);
////            c.doFinal();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        byte[] packet = makeStartPacket(keys.getPublic());

        ConnectionManager cman = ConnectionManager.getInstance();
        cman.createPool(userInfo);

        Object monitor = new Object();
        ThreadPool threadPool = new ThreadPool(() -> establishSessionKey(packet, keys.getPrivate(), context), monitor, cman, null);
        threadPool.start();
        DataThread winner = (DataThread) threadPool.getWinner();

        cman.endSession();
        return winner == null ? null : winner.getConenction();
    }

    private byte[] makeStartPacket(PublicKey key) {
        byte[] data = key.getEncoded();
        String header = String.format("GRPACKET(%d);REQSK;DATA;", data.length);
        byte[] headerBytes = UnicodUtils.toBytes(header);
        byte[] out = new byte[data.length + headerBytes.length];

        System.arraycopy(headerBytes, 0, out, 0, headerBytes.length);
        System.arraycopy(data, 0, out, headerBytes.length, data.length);
        return out;
    }

    private KeyPair getKeyPair(Context context) {
        KeyPair ret = null;
        try {
            File dir = context.getFilesDir();
            byte[] prv = readFile(dir, PRV_KEY);
            if(prv == null) {
                ret = createKeyPair(context);
            } else {
                byte[] pub = readFile(dir, PUB_KEY);

                KeyFactory kf = KeyFactory.getInstance("RSA");
                PrivateKey prvKey = kf.generatePrivate(new PKCS8EncodedKeySpec(prv));
                PublicKey pubKey = kf.generatePublic(new X509EncodedKeySpec(pub));

                ret = new KeyPair(pubKey, prvKey);
            }
        } catch (Exception e) {

        }
        return ret;
    }

    private KeyPair createKeyPair(Context context) {
        KeyPair ret = null;
        try {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
            kpg.initialize(2048);
            ret = kpg.generateKeyPair();

            File dir = context.getFilesDir();

            FileOutputStream fos = new FileOutputStream(new File(dir, PRV_KEY));
            fos.write(ret.getPrivate().getEncoded());
            fos.close();

            fos = new FileOutputStream(new File(dir, PUB_KEY));
            fos.write(ret.getPublic().getEncoded());
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    byte[] readFile(File dir, String name) {
        byte[] res = null;
        File f = new File(dir, name);
        if(f.exists()) {
            try {
                res = new byte[(int) f.length()];
                FileInputStream fis = new FileInputStream(f);
                fis.read(res);
                fis.close();
            } catch (Exception e) {
                res = null;
            }
        }
        return  res;
    }

    Object establishSessionKey(byte[] packet, PrivateKey pk, Context context) {
        DataThread t = (DataThread) Thread.currentThread();
        EncodableConnection conn = (EncodableConnection) t.getConenction();
        return conn.requestKey(packet, pk, context);
    }
}
