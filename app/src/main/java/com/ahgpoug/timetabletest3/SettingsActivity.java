package com.ahgpoug.timetabletest3;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.Calendar;

public class SettingsActivity extends PreferenceActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref);

        LinearLayout root = (LinearLayout)findViewById(android.R.id.list).getParent().getParent().getParent();
        Toolbar toolbar = (Toolbar) LayoutInflater.from(this).inflate(R.layout.settings_toolbar, root, false);
        root.addView(toolbar, 0);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DataBaseIO.writeCfg();
                finish();
            }
        });

        CheckBoxPreference chBox = (CheckBoxPreference) findPreference("twoWeeksModeDisabled");
        chBox.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                if (GlobalVariables.twoWeeksMode == 1) {
                    GlobalVariables.twoWeeksMode = 0;
                    Snackbar.make(findViewById(android.R.id.content), "Изменения вступят в силу после перезапуска программы. По умолчанию будет стоять последняя выбранная неделя.", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                } else {
                    GlobalVariables.twoWeeksMode = 1;
                }
                return false;
            }
        });

        CheckBoxPreference startNotifications = (CheckBoxPreference) findPreference("startNotifications");
        startNotifications.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                if (GlobalVariables.startNotifications == 1) {
                    GlobalVariables.startNotifications = 0;
                    Snackbar.make(findViewById(android.R.id.content), "Уведомление отключено", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                } else {
                    GlobalVariables.startNotifications = 1;
                    Snackbar.make(findViewById(android.R.id.content), "Уведомление включено", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                }
                return false;
            }
        });

        Preference setWeek = (Preference) findPreference("setWeek");
        setWeek.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                Calendar calendar = Calendar.getInstance();
                GlobalVariables.wk = calendar.get(Calendar.WEEK_OF_YEAR);
                GlobalVariables.startWeek = GlobalVariables.weekType;
                if (GlobalVariables.weekType.equals("Red"))
                    Snackbar.make(findViewById(android.R.id.content), "Текущая неделя будет считаться красной", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                else
                    Snackbar.make(findViewById(android.R.id.content), "Текущая неделя будет считаться зеленой", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                return false;
            }
        });

        Preference delWeek = (Preference) findPreference("delWeek");
        delWeek.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                GlobalVariables.wk = 0;
                GlobalVariables.startWeek = null;
                Snackbar.make(findViewById(android.R.id.content), "Текущая неделя больше не считается начальной", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                return false;
            }
        });

        Preference showId = (Preference) findPreference("showId");
        showId.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Уникальный идентификатор");
                builder.setMessage(GlobalVariables.id);
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int arg1) {

                    }
                });
                builder.setNeutralButton("Скопировать в буфер обмена", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int arg1) {
                        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                        ClipData clip = ClipData.newPlainText("Текст", GlobalVariables.id);
                        clipboard.setPrimaryClip(clip);
                        Toast.makeText(MyAdapter.getContext(), "ID скопироан в буфер обмена", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setCancelable(true);
                AlertDialog alert = builder.create();
                alert.show();
                return false;
            }
        });
    }

    @Override
    public void onBackPressed() {
        DataBaseIO.writeCfg();
        finish();
        return;
    }

    private Context getContext(){
        return this;
    }
}

