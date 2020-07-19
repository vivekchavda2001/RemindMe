package com.example.remindme;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import static android.app.PendingIntent.FLAG_CANCEL_CURRENT;
import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;
import static android.content.Context.ALARM_SERVICE;

public class CustomAdapter extends BaseAdapter {
    ArrayList<mSingleRow> list;
    Context c;
    private Dialog dialog;
    DatabaseHelper databaseHelper;
    CustomAdapter(Context c, ArrayList<mSingleRow> list){
        this.c=c;
        this.list = list;
        databaseHelper = new DatabaseHelper(c);

    }
    CustomAdapter()
    {}

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.reminder_row,viewGroup,false);
        TextView task =  row.findViewById(R.id.task);
        TextView date = row.findViewById(R.id.dateAndTime);
        final ImageView delete = row.findViewById(R.id.delete);
        final mSingleRow tempData = list.get(i);
        task.setText(tempData.task);
        date.setText(tempData.date);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    deleteDialog(tempData.id);
            }
        });

        return row;
    }
    public void deleteDialog(String id){
        dialog = new Dialog(c);
        dialog.setContentView(R.layout.delete_confirm_dg);
        dialog.setCanceledOnTouchOutside(true);
        Button yes = dialog.findViewById(R.id.yes);
        Button no = dialog.findViewById(R.id.no);
        dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
        final String delete = id;
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteforBackEnd(delete);
                dialog.dismiss();
            }
        });
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();


    }
    public void mdeleteReminder(String id){
        Boolean res =  databaseHelper.mDeleteReminder(id);
        if(res){
            Toast.makeText(c, "Reminder Deleted", Toast.LENGTH_SHORT).show();
        }
    }
    public void deleteforBackEnd(String id){
        int requestId = databaseHelper.mReturnId(id);
        //Toast.makeText(c, requestId+":"+id+":Reminder Deleted", Toast.LENGTH_SHORT).show();
        Intent intent =  new Intent(c,ReminderBrodcast.class);
        intent.putExtra("remind", "");
        intent.putExtra("request","");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(c,requestId,intent,FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) c.getSystemService(ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
        mdeleteReminder(id);
        ((Activity) c).finish();
        ((Activity) c).overridePendingTransition( 0, 0);
        c.startActivity(((Activity) c).getIntent());
        ((Activity) c).overridePendingTransition( 0, 0);


    }
}