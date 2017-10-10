package helper;

/**
 * Created by Sector4 Dev on 30-Jul-17.
 */
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

public class SQLiteHandler extends SQLiteOpenHelper {

    private static final String TAG = SQLiteHandler.class.getSimpleName();

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "android_api";

    // Login table name
    private static final String TABLE_USER = "user";
    //Regular Timing table name
    private static final String TABLE_REGULAR_TIMING = "timings";
    //Regular Timing table name
    private static final String TABLE_EXAM_TIMING = "examtimings";

    // Login Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_TOKEN = "token";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_ROLE = "role";
    private static final String KEY_TIMEZONE = "timezone";
    private static final String KEY_DBID = "dbid";
    private static final String KEY_USER = "username";
    private static final String KEY_SCHOOL = "institution";
    private static final String KEY_LOCATION = "location";

    // Regular/Exam Timings Table Columns names
    private static final String KEY_TID = "id";
    private static final String KEY_DAY = "day";
    private static final String KEY_TIME = "timing";
    private static final String KEY_AUDIO = "audio";
    private static final String KEY_DESC = "description";

    public SQLiteHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_LOGIN_TABLE = "CREATE TABLE " + TABLE_USER + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_DBID + " TEXT," + KEY_TOKEN + " TEXT,"
                + KEY_EMAIL + " TEXT UNIQUE," + KEY_ROLE + " TEXT,"
                + KEY_TIMEZONE + " TEXT,"+ KEY_USER + " TEXT,"
                + KEY_SCHOOL + " TEXT," + KEY_LOCATION + " TEXT" + ")";
        db.execSQL(CREATE_LOGIN_TABLE);
        Log.d(TAG, "User Database tables created");

        //Regular Timing Table Creation
        String CREATE_REGULAR_TIMING_TABLE = "CREATE TABLE " + TABLE_REGULAR_TIMING + "("
                + KEY_TID + " INTEGER PRIMARY KEY,"
                + KEY_DAY + " TEXT," + KEY_TIME + " TEXT," + KEY_AUDIO + " TEXT," + KEY_DESC + " TEXT" + ")";
        db.execSQL(CREATE_REGULAR_TIMING_TABLE);
        Log.d(TAG, "Regular Timing Database tables created");

        //Exam Timing Table Creation
        String CREATE_EXAM_TIMING_TABLE = "CREATE TABLE " + TABLE_EXAM_TIMING + "("
                + KEY_TID + " INTEGER PRIMARY KEY,"
                + KEY_DAY + " TEXT," + KEY_TIME+ " TEXT," + KEY_AUDIO + " TEXT," + KEY_DESC + " TEXT" + ")";
        db.execSQL(CREATE_EXAM_TIMING_TABLE);
        Log.d(TAG, "Exam Timing Database tables created");
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_REGULAR_TIMING);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EXAM_TIMING);

        // Create tables again
        onCreate(db);
    }

    public void ResetDB(){
        SQLiteDatabase db=this.getWritableDatabase();
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_REGULAR_TIMING);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EXAM_TIMING);
        // Create tables again
        onCreate(db);
    }

    public boolean CheckexamScheduleAvail(){
        String selectQuery = "SELECT  * FROM " + TABLE_EXAM_TIMING;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cur = db.rawQuery(selectQuery, null);
        //Log.d("Table Status",cur.getCount()+"");
        boolean statustable=false;
        if (cur!=null){
            cur.moveToFirst();
            if (cur.getCount() <= 0) {
                //Log.d("SQLITE status", String.valueOf(cur.getInt(0)));
                //Log.d("SQLite Status","Empty Exam schedules");
                statustable=false;
            }else{
                //Log.d("SQLITE status", String.valueOf(cur.getInt(0)));
                //Log.d("SQLite Status","Something there in Exam schedules");
                statustable=true;
            }
        }
        return statustable;
    }

    public boolean CheckregularScheduleAvail(){
        String selectQuery = "SELECT  * FROM " + TABLE_REGULAR_TIMING;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cur = db.rawQuery(selectQuery, null);
        //Log.d("Table Status",cur.getCount()+"");
        boolean statustable=false;
        if (cur!=null){
            cur.moveToFirst();
            if (cur.getCount() <= 0) {
                //Log.d("SQLite Status","Empty Regular schedules");
                statustable=false;
            }else{
                //Log.d("SQLite Status","Something there in Regular schedules");
                statustable=true;
            }
        }
        return statustable;
    }

    //Storing user details in database
    public void addUser(String token, String email, String role,String dbids, String timezone,String username,String school,String location) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_DBID, dbids); // ID
        values.put(KEY_TOKEN, token); // Token
        values.put(KEY_EMAIL, email); // Email
        values.put(KEY_ROLE, role); // Role
        values.put(KEY_TIMEZONE, timezone); // TimeZone
        values.put(KEY_USER, username); // Username
        values.put(KEY_SCHOOL, school); // School
        values.put(KEY_LOCATION, location); // Location

        // Inserting Row
        long id = db.insert(TABLE_USER, null, values);
        db.close(); // Closing database connection

        Log.d(TAG, "New user inserted into sqlite: " + id);
    }
    //Storing regular timings details in database
    public void addRegularTimings(String day, String timings, String audio,String description) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_DAY, day);
        values.put(KEY_TIME, timings);
        values.put(KEY_AUDIO, audio);
        values.put(KEY_DESC, description);
        // Inserting Row
        long id = db.insert(TABLE_REGULAR_TIMING, null, values);
        db.close(); // Closing database connection

        Log.d(TAG, "New regular timing inserted into sqlite: " + id);
    }
    //Storing exam timings details in database
    public void addExamTimings(String day, String timings, String audio,String description) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_DAY, day);
        values.put(KEY_TIME, timings);
        values.put(KEY_AUDIO, audio);
        values.put(KEY_DESC, description);
        // Inserting Row
        long id = db.insert(TABLE_EXAM_TIMING, null, values);
        db.close(); // Closing database connection

        Log.d(TAG, "New exam timing inserted into sqlite: " + id);
    }

    //Getting user data from database
    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<String, String>();
        String selectQuery = "SELECT  * FROM " + TABLE_USER;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            user.put("dbid", cursor.getString(1));
            user.put("token", cursor.getString(2));
            user.put("email", cursor.getString(3));
            user.put("role", cursor.getString(4));
            user.put("timezone", cursor.getString(5));
            user.put("username", cursor.getString(6));
            user.put("institution", cursor.getString(7));
            user.put("location", cursor.getString(8));
        }
        cursor.close();
        db.close();
        // return user
        Log.d(TAG, "Fetching user from Sqlite: " + user.toString());

        return user;
    }

    //Getting regular timings data from database
    public ArrayList<String> getRegularTimingDetails() {
        ArrayList<String> regularTimings = new ArrayList<String>();
        String selectQuery = "SELECT  * FROM " + TABLE_REGULAR_TIMING;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if (cursor.getCount()>0){
            do {
                regularTimings.add(cursor.getString(1)+"|"+cursor.getString(2)+"|"+cursor.getString(3)+"|"+cursor.getString(4));

            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        //Log.d(TAG, "Fetching exam timings from Sqlite: " + regularTimings.toString());
        return regularTimings;
    }

    //Getting exam timings data from database
    public ArrayList<String> getExamTimingDetails() {
        //HashMap<String, String> examTimings = new HashMap<String, String>();
        ArrayList<String> examTimings = new ArrayList<String>();
        String selectQuery = "SELECT  * FROM " + TABLE_EXAM_TIMING;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if (cursor.getCount()>0){
            do {
                examTimings.add(cursor.getString(1)+"|"+cursor.getString(2)+"|"+cursor.getString(3)+"|"+cursor.getString(4));

            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        //Log.d(TAG, "Fetching exam timings from Sqlite: " + examTimings.toString());
        return examTimings;
    }

    //Re crate database Delete all tables and create them again
    public void deleteUsers() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(TABLE_USER, null, null);
        db.delete(TABLE_REGULAR_TIMING, null, null);
        db.delete(TABLE_EXAM_TIMING, null, null);
        db.close();

        Log.d(TAG, "Deleted all user info from sqlite");
    }

}
