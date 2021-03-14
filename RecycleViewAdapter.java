package com.example.registerloginsp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class RecycleViewAdapter extends RecyclerView.Adapter<RecycleViewAdapter.ViewHolder> {
    private final Activity activity;
    private static final String TAG = "RecycleViewAdapter";

    private Cursor cursor;
    private OnClickListener myOnClickListener;
    private MyService mService;

    private String my_key = "pePPRBNwkcjwUdec";
    private String my_spec_key = "z2d0tCfmrBvLQGdg";
    /* private Bitmap bitmap=null;*/

    public interface OnClickListener {
        void OnClickImage(Uri imageuri);
    }

    public RecycleViewAdapter(Activity activity) {
        Log.d(TAG, "RecycleViewAdapter: Thread:"+Thread.currentThread().getId());
        this.activity = activity;
        mService = new MyService();
        this.myOnClickListener = (OnClickListener) activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: 1:Thread"+Thread.currentThread().getId());
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.imglist, parent, false);
        ViewHolder v = new ViewHolder(view);
        Log.d(TAG, "onCreateViewHolder: 2");
        return v;
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: 1::" + position+"::Thread::"+Thread.currentThread().getId());
        Bitmap bitmap=null;
        String name="";
        Log.d(TAG, "onBindViewHolder: 2");
        try{
            bitmap=GetImgfromPrivateStorage(position);
            name=GetImgNamefromPrivateStorage(position);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "onBindViewHolder: 3::" + position);
        holder.im.setImageBitmap(bitmap);
        Log.d(TAG, "onBindViewHolder: 4::");
        holder.tt.setText(name);
        try {
            if(foundOriginalImg(position)){
                holder.pb.setVisibility(View.INVISIBLE);
            }
        } catch (FileNotFoundException e) {
            holder.pb.setVisibility(View.VISIBLE);

            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return cursor == null ? 0 : cursor.getCount();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tt;
        ImageView im;
        ProgressBar pb;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            Log.d(TAG, "ViewHolder: Thread::"+Thread.currentThread().getId());
            tt = itemView.findViewById(R.id.textalbumtitel);
            im = itemView.findViewById(R.id.textalbumimg);
            pb=itemView.findViewById(R.id.imgprogressbar);
            pb.setVisibility(View.VISIBLE);
            im.setOnClickListener(this);
        }
        @Override
        public void onClick(View v) {
            getOnClickUri(getAdapterPosition());
        }
    }

    private Cursor swapCursor(Cursor mcursor) {
        Log.d(TAG, "swapCursor: Thread:"+Thread.currentThread().getId());
        if (cursor == mcursor) {
            Log.d(TAG, "swapCursor: SameCursor");
            return null;
        } else {
            Cursor old = cursor;
            cursor = mcursor;
            if (mcursor != null) {
                Log.d(TAG, "swapCursor: old is not null change happen");
                this.notifyDataSetChanged();
            }
            return old;
        }
    }

    public void changeCursor(Cursor mcursor) {
        Log.d(TAG, "changeCursor: Thread::"+Thread.currentThread().getId());
        Cursor old = swapCursor(mcursor);
        if (old != null) {
            Log.d(TAG, "changeCursor: 1");
            old.close();
        }
    }

    private void getOnClickUri(int position) {
        Log.d(TAG, "getOnClickUri: THread::"+Thread.currentThread().getId());
        cursor.moveToPosition(position);
        int id = cursor.getColumnIndex(TableItems.COL_2);
        String imuri = cursor.getString(id);
        myOnClickListener.OnClickImage(Uri.parse(imuri));
    }

    private Bitmap GetImgfromPrivateStorage(int position) throws FileNotFoundException {
        Log.d(TAG, "GetImgfromPrivateStorage: Thread::"+Thread.currentThread().getId());
        cursor.moveToPosition(position);
        Log.d(TAG, "GetImgfromPrivateStorage: position::"+position);
        int id = cursor.getColumnIndex(TableItems.COL_2);

        Log.d(TAG, "GetImgfromPrivateStorage: id::"+id);
        String imuri = cursor.getString(id);
        Uri imurifinal = Uri.parse(imuri);
        Log.d(TAG, "GetImgfromPrivateStorage:  imuri::"+ imuri + "  ::id::" + id);


        FileInputStream fileInputStream = null;
        InputStream is = null;
        Bitmap bitmap = null;
        File file = new File(imurifinal.getPath());
        is = new FileInputStream(file);
        Log.d(TAG, "GetImgfromPrivateStorage: "+file.toString());

        try {
            byte[] bytes = mService.decrypt(my_key, my_spec_key, is, file);
            bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    private String GetImgNamefromPrivateStorage(int position) throws FileNotFoundException {
        Log.d(TAG, "GetImgfromPrivateStorage: Thread::"+Thread.currentThread().getId());
        cursor.moveToPosition(position);
        Log.d(TAG, "GetImgfromPrivateStorage: position::"+position);
        int id = cursor.getColumnIndex(TableItems.COL_2);

        Log.d(TAG, "GetImgfromPrivateStorage: id::"+id);
        String imuri = cursor.getString(id);
        Uri imurifinal = Uri.parse(imuri);
        Log.d(TAG, "GetImgfromPrivateStorage:  imuri::"+ imuri + "  ::id::" + id);
        return getFileName(imurifinal);

    }
    boolean foundOriginalImg(int position) throws FileNotFoundException {
        Log.d(TAG, "GetImgfromPrivateStorage: Thread::"+Thread.currentThread().getId());
        cursor.moveToPosition(position);
        Log.d(TAG, "GetImgfromPrivateStorage: position::"+position);
        int id = cursor.getColumnIndex(TableItems.COL_2);

        Log.d(TAG, "GetImgfromPrivateStorage: id::"+id);
        String imuri = cursor.getString(id);
        Uri imageuri = Uri.parse(imuri);

        String Thumbimag=imageuri.getPath();
        String name=imageuri.getPath();
        String fname=name.substring(name.lastIndexOf("/"));
        name=name.substring(0,name.lastIndexOf("/"));
        Log.d(TAG, "onCreate: "+name);

        name=name.substring(0,name.lastIndexOf("/"));
        Log.d(TAG, "onCreate: "+name);

        name=name+File.separator+"app_myimage"+fname;
        Log.d(TAG, "foundOriginalImg: ::::::::::::::::::::::::::::::::::<><>"+name);
        
        /*
        /data/user/0/com.example.registerloginsp/app_myimage/image:291
        */
        
        File nfile=new File(name);
        
        if(nfile==null){
            Log.d(TAG, "foundOriginalImg: :::::::::::::::::::::::::LOLOLOLOL");
            return false;
        }
        
        FileInputStream fileInputStream = new FileInputStream(nfile);
        
        if(fileInputStream==null){
            Log.d(TAG, "foundOriginalImg: ::::::::::::::::::::::1");
            return false;
        }
        


        
        return true;

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

