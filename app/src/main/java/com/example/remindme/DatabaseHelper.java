package com.example.remindme;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "Reminders.db";
    public static final String TABLE_NAME = "Reminder";
    public static final String Col_1 = "id";
    public static final String Col_2 = "date_and_time";
    public static final String Col_3 = "task";
    public static final String Col_4 = "brodcast_id";
    private SQLiteDatabase db;


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
         db = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table "+TABLE_NAME+"(id INTEGER PRIMARY KEY AUTOINCREMENT,date_and_time TEXT,task TEXT,brodcast_id INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
    public boolean insertdata(String task, String date,int b_id){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Col_2,task);
        contentValues.put(Col_3,date);
        contentValues.put(Col_4,b_id);
        long res = db.insert(TABLE_NAME,null,contentValues);
        if(res==-1){
            return false;
        }
        else{
            return true;
        }
    }
    public boolean mDeleteReminder(String id){
        int id_;
        id_ = Integer.valueOf(id);
         return db.delete(TABLE_NAME, "id" + "=" + id_, null) > 0;
    }
    public boolean mAfterDeleteReminder(String id){
        int id_;
        id_ = Integer.valueOf(id);
        return db.delete(TABLE_NAME, Col_4 + "=" + id_, null) > 0;
    }
    public Cursor display(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cur = db.rawQuery("select * from "+TABLE_NAME+" order by id desc",null);
        return cur;
    }

    public int mReturnId(String id){
        int id_,mRequestId=1;
        //String mRequestId;
        id_ = Integer.valueOf(id);
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cur = db.rawQuery("SELECT * FROM "+TABLE_NAME+" WHERE "+Col_1+" = "+id_,null);
        if(cur != null && cur.moveToFirst() ){

            mRequestId = cur.getInt(cur.getColumnIndex(Col_4));
            cur.close();
        }
        return mRequestId;


    }
}
