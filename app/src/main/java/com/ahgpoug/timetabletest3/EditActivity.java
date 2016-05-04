package com.ahgpoug.timetabletest3;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class EditActivity extends AppCompatActivity
{
    ArrayList<String> listItems;
    ArrayAdapter<String> adapter;
    static ArrayList<DataInfo> lList;
    private int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        String str = "";
        if (GlobalVariables.twoWeeksMode == 1)
            if (GlobalVariables.weekType.equals("Red"))
                str = ", красная неделя";
            else
                str = ", зеленая неделя";
;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(GlobalVariables.days[MainActivity.getCurrPosition()] + str);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        listItems = new ArrayList<String>();
        final ListView lvMain = (ListView) findViewById(R.id.lvMain);

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listItems);
        lList = new ArrayList<DataInfo>();

        lvMain.setAdapter(adapter);

        final Intent intent = new Intent(this, EnterActivity.class);

        if (GlobalVariables.weekType.equals("Red"))
            for (int i = 0; i < GlobalVariables.mListRed.get(MainActivity.getCurrPosition()).size(); i++){
                listItems.add("Редактировать " + (count + 1) + " пару");
                adapter.notifyDataSetChanged();
                count++;
                lList.add(GlobalVariables.mListRed.get(MainActivity.getCurrPosition()).get(i));
            }
        else
            for (int i = 0; i < GlobalVariables.mListGreen.get(MainActivity.getCurrPosition()).size(); i++){
                listItems.add("Редактировать " + (count + 1) + " пару");
                adapter.notifyDataSetChanged();
                count++;
                lList.add(GlobalVariables.mListGreen.get(MainActivity.getCurrPosition()).get(i));
            }

        lvMain.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                intent.putExtra("lessonNumber", Integer.toString(position + 1));
                startActivity(intent);
            }
        });
    }

    public static void setNew(String lType, String lName, String roomN, String tName, String aText, int pos){
        lList.set(pos - 1, new DataInfo(lType, lName, roomN, tName, aText));
    }

    public static DataInfo getInfo(int pos){
        return lList.get(pos - 1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home){
            editClose();
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed(){
        editClose();
        finish();
        return;
    }

    public void addButton(View view) {
        if (count < 7){
            listItems.add("Редактировать " + (count + 1) + " пару");
            adapter.notifyDataSetChanged();
            lList.add(new DataInfo());

            int max = 0;
            for (int i = 0; i < GlobalVariables.mListGreen.size(); i++)
                if (GlobalVariables.mListGreen.get(i).size() > max)
                    max = GlobalVariables.mListGreen.get(i).size();
            for (int i = 0; i < GlobalVariables.mListRed.size(); i++)
                if (GlobalVariables.mListRed.get(i).size() > max)
                    max = GlobalVariables.mListRed.get(i).size();

            if (max > GlobalVariables.scheduleList.size())
                GlobalVariables.scheduleList.add(new ScheduleInfo("", ""));
            count++;
        }
        else
            Toast.makeText(getApplicationContext(), "Превышен лимит количества пар", Toast.LENGTH_SHORT).show();
    }

    public void removeButton(View view) {
        if (count > 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(EditActivity.this);
            builder.setTitle("Удаление");
            builder.setMessage("Все даннные о " + (count) + " паре будут безвозвратно удалены. Продолжить?");
            builder.setPositiveButton("Да", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int arg1) {
                    listItems.remove(adapter.getItem(count - 1));
                    adapter.notifyDataSetChanged();
                    lList.remove(lList.size() - 1);
                    count--;
                }
            });
            builder.setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int arg1) {

                }
            });
            builder.setCancelable(false);
            AlertDialog alert = builder.create();
            alert.show();
        }
        else
            Toast.makeText(getApplicationContext(), "Больше нечего удалять", Toast.LENGTH_SHORT).show();
    }

    private void editClose() {
        if (GlobalVariables.weekType.equals("Red"))
            GlobalVariables.mListRed.set(MainActivity.getCurrPosition(), lList);
        else
            GlobalVariables.mListGreen.set(MainActivity.getCurrPosition(), lList);

        ArrayList<String> lst = new ArrayList<String>();
        ListView lstView = (ListView) PageFragment.vArr[MainActivity.getCurrPosition()].findViewById(R.id.listView);
        ArrayAdapter<String> adapterN = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, lst);
        lstView.setAdapter(adapterN);
        if (GlobalVariables.weekType.equals("Red"))
            for (int i = 0; i < GlobalVariables.mListRed.get(MainActivity.getCurrPosition()).size(); i++) {
                DataInfo dtI = GlobalVariables.mListRed.get(MainActivity.getCurrPosition()).get(i);
                if (dtI.getLessonType() == null)
                    dtI.setLessonType("");
                if (dtI.getLessonName() == null)
                    dtI.setLessonName("");
                if (dtI.getTeacherName() == null)
                    dtI.setTeacherName("");
                if (dtI.getRoomNumber() == null)
                    dtI.setRoomNumber("");
                if (dtI.getAddressText() == null)
                    dtI.setAddressText("");

                if (GlobalVariables.scheduleList.size() < GlobalVariables.mListRed.get(MainActivity.getCurrPosition()).size())
                    for (int k = GlobalVariables.scheduleList.size(); k < GlobalVariables.mListRed.get(MainActivity.getCurrPosition()).size() + 1; k++)
                        GlobalVariables.scheduleList.add(new ScheduleInfo("", ""));
                String str = String.format("%d пара     %s - %s\n\n%s\n%s\n%s\n%s\n%s", i + 1, GlobalVariables.scheduleList.get(i).getStartT(), GlobalVariables.scheduleList.get(i).getEndT(), dtI.getLessonType(), dtI.getLessonName(), dtI.getRoomNumber(), dtI.getTeacherName(), dtI.getAddressText());
                lst.add(str);
                adapterN.notifyDataSetChanged();
            }
        else
            for (int i = 0; i < GlobalVariables.mListGreen.get(MainActivity.getCurrPosition()).size(); i++) {
                DataInfo dtI = GlobalVariables.mListGreen.get(MainActivity.getCurrPosition()).get(i);
                if (dtI.getLessonType() == null)
                    dtI.setLessonType("");
                if (dtI.getLessonName() == null)
                    dtI.setLessonName("");
                if (dtI.getTeacherName() == null)
                    dtI.setTeacherName("");
                if (dtI.getRoomNumber() == null)
                    dtI.setRoomNumber("");
                if (dtI.getAddressText() == null)
                    dtI.setAddressText("");

                if (GlobalVariables.scheduleList.size() < GlobalVariables.mListGreen.get(MainActivity.getCurrPosition()).size())
                    for (int k = GlobalVariables.scheduleList.size(); k < GlobalVariables.mListGreen.get(MainActivity.getCurrPosition()).size() + 1; k++)
                        GlobalVariables.scheduleList.add(new ScheduleInfo("", ""));
                String str = String.format("%d пара     %s - %s\n\n%s\n%s\n%s\n%s\n%s", i + 1, GlobalVariables.scheduleList.get(i).getStartT(), GlobalVariables.scheduleList.get(i).getEndT(), dtI.getLessonType(), dtI.getLessonName(), dtI.getRoomNumber(), dtI.getTeacherName(), dtI.getAddressText());
                lst.add(str);
                adapterN.notifyDataSetChanged();
            }

        int max = 0;
        for (int i = 0; i < GlobalVariables.mListGreen.size(); i++)
            if (GlobalVariables.mListGreen.get(i).size() > max)
                max = GlobalVariables.mListGreen.get(i).size();
        for (int i = 0; i < GlobalVariables.mListRed.size(); i++)
            if (GlobalVariables.mListRed.get(i).size() > max)
                max = GlobalVariables.mListRed.get(i).size();

        ArrayList<ScheduleInfo> schN = new ArrayList<ScheduleInfo>();

        for (int i = 0; i < max; i++)
            schN.add(GlobalVariables.scheduleList.get(i));

        GlobalVariables.scheduleList.clear();
        GlobalVariables.scheduleList = schN;

        DataBaseIO.Write(MainActivity.getCurrPosition());
        DataBaseIO.writeCfg();
    }
}
