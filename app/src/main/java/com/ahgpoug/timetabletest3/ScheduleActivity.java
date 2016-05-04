package com.ahgpoug.timetabletest3;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class ScheduleActivity extends AppCompatActivity
{
    ArrayList<String> listItems;
    ArrayList<ScheduleInfo> schList;
    ArrayAdapter<String> adapter;
    int max = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Установить время пар");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        listItems = new ArrayList<String>();
        schList = new ArrayList<ScheduleInfo>();
        final ListView lvMain = (ListView) findViewById(R.id.lvMain);

        schList = GlobalVariables.scheduleList;

        for (int i = 0; i < GlobalVariables.mListGreen.size(); i++)
            if (GlobalVariables.mListGreen.get(i).size() > max)
                max = GlobalVariables.mListGreen.get(i).size();

        for (int i = 0; i < GlobalVariables.mListRed.size(); i++)
            if (GlobalVariables.mListRed.get(i).size() > max)
                max = GlobalVariables.mListRed.get(i).size();

        for (int i = 0; i < max; i++) {
            listItems.add("Установить время " + (i + 1) + " пары");
        }

        for (int i = GlobalVariables.scheduleList.size(); i < max; i++){
            schList.add(new ScheduleInfo("", ""));
        }

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listItems);

        lvMain.setAdapter(adapter);

        lvMain.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0)
                    showDialogN(position, schList.get(position).getStartT(), schList.get(position).getEndT());
                else if (schList.get(position - 1).getStartT().equals("") || schList.get(position - 1).getEndT().equals(""))
                    Toast.makeText(getContext(), "Сначала нужно ввести данные о " + (position) + " паре", Toast.LENGTH_SHORT).show();
                else
                    showDialogN(position, schList.get(position).getStartT(), schList.get(position).getEndT());

                GlobalVariables.scheduleList = schList;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home){
            scheduleClose();
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed(){
        scheduleClose();
        finish();
        return;
    }

    private void showDialogN(int pos, String s, String e){
        LayoutInflater li = LayoutInflater.from(MyAdapter.getContext());
        View promptsView = li.inflate(R.layout.prompts_time, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());

        alertDialogBuilder.setView(promptsView);

        final EditText startT = (EditText) promptsView.findViewById(R.id.editText1);
        final EditText endT = (EditText) promptsView.findViewById(R.id.editText2);
        final int p = pos;

        startT.setText(s);
        endT.setText(e);

        alertDialogBuilder
                .setCancelable(true)
                .setPositiveButton("Ок",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                boolean dl = false;
                                try {
                                    String sT = startT.getText().toString();
                                    String eT = endT.getText().toString();

                                    if (sT.length() != 5 || eT.length() != 5) {
                                        Toast.makeText(getContext(), "Ошибка ввода", Toast.LENGTH_SHORT).show();
                                        dialog.dismiss();
                                        showDialogN(p, sT, eT);
                                    }

                                    if (sT.charAt(2) != ':' || eT.charAt(2) != ':') {
                                        Toast.makeText(getContext(), "Ошибка ввода", Toast.LENGTH_SHORT).show();
                                        dialog.dismiss();
                                        showDialogN(p, sT, eT);
                                    }

                                    if ((sT.equals("") || eT.equals("") && p != schList.size() - 1))
                                        if (!schList.get(p + 1).getStartT().equals("") || !schList.get(p + 1).getEndT().equals("")) {
                                            dl = true;
                                            Toast.makeText(getContext(), "Сначала обнулите следующие пары", Toast.LENGTH_SHORT).show();
                                            dialog.dismiss();
                                        }

                                    if (Integer.parseInt(sT.substring(0, 2)) > 23 || Integer.parseInt(sT.substring(0, 2)) < 1 || Integer.parseInt(sT.substring(3, 5)) > 59 || Integer.parseInt(sT.substring(3, 5)) < 0) {
                                        Toast.makeText(getContext(), "Ошибка ввода", Toast.LENGTH_SHORT).show();
                                        dialog.dismiss();
                                        showDialogN(p, sT, eT);
                                    }

                                    if (Integer.parseInt(eT.substring(0, 2)) > 23 || Integer.parseInt(eT.substring(0, 2)) < 1 || Integer.parseInt(eT.substring(3, 5)) > 59 || Integer.parseInt(eT.substring(3, 5)) < 0) {
                                        Toast.makeText(getContext(), "Ошибка ввода", Toast.LENGTH_SHORT).show();
                                        dialog.dismiss();
                                        showDialogN(p, sT, eT);
                                    }


                                    String DATE_FORMAT = "H:mm";
                                    SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);

                                    Calendar cS = Calendar.getInstance();
                                    Calendar cE = Calendar.getInstance();
                                    Calendar prE = Calendar.getInstance();
                                    Calendar nS = Calendar.getInstance();

                                    cS.set(Calendar.HOUR_OF_DAY, Integer.parseInt(sT.substring(0, 2)));
                                    cS.set(Calendar.MINUTE, Integer.parseInt(sT.substring(3, 5)));

                                    cE.set(Calendar.HOUR_OF_DAY, Integer.parseInt(eT.substring(0, 2)));
                                    cE.set(Calendar.MINUTE, Integer.parseInt(eT.substring(3, 5)));

                                    if (p == 0) {
                                        if (cS.before(cE) && !cS.equals(cE))
                                            schList.set(p, new ScheduleInfo(sT, eT));
                                        else {
                                            Toast.makeText(getContext(), "Ошибка ввода", Toast.LENGTH_SHORT).show();
                                            dialog.dismiss();
                                            showDialogN(p, sT, eT);
                                        }
                                    } else if (p == schList.size() - 1) {
                                        prE.set(Calendar.HOUR_OF_DAY, Integer.parseInt(schList.get(p - 1).getEndT().substring(0, 2)));
                                        prE.set(Calendar.MINUTE, Integer.parseInt(schList.get(p - 1).getEndT().substring(3, 5)));

                                        if (cS.before(cE) && !cS.equals(cE))
                                            if (prE.before(cS) && !prE.equals(cS))
                                                schList.set(p, new ScheduleInfo(sT, eT));
                                            else {
                                                Toast.makeText(getContext(), "Ошибка ввода", Toast.LENGTH_SHORT).show();
                                                dialog.dismiss();
                                                showDialogN(p, sT, eT);
                                            }
                                        else {
                                            Toast.makeText(getContext(), "Ошибка ввода", Toast.LENGTH_SHORT).show();
                                            dialog.dismiss();
                                            showDialogN(p, sT, eT);
                                        }
                                    } else {
                                        prE.set(Calendar.HOUR_OF_DAY, Integer.parseInt(schList.get(p - 1).getEndT().substring(0, 2)));
                                        prE.set(Calendar.MINUTE, Integer.parseInt(schList.get(p - 1).getEndT().substring(3, 5)));

                                        if (!schList.get(p + 1).getStartT().equals("") && !schList.get(p + 1).getEndT().equals("")) {
                                            nS.set(Calendar.HOUR_OF_DAY, Integer.parseInt(schList.get(p + 1).getStartT().substring(0, 2)));
                                            nS.set(Calendar.MINUTE, Integer.parseInt(schList.get(p + 1).getStartT().substring(3, 5)));
                                            if (cS.before(cE) && !cS.equals(cE))
                                                if (prE.before(cS) && !prE.equals(cS))
                                                    if (cE.before(nS) && !cE.equals(nS))
                                                        schList.set(p, new ScheduleInfo(sT, eT));
                                                    else {
                                                        Toast.makeText(getContext(), "Ошибка ввода", Toast.LENGTH_SHORT).show();
                                                        dialog.dismiss();
                                                        showDialogN(p, sT, eT);
                                                    }
                                                else {
                                                    Toast.makeText(getContext(), "Ошибка ввода", Toast.LENGTH_SHORT).show();
                                                    dialog.dismiss();
                                                    showDialogN(p, sT, eT);
                                                }
                                            else {
                                                Toast.makeText(getContext(), "Ошибка ввода", Toast.LENGTH_SHORT).show();
                                                dialog.dismiss();
                                                showDialogN(p, sT, eT);
                                            }
                                        } else {
                                            if (cS.before(cE) && !cS.equals(cE))
                                                if (prE.before(cS) && !prE.equals(cS))
                                                    schList.set(p, new ScheduleInfo(sT, eT));
                                                else {
                                                    Toast.makeText(getContext(), "Ошибка ввода", Toast.LENGTH_SHORT).show();
                                                    dialog.dismiss();
                                                    showDialogN(p, sT, eT);
                                                }
                                            else {
                                                Toast.makeText(getContext(), "Ошибка ввода", Toast.LENGTH_SHORT).show();
                                                dialog.dismiss();
                                                showDialogN(p, sT, eT);
                                            }
                                        }
                                    }
                                } catch (Exception e) {
                                    if (!dl) {
                                        Toast.makeText(getContext(), "Ошибка ввода", Toast.LENGTH_SHORT).show();
                                        dialog.dismiss();
                                        showDialogN(p, schList.get(p).getStartT(), schList.get(p).getEndT());
                                    }
                                }
                            }
                        })
                .setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private Context getContext(){
        return this;
    }


    private void scheduleClose() {
        for (int j = 0; j < 7; j++) {
            ArrayList<String> lst = new ArrayList<String>();
            ListView lstView = (ListView) PageFragment.vArr[j].findViewById(R.id.listView);
            ArrayAdapter<String> adapterN = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, lst);
            lstView.setAdapter(adapterN);
            if (GlobalVariables.weekType.equals("Red"))
                for (int i = 0; i < GlobalVariables.mListRed.get(j).size(); i++) {
                    DataInfo dtI = GlobalVariables.mListRed.get(j).get(i);
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

                    String str = String.format("%d пара     %s - %s\n\n%s\n%s\n%s\n%s\n%s", i + 1, GlobalVariables.scheduleList.get(i).getStartT(), GlobalVariables.scheduleList.get(i).getEndT(), dtI.getLessonType(), dtI.getLessonName(), dtI.getRoomNumber(), dtI.getTeacherName(), dtI.getAddressText());
                    lst.add(str);
                    adapterN.notifyDataSetChanged();
                }
            else
                for (int i = 0; i < GlobalVariables.mListGreen.get(j).size(); i++) {
                    DataInfo dtI = GlobalVariables.mListGreen.get(j).get(i);
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

                    String str = String.format("%d пара     %s - %s\n\n%s\n%s\n%s\n%s\n%s", i + 1, GlobalVariables.scheduleList.get(i).getStartT(), GlobalVariables.scheduleList.get(i).getEndT(), dtI.getLessonType(), dtI.getLessonName(), dtI.getRoomNumber(), dtI.getTeacherName(), dtI.getAddressText());
                    lst.add(str);
                    adapterN.notifyDataSetChanged();
                }
            DataBaseIO.Write(j);
        }
        DataBaseIO.writeCfg();
    }
}
