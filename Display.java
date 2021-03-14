package com.example.registerloginsp;

import android.Manifest;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class Display extends AppCompatActivity implements android.app.LoaderManager.LoaderCallbacks<Cursor>,RecycleViewAdapter.OnClickListener, TextViewAdapter.OnClickListener, TabLayout.OnTabSelectedListener {
    private static final String TAG = "Display";
    private ProgressBar progressBar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private PagerAdapter pageadapter;
    boolean onTime=false;

    private final static int READ_EXTERNAL_STORAGE_PERMISSION_RESULT=0;

    private final static int TXTSTORE_LOADER_ID=0;
    private final static int THUMB_IMGSTORE_LOADER_ID=1;

    private int THUMB_WIDTH=128;
    private int THUMB_HEIGHT=128;


    private RecycleViewAdapter adapter;
    private TextViewAdapter Textadapter;

    private MyService mService;
    private Boolean mIsBound=false;

    private String my_key="pePPRBNwkcjwUdec";
    private String my_spec_key="z2d0tCfmrBvLQGdg";
     private ArrayList<Uri>dataUri;

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


    @RequiresApi(api = Build.VERSION_CODES.M)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash2);
        getSupportActionBar().hide();


        onTime=false;
        progressBar=findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        checkPermission();
        dataUri=null;

        Intent intent = getIntent().getParcelableExtra(Intent.EXTRA_INTENT);
        if(intent==null){
            Log.d(TAG, "onCreate: NOTICED::::::::::::::::::::::");
        }
        if(intent!=null){
            String action = intent.getAction();
            String type = intent.getType();
            Log.d(TAG, "onCreate: actions::"+action);
            Log.d(TAG, "onCreate: type::"+type);
            if (Intent.ACTION_SEND.equals(action) && type != null) {
                if (type.equals("text/plain")||type.equals("application/txt")) {
                    Log.d(TAG, "onCreate: "+"SINGLE TEXT FILE");

                    handleSendText(intent);                                                                             // Handle text being sent
                } else if (type.startsWith("image/")) {
                    Log.d(TAG, "onCreate: "+"SINGLE IMG FILE");
                    handleSendImage(intent);                                                                            // Handle single image being sent
                }
            }
            else if (Intent.ACTION_SEND_MULTIPLE.equals(action) && type != null) {
                if (type.startsWith("image/")) {
                    handleSendMultipleImages(intent);                                                                   // Handle multiple images being sent
                }
            } else {
                Log.d(TAG, "onCreate: :::::::::::::::::::::::::::::::::::::::reac");
                afterLoadingThumbnail();
                // Handle other intents, such as being started from the home screen
            }
        }
        else {
            Log.d(TAG, "onCreate: :::::::::::::::::::::::::::::::::::::::reac");
            afterLoadingThumbnail();
        }

    }
    void afterLoadingThumbnail(){
        if(!onTime) {
            onTime=true;
            setContentView(R.layout.displayinfo);
            getSupportActionBar().show();

            tabLayout = (TabLayout) findViewById(R.id.tabbar);
            viewPager = findViewById(R.id.viewpager);

            pageadapter = new PagerAdapter(getSupportFragmentManager());
            pageadapter.addFragment(new ImgFragment(this), "Image");
            pageadapter.addFragment(new TextFragment(this), "File");


            viewPager.setAdapter(pageadapter);
            tabLayout.setupWithViewPager(viewPager);

            Textadapter = ((TextFragment) pageadapter.getItem(1)).getAdapter();
            adapter = ((ImgFragment) pageadapter.getItem(0)).getAdapter();
            tabLayout.setOnTabSelectedListener(this);
            Log.d(TAG, "onCreate: Thread:" + Thread.currentThread().getId());
        }
        restartloader();

        Handler handler = new Handler();


        Thread Refresh=new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    try {

                        Thread.sleep(3000);

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    handler.post(new Runnable() {
                        public void run() {
                            restartloader();
                        }
                    });
                }
            }
        });
        Refresh.setPriority(Thread.MIN_PRIORITY);
        Refresh.start();

        if(dataUri!=null)
             startMyService(dataUri);
    }



    @Override
    public void onBackPressed() {
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
        //  super.onBackPressed();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: Thread:"+Thread.currentThread().getId());
        Intent intent = new Intent(this, MyService.class);
        bindService(intent, mServiceCon, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: Thread:"+ Thread.currentThread().getId());
        if(mIsBound && mService!=null)
        {
            unbindService(mServiceCon);
        }
    }


    public void startMyService(ArrayList<Uri>data){
        Log.d(TAG, "::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::startService: ");
        Intent serviceintent=new Intent(this,EncrptionsServices.class);
        if(data!=null) {
            serviceintent.putParcelableArrayListExtra(Intent.EXTRA_STREAM,data);
            startService(serviceintent);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case READ_EXTERNAL_STORAGE_PERMISSION_RESULT:
                if(grantResults==null){
                    Toast.makeText(this,"YOu can not give permissions please give permissions to use app",Toast.LENGTH_LONG).show();
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},READ_EXTERNAL_STORAGE_PERMISSION_RESULT);
                }
                else if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
                }
                break;
            default:
                Toast.makeText(this,"YOu can not give permissions please give permissions to use app",Toast.LENGTH_LONG).show();
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},READ_EXTERNAL_STORAGE_PERMISSION_RESULT);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void checkPermission(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED){

        }
        else{
            if(shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)){
                Toast.makeText(this,"App needs this permission",Toast.LENGTH_SHORT).show();
            }
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},READ_EXTERNAL_STORAGE_PERMISSION_RESULT);
        }
    }

    void handleSendText(Intent intent) {
        Log.d(TAG, "handleSendText: "+Thread.currentThread().getId());
        Uri textUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (textUri != null){
            String name=getFileName(textUri);
            Log.d(TAG, "handleSendImages: NAME::"+name);
            try {
                FileInsertInPrivateStorage(textUri,name);
            }catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            Log.d(TAG, "handleSendImage: "+textUri.getPath());
        }
    }


    void handleSendImage(Intent intent) {
        Log.d(TAG, "handleSendImage: Thread::" + Thread.currentThread().getId());
        Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
        Log.d(TAG, "handleSendImage: " + imageUri);
        ArrayList<Uri> uris = new ArrayList<>();
        uris.add(imageUri);

        if (uris != null) {
            {
                try {
                    InsertInPrivateStorage(uris);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                Log.d(TAG, "handleSendImage: " + imageUri.getPath());
            }
        }
    }


    void handleSendMultipleImages(Intent intent) {
        Log.d(TAG, "handleSendMultipleImages: Thread:"+Thread.currentThread().getId());
        ArrayList<Uri> imageUris = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
        try {
            InsertInPrivateStorage(imageUris);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }






    private void InsertInPrivateStorage(ArrayList<Uri> uris) throws FileNotFoundException {
        Handler handler = new Handler(Looper.getMainLooper());
        Handler handler1 = new Handler(Looper.getMainLooper());

        new Thread(new Runnable() {
            private static final String TAG = "senIMgDisplay";
            ArrayList<Uri> imageUris = uris;
            int count=0;
            int tot=imageUris.size();

            public void run() {
                if (imageUris != null) {
                    for(Uri imgUri:imageUris){
                        String name=getFileName(imgUri);
                        Log.d(TAG, "handleSendMultipleImages: NAME::"+name);
                        Thumbloader(imgUri,name);
                        Log.d(TAG, "handleSendMultipleImages: "+imgUri.getPath());
                        count++;
                        handler.post(new Runnable() {
                            public void run() {
                                progressBar.setProgress(count);

                            }
                        });

                    }
                    handler1.post(new Runnable() {
                        public void run() {
                            dataUri=imageUris;
                            afterLoadingThumbnail();

                        }
                    });

                }
            }

        }).start();

    }

    private void Thumbloader(Uri uri,String name){
        long startTime = System.nanoTime();
        Log.d(TAG, "doInBackground: Thread:"+Thread.currentThread().getId());
        Log.d(TAG, "doInBackground: "+Thread.currentThread().getPriority());


        OutputStream fos = null;
        FileInputStream fileInputStream=null;
        InputStream is=null;

        try {
            fileInputStream = (FileInputStream) getContentResolver().openInputStream(uri);

            BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
            bitmapOptions.inJustDecodeBounds = true; // obtain the size of the image, without loading it in memory
            BitmapFactory.decodeStream(fileInputStream,null,bitmapOptions);
            fileInputStream.close();
            float widthScale = (float)bitmapOptions.outWidth/THUMB_WIDTH;
            float heightScale = (float)bitmapOptions.outHeight/THUMB_HEIGHT;
            float scale = Math.min(widthScale, heightScale);

            int sampleSize = 1;
            while (sampleSize < scale) {
                sampleSize *= 2;
            }
            bitmapOptions.inSampleSize = sampleSize; // this value must be a power of 2,
            // this is why you can not have an image scaled as you would like
            bitmapOptions.inJustDecodeBounds = false; // now we want to load the image
            fileInputStream = (FileInputStream) getContentResolver().openInputStream(uri);

            // Let's load just the part of the image necessary for creating the thumbnail, not the whole image
            //  Bitmap thumbnail = BitmapFactory.decodeFile(file.getAbsolutePath(), bitmapOptions);

            Bitmap bitmap1 = BitmapFactory.decodeStream(fileInputStream,null,bitmapOptions);

            // Bitmap bitmap1 = ThumbnailUtils.extractThumbnail(bitmap,THUMB_WIDTH,THUMB_HEIGHT);
            InputStream is1=null;

            ByteArrayOutputStream baos1 = new ByteArrayOutputStream();
            bitmap1.compress(Bitmap.CompressFormat.PNG, 100, baos1);
            is1 = new ByteArrayInputStream(baos1.toByteArray());

            fileInputStream.close();


            File directory1 =null;
            directory1 = getApplicationContext().getDir("mythumb", Context.MODE_PRIVATE);
            Log.d(TAG, "doInBackground: "+directory1.toString());

            File mypath1=new File(directory1,File.separator+name);



            OutputStream fos1 = new FileOutputStream(mypath1);
            Uri insert_in1=Uri.fromFile(mypath1);

            Log.d(TAG, "doInBackground: "+insert_in1);
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

            ContentValues value1=new ContentValues();
            Log.d(TAG, "doInBackground: "+"Last Modified Date: " + sdf.format(mypath1.lastModified()));

            value1.put(TableItems.COL_2, String.valueOf(insert_in1));
            value1.put(TableItems.COL_3,sdf.format(mypath1.lastModified()));

            Log.d(TAG, "doInBackground: STOREDIN:::::::::::::TUMB IMAGE TABLE");
            getContentResolver().insert(ImgUrlContentProvider.THUMB_IMG_CONTENT_URI, value1);

            fos1= mService.encrypt(my_key,my_spec_key,is1,mypath1);
            fos1.close();
            //     adapter.notifyDataSetChanged();

        } catch (Exception e) {
            e.printStackTrace();
        }

        long stopTime = System.nanoTime();
        long ans=(stopTime - startTime)/1000000;

        Log.d(TAG, "mylable : TIME TO COMPLETE "+ans);
    }


    private void FileInsertInPrivateStorage(Uri uris, String name) throws FileNotFoundException {
        Handler handler = new Handler(Looper.getMainLooper());
        Handler handler1 = new Handler(Looper.getMainLooper());

        new Thread(new Runnable() {
            private static final String TAG = "senIMgDisplay";
            int count=0;
            int tot=1;

            public void run() {
                if (uris != null) {
                        String name=getFileName(uris);
                        fileInsert(uris,name);
                        Log.d(TAG, "handleSendMultipleImages: "+uris.getPath());
                        count++;
                        handler.post(new Runnable() {
                            public void run() {
                                progressBar.setProgress(count);

                            }
                        });

                    }
                handler1.post(new Runnable() {
                    public void run() {
                        dataUri=null;
                        afterLoadingThumbnail();

                    }
                });

            }

        }).start();

    }
    void fileInsert(Uri uri,String name){
        Thread.currentThread().setPriority(Thread.MAX_PRIORITY);


        long startTime = System.nanoTime();
        Log.d(TAG, "doInBackground: Thread:"+Thread.currentThread().getId());
        Log.d(TAG, "doInBackground: "+Thread.currentThread().getPriority());


        OutputStream fos = null;
        FileInputStream fileInputStream=null;
        InputStream is=null;

        try {
            fileInputStream = (FileInputStream) getContentResolver().openInputStream(uri);
            is=fileInputStream;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        File txtdirectory =null;
        txtdirectory=getApplicationContext().getDir("myfile", Context.MODE_PRIVATE);
        Log.d(TAG, "doInBackground: "+txtdirectory.toString());

        File txtmypath=new File(txtdirectory,File.separator+name);

        Log.d(TAG, "doInBackground: "+txtmypath);
        try {
            fos = new FileOutputStream(txtmypath);
            Uri txtinsert_in = Uri.fromFile(txtmypath);
            Log.d(TAG, "doInBackground: " + txtinsert_in);

            ContentValues txtvalue = new ContentValues();
            SimpleDateFormat txtsdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
            Log.d(TAG, "doInBackground: " + "Last Modified Date: " + txtsdf.format(txtmypath.lastModified()));

            txtvalue.put(TableItems.COL_2, String.valueOf(txtinsert_in));
            txtvalue.put(TableItems.COL_3, txtsdf.format(txtmypath.lastModified()));

            Log.d(TAG, "doInBackground: STOREDIN:::::::::::::TEXT TABLE");
            getContentResolver().insert(ImgUrlContentProvider.TXT_CONTENT_URI, txtvalue);

            fos = mService.encrypt(my_key, my_spec_key, is, txtmypath);
            fos.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        if(id==THUMB_IMGSTORE_LOADER_ID) {

            Log.d(TAG, "onCreateLoader: ID:::"+id);

            Log.d(TAG, "CreateLoader: 1:Thread:" + Thread.currentThread().getId());
            String[] projections = {TableItems._ID, TableItems.COL_2, TableItems.COL_3};
            Log.d(TAG, "onCreateLoader: 2");
            return new CursorLoader(this, ImgUrlContentProvider.THUMB_IMG_CONTENT_URI, null, null, null, TableItems.COL_3+" DESC");
        }
        else{
            Log.d(TAG, "onCreateLoader: ID:::"+id);
            Log.d(TAG, "CreateLoader: 1:Thread:" + Thread.currentThread().getId());
            String[] projections = {TableItems._ID, TableItems.COL_2, TableItems.COL_3};
            Log.d(TAG, "onCreateLoader: 2");
            return new CursorLoader(this, ImgUrlContentProvider.TXT_CONTENT_URI, null, null, null, TableItems.COL_3+" DESC");

        }
    }

    @Override
    public void onLoadFinished(android.content.Loader<Cursor> loader, Cursor data) {
        int id=loader.getId();
        Log.d(TAG, "onLoadFinished: ID::"+id);
        if(id==THUMB_IMGSTORE_LOADER_ID) {
            Log.d(TAG, "LoadFinished: 1:Thread:" + Thread.currentThread().getId());
            if (data != null) {
                Log.d(TAG, "onLoadFinished: IMG_TABLE_ITEM::::::::::::::::" + data.getCount());
            } else {
                Log.d(TAG, "onLoadFinished: IMG_TABLE_ITEM:::::::::::::::::no");
            }
            ImgFragment imgFrag = (ImgFragment) pageadapter.getItem(0);

            adapter = imgFrag.getAdapter();

            if (adapter != null)
                adapter.changeCursor(data);
        }

        else{
            Log.d(TAG, "LoadFinished: 1:Thread:" + Thread.currentThread().getId());
            if (data != null) {
                Log.d(TAG, "onLoadFinished: TXT_TABLE_ITEM:::::::::::::::::::" + data.getCount());
            } else {
                Log.d(TAG, "onLoadFinished: TXT_TABLE_ITEM:::::::::::::::::::no");
            }
            TextFragment txtFrag = (TextFragment) pageadapter.getItem(1);

            Textadapter = txtFrag.getAdapter();

            if (Textadapter != null)
                Textadapter.changeCursor(data);

        }
    }

    @Override
    public void onLoaderReset(android.content.Loader<Cursor> loader) {
        int id=loader.getId();
        Log.d(TAG, "onLoadReset: ID::"+id);
        if(id==THUMB_IMGSTORE_LOADER_ID) {
            Log.d(TAG, "onLoaderReset: :Thread:" + Thread.currentThread().getId());
            ImgFragment imgFrag = (ImgFragment) pageadapter.getItem(0);

            adapter = imgFrag.getAdapter();
            if (adapter != null)
                adapter.changeCursor(null);
        }
        else{
            Log.d(TAG, "onLoaderReset: :Thread:" + Thread.currentThread().getId());
            TextFragment txtFrag = (TextFragment) pageadapter.getItem(1);

            Textadapter = txtFrag.getAdapter();
            if (Textadapter != null)
                Textadapter.changeCursor(null);

        }
    }





    @Override
    public void OnClickImage(Uri imageuri) {
        Log.d(TAG, "OnClickImage: Thread:"+Thread.currentThread().getId());
        // Toast.makeText(MainActivity.this,"Imge URI::::"+imageuri.toString(), Toast.LENGTH_SHORT).show();
        Intent sendintent=new Intent(this,MyImgViewActivity.class);
        sendintent.setData(imageuri);
        startActivity(sendintent);

    }

    @Override
    public void OnClickText(Uri txturi) {
        Log.d(TAG, "OnClickText: Thread:"+Thread.currentThread().getId());
        // Toast.makeText(MainActivity.this,"Imge URI::::"+imageuri.toString(), Toast.LENGTH_SHORT).show();
        Intent sendintent=new Intent(this,MyFileViewActivity.class);
        sendintent.setData(txturi);
        startActivity(sendintent);
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        if(tab.getPosition()==0)
            getLoaderManager().restartLoader(THUMB_IMGSTORE_LOADER_ID, null, this);
        else
            getLoaderManager().restartLoader(TXTSTORE_LOADER_ID, null, this);
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    public void restartloader(){
        Log.d(TAG, "restartloader: "+Thread.currentThread().getId());
        Log.d(TAG, "restartloader: "+Thread.currentThread().getPriority());
        getLoaderManager().restartLoader(THUMB_IMGSTORE_LOADER_ID, null, this);
        getLoaderManager().restartLoader(TXTSTORE_LOADER_ID, null, this);

    }
    private String getFileName(Uri imgUri) {
        String res=null;
        if(imgUri.getScheme().equals("content")){
            res=imgUri.getPath();
            int id=res.lastIndexOf('/');
            if(id!=-1)res=res.substring(id+1);
            return res;
        }
        return res;
    }

}
