package com.example.registerloginsp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MyFileViewActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "MyFileViewActivity";
    private MyService mService;
    private String my_key="pePPRBNwkcjwUdec";
    private String my_spec_key="z2d0tCfmrBvLQGdg";
    String filenamehave="";
    StringBuilder bitmap =null;

    StringBuilder bitmap1 =null;
    boolean set;
    TextView tt1;
    FloatingActionButton fab;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mytextviewer);
        Log.d(TAG, "onCreate: ThreadID::"+Thread.currentThread().getId());
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        CollapsingToolbarLayout toolBarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        toolBarLayout.setTitle(getTitle());

        set=false;

        Intent calling=getIntent();

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this);
        tt1=findViewById(R.id.filetextview);


        if(calling!=null){
            Uri fileuri=calling.getData();
            String name=(fileuri.getPath());
            filenamehave=name;
            name=name.substring(name.lastIndexOf('/')+1);

            if(fileuri!=null){

                /*myAsyncTask mytask=new myAsyncTask();
                mytask.execute(imageuri.toString());
                */
                try {
                    bitmap= GetFilefromPrivateStorage(fileuri,true);
                    bitmap1=GetFilefromPrivateStorage(fileuri,false);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                tt1.setText(bitmap);
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
    private StringBuilder GetFilefromPrivateStorage(Uri uri,boolean flag) throws FileNotFoundException {
        StringBuilder total = new StringBuilder();

        Log.d(TAG, "GetFilefromPrivateStorage: Thread"+Thread.currentThread().getId());
        Uri imurifinal=uri;
        InputStream is=null;
        File file = new File(imurifinal.getPath());
        try{
            is= new FileInputStream(file);

        } catch(Exception e) {
            e.printStackTrace();
        }

        try {
            InputStream inputStream=null;
            if(flag) {

                byte[] bytes = mService.decrypt(my_key, my_spec_key, is, file);
                inputStream = new ByteArrayInputStream(bytes);
            }
            else{
                inputStream=is;
            }

            if (inputStream != null) {
                    BufferedReader r = new BufferedReader(new InputStreamReader(inputStream));
                    String line;
                    while ((line = r.readLine()) != null) {
                        total.append(line);
                    }
                    r.close();
                    inputStream.close();
            }
            is.close();

        }
            catch (Exception e) {
            e.printStackTrace();
            }
        return total;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onClick(View v) {
        if (!set) {
            set=true;
            tt1.setText(bitmap1);
            fab.setForeground(ContextCompat.getDrawable(this, R.mipmap.textlockdecrypt));

        }
        else{
            set=false;
            tt1.setText(bitmap);
            fab.setForeground(ContextCompat.getDrawable(this, R.mipmap.textlockencrypt));

        }


    }
}
