package com.example.singheart.Util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQliteUtil extends SQLiteOpenHelper {
    public static final String NAME = "aaoInfo";
    public static final int VERSION = 1;

    public SQliteUtil(Context context) {
        super(context, NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String userSQL = "create table UserInfo"
                + "("
                + "name varchar(20),"
                + "en_name varchar(20)," + "gender varchar(20),"
                + "grade varchar(20)," + "es varchar(20),"
                + "type varchar(20)," + "edu_level varchar(20),"
                + "college varchar(20)," + "major varchar(20),"
                + "school_area varchar(20)," + "class varchar(20),"
                + "in_time varchar(20)," + "out_time varchar(20)"
                + ")";
        db.execSQL(userSQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
