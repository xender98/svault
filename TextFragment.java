package com.example.registerloginsp;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class TextFragment extends Fragment {
    private static final String TAG = "TextFragment";
    View v;
    Activity act;

    private RecyclerView rv;
    private RecyclerView.LayoutManager rv_manager;
    private TextViewAdapter adapter;
    private final static int SELECT_TEXT=1;

    Display myd;

    public TextFragment(Display display){
        adapter=null;
        myd=display;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v=inflater.inflate(R.layout.text_view,container,false);
        act=getActivity();
        Log.d(TAG, "onCreate: Thread:"+Thread.currentThread().getId());
        rv = v.findViewById(R.id.recycletextview);
        rv_manager = new GridLayoutManager(getContext(), 2);
        rv.setHasFixedSize(true);
        rv.setLayoutManager(rv_manager);
        adapter = new TextViewAdapter(act);
        rv.setAdapter(adapter);
        FloatingActionButton fabtxt=v.findViewById(R.id.textfloatingActionButton);
        fabtxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent imgget=new Intent();
                imgget.setType("text/*");
                imgget.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(imgget,"Select Picture"),SELECT_TEXT);
            }
        });
        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Log.d(TAG, "onActivityResult: ");
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == act.RESULT_OK ) {
            if(requestCode==SELECT_TEXT){
                Uri imgdataget=data.getData();
                Intent send1=new Intent();
                send1.putExtra(Intent.EXTRA_STREAM,imgdataget);

                Toast.makeText(act,imgdataget.toString(),Toast.LENGTH_LONG).show();
                Log.d(TAG, "onActivityResult: ::::::"+imgdataget.getPath());
                myd.handleSendText(send1);
            }
        }
    }
    public TextViewAdapter getAdapter(){
        return adapter;
    }
}

