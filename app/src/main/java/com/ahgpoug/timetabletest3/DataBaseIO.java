package com.ahgpoug.timetabletest3;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class DataBaseIO {
    public static void Write(int i){
        DataBaseHelper mDatabaseHelper = new DataBaseHelper(MyAdapter.getContext(), GlobalVariables.id + ".db", null, 1);
        SQLiteDatabase mSqLiteDatabase = mDatabaseHelper.getWritableDatabase();

        mSqLiteDatabase.delete("schedule", null, null);
        for (int j = 0; j < GlobalVariables.scheduleList.size(); j++) {
            ScheduleInfo dtI = GlobalVariables.scheduleList.get(j);
            ContentValues values = new ContentValues();
            values.put("Start", dtI.getStartT());
            values.put("End", dtI.getEndT());
            mSqLiteDatabase.insert("schedule", null, values);
        }


        if (GlobalVariables.weekType.equals("Red")) {
            clear(i, "R");
            for (int j = 0; j < GlobalVariables.mListRed.get(i).size(); j++) {
                DataInfo dtI = GlobalVariables.mListRed.get(i).get(j);
                ContentValues values = new ContentValues();
                values.put(DataBaseHelper.LESSON_NAME_COLUMN, dtI.getLessonName());
                values.put(DataBaseHelper.LESSON_TYPE_COLUMN, dtI.getLessonType());
                values.put(DataBaseHelper.ROOM_NUMBER_COLUMN, dtI.getRoomNumber());
                values.put(DataBaseHelper.TEACHER_NAME_COLUMN, dtI.getTeacherName());
                values.put(DataBaseHelper.ADDRESS_TEXT_COLUMN, dtI.getAddressText());
                mSqLiteDatabase.insert(DataBaseHelper.getDay(i) + "R", null, values);
            }
        }
        else {
            clear(i, "G");
            for (int j = 0; j < GlobalVariables.mListGreen.get(i).size(); j++) {
                DataInfo dtI = GlobalVariables.mListGreen.get(i).get(j);
                ContentValues values = new ContentValues();
                values.put(DataBaseHelper.LESSON_NAME_COLUMN, dtI.getLessonName());
                values.put(DataBaseHelper.LESSON_TYPE_COLUMN, dtI.getLessonType());
                values.put(DataBaseHelper.ROOM_NUMBER_COLUMN, dtI.getRoomNumber());
                values.put(DataBaseHelper.TEACHER_NAME_COLUMN, dtI.getTeacherName());
                values.put(DataBaseHelper.ADDRESS_TEXT_COLUMN, dtI.getAddressText());
                mSqLiteDatabase.insert(DataBaseHelper.getDay(i) + "G", null, values);
            }
        }
        writeCfg();
    }

    public static void Read(){
        DataBaseHelper mDatabaseHelper = new DataBaseHelper(MyAdapter.getContext(), GlobalVariables.id + ".db", null, 1);
        SQLiteDatabase mSqLiteDatabase = mDatabaseHelper.getWritableDatabase();

        Cursor cursor = mSqLiteDatabase.query("schedule", new String[]{"Start",
                        "End"},
                null,
                null,
                null,
                null,
                null
        );
        int id = 0;
        while (cursor.moveToNext()) {
            String start = cursor.getString(cursor.getColumnIndex("Start"));
            String end = cursor.getString(cursor.getColumnIndex("End"));
            GlobalVariables.scheduleList.add(new ScheduleInfo(start, end));
            id++;
        }
        cursor.close();

        for (int i = 0; i < 7; i++) {
            cursor = mSqLiteDatabase.query(DataBaseHelper.getDay(i) + "R", new String[]{mDatabaseHelper.LESSON_TYPE_COLUMN,
                            mDatabaseHelper.LESSON_NAME_COLUMN,
                            mDatabaseHelper.ROOM_NUMBER_COLUMN,
                            mDatabaseHelper.TEACHER_NAME_COLUMN,
                            mDatabaseHelper.ADDRESS_TEXT_COLUMN},
                            null,
                            null,
                            null,
                            null,
                            null
            );
            id = 0;
            while (cursor.moveToNext()) {
                String type = cursor.getString(cursor.getColumnIndex(mDatabaseHelper.LESSON_TYPE_COLUMN));
                String name = cursor.getString(cursor.getColumnIndex(mDatabaseHelper.LESSON_NAME_COLUMN));
                String room = cursor.getString(cursor.getColumnIndex(mDatabaseHelper.ROOM_NUMBER_COLUMN));
                String teacher = cursor.getString(cursor.getColumnIndex(mDatabaseHelper.TEACHER_NAME_COLUMN));
                String address = cursor.getString(cursor.getColumnIndex(mDatabaseHelper.ADDRESS_TEXT_COLUMN));
                GlobalVariables.mListRed.get(i).add(new DataInfo(name, type, room, teacher, address));
                id++;
            }
            cursor.close();
        }

        for (int i = 0; i < 7; i++) {
            cursor = mSqLiteDatabase.query(DataBaseHelper.getDay(i) + "G", new String[]{mDatabaseHelper.LESSON_TYPE_COLUMN,
                            mDatabaseHelper.LESSON_NAME_COLUMN,
                            mDatabaseHelper.ROOM_NUMBER_COLUMN,
                            mDatabaseHelper.TEACHER_NAME_COLUMN,
                            mDatabaseHelper.ADDRESS_TEXT_COLUMN},
                            null,
                            null,
                            null,
                            null,
                            null
            );
            id = 0;
            while (cursor.moveToNext()) {
                String type = cursor.getString(cursor.getColumnIndex(mDatabaseHelper.LESSON_TYPE_COLUMN));
                String name = cursor.getString(cursor.getColumnIndex(mDatabaseHelper.LESSON_NAME_COLUMN));
                String room = cursor.getString(cursor.getColumnIndex(mDatabaseHelper.ROOM_NUMBER_COLUMN));
                String teacher = cursor.getString(cursor.getColumnIndex(mDatabaseHelper.TEACHER_NAME_COLUMN));
                String address = cursor.getString(cursor.getColumnIndex(mDatabaseHelper.ADDRESS_TEXT_COLUMN));
                GlobalVariables.mListGreen.get(i).add(new DataInfo(name, type, room, teacher, address));
                id++;
            }
            cursor.close();
        }
    }

    public static void clear(int i, String str){
        DataBaseHelper mDatabaseHelper = new DataBaseHelper(MyAdapter.getContext(), GlobalVariables.id + ".db", null, 1);
        SQLiteDatabase mSqLiteDatabase = mDatabaseHelper.getWritableDatabase();
        mSqLiteDatabase.delete(DataBaseHelper.getDay(i) + str, null, null);
    }

    public static void loadCfg(){
        try {
            String ret = "";
            InputStream inputStream = MyAdapter.getContext().openFileInput("config.cfg");

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();
                while ((receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }
                inputStream.close();
                ret = stringBuilder.toString();
                String[] s = ret.split("\\|");
                if (s[0].equals(""))
                    GlobalVariables.weekType = "Red";
                else
                    GlobalVariables.weekType = s[0];
                GlobalVariables.wk = Integer.parseInt(s[1]);
                GlobalVariables.startWeek = s[2];
                GlobalVariables.id = s[3];
                GlobalVariables.startNotifications = Integer.parseInt(s[4]);
            }
        } catch (FileNotFoundException e) {
            GlobalVariables.weekType = "Red";
            e.printStackTrace();
        } catch (Exception e) {
            GlobalVariables.weekType = "Red";
            e.printStackTrace();
        }
    }

    public static void writeCfg(){
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(MyAdapter.getContext().openFileOutput("config.cfg", Context.MODE_PRIVATE));
            outputStreamWriter.write(GlobalVariables.weekType + "|" + GlobalVariables.wk + "|" + GlobalVariables.startWeek + "|" + GlobalVariables.id + "|" + GlobalVariables.startNotifications);
            outputStreamWriter.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
