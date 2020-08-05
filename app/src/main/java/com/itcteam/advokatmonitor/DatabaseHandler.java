package com.itcteam.advokatmonitor;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class DatabaseHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "login_save";
    private static final String TABLE_SETTINGS = "settings";
    private static final String KEY_AUTO = "autologin";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_TOKEN = "token";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_NAME = "name";
    private static final String KEY_LEVEL = "level";
    private static final String KEY_UID = "UID";
    String username;
    String password;
    String token;
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
                + KEY_AUTO + " INTEGER , " + KEY_NAME + " TEXT, "  + KEY_EMAIL + " TEXT, " + KEY_UID + " INTEGER , "
                + KEY_LEVEL + " INTEGER , " + KEY_TOKEN + " TEXT " + " ) ";
        Log.w("BENTUK QUERY",CREATE_CONTACTS_TABLE);
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SETTINGS);
        onCreate(db);
    }

    public boolean checkRow(){
        db = this.getReadableDatabase();
        Cursor mCursor = db.rawQuery("SELECT * FROM " + TABLE_SETTINGS, null);
        if (mCursor.moveToFirst())
        {
            Log.w("checkRow","Ada row");
            return true;
        }
        else
        {
            Log.w("checkRow","Tidak Ada row");
            return false;
        }
    }

    public boolean insertFirstRow(){
        db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_AUTO, 0 );
        if (db.insert(TABLE_SETTINGS, null, contentValues)!=-1){
            Log.w("insertFirstRow","Berhasil");
            return true;
        }else{
            Log.w("insertFirstRow","Gagal");
            return false;
        }
    }

    public boolean autoLogin(String response) {
        Boolean c;
        if (!checkRow()) {
            if (insertFirstRow()) {
                Log.w("updateSession di habis Insert","Mencoba");
                c = updateSession(response);
                Log.w("updateSession di habis Insert","Nilai : "+Boolean.toString(c));
            } else {
                c = false;
            }
        } else{
            c = updateSession(response);
            Log.w("updateSession jika ada row","Nilai : "+Boolean.toString(c));
        }
        Log.w("Return C ",Boolean.toString(c));
        return  c;
    }

    public boolean checkLogin() {
        db = this.getReadableDatabase();
        Cursor res = db.rawQuery( "select * from "+TABLE_SETTINGS, null );
        res.moveToFirst();
        if (res.moveToLast()) {
            if (res.getInt(0)==1){
                return true;
            }
            else{
                return false;
            }
        }else {
            Log.e("error not found", "data can't be found or database empty");
            return false;
        }
    }

    public String getToken() {
        db = this.getReadableDatabase();
        Cursor res = db.rawQuery( "select * from "+TABLE_SETTINGS, null );
        res.moveToFirst();
        if (res.moveToLast()) {
            return res.getString(5);
        }else {
            Log.e("error not found", "data can't be found or database empty");
            return null;
        }
    }

    public void reLogin() {
        db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_AUTO, 0);
        db.update(TABLE_SETTINGS, contentValues, null, null);
    }

    public Integer getLevel() {
        db = this.getReadableDatabase();
        Cursor res = db.rawQuery( "select * from "+TABLE_SETTINGS, null );
        res.moveToFirst();
        if (res.moveToLast()) {
            return res.getInt(4);
        }else {
            Log.e("error not found", "data can't be found or database empty");
            return null;
        }
    }

    private boolean updateSession(String response) {
        db = this.getWritableDatabase();
        JSONObject reader;
        try {
            reader = new JSONObject(response);
            level = reader.getInt("level");
            token = reader.getString("token");
            email = reader.getString("email");
            uid = reader.getInt("uid");
            name = reader.getString("name");
            ContentValues contentValues = new ContentValues();
            contentValues.put(KEY_LEVEL, level);
            contentValues.put(KEY_EMAIL, email);
            contentValues.put(KEY_TOKEN, token);
            contentValues.put(KEY_UID, uid);
            contentValues.put(KEY_NAME, name);
            contentValues.put(KEY_AUTO, 1);
            Integer ret = db.update(TABLE_SETTINGS, contentValues, null, null);
            if (ret>0)
                return true;
            else{
                return false;
            }
        }catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
    }

    }
