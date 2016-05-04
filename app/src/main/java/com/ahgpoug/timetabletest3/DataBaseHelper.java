package com.ahgpoug.timetabletest3;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

public class DataBaseHelper extends SQLiteOpenHelper {
    public static String DATABASE_NAME;
    public static final String LESSON_NAME_COLUMN = "lesson_name";
    public static final String LESSON_TYPE_COLUMN = "lesson_type";
    public static final String ROOM_NUMBER_COLUMN = "room_number";
    public static final String TEACHER_NAME_COLUMN = "teacher_name";
    public static final String ADDRESS_TEXT_COLUMN = "address_text";

    private static final int DATABASE_VERSION = 1;

    DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public DataBaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public DataBaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String DATABASE_CREATE_SCRIPT;

        DATABASE_CREATE_SCRIPT = "create table "
                + "schedule" + " ("
                + BaseColumns._ID + " integer primary key autoincrement, "
                + "Start" + " text not null, "
                + "End" + " text not null);";
        db.execSQL(DATABASE_CREATE_SCRIPT);

        for (int i = 0; i < 7; i++){
            DATABASE_CREATE_SCRIPT = "create table "
                    + getDay(i) + "R" + " ("
                    + BaseColumns._ID + " integer primary key autoincrement, "
                    + LESSON_NAME_COLUMN + " text not null, "
                    + LESSON_TYPE_COLUMN + " text not null, "
                    + ROOM_NUMBER_COLUMN + " integer, "
                    + TEACHER_NAME_COLUMN + " text not null, "
                    + ADDRESS_TEXT_COLUMN + " text not null);";
            db.execSQL(DATABASE_CREATE_SCRIPT);
            DATABASE_CREATE_SCRIPT = "create table "
                    + getDay(i) + "G" + " ("
                    + BaseColumns._ID + " integer primary key autoincrement, "
                    + LESSON_NAME_COLUMN + " text not null, "
                    + LESSON_TYPE_COLUMN + " text not null, "
                    + ROOM_NUMBER_COLUMN + " integer, "
                    + TEACHER_NAME_COLUMN + " text not null, "
                    + ADDRESS_TEXT_COLUMN + " text not null);";
            db.execSQL(DATABASE_CREATE_SCRIPT);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w("SQLite", "Обновляемся с версии " + oldVersion + " на версию " + newVersion);
        db.execSQL("DROP TABLE IF IT EXISTS " + "schedule");
        for (int i = 0; i < 7; i++){
            db.execSQL("DROP TABLE IF IT EXISTS " + getDay(i) + "R");
            db.execSQL("DROP TABLE IF IT EXISTS " + getDay(i) + "G");
        }
        onCreate(db);
    }

    public static String getDay(int i){
        switch(i + 1) {
            case 1: return "monday";
            case 2: return "tuesday";
            case 3: return "wednesday";
            case 4: return "thursday";
            case 5: return "friday";
            case 6: return "saturday";
            case 7: return "sunday";
            default: return "";
        }
    }
}