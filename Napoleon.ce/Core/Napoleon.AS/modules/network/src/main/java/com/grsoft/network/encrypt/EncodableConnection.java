package com.grsoft.network.encrypt;

import android.content.Context;

import com.grsoft.dataobjects.DataObjectPool;
import com.grsoft.network.ByteStream;
import com.grsoft.network.SocketConnection;
import com.grsoft.network.exception.RuntimeException;

import java.security.PrivateKey;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class EncodableConnection extends SocketConnection {
    public static final String AES_PRV = "AES/CBC/PKCS5Padding";
    Cipher encrypt = null;
    Cipher decrypt = null;

    public EncodableConnection(String address, int port) {
        super(address, port);
    }

    @Override
    public void send(DataObjectPool pool, String tag, Context context) throws RuntimeException {
        byte[] streamData = pool.toStreamData();
        ByteStream byteStream = new ByteStream(streamData, context);
        byteStream.send(getOutputStream(), tag, encrypt);
        byteStream.close();
    }

    @Override
    public ByteStream receive(Context context) throws RuntimeException {
        received = ByteStream.receive(getInputStream(), context, decrypt);
        return  received;
    }

    public boolean requestKey(byte[] packet, PrivateKey pk, Context context) {
        if(!connect()) {
            return false;
        }

        boolean ret = false;
        try {
            getOutputStream().write(packet);
            ByteStream bs = ByteStream.receive(getInputStream(), context);
            if(bs != null) {
                byte[] data = bs.getData();

                Cipher cipher = Cipher.getInstance(Encryptor.RSA_PRV);
                cipher.init(Cipher.DECRYPT_MODE, pk);
                byte[] sessData = cipher.doFinal(data);

                if(sessData != null && sessData.length == 48) {
                    byte[] keyData = Arrays.copyOfRange(sessData, 0, 32);
                    byte[] ivData = Arrays.copyOfRange(sessData, 32, 48);

                    SecretKeySpec key = new SecretKeySpec(keyData, "AES");
                    IvParameterSpec iv = new IvParameterSpec(ivData);
                    encrypt = Cipher.getInstance(AES_PRV);
                    encrypt.init(Cipher.ENCRYPT_MODE, key, iv);

                    decrypt = Cipher.getInstance(AES_PRV);
                    decrypt.init(Cipher.DECRYPT_MODE, key, iv);
                    ret = true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }
}
