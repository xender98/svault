package com.example.registerloginsp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.FileNotFoundException;

public class TextViewAdapter extends RecyclerView.Adapter<TextViewAdapter.ViewHolder>  {
    private final Activity activity;
    private static final String TAG = "TextViewAdapter";
    private Cursor cursor;
    private OnClickListener myOnTextClickListener;

    public interface OnClickListener {
        void OnClickText(Uri txturi);
    }


    public TextViewAdapter(Activity activity) {
        Log.d(TAG, "TextViewAdapter: Thread:"+Thread.currentThread().getId());
        this.activity = activity;
        this.myOnTextClickListener = (OnClickListener) activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: 1:Thread"+Thread.currentThread().getId());
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.txtlist, parent, false);
        ViewHolder v = new ViewHolder(view);
        Log.d(TAG, "onCreateViewHolder: 2");
        return v;
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: 1::" + position+"::Thread::"+Thread.currentThread().getId());

        holder.tt.setText("File:" + position);
    }

    @Override
    public int getItemCount() {
        return cursor == null ? 0 : cursor.getCount();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView tt;
        ImageView im;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            Log.d(TAG, "ViewHolder: Thread::"+Thread.currentThread().getId());
            tt = itemView.findViewById(R.id.textalbumtitel);
            im = itemView.findViewById(R.id.textalbumimg);
            im.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            getOnClickUri(getAdapterPosition());
        }
    }
    private void getOnClickUri(int position) {
        Log.d(TAG, "getOnClickUri: THread::"+Thread.currentThread().getId());
        cursor.moveToPosition(position);
        int id = cursor.getColumnIndex(TableItems.COL_2);
        String imuri = cursor.getString(id);
        myOnTextClickListener.OnClickText(Uri.parse(imuri));
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


}
