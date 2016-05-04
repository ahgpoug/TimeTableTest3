package com.ahgpoug.timetabletest3;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

public class EnterActivity extends AppCompatActivity {
    private int position;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter);
        Intent intent = getIntent();

        position = Integer.parseInt(intent.getStringExtra("lessonNumber"));

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(GlobalVariables.days[MainActivity.getCurrPosition()] + ", "+ position + " пара");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        EditText lName = (EditText) findViewById(R.id.lessonName);
        EditText lType = (EditText) findViewById(R.id.lessonType);
        EditText roomN = (EditText) findViewById(R.id.roomNumber);
        EditText tName = (EditText) findViewById(R.id.teacherName);
        EditText aText = (EditText) findViewById(R.id.addressText);

        lName.setText(EditActivity.getInfo(position).getLessonName());
        lType.setText(EditActivity.getInfo(position).getLessonType());
        roomN.setText(EditActivity.getInfo(position).getRoomNumber());
        tName.setText(EditActivity.getInfo(position).getTeacherName());
        aText.setText(EditActivity.getInfo(position).getAddressText());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_enter, menu);
        return true;
    }

    @Override
    public void onBackPressed(){
        finish();
        return;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home){
            finish();
        }

        if (id == R.id.action_save) {
            EditText lName = (EditText) findViewById(R.id.lessonName);
            EditText lType = (EditText) findViewById(R.id.lessonType);
            EditText roomN = (EditText) findViewById(R.id.roomNumber);
            EditText tName = (EditText) findViewById(R.id.teacherName);
            EditText aText = (EditText) findViewById(R.id.addressText);

            EditActivity.setNew(lName.getText().toString(), lType.getText().toString(), roomN.getText().toString(), tName.getText().toString(), aText.getText().toString(), position);
            Toast.makeText(getApplicationContext(), "Сохранено", Toast.LENGTH_SHORT).show();
            return true;
        }

        if (id == R.id.action_clear){
            EditText lName = (EditText) findViewById(R.id.lessonName);
            EditText lType = (EditText) findViewById(R.id.lessonType);
            EditText roomN = (EditText) findViewById(R.id.roomNumber);
            EditText tName = (EditText) findViewById(R.id.teacherName);
            EditText aText = (EditText) findViewById(R.id.addressText);

            lName.setText(null);
            lType.setText(null);
            roomN.setText(null);
            tName.setText(null);
            aText.setText(null);
        }


        return super.onOptionsItemSelected(item);
    }
}
