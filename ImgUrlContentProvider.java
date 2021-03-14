package com.example.registerloginsp;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.tabs.TabLayout;

public class ImgUrlContentProvider extends ContentProvider {
    private ImgDbHelper imgdb;
    private static final String TAG = "ImgUrlContentProvider";
    public static final String AUTHORITY ="com.example.registerloginsp.contentprovider";
    public static final Uri IMG_CONTENT_URI=Uri.parse("content://"+AUTHORITY+"/"+TableItems.IMG_TABLE_NAME);
    public static final Uri TXT_CONTENT_URI=Uri.parse("content://"+AUTHORITY+"/"+TableItems.TXT_TABLE_NAME);
    public static final Uri THUMB_IMG_CONTENT_URI=Uri.parse("content://"+AUTHORITY+"/"+TableItems.THUMB_IMG_TABLE_NAME);


    @Override
    public boolean onCreate() {
        Log.d(TAG, "onCreate: Thread"+Thread.currentThread().getId());
        imgdb=new ImgDbHelper(this.getContext());
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Log.d(TAG, "query: "+uri);
        Log.d(TAG, "query: Thread"+Thread.currentThread().getId());
        SQLiteDatabase dbread=imgdb.getWritableDatabase();
        SQLiteQueryBuilder sql=new SQLiteQueryBuilder();
        switch (gettype(uri.toString())) {
            case 0:
                Log.d(TAG, "query: " + TableItems.IMG_TABLE_NAME);
                sql.setTables(TableItems.IMG_TABLE_NAME);
                break;
            case 1:
                Log.d(TAG, "query: " + TableItems.THUMB_IMG_TABLE_NAME);
                sql.setTables(TableItems.THUMB_IMG_TABLE_NAME);
                break;
            default:
                Log.d(TAG, "query: " + TableItems.TXT_TABLE_NAME);
                sql.setTables(TableItems.TXT_TABLE_NAME);
        }
        Cursor c;
        c=sql.query(dbread,projection,selection,selectionArgs,null,null,sortOrder);
        Log.d(TAG, "query: COUNT:::::::::::::::"+c.getCount());
        return c;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        Log.d(TAG, "getType: "+uri);
        switch (gettype(uri.toString())) {
            case 0:
                Log.d(TAG, "getType: "+0);
                return "vnd.android.cursor.dir/vnd."+AUTHORITY+"."+ TableItems.IMG_TABLE_NAME;

            case 1:
                Log.d(TAG, "getType: "+1);
                return "vnd.android.cursor.dir/vnd."+AUTHORITY+"."+ TableItems.THUMB_IMG_TABLE_NAME;


            default:
                Log.d(TAG, "getType: "+2);
                return "vnd.android.cursor.dir/vnd."+AUTHORITY+"."+ TableItems.TXT_TABLE_NAME;

        }
    }

    int gettype(String name){
        Log.d(TAG, "gettype: "+name);
        String rr=name.substring(name.lastIndexOf("/")+1);
        if(rr.equals(TableItems.IMG_TABLE_NAME))return 0;
        else if(rr.equals(TableItems.THUMB_IMG_TABLE_NAME))return 1;
        else if(rr.equals(TableItems.TXT_TABLE_NAME))return 2;

        Log.d(TAG, "gettype: "+rr);
        String res=rr.substring(rr.lastIndexOf(".")+1);
        if(res.equals("jpg"))return 0;
        return 1;

    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        Log.d(TAG, "insert: "+uri);
        Log.d(TAG, "insert: Thread"+Thread.currentThread().getId());
        SQLiteDatabase dbwrite=imgdb.getWritableDatabase();
        switch (gettype(uri.toString())) {
            case 0:
                Log.d(TAG, "insert: " + TableItems.IMG_TABLE_NAME);
                long res = dbwrite.insert(TableItems.IMG_TABLE_NAME, null, values);
                getContext().getContentResolver().notifyChange(uri, null);
                return Uri.parse(IMG_CONTENT_URI + "/" + res);

            case 1:
                Log.d(TAG, "insert: " + TableItems.THUMB_IMG_TABLE_NAME);
                long res1 = dbwrite.insert(TableItems.THUMB_IMG_TABLE_NAME, null, values);
                getContext().getContentResolver().notifyChange(uri, null);
                return Uri.parse(THUMB_IMG_CONTENT_URI + "/" + res1);

            default:
                Log.d(TAG, "insert: " + TableItems.TXT_TABLE_NAME);
                long res2 = dbwrite.insert(TableItems.TXT_TABLE_NAME, null, values);
                getContext().getContentResolver().notifyChange(uri, null);
                return Uri.parse(TXT_CONTENT_URI + "/" + res2);
        }
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return -1;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return -1;
    }
    static class ImgDbHelper extends SQLiteOpenHelper {
        public static final String DATABASE_NAME = "ImgUrl.db";

        public ImgDbHelper(@Nullable Context context) {
            super(context, DATABASE_NAME, null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(TableItems.CreateTable(TableItems.IMG_TABLE_NAME));
            db.execSQL(TableItems.CreateTable(TableItems.TXT_TABLE_NAME));
            db.execSQL(TableItems.CreateTable(TableItems.THUMB_IMG_TABLE_NAME));

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL(TableItems.DropTable(TableItems.IMG_TABLE_NAME));
            db.execSQL(TableItems.DropTable(TableItems.TXT_TABLE_NAME));
            db.execSQL(TableItems.DropTable(TableItems.THUMB_IMG_TABLE_NAME));

            onCreate(db);

        }
    }


}

