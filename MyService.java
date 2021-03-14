package com.example.registerloginsp;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class MyService extends Service {
    private static final String TAG = "MyService";
    private static final String ALGO_SECRET_KEY = "AES";
    String data;
    private final static int DEFAULT_READ_WRITE_BLOCK_BUFFER_SIZE = 1024;
    private final static String ALGO_VIDEO_ENCRYPTOR = "AES/CBC/PKCS5Padding";

    private final IBinder mBinder=new MyBinder();
    private final Random mGenerator=new Random();

    public class MyBinder extends Binder{
        MyService getService(){
            return MyService.this;
        }
    }

    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: Thread"+Thread.currentThread().getId());
        data=(String) intent.getExtras().get("data");
        Log.d(TAG, "onStartCommand: "+data);
        return flags;
    }



    public static OutputStream encrypt(String keyStr, String specStr, InputStream in, File path) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IOException {
        OutputStream out=new FileOutputStream(path);
        try {
            Log.d(TAG, "encrypt: Thread"+Thread.currentThread().getId());
            SecretKeySpec keySpec = new SecretKeySpec(keyStr.getBytes(),ALGO_SECRET_KEY);
            IvParameterSpec paramSpec= new IvParameterSpec(specStr.getBytes());

            Cipher c = Cipher.getInstance(ALGO_VIDEO_ENCRYPTOR);
            c.init(Cipher.ENCRYPT_MODE, keySpec, paramSpec);
            out = new CipherOutputStream(out, c);

            int count = 0;
            byte[] buffer = new byte[DEFAULT_READ_WRITE_BLOCK_BUFFER_SIZE];

            while ((count = in.read(buffer)) >= 0) {
                out.write(buffer, 0, count);
            }

        } finally {
            return out;
            //out.close();
        }

    }

    public static byte[] decrypt(String keyStr,String specStr, InputStream in,File path) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IOException {
        InputStream out=new FileInputStream(path);
        try {
            Log.d(TAG, "decrypt: Thread"+Thread.currentThread().getId());
            SecretKeySpec keySpec = new SecretKeySpec(keyStr.getBytes(),ALGO_SECRET_KEY);
            IvParameterSpec paramSpec= new IvParameterSpec(specStr.getBytes());

            Cipher c = Cipher.getInstance(ALGO_VIDEO_ENCRYPTOR);
            c.init(Cipher.DECRYPT_MODE, keySpec, paramSpec);
            out = new CipherInputStream(out,c);

            int count = 0;
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            int nRead;
            byte[] data = new byte[16384];

            while ((nRead = out.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }

            return buffer.toByteArray();

        }finally {

        }

    }


}
