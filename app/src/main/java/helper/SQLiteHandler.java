package helper;

/**
 * Created by Sajir Mohammed on 30-Jul-17.
 */
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

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
    private static final String TABLE_EXAM_TIMING = "timings";

    // Login Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_TOKEN = "token";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_ROLE = "role";
    private static final String KEY_TIMEZONE = "timezone";
    private static final String KEY_DBID = "dbid";

    // Regular/Exam Timings Table Columns names
    private static final String KEY_TID = "id";
    private static final String KEY_DAY = "day";
    private static final String KEY_TIME = "timing";
    //private static final String KEY_DATE = "date";

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
                + KEY_TIMEZONE + " TEXT" + ")";
        db.execSQL(CREATE_LOGIN_TABLE);
        Log.d(TAG, "User Database tables created");

        //Regular Timing Table Creation
        String CREATE_REGULAR_TIMING_TABLE = "CREATE TABLE " + TABLE_REGULAR_TIMING + "("
                + KEY_TID + " INTEGER PRIMARY KEY,"
                + KEY_DAY + " TEXT," + KEY_TIME + " TEXT" + ")";
        db.execSQL(CREATE_REGULAR_TIMING_TABLE);
        Log.d(TAG, "Regular Timing Database tables created");

        //Exam Timing Table Creation
        String CREATE_EXAM_TIMING_TABLE = "CREATE TABLE " + TABLE_EXAM_TIMING + "("
                + KEY_TID + " INTEGER PRIMARY KEY,"
                + KEY_DAY + " TEXT," + KEY_TIME+ " TEXT" + ")";
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

    //Storing user details in database
    public void addUser(String token, String email, String role,String dbids, String timezone) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_DBID, dbids); // ID
        values.put(KEY_TOKEN, token); // Token
        values.put(KEY_EMAIL, email); // Email
        values.put(KEY_ROLE, role); // Role
        values.put(KEY_TIMEZONE, timezone); // TimeZone

        // Inserting Row
        long id = db.insert(TABLE_USER, null, values);
        db.close(); // Closing database connection

        Log.d(TAG, "New user inserted into sqlite: " + id);
    }
    //Storing regular timings details in database
    public void addRegularTimings(String day, String timings) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_DAY, day); // ID
        values.put(KEY_TIME, timings); // Token
        // Inserting Row
        long id = db.insert(TABLE_REGULAR_TIMING, null, values);
        db.close(); // Closing database connection

        Log.d(TAG, "New regular timing inserted into sqlite: " + id);
    }
    //Storing exam timings details in database
    public void addExamTimings(String day, String timings) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_DAY, day); // ID
        values.put(KEY_TIME, timings); // Token
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
        }
        cursor.close();
        db.close();
        // return user
        Log.d(TAG, "Fetching user from Sqlite: " + user.toString());

        return user;
    }

    //Getting regular timings data from database
    public HashMap<String, String> getRegularTimingDetails() {
        HashMap<String, String> regularTimings = new HashMap<String, String>();
        String selectQuery = "SELECT  * FROM " + TABLE_REGULAR_TIMING;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            regularTimings.put(KEY_DAY, cursor.getString(1));
            regularTimings.put(KEY_TIME, cursor.getString(2));
        }
        cursor.close();
        db.close();
        // return user
        Log.d(TAG, "Fetching user from Sqlite: " + regularTimings.toString());

        return regularTimings;
    }

    //Getting exam timings data from database
    public HashMap<String, String> getExamTimingDetails() {
        HashMap<String, String> examTimings = new HashMap<String, String>();
        String selectQuery = "SELECT  * FROM " + TABLE_EXAM_TIMING;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            examTimings.put(KEY_DAY, cursor.getString(1));
            examTimings.put(KEY_TIME, cursor.getString(2));
        }
        cursor.close();
        db.close();
        // return user
        Log.d(TAG, "Fetching user from Sqlite: " + examTimings.toString());

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
