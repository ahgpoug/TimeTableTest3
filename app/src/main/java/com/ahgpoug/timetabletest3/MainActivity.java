package com.ahgpoug.timetabletest3;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity
{
    private Menu mn;
    private int num;
    private int color;
    private ListView lstView;
    private ViewPager pager;
    private FloatingActionButton fab;
    private String modTime = "";
    private String xId;
    private boolean upload = false;
    private static int CurrentPosition;
    private MyAdapter AdapterN;
    private static final int REQUEST_PERMISSIONS_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.READ_PHONE_STATE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        verifyPermissions(this);

        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        boolean twoWeeksModeDisabled = SP.getBoolean("twoWeeksModeDisabled", false);
        if (twoWeeksModeDisabled)
            GlobalVariables.twoWeeksMode = 0;
        else
            GlobalVariables.twoWeeksMode = 1;

        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Расписание");
        setSupportActionBar(toolbar);

        for (int i = 0; i < 7; i++) {
            GlobalVariables.mListRed.add(new ArrayList<DataInfo>());
            GlobalVariables.mListGreen.add(new ArrayList<DataInfo>());
        }

        pager = (ViewPager) findViewById(R.id.pager);
        pager.setOffscreenPageLimit(7);
        AdapterN = new MyAdapter(this, getSupportFragmentManager());
        DataBaseIO.loadCfg();

        BigInteger it = GlobalVariables.hex2decimal(Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID));
        GlobalVariables.id = GlobalVariables.dec2any(it);

        DataBaseHelper.DATABASE_NAME = GlobalVariables.id + ".db";

        if (GlobalVariables.wk != 0 && GlobalVariables.startWeek != null){
            calendar = Calendar.getInstance();
            int wk = calendar.get(Calendar.WEEK_OF_YEAR);
            if ((wk - GlobalVariables.wk) % 2 == 0)
                GlobalVariables.weekType = GlobalVariables.startWeek;
            else {
                if (GlobalVariables.startWeek.equals("Red"))
                    GlobalVariables.weekType = "Green";
                else
                    GlobalVariables.weekType = "Red";
            }
        }
        pager.setAdapter(AdapterN);

        GlobalVariables.nWeek = GlobalVariables.weekType;

        pager.setCurrentItem(getDayOfweek(day) - 1);
        CurrentPosition = getDayOfweek(day) - 1;

        pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                CurrentPosition = position;
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        fab = (FloatingActionButton) findViewById(R.id.fab);
        if (GlobalVariables.startNotifications == 1) {
            Snackbar.make(findViewById(android.R.id.content), "Ваш уникальный идентификатор можно узнать в настройках приложения", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
        }

        if (!twoWeeksModeDisabled) {
            if (GlobalVariables.weekType.equals("Red"))
                fab.setBackgroundTintList(getResources().getColorStateList(R.color.colorRed));
            else
                fab.setBackgroundTintList(getResources().getColorStateList(R.color.colorGreen));
            fab.setVisibility(1);

            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
                    if (GlobalVariables.weekType.equals("Red")) {
                        ArrayAdapter<String> adapterNR;
                        fab.setBackgroundTintList(getResources().getColorStateList(R.color.colorGreen));
                        Snackbar.make(view, "Выбрана зеленая неделя", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                        GlobalVariables.weekType = "Green";
                        for (int i = 0; i < 7; i++) {
                            ArrayList<String> lst = new ArrayList<String>();
                            ListView lstView = (ListView) PageFragment.vArr[i].findViewById(R.id.listView);
                            adapterNR = new ArrayAdapter<String>(MyAdapter.getContext(), android.R.layout.simple_list_item_1, lst);
                            lstView.setAdapter(adapterNR);
                            for (int j = 0; j < GlobalVariables.mListGreen.get(i).size(); j++) {
                                DataInfo dtI = GlobalVariables.mListGreen.get(i).get(j);
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

                                String str = String.format("%d пара     %s - %s\n\n%s\n%s\n%s\n%s\n%s", j + 1, GlobalVariables.scheduleList.get(j).getStartT(), GlobalVariables.scheduleList.get(j).getEndT(), dtI.getLessonType(), dtI.getLessonName(), dtI.getRoomNumber(), dtI.getTeacherName(), dtI.getAddressText());
                                lst.add(str);
                                adapterNR.notifyDataSetChanged();
                            }
                        }
                    } else {
                        fab.setBackgroundTintList(getResources().getColorStateList(R.color.colorRed));
                        Snackbar.make(view, "Выбрана красная неделя", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                        GlobalVariables.weekType = "Red";
                        ArrayAdapter<String> adapterNR;
                        for (int i = 0; i < 7; i++) {
                            ArrayList<String> lst = new ArrayList<String>();
                            ListView lstView = (ListView) PageFragment.vArr[i].findViewById(R.id.listView);
                            adapterNR = new ArrayAdapter<String>(MyAdapter.getContext(), android.R.layout.simple_list_item_1, lst);
                            lstView.setAdapter(adapterNR);
                            for (int j = 0; j < GlobalVariables.mListRed.get(i).size(); j++) {
                                DataInfo dtI = GlobalVariables.mListRed.get(i).get(j);
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

                                String str = String.format("%d пара     %s - %s\n\n%s\n%s\n%s\n%s\n%s", j + 1, GlobalVariables.scheduleList.get(j).getStartT(), GlobalVariables.scheduleList.get(j).getEndT(), dtI.getLessonType(), dtI.getLessonName(), dtI.getRoomNumber(), dtI.getTeacherName(), dtI.getAddressText());
                                lst.add(str);
                                adapterNR.notifyDataSetChanged();
                            }
                        }
                    }
                    DataBaseIO.writeCfg();
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mn = menu;
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        if (id == R.id.action_about){
            AlertDialog.Builder builder = new AlertDialog.Builder(MyAdapter.getContext());
            builder.setTitle("О программе");
            String str = String.format("Расписание v1.7\nMaxim Kostin, 2016");
            builder.setMessage(str);
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int arg1) {

                }
            });
            builder.setCancelable(false);
            AlertDialog alert = builder.create();
            alert.show();
            return true;
        }

        if (id == R.id.action_exit) {
            DataBaseIO.writeCfg();
            finish();
            System.exit(0);
        }

        if (id == R.id.action_schedule) {
            Intent intent = new Intent(this, ScheduleActivity.class);
            startActivity(intent);
            return true;
        }

        if (id == R.id.action_edit) {
            Intent intent = new Intent(this, EditActivity.class);
            startActivity(intent);
            return true;
        }

        if (id == R.id.action_now) {
            getNow();
            return true;
        }

        if (id == R.id.action_sync) {
            try {
                if (isOnline()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("Загрузка данных");
                    builder.setMessage("Что сделать с базой данных?");
                    builder.setPositiveButton("Загрузить с сервера", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int arg1) {
                            upload = false;
                            xId = GlobalVariables.id;
                            new FtpInfo().execute();
                        }
                    });
                    builder.setNegativeButton("Загрузить на сервер", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int arg1) {
                            upload = true;
                            new FtpInfo().execute();
                        }
                    });
                    builder.setNeutralButton("Загрузить с сервера ID", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int arg1) {
                            upload = false;
                            LayoutInflater li = LayoutInflater.from(MyAdapter.getContext());
                            View promptsView = li.inflate(R.layout.prompts, null);

                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MyAdapter.getContext());

                            alertDialogBuilder.setView(promptsView);

                            final EditText userInput = (EditText) promptsView
                                    .findViewById(R.id.editTextDialogUserInput);

                            alertDialogBuilder
                                    .setCancelable(true)
                                    .setPositiveButton("Ок",
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    xId = userInput.getText().toString();
                                                    if (!xId.equals(""))
                                                        new FtpInfo().execute();
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
                    });
                    builder.setCancelable(true);
                    AlertDialog alert = builder.create();
                    alert.show();
                    return true;
                } else {
                    Snackbar.make(findViewById(android.R.id.content), "Проверьте ваше подключение к интернету", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                }
            } catch (Exception e){
                Snackbar.make(findViewById(android.R.id.content), "Ошибка", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    public static int getCurrPosition(){
        return CurrentPosition;
    }

    @Override
    public void onBackPressed() {
        DataBaseIO.writeCfg();
        finish();
        return;
    }

    private void getNow(){
        Calendar calendar = Calendar.getInstance();

        int day = getDayOfweek(calendar.get(Calendar.DAY_OF_WEEK)) - 1;

        if (GlobalVariables.scheduleList.size() == 0)
            return;
        for (int i = 0; i < GlobalVariables.scheduleList.size(); i++)
            if (GlobalVariables.scheduleList.get(i).getStartT().equals("") || GlobalVariables.scheduleList.get(i).getEndT().equals("")){
                Toast.makeText(MyAdapter.getContext(), "Сначала заполните все расписание!", Toast.LENGTH_SHORT).show();
                return;
            }

        if (!GlobalVariables.weekType.equals(GlobalVariables.nWeek)) {
            fab.performClick();
        }

        Calendar itmS = Calendar.getInstance();
        Calendar itmE = Calendar.getInstance();
        Calendar itmL = Calendar.getInstance();
        Calendar itmNs = Calendar.getInstance();

        num = -1;
        int len;

        if (GlobalVariables.weekType.equals("Red"))
            len = GlobalVariables.mListRed.get(day).size();
        else
            len = GlobalVariables.mListGreen.get(day).size();
        try {
            if (len == 0)
                day = cycle(day + 1);

            boolean add = false;

            for (int i = 0; i < len; i++) {
                itmS.set(Calendar.HOUR_OF_DAY, Integer.parseInt(GlobalVariables.scheduleList.get(i).getStartT().substring(0, 2)));
                itmS.set(Calendar.MINUTE, Integer.parseInt(GlobalVariables.scheduleList.get(i).getStartT().substring(3, 5)));

                itmE.set(Calendar.HOUR_OF_DAY, Integer.parseInt(GlobalVariables.scheduleList.get(i).getEndT().substring(0, 2)));
                itmE.set(Calendar.MINUTE, Integer.parseInt(GlobalVariables.scheduleList.get(i).getEndT().substring(3, 5)));

                itmL.set(Calendar.HOUR_OF_DAY, Integer.parseInt(GlobalVariables.scheduleList.get(len - 1).getEndT().substring(0, 2)));
                itmL.set(Calendar.MINUTE, Integer.parseInt(GlobalVariables.scheduleList.get(len - 1).getEndT().substring(3, 5)));

                if (i != len - 1) {
                    itmNs.set(Calendar.HOUR_OF_DAY, Integer.parseInt(GlobalVariables.scheduleList.get(i + 1).getStartT().substring(0, 2)));
                    itmNs.set(Calendar.MINUTE, Integer.parseInt(GlobalVariables.scheduleList.get(i + 1).getStartT().substring(3, 5)));

                    if (calendar.before(itmS) && i == 0) {
                        if (GlobalVariables.weekType.equals("Red")) {
                            if (GlobalVariables.mListRed.get(day).size() != 0) {
                                for (int j = 0; j < GlobalVariables.mListRed.get(day).size(); j++)
                                    if (!GlobalVariables.mListRed.get(day).get(j).getLessonName().equals("") || !GlobalVariables.mListRed.get(day).get(j).getLessonType().equals("") || !GlobalVariables.mListRed.get(day).get(j).getRoomNumber().equals("") || !GlobalVariables.mListRed.get(day).get(j).getTeacherName().equals("")) {
                                        add = true;
                                        num = j;
                                        break;
                                    }
                                if (add)
                                    break;
                            }
                        } else if (GlobalVariables.weekType.equals("Green")) {
                            if (GlobalVariables.mListGreen.get(day).size() != 0)
                                for (int j = 0; j < GlobalVariables.mListGreen.get(day).size(); j++)
                                    if (!GlobalVariables.mListGreen.get(day).get(j).getLessonName().equals("") || !GlobalVariables.mListGreen.get(day).get(j).getLessonType().equals("") || !GlobalVariables.mListGreen.get(day).get(j).getRoomNumber().equals("") || !GlobalVariables.mListGreen.get(day).get(j).getTeacherName().equals("")) {
                                        add = true;
                                        num = j;
                                        break;
                                    }
                            if (add)
                                break;
                        }
                    } else if ((calendar.after(itmS) && calendar.before(itmE)) || (calendar.equals(itmS) && calendar.before(itmE)) || (calendar.after(itmS) && calendar.equals(itmE))) {
                        if (GlobalVariables.weekType.equals("Red")) {
                            if (GlobalVariables.mListRed.get(day).size() != 0) {
                                for (int j = i; j < GlobalVariables.mListRed.get(day).size(); j++)
                                    if (!GlobalVariables.mListRed.get(day).get(j).getLessonName().equals("") || !GlobalVariables.mListRed.get(day).get(j).getLessonType().equals("") || !GlobalVariables.mListRed.get(day).get(j).getRoomNumber().equals("") || !GlobalVariables.mListRed.get(day).get(j).getTeacherName().equals("")) {
                                        add = true;
                                        num = j;
                                        break;
                                    }
                                if (add)
                                    break;
                            }
                        } else if (GlobalVariables.weekType.equals("Green")) {
                            if (GlobalVariables.mListGreen.get(day).size() != 0)
                                for (int j = i; j < GlobalVariables.mListGreen.get(day).size(); j++)
                                    if (!GlobalVariables.mListGreen.get(day).get(j).getLessonName().equals("") || !GlobalVariables.mListGreen.get(day).get(j).getLessonType().equals("") || !GlobalVariables.mListGreen.get(day).get(j).getRoomNumber().equals("") || !GlobalVariables.mListGreen.get(day).get(j).getTeacherName().equals("")) {
                                        add = true;
                                        num = j;
                                        break;
                                    }
                            if (add)
                                break;
                        }
                    } else if (calendar.after(itmE) && calendar.before(itmNs)) {
                        if (GlobalVariables.weekType.equals("Red")) {
                            if (GlobalVariables.mListRed.get(day).size() != 0) {
                                for (int j = i + 1; j < GlobalVariables.mListRed.get(day).size(); j++)
                                    if (!GlobalVariables.mListRed.get(day).get(j).getLessonName().equals("") || !GlobalVariables.mListRed.get(day).get(j).getLessonType().equals("") || !GlobalVariables.mListRed.get(day).get(j).getRoomNumber().equals("") || !GlobalVariables.mListRed.get(day).get(j).getTeacherName().equals("")) {
                                        add = true;
                                        num = j;
                                        break;
                                    }
                                if (add)
                                    break;
                            }
                        } else if (GlobalVariables.weekType.equals("Green")) {
                            if (GlobalVariables.mListGreen.get(day).size() != 0)
                                for (int j = i + 1; j < GlobalVariables.mListGreen.get(day).size(); j++)
                                    if (!GlobalVariables.mListGreen.get(day).get(j).getLessonName().equals("") || !GlobalVariables.mListGreen.get(day).get(j).getLessonType().equals("") || !GlobalVariables.mListGreen.get(day).get(j).getRoomNumber().equals("") || !GlobalVariables.mListGreen.get(day).get(j).getTeacherName().equals("")) {
                                        add = true;
                                        num = j;
                                        break;
                                    }
                            if (add)
                                break;
                        }
                    }
                } else if (i == len - 1 && calendar.before(itmL)) {
                    if (GlobalVariables.weekType.equals("Red")) {
                        if (!GlobalVariables.mListRed.get(day).get(i).getLessonName().equals("") || !GlobalVariables.mListRed.get(day).get(i).getLessonType().equals("") || !GlobalVariables.mListRed.get(day).get(i).getRoomNumber().equals("") || !GlobalVariables.mListRed.get(day).get(i).getTeacherName().equals("")) {
                            num = i;
                            break;
                        } else {
                            day = cycle(day + 1);
                            break;
                        }
                    } else {
                        if (!GlobalVariables.mListGreen.get(day).get(i).getLessonName().equals("") || !GlobalVariables.mListGreen.get(day).get(i).getLessonType().equals("") || !GlobalVariables.mListGreen.get(day).get(i).getRoomNumber().equals("") || !GlobalVariables.mListGreen.get(day).get(i).getTeacherName().equals("")) {
                            num = i;
                            break;
                        } else {
                            day = cycle(day + 1);
                            break;
                        }
                    }
                } else if (i == len - 1 && calendar.after(itmL)) {
                    day = cycle(day + 1);
                    break;
                }
            }

            pager.setCurrentItem(day);
            CurrentPosition = day;

            lstView = (ListView) PageFragment.vArr[CurrentPosition].findViewById(R.id.listView);
            lstView.smoothScrollToPosition(num);

            new CountDownTimer(200, 200){
                public void onTick(long millisUntilFinished){

                }
                public void onFinish(){
                    color = Color.TRANSPARENT;
                    Drawable background = findViewById(android.R.id.content).getBackground();
                    if (background instanceof ColorDrawable)
                        color = ((ColorDrawable) background).getColor();

                    getViewByPosition(num, lstView).setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.colorGreen));
                    AlphaAnimation animation1 = new AlphaAnimation(1.0f, 1.0f);
                    animation1.setDuration(2000); //время подсветки
                    getViewByPosition(num, lstView).startAnimation(animation1);

                    animation1.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            getViewByPosition(num, lstView).setBackgroundColor(color);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {
                        }
                    });
                }
            }.start();

        } catch (Exception e){
            e.printStackTrace();
            Toast.makeText(MyAdapter.getContext(), "ERROR", Toast.LENGTH_SHORT).show();
        }
    }

    private View getViewByPosition(int pos, ListView listView) {
            final int firstListItemPosition = listView.getFirstVisiblePosition();
            final int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;

            if (pos < firstListItemPosition || pos > lastListItemPosition) {
                return listView.getAdapter().getView(pos, null, listView);
            } else {
                final int childIndex = pos - firstListItemPosition;
                return listView.getChildAt(childIndex);
            }
    }

    private int cycle(int day){
        boolean add = false;
        for (int j = 0; j < 14; j++){
            if (day == 7 && GlobalVariables.twoWeeksMode == 1 && GlobalVariables.wk != 0){
                day = 0;
                fab.performClick();
            } else if (day == 7)
                day = 0;
            if (GlobalVariables.weekType.equals("Red")) {
                if (GlobalVariables.mListRed.get(day).size() != 0) {
                    for (int i = 0; i < GlobalVariables.mListRed.get(day).size(); i++)
                        if (!GlobalVariables.mListRed.get(day).get(i).getLessonName().equals("") || !GlobalVariables.mListRed.get(day).get(i).getLessonType().equals("") || !GlobalVariables.mListRed.get(day).get(i).getRoomNumber().equals("") || !GlobalVariables.mListRed.get(day).get(i).getTeacherName().equals("")){
                            add = true;
                            num = i;
                            break;
                        }
                    if (add)
                        break;
                    else
                        day++;
                } else
                    day++;
            } else if (GlobalVariables.weekType.equals("Green"))
                if (GlobalVariables.mListGreen.get(day).size() != 0){
                    for (int i = 0; i < GlobalVariables.mListGreen.get(day).size(); i++)
                        if (!GlobalVariables.mListGreen.get(day).get(i).getLessonName().equals("") || !GlobalVariables.mListGreen.get(day).get(i).getLessonType().equals("") || !GlobalVariables.mListGreen.get(day).get(i).getRoomNumber().equals("") || !GlobalVariables.mListGreen.get(day).get(i).getTeacherName().equals("")){
                            add = true;
                            num = i;
                            break;
                        }
                    if (add)
                        break;
                    else
                        day++;
                } else
                    day++;
        }
        return day;
    }

    private int getDayOfweek(int day){
        switch (day) {
            case 1:  return 7;
            case 2:  return 1;
            case 3:  return 2;
            case 4:  return 3;
            case 5:  return 4;
            case 6:  return 5;
            case 7:  return 6;
            default: return 1;
        }
    }
    public static void verifyPermissions(Activity activity) {
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permission1 = ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_PHONE_STATE);

        if (permission != PackageManager.PERMISSION_GRANTED || permission1 != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, REQUEST_PERMISSIONS_STORAGE);
        }
    }

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    class FtpSrv extends AsyncTask<Void, Void, Void> {
        ProgressDialog dialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(MyAdapter.getContext());
            dialog.setMessage("Загрузка...");
            dialog.setCancelable(false);
            dialog.setInverseBackgroundForced(false);
            dialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            FTPClient ftp = null;
            String ip = "aberon.atwebpages.com";
            String user = "2102708_guest";
            String pass = "12345678M";

            try
            {
                ftp = new FTPClient();
                ftp.connect(ip);

                if (ftp.login(user, pass))
                {
                    if (!upload) {
                        ftp.enterLocalPassiveMode();
                        ftp.setFileType(FTP.BINARY_FILE_TYPE);
                        String data = getApplicationInfo().dataDir + "/databases/" + GlobalVariables.id + ".db";

                        OutputStream out = new FileOutputStream(new File(data));
                        boolean result = ftp.retrieveFile(xId + ".db", out);
                        out.close();
                        if (result) Log.v("download result", "succeeded");
                        ftp.logout();
                        ftp.disconnect();
                    }
                    else{
                        ftp.enterLocalPassiveMode();
                        ftp.setFileType(FTP.BINARY_FILE_TYPE);
                        String data = getApplicationInfo().dataDir + "/databases/" + GlobalVariables.id + ".db";
                        FileInputStream in = new FileInputStream(new File(data));
                        boolean result = ftp.storeFile("/" + GlobalVariables.id + ".db", in);
                        in.close();

                        if (result) Log.v("upload result", "succeeded");
                        ftp.logout();
                        ftp.disconnect();
                    }
                }
            }
            catch (Exception e)
            {
                Log.v("download result","failed");
                e.printStackTrace();
                Toast.makeText(MyAdapter.getContext(), "ERROR", Toast.LENGTH_SHORT).show();
            }
            return null;
        }

        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            dialog.dismiss();
            if (!upload){
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Перезагрузка");
                builder.setMessage("Для применения изменией приложение будет выключено");
                builder.setPositiveButton("Ок", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int arg1) {
                        android.os.Process.killProcess(android.os.Process.myPid());
                    }
                });
                builder.setCancelable(false);
                AlertDialog alert = builder.create();
                alert.show();
                xId = "";
            }
        }
    }

    class FtpInfo extends AsyncTask<Void, Void, Void> {
        ProgressDialog dialog;
        String tm = "";
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(MyAdapter.getContext());
            dialog.setMessage("Загрузка...");
            dialog.setCancelable(false);
            dialog.setInverseBackgroundForced(false);
            dialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            FTPClient ftp = null;
            String ip = "aberon.atwebpages.com";
            String user = "2102708_guest";
            String pass = "12345678M";

            try
            {
                ftp = new FTPClient();
                ftp.connect(ip);

                if (ftp.login(user, pass))
                {
                    tm = ftp.getModificationTime("/" + xId + ".db");
                }
            }
            catch (Exception e)
            {
                Log.v("download result", "failed");
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            dialog.dismiss();
            try {
                DateFormat dfm = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                dfm.setTimeZone(TimeZone.getTimeZone("Atlantic/Azores"));
                String strn = tm.substring(0, 4) + "-" + tm.substring(4, 6) + "-" + tm.substring(6, 8) + " " + tm.substring(8, 10) + ":" + tm.substring(10, 12) + ":" + tm.substring(12, 14);
                Date d = dfm.parse(strn);

                DateFormat iso = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                iso.setTimeZone(TimeZone.getTimeZone("Atlantic/Azores"));
                System.out.println(iso.format(d));

                iso.setTimeZone(TimeZone.getDefault());
                System.out.println(iso.format(d));

                modTime = iso.format(d);
            } catch (Exception e){
                modTime = "неизвестно";
            }
            String st = "";
            if (upload)
                st = "сервере";
            else
                st = "устройстве";

            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Загрузка");
            if (!upload && modTime.equals("такой базы не существует"))
                builder.setMessage("База пуста. Вы действительно хотите ее загрузить?\nВся текущая база на " + st + " будет безвозвратно удалена.");
            else
                builder.setMessage("Последнее изменение базы на сервере было: " + modTime + "\nВся текущая база на " + st + " будет безвозвратно удалена.\nВы уверены?");
            builder.setPositiveButton("Да", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int arg1) {
                    new FtpSrv().execute();
                }
            });
            builder.setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int arg1) {
                    dialog.cancel();
                }
            });
            builder.setCancelable(true);
            AlertDialog alert = builder.create();
            alert.show();
        }
    }
}

