package com.itcteam.advokatmonitor.dbclass;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class DatabaseHandlerAppSave extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "advokatdb";
    private static final String TABLE_SETTINGS = "login";
    private static final String TABLE_KASUS = "kasus";
    private static final String KEY_AUTO = "autologin";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_TOKEN = "token";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_NAME = "name";
    private static final String KEY_LEVEL = "level";
    private static final String KEY_UID = "UID";
    private static final String KEY_SYNC = "sync";
    String username;
    String password;
    String token;
    Integer level;
    String email;
    Integer uid;
    String name;
    SQLiteDatabase db;
    private boolean notif;

    public DatabaseHandlerAppSave(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        db = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = " CREATE TABLE " + TABLE_SETTINGS + " ( "
                + KEY_AUTO + " INTEGER , " + KEY_NAME + " TEXT, "  + KEY_EMAIL + " TEXT, " + KEY_UID + " INTEGER , "
                + KEY_LEVEL + " INTEGER , " + KEY_TOKEN + " TEXT , " + KEY_SYNC + " TEXT ) ";
        Log.w("BENTUK QUERY",CREATE_CONTACTS_TABLE);
        db.execSQL(CREATE_CONTACTS_TABLE);
        String CREATE_KASUS = " CREATE TABLE " + TABLE_KASUS + " (" +
                "  id_masalah int(11) NOT NULL," +
                "  id_p int(11) NULL," +
                "  nama text NOT NULL," +
                "  ktp varchar(16) NOT NULL," +
                "  nohp text NOT NULL," +
                "  email text NOT NULL," +
                "  alamat text NOT NULL," +
                "  deskripsi text NOT NULL," +
                "  jenis_kelamin int(11) NOT NULL ," +
                "  status int(11) NOT NULL ," +
                "  tanggal_jumpa text," +
                "  pekerjaan text," +
                "  tempat_lahir text," +
                "  tanggal_lahir text," +
                "  update_time text NOT NULL" +
                ") ";
        Log.w("BENTUK QUERY",CREATE_KASUS);
        db.execSQL(CREATE_KASUS);
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

    public ArrayList<HashMap<String, String>> getKasus(Integer statusInt){
        db = this.getReadableDatabase();
        ArrayList<HashMap<String, String>> daftarKasus = new ArrayList<>();
        Cursor cursor;
        if (statusInt==4){
            cursor = db.rawQuery( "select * from "+TABLE_KASUS + " where status = 4 OR status = 0", null );
        }else{
            cursor = db.rawQuery( "select * from "+TABLE_KASUS + " where status = " + statusInt, null );
        }
        while (cursor.moveToNext()){
            HashMap<String,String> kasus = new HashMap<>();
            kasus.put("id",Integer.toString(cursor.getInt(cursor.getColumnIndex("id_masalah"))));
            kasus.put("judul",cursor.getString(cursor.getColumnIndex("deskripsi")));
            kasus.put("pengirim",cursor.getString(cursor.getColumnIndex("nama")));
            kasus.put("ktp",cursor.getString(cursor.getColumnIndex("ktp")));
            daftarKasus.add(kasus);
        }
        return daftarKasus;
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

    public void clearDB(){
        db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_KASUS);
    }

    public boolean syncKasus(String response){
        db = this.getWritableDatabase();
        Boolean done = false;
        try {
            JSONObject Jselect = new JSONObject(response);
            if (checkUpdateTime(Jselect.getString("update_time"))){
                clearDB();
                JSONArray jsonArray = new JSONArray(Jselect.getString("kasus"));
                for (int i = 0; i < jsonArray.length(); i++){
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("id_masalah", jsonObject.getInt("id_masalah"));
                    contentValues.put("id_masalah", jsonObject.getInt("id_masalah"));
                    contentValues.put("id_p", jsonObject.getString("id_p"));
                    contentValues.put("nama", jsonObject.getString("nama"));
                    contentValues.put("ktp", jsonObject.getString("ktp"));
                    contentValues.put("nohp", jsonObject.getString("nohp"));
                    contentValues.put("email", jsonObject.getString("email"));
                    contentValues.put("alamat", jsonObject.getString("alamat"));
                    contentValues.put("deskripsi", jsonObject.getString("deskripsi"));
                    contentValues.put("jenis_kelamin", jsonObject.getInt("jenis_kelamin"));
                    contentValues.put("status", jsonObject.getInt("status"));
                    contentValues.put("tanggal_jumpa", jsonObject.getString("tanggal_jumpa"));
                    contentValues.put("pekerjaan", jsonObject.getString("pekerjaan"));
                    contentValues.put("tempat_lahir", jsonObject.getString("tempat_lahir"));
                    contentValues.put("tanggal_lahir", jsonObject.getString("tanggal_lahir"));
                    contentValues.put("update_time", jsonObject.getString("update_time"));
                    long ret = db.insert(TABLE_KASUS, null, contentValues);
                    notif = true;
                    if (ret!=-1){
                        done = true;
                    }else{
                        done = false;
                    }
                }
            }else{
                done = true;
                notif = false;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return done;
    }

    public boolean getNotif(){
        return notif;
    }

    private boolean checkUpdateTime(String update_time) {
        db = this.getReadableDatabase();
        Cursor res = db.rawQuery( "select * from "+TABLE_SETTINGS, null );
        res.moveToFirst();
        boolean keputusan = false;
        if (res.moveToLast()) {
            if (!update_time.equals(res.getString(6))){
//                Log.w("UPD", update_time + "++++" + res.getString(6));
                db = this.getWritableDatabase();
                ContentValues contentValues = new ContentValues();
                contentValues.put("sync", update_time);
                int ret = db.update(TABLE_SETTINGS, contentValues, null, null);
                keputusan = true;
            }else{
                keputusan = false;
            }
        }else {
            keputusan = false;
        }
        Log.e("le", String.valueOf(keputusan));
        return keputusan;
    }
}
