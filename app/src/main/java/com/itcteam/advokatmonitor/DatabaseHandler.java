package com.itcteam.advokatmonitor;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class DatabaseHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "login_save";
    private static final String TABLE_SETTINGS = "settings";
    private static final String KEY_AUTO = "autologin";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_NAME = "name";
    private static final String KEY_LEVEL = "level";
    private static final String KEY_UID = "UID";
    String username;
    String password;
    Integer level;
    String email;
    Integer uid;
    String name;
    SQLiteDatabase db;

    public DatabaseHandler(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        db = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = " CREATE TABLE " + TABLE_SETTINGS + " ( "
                + KEY_AUTO + " INTEGER , " + KEY_NAME + " TEXT, " + KEY_USERNAME + " TEXT, " + KEY_EMAIL + " TEXT, " + KEY_UID + " INTEGER , "
                + KEY_LEVEL + " INTEGER, " + KEY_PASSWORD + " TEXT " + " ) ";
        Log.w("BENTUK QUERY",CREATE_CONTACTS_TABLE);
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SETTINGS);
        onCreate(db);
    }


    public boolean autoLogin(String response) {
        JSONObject reader;
        try {
            reader = new JSONObject(response);
            username = reader.getString("username");
            password = reader.getString("password");
            level = reader.getInt("level");
            email = reader.getString("email");
            uid = reader.getInt("uid");
            name = reader.getString("name");
            ContentValues contentValues = new ContentValues();
            contentValues.put(KEY_USERNAME, username);
            contentValues.put(KEY_PASSWORD, password);
            contentValues.put(KEY_LEVEL, level);
            contentValues.put(KEY_EMAIL, email);
            contentValues.put(KEY_UID, uid);
            contentValues.put(KEY_NAME, name);
            contentValues.put(KEY_AUTO, 1);
            Long ret = db.insert(TABLE_SETTINGS, null, contentValues);
            if (ret==-1)
                return false;
            else{
                return true;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Integer getLevel() {
        return level;
    }
}
