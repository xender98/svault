package com.example.registerloginsp;


import android.util.Log;

public class TableItems {
    private static final String TAG = "TableItems";
    public static final String THUMB_IMG_TABLE_NAME = "Thumb_Img_url";
    public static final String IMG_TABLE_NAME = "Img_url";
    public static final String TXT_TABLE_NAME = "Text_url";

    public static final String _ID = "_id";
    public static final String COL_2 = "URL";
    public static final String COL_3 = "DATE_ADDED";

    public static String CreateTable(String name){
        Log.d(TAG, "CreateTable: "+name);
        String CREATE_TABLE =
                "CREATE TABLE " + name +
                        " ( " +
                        _ID + " integer primary key autoincrement, " +
                        COL_2 + " text, " +
                        COL_3 + " text " +
                        " ); ";

        return CREATE_TABLE;
    }

    public static String DropTable(String name){
        Log.d(TAG, "DropTable: "+name);
        String DROP_TABLE = "DROP TABLE IF EXISTS " + name;

        return DROP_TABLE;
    }

    public String[] Columns = new String[]{_ID, COL_2,COL_3};

}

