package com.example.registerloginsp;

import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

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


public class ImgFragment extends Fragment {
    private static final String TAG = "ImgFragment";
    View v;
    Activity act;

    private RecyclerView rv;
    private RecyclerView.LayoutManager rv_manager;
    private RecycleViewAdapter adapter;

    private final static int SELECT_PICTURE=1;

    private Display myd;


    public ImgFragment(Display display)

    {
        this.myd=display;
        adapter=null;


    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: ");
        v=inflater.inflate(R.layout.imgview,container,false);
        act=getActivity();
        Log.d(TAG, "onCreate: Thread:"+Thread.currentThread().getId());
        rv = v.findViewById(R.id.recycleimgview);
        rv_manager = new GridLayoutManager(getContext(), 3);
        rv.setHasFixedSize(true);
        rv.setLayoutManager(rv_manager);
        adapter = new RecycleViewAdapter(act);
        rv.setAdapter(adapter);
        FloatingActionButton fabimg=v.findViewById(R.id.floatingActionButton);
        fabimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent imgget=new Intent();
                imgget.setType("image/*");
                imgget.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(imgget,"Select Picture"),SELECT_PICTURE);
            }
        });

        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == act.RESULT_OK ) {
            if(requestCode==SELECT_PICTURE){
                Uri imgdataget=data.getData();
                Intent send1=new Intent();
                send1.putExtra(Intent.EXTRA_STREAM,imgdataget);
                Toast.makeText(act,imgdataget.toString(),Toast.LENGTH_LONG).show();
                Log.d(TAG, "onActivityResult: ::::::"+imgdataget.getPath());
                myd.handleSendImage(send1);

            }
        }
    }

    public RecycleViewAdapter getAdapter(){
        return adapter;
    }


}
