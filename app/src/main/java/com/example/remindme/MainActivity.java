package com.example.remindme;

import androidx.annotation.Nullable;
import java.util.Random;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.ActionBar;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.TimeZone;

import static android.app.Notification.FLAG_AUTO_CANCEL;

public class MainActivity extends AppCompatActivity {
   // Button mReminder;
    DatabaseHelper databaseHelper;
    Intent starterIntent;
    int i=0;
    Random random;
    private Dialog dialog;

    private final String CHANNEL_ID = "Personal Notification";
    private final int NOTIFICATION_ID = 001;
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ListView list = findViewById(R.id.reminders);
        getSupportActionBar().setTitle(R.string.titleBar);
        starterIntent = getIntent();
        databaseHelper = new DatabaseHelper(this);
        random = new Random();
        createNotificationChannel();
        FloatingActionButton fab = findViewById(R.id.fab);
        ArrayList<mSingleRow> arrayList = new ArrayList<>();
        mAddToList(arrayList);
        CustomAdapter customAdapter = new CustomAdapter(this,arrayList);
        list.setAdapter(customAdapter);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addReminder();
            }
        });


    }
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.abt:
                Intent i = new Intent(MainActivity.this,AboutScreen.class);
                startActivity(i);
                finish();
                return true;
        }
        return false;
    }
    private void mAddToList(ArrayList<mSingleRow> arrayList) {
        Cursor data = databaseHelper.display();
        if(data.getCount()==0){
            Toast.makeText(this, R.string.noRemider, Toast.LENGTH_SHORT).show();

        }else {

            while(data.moveToNext()){
                arrayList.add(new mSingleRow(data.getString(0),data.getString(1),data.getString(2)));
            }
        }

    }
    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = CHANNEL_ID;
            String description = CHANNEL_ID;
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager =(NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
        }
    }
    public void addReminder(){

        final String[] mssg = new String[1];
        dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.dialogforreminder);
        dialog.setCanceledOnTouchOutside(true);
        final TextView textView = dialog.findViewById(R.id.date);
        Button add;
        add = dialog.findViewById(R.id.addButton);
        final EditText message = dialog.findViewById(R.id.message);
        dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
        final Calendar newCalender = Calendar.getInstance();
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog dialog = new DatePickerDialog(MainActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, final int year, final int month, final int dayOfMonth) {

                        final Calendar newDate = Calendar.getInstance();
                        Calendar newTime = Calendar.getInstance();
                        TimePickerDialog time = new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                                newDate.set(year,month,dayOfMonth,hourOfDay,minute,0);
                                Calendar tem = Calendar.getInstance();
                                Log.w("TIME",System.currentTimeMillis()+"");
                                if(newDate.getTimeInMillis()-tem.getTimeInMillis()>0)
                                    textView.setText(newDate.getTime().toString());
                                else
                                    Toast.makeText(MainActivity.this,"Invalid time",Toast.LENGTH_SHORT).show();

                            }
                        },newTime.get(Calendar.HOUR_OF_DAY),newTime.get(Calendar.MINUTE),true);
                        time.show();
                    }
                },newCalender.get(Calendar.YEAR),newCalender.get(Calendar.MONTH),newCalender.get(Calendar.DAY_OF_MONTH));

                dialog.getDatePicker().setMinDate(System.currentTimeMillis());
                dialog.show();

            }
        });
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(textView.getText().length() > 0 ){
                    mSetBackEnd(textView,message);

                }else{
                    Toast.makeText(getApplicationContext(), R.string.nullAlert, Toast.LENGTH_SHORT).show();

                }
            }
        });
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

    }
    public void mSetBackEnd(TextView textView,EditText message){
        String msg;

        Date remind = new Date(textView.getText().toString().trim());
         Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+5:30"));
            calendar.setTime(remind);
            calendar.set(Calendar.SECOND, 0);
            msg = message.getText().toString();
            Log.e("ID", i + "");
            i = random.nextInt(100);
            Intent intent = new Intent(MainActivity.this, ReminderBrodcast.class);
            intent.putExtra("reminder", msg);
            intent.putExtra("requestCode", Integer.toString(i));
            PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, i, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            mInsertIntoDataBase(msg, remind.toString(), i);
            dialog.dismiss();
            finish();
            overridePendingTransition(0, 0);
            startActivity(getIntent());
            overridePendingTransition(0, 0);
            Toast.makeText(MainActivity.this, R.string.RemiderSet, Toast.LENGTH_LONG).show();




    }
    public void mInsertIntoDataBase(String task,String date,int brodcast_code){
        Boolean result = databaseHelper.insertdata(task,date,brodcast_code);
        if(result){
            Toast.makeText(this, "Data Inserted", Toast.LENGTH_SHORT).show();
        }

    }

}
class mSingleRow {
    String id,task,date;
    mSingleRow(String id,String task,String date){
        this.id = id;
        this.task = task;
        this.date = date;
    }
}