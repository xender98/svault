package com.example.registerloginsp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class MyImgViewActivity extends AppCompatActivity {
    private MyService mService;
    private String my_key="pePPRBNwkcjwUdec";
    private String my_spec_key="z2d0tCfmrBvLQGdg";
    String filenamehave;
    /*private  Bitmap bitmap;
     */
    private static final String TAG = "MyImgViewActivity";
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.myimgviewer);
        Log.d(TAG, "onCreate: ThreadID::"+Thread.currentThread().getId());
        ImageView fullimg=findViewById(R.id.showimg);
        TextView tt=findViewById(R.id.txtview);
        ProgressBar pb1=findViewById(R.id.progressBar2);
        Intent calling=getIntent();
        if(calling!=null){
            Uri imageuri=calling.getData();
            String Thumbimag=imageuri.getPath();
            String name=imageuri.getPath();
            String fname=name.substring(name.lastIndexOf("/"));
            name=name.substring(0,name.lastIndexOf("/"));
            Log.d(TAG, "onCreate: "+name);

            name=name.substring(0,name.lastIndexOf("/"));
            Log.d(TAG, "onCreate: "+name);

            name=name+File.separator+"app_myimage"+fname;
            filenamehave=name;
            Log.d(TAG, "onCreate: IMPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPP"+imageuri.getPath());
            Log.d(TAG, "onCreate: "+imageuri);
            Log.d(TAG, "onCreate: "+name);

            if(imageuri!=null&&fullimg!=null){
                Bitmap[] bitmap = {null};

                /*myAsyncTask mytask=new myAsyncTask();
                mytask.execute(imageuri.toString());
                */
                try {
                    bitmap[0] = GetImgfromPrivateStorage(name);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                if(bitmap[0] !=null) {
                    fullimg.setImageBitmap(bitmap[0]);

                    pb1.setVisibility(View.INVISIBLE);
                }
                else{
                    Handler handler = new Handler(Looper.getMainLooper());

                    boolean[] encrypterdfound = {false};
                    pb1.setVisibility(View.VISIBLE);
                    fullimg.setImageBitmap(bitmap[0]);

                    String finalName = name;
                    Bitmap[] mbit = {bitmap[0]};
                    new Thread(new Runnable() {

                        @Override
                        public void run() {
                            try{
                                while (!encrypterdfound[0]) {
                                    mbit[0] = GetImgfromPrivateStorage(finalName);
                                    try {
                                        Thread.sleep(1000);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    handler.post(new Runnable() {
                                        public void run() {
                                            if(mbit[0]!=null) {
                                                pb1.setVisibility(View.INVISIBLE);
                                                encrypterdfound[0] =true;
                                                fullimg.setImageBitmap(mbit[0]);

                                            }

                                        }
                                    });
                                }
                            }catch (Exception e){
                                e.printStackTrace();
                            }

                        }
                    }).start();

                }
                tt.setText(fname.substring(1));
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.imgviewmenu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.firstitem:
                Toast.makeText(this,filenamehave,Toast.LENGTH_LONG).show();
        }
        return false;
    }

    private Bitmap GetImgfromPrivateStorage(String path) throws FileNotFoundException {
        Log.d(TAG, "GetImgfromPrivateStorage: Thread"+Thread.currentThread().getId());
        FileInputStream fileInputStream=null;
        InputStream is=null;
        Bitmap bitmap = null;
        File file = new File(path);
        try{
            is= new FileInputStream(file);
        } catch(Exception e) {
            e.printStackTrace();
        }

        try {

            byte[] bytes=mService.decrypt(my_key,my_spec_key,is,file);
            bitmap = BitmapFactory.decodeByteArray(bytes, 0,bytes.length);


        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

}

