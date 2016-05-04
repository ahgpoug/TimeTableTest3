package com.ahgpoug.timetabletest3;

import android.content.Context;
import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class Serialization {

    public static void Serialize(){
        try {
            FileOutputStream fileOut;
            ObjectOutputStream out;

            new File(Environment.getExternalStorageDirectory().getPath() + "/Android/data/com.ahgpoug.timetable/files").mkdirs();

            File fileR = new File(Environment.getExternalStorageDirectory().getPath() + "/Android/data/com.ahgpoug.timetable/files", "dataR.dat");
            fileOut = new FileOutputStream(fileR);
            out = new ObjectOutputStream(fileOut);
            out.writeObject(GlobalVariables.mListRed);
            out.close();
            fileOut.close();

            File fileG = new File(Environment.getExternalStorageDirectory().getPath() + "/Android/data/com.ahgpoug.timetable/files", "dataG.dat");
            fileOut = new FileOutputStream(fileG);
            out = new ObjectOutputStream(fileOut);
            out.writeObject(GlobalVariables.mListGreen);
            out.close();
            fileOut.close();

            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(MyAdapter.getContext().openFileOutput("config.cfg", Context.MODE_PRIVATE));
            outputStreamWriter.write(GlobalVariables.weekType);
            outputStreamWriter.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }
    public static void Deserialize(){
        try {
            FileInputStream fileIn;
            ObjectInputStream in;
            String ret = "";

            new File(Environment.getExternalStorageDirectory().getPath() + "/Android/data/com.ahgpoug.timetable/files").mkdirs();

            File fileR = new File(Environment.getExternalStorageDirectory().getPath() + "/Android/data/com.ahgpoug.timetable/files", "dataR.dat");
            File fileG = new File(Environment.getExternalStorageDirectory().getPath() + "/Android/data/com.ahgpoug.timetable/files", "dataG.dat");

            fileIn = new FileInputStream(fileR);
            in = new ObjectInputStream(fileIn);
            GlobalVariables.mListRed = (ArrayList<ArrayList<DataInfo>>) in.readObject();
            in.close();
            fileIn.close();

            fileIn = new FileInputStream(fileG);
            in = new ObjectInputStream(fileIn);
            GlobalVariables.mListGreen = (ArrayList<ArrayList<DataInfo>>) in.readObject();
            in.close();
            fileIn.close();

            InputStream inputStream = MyAdapter.getContext().openFileInput("config.cfg");

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();
                int count = 0;
                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }

            GlobalVariables.weekType = ret;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}