package com.example.registerloginsp;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import static com.example.registerloginsp.App.CHANNEL_ID;

public class EncrptionsServices extends Service {

    private static final String TAG = "EncrptionsServices";


    private MyService mService;
    private Boolean mIsBound=false;

    private String my_key="pePPRBNwkcjwUdec";
    private String my_spec_key="z2d0tCfmrBvLQGdg";

    Notification notification;
    PendingIntent pendingintent;
    NotificationCompat.Builder builder;
    private int cn=0;

    int PROGRESS_MAX = 100;
    int PROGRESS_CURRENT = 0;

    int tot=0;

    private ServiceConnection mServiceCon=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder iBinder) {
            MyService.MyBinder mServiceBinder = (MyService.MyBinder) iBinder;/// check here
            mService = mServiceBinder.getService();
            mIsBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mIsBound = false;
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: Thread:"+Thread.currentThread().getId());
        Intent intent = new Intent(this, MyService.class);
        bindService(intent, mServiceCon, Context.BIND_AUTO_CREATE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        cn++;
        Intent notificationsintent=new Intent(this, MainActivity.class);
        Log.d(TAG, "onStartCommand: flag::"+flags+" startid::"+startId);
        pendingintent=PendingIntent.getActivity(this,0,notificationsintent,0);

        builder=new NotificationCompat.Builder(this,CHANNEL_ID)
                .setContentTitle("AES Encryption Is Running")
                .setContentText("starting .....")
                .setSmallIcon(R.mipmap.lockicon)
                .setContentIntent(pendingintent);


        builder.setProgress(PROGRESS_MAX, PROGRESS_CURRENT, false);


        notification=builder.build();
        startForeground(1,notification);

        ImgAsynckTask at=new ImgAsynckTask();
        at.execute(intent);
        return START_STICKY;
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onStop: Thread:"+ Thread.currentThread().getId());
        if(mIsBound && mService!=null)
        {
            unbindService(mServiceCon);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    private class ImgAsynckTask extends AsyncTask<Intent,Integer,String> {

        private void InsertInPrivateStorage(String... strings) {
            long startTime = System.nanoTime();

            Thread.currentThread().setPriority(Thread.MIN_PRIORITY);

            Log.d(TAG, "ImgAsynckTask:::::doInBackground: Thread:"+Thread.currentThread().getId());

            Log.d(TAG, "ImgAsynckTask:::::doInBackground: "+Thread.currentThread().getPriority());
            OutputStream fos = null;
            FileInputStream fileInputStream;
            Bitmap bitmap = null;
            InputStream is=null;
            Uri uri=Uri.parse(strings[0]);
            String name=strings[1];
            try{
                fileInputStream = (FileInputStream) getContentResolver().openInputStream(uri);
                bitmap = BitmapFactory.decodeStream(fileInputStream);

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                is = new ByteArrayInputStream(baos.toByteArray());

                fileInputStream.close();
                File directory =null;
                directory = getApplicationContext().getDir("myimage", Context.MODE_PRIVATE);
                Log.d(TAG, "ImgAsynckTask:::doInBackground: "+directory.toString());

                File mypath=new File(directory,File.separator+name);


                Log.d(TAG, "ImgAsynckTask::::doInBackground: "+mypath);

                fos = new FileOutputStream(mypath);
                Uri insert_in=Uri.fromFile(mypath);
                Log.d(TAG, "ImgAsynckTask:::::::doInBackground: "+insert_in);


                ContentValues value=new ContentValues();
                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
                Log.d(TAG, "ImgAsynckTask::::::::doInBackground: "+"Last Modified Date: " + sdf.format(mypath.lastModified()));

                value.put(TableItems.COL_2, String.valueOf(insert_in));
                value.put(TableItems.COL_3,sdf.format(mypath.lastModified()));

                Log.d(TAG, "ImgAsynckTask:::::::::doInBackground: STOREDIN:::::::::::::IMAGE TABLE");

                fos= mService.encrypt(my_key,my_spec_key,is,mypath);
                fos.close();
                getContentResolver().insert(ImgUrlContentProvider.IMG_CONTENT_URI, value);



            }catch (Exception e){
                e.printStackTrace();
            }

            long stopTime = System.nanoTime();
            long ans=(stopTime - startTime)/1000000;
            Log.d(TAG, "ImgAsynckTask:::::::::doInBackground: TIME TO COMPLETE  "+ans);

        }

        @Override
        protected String doInBackground(Intent... intents) {
            Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
            Log.d(TAG, "handleSendMultipleImages: Thread:"+Thread.currentThread().getId());
            Intent intent=intents[0];
            int count=0;
            ArrayList<Uri> imageUris = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
            tot=imageUris.size();
            if (imageUris != null) {
                for(Uri imgUri:imageUris){
                    String name=getname(imgUri);
                    Log.d(TAG, "handleSendMultipleImages: NAME::"+name);
                    InsertInPrivateStorage(imgUri.toString(),name);
                    Log.d(TAG, "handleSendMultipleImages: "+imgUri.getPath());
                    count++;
                    publishProgress(tot,count);
                }

            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {

            int dis=100*(values[1])/values[0];
            if(dis==PROGRESS_MAX){
                builder.setContentText("Encryption complete")
                        .setProgress(0,0,false);
            }
            else {
                builder.setContentText(dis+"%");
                builder.setProgress(PROGRESS_MAX, dis, false);
            }
            notification=builder.build();

            startForeground(1,notification);
        }

        private String getname(Uri imgUri) {
            String res=null;
            if(imgUri.getScheme().equals("content")){
                res=imgUri.getPath();
                int id=res.lastIndexOf('/');
                if(id!=-1)res=res.substring(id+1);
                return res;
            }
            return res;
        }

        @Override
        protected void onPostExecute(String s) {
            Log.d(TAG, "ImgAsynckTask::::::::onPostExecute: Thread:"+Thread.currentThread().getId());
             Log.d(TAG, "ImgAsynckTask:::::::::::onPostExecute: "+s);
            Log.d(TAG, "ImgAsynckTask:::::::::onPostExecute: FILE:::::::::::::::::::::::::::::::::");
            cn--;
            if(cn==0)
                stopSelf();
        }
    }
}
