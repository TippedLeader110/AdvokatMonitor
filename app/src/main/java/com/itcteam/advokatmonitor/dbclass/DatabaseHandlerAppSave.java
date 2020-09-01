package com.itcteam.advokatmonitor.dbclass;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
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
    private static final String TABLE_PENGACARA = "pengacara";
    private static final String TABLE_BERKAS = "berkas";
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
        String CREATE_APPSAVE = " CREATE TABLE " + TABLE_SETTINGS + " ( "
                + KEY_AUTO + " INTEGER , " + KEY_NAME + " TEXT, "  + KEY_EMAIL + " TEXT, " + KEY_UID + " INTEGER , "
                + KEY_LEVEL + " INTEGER , " + KEY_TOKEN + " TEXT , " + KEY_SYNC + " TEXT ) ";
//        Log.w("BENTUK QUERY",CREATE_CONTACTS_TABLE);
        db.execSQL(CREATE_APPSAVE);

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

        String CREATE_PENGACARA = " CREATE TABLE " + TABLE_PENGACARA + " ( " +
                "  id int(11) NOT NULL," +
                "  nama text NOT NULL," +
                "  nohp text NOT NULL," +
                "  email text NOT NULL," +
                "  foto text NOT NULL" +
                ") ";
        Log.w("BENTUK QUERY",CREATE_PENGACARA);
        db.execSQL(CREATE_PENGACARA);

        String CREATE_BERKAS = " CREATE TABLE " + TABLE_BERKAS + " ( " +
                "  id_berkas int(11) NOT NULL," +
                "  id_masalah int(11) NOT NULL," +
                "  nama_berkas text ," +
                "  file text" +
                ") ";
        Log.w("BENTUK QUERY",CREATE_BERKAS);
        db.execSQL(CREATE_BERKAS);
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
            mCursor.close();
            db.close();
            return true;
        }
        else
        {
            Log.w("checkRow","Tidak Ada row");
            mCursor.close();
            db.close();
            return false;
        }
    }

    public boolean insertFirstRow(){
        db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_AUTO, 0 );
        if (db.insert(TABLE_SETTINGS, null, contentValues)!=-1){
            Log.w("insertFirstRow","Berhasil");
            db.close();
            return true;
        }else{
            Log.w("insertFirstRow","Gagal");
            db.close();
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
        db.close();
        return  c;
    }

    public boolean checkLogin() {
        db = this.getReadableDatabase();
        Cursor res = db.rawQuery( "select * from "+TABLE_SETTINGS, null );
        res.moveToFirst();
        if (res.moveToLast()) {
            if (res.getInt(0)==1){
                res.close();
                db.close();
                return true;
            }
            else{
                res.close();
                db.close();
                return false;
            }
        }else {
            Log.e("error not found", "data can't be found or database empty (login)");
            res.close();
            db.close();
            return false;
        }
    }

    public String getToken() {
        db = this.getReadableDatabase();
        Cursor res = db.rawQuery( "select * from "+TABLE_SETTINGS, null );
        res.moveToFirst();
        if (res.moveToLast()) {
            String ret = res.getString(5);
            res.close();
            db.close();
            return ret;
        }else {
            Log.e("error not found", "data can't be found or database empty (token)");
            res.close();
            db.close();
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
        cursor.close();
        db.close();
        return daftarKasus;
    }

    public ArrayList<HashMap<String, String>> getKasusPengacara(Integer statusInt){
        db = this.getReadableDatabase();
        ArrayList<HashMap<String, String>> daftarKasus = new ArrayList<>();
        Cursor cursor;
//        Log.e("statusInt" , String.valueOf(statusInt));
        if (statusInt==1){
            cursor = db.rawQuery( "select * from "+TABLE_KASUS + " where status = 2 AND tanggal_jumpa = 'null' ", null );
        }
        else if (statusInt==2){
            cursor = db.rawQuery( "select * from "+TABLE_KASUS + " where status = 2 AND tanggal_jumpa <> 'null' ", null );
        }
        else if (statusInt==3){
            cursor = db.rawQuery( "select * from "+TABLE_KASUS + " where status = 3 ", null );
        }
        else{
            cursor = db.rawQuery( "select * from "+TABLE_KASUS + " where status = 4 OR status = 0", null );
        }

        while (cursor.moveToNext()){
            HashMap<String,String> kasus = new HashMap<>();
            kasus.put("id",Integer.toString(cursor.getInt(cursor.getColumnIndex("id_masalah"))));
            kasus.put("judul",cursor.getString(cursor.getColumnIndex("deskripsi")));
            kasus.put("pengirim",cursor.getString(cursor.getColumnIndex("nama")));
            kasus.put("ktp",cursor.getString(cursor.getColumnIndex("ktp")));
            daftarKasus.add(kasus);
        }
        cursor.close();
        db.close();
        return daftarKasus;
    }

    public void reLogin() {
        db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_AUTO, 0);
        db.update(TABLE_SETTINGS, contentValues, null, null);
        db.close();
    }

    public Integer getLevel() {
        db = this.getReadableDatabase();
        Cursor res = db.rawQuery( "select * from "+TABLE_SETTINGS, null );
        res.moveToFirst();
        if (res.moveToLast()) {
            Integer ret = res.getInt(4);
            res.close();
            db.close();
            return ret;
        }else {
            res.close();
            db.close();
            Log.e("error not found", "data can't be found or database empty (level)");
            return null;
        }
    }

    public String namaPengacara(String id) {
        db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery( "select * from "+TABLE_PENGACARA+ " where id = " + id, null );
        String pengacara = "";
//        cursor.moveToFirst();
        if (cursor.moveToFirst()) {
            pengacara = cursor.getString(cursor.getColumnIndex("nama"));
        }else {
            Log.e("error not found", "data can't be found or database empty (pengacara)");
            pengacara = "Belum Dipilih";
        }
        cursor.close();
        db.close();
        return pengacara;
    }

    public HashMap<String, String> getDetailKasus(Integer id, Integer status) {
        db = this.getReadableDatabase();
        String statusString = "Kasus Baru";
        HashMap<String,String> dataKasus = new HashMap<>();
        Cursor cursor = db.rawQuery( "select * from "+TABLE_KASUS+ " where id_masalah = " + id, null );
        cursor.moveToFirst();
        if (cursor.moveToLast()) {
            if (status == 1){
                statusString = "Kasus Baru";
            }
            else if (status == 2){
                statusString = "Kasus Berjalan";
            }
            else if (status == 3){
                statusString = "Kasus Selesai";
            }
            else if (status == 4){
                statusString = "Kasus Ditutup/Ditolak";
            }
            dataKasus.put("status", statusString);
            dataKasus.put("id",Integer.toString(cursor.getInt(cursor.getColumnIndex("id_masalah"))));
            dataKasus.put("judul",cursor.getString(cursor.getColumnIndex("deskripsi")));
            dataKasus.put("nohp",cursor.getString(cursor.getColumnIndex("nohp")));
            dataKasus.put("email",cursor.getString(cursor.getColumnIndex("email")));
            dataKasus.put("pengirim",cursor.getString(cursor.getColumnIndex("nama")));
            dataKasus.put("ktp",cursor.getString(cursor.getColumnIndex("ktp")));
            dataKasus.put("id_p",cursor.getString(cursor.getColumnIndex("id_p")));
            dataKasus.put("tanggal_jumpa",cursor.getString(cursor.getColumnIndex("tanggal_jumpa")));
            dataKasus.put("pekerjaan",cursor.getString(cursor.getColumnIndex("pekerjaan")));
            dataKasus.put("tempat_lahir",cursor.getString(cursor.getColumnIndex("tempat_lahir")));
            dataKasus.put("tanggal_lahir",cursor.getString(cursor.getColumnIndex("tanggal_lahir")));
            cursor.close();
            db.close();
            return dataKasus;
        }else {
            Log.e("error not found", "data can't be found or database empty (kasus)");
            cursor.close();
            db.close();
            return dataKasus;
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
            db.close();
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

    public void clearDB(int dbtable){
        db = this.getWritableDatabase();
        String table = null;
        if (dbtable==1){
            table = TABLE_KASUS;
        }else if(dbtable==2){
            table = TABLE_PENGACARA;
        }else if (dbtable==3){
            table = TABLE_BERKAS;
        }
        else{
            table = TABLE_SETTINGS;
        }
        db.execSQL("DELETE FROM " + table);
        db.close();
    }

    public boolean syncKasus(String response){
//        db = this.getWritableDatabase();
        Boolean done = false;
        try {
            JSONObject Jselect = new JSONObject(response);
            if (checkUpdateTime(Jselect.getString("update_time"))){
                clearDB(1);
                JSONArray jsonArray = new JSONArray(Jselect.getString("kasus"));
                for (int i = 0; i < jsonArray.length(); i++){
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    ContentValues contentValues = new ContentValues();
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
                    db = this.getWritableDatabase();
                    long ret = db.insert(TABLE_KASUS, null, contentValues);
                    db.close();
                    notif = true;
                    if (ret!=-1){
                        done = true;
                    }else{
                        done = false;
                    }
                }
                clearDB(2);
                JSONArray jsonArrayP = new JSONArray(Jselect.getString("pengacara"));
                for (int iP = 0; iP < jsonArrayP.length(); iP++){
                    JSONObject jsonObjectP = jsonArrayP.getJSONObject(iP);
//                            Log.e("SSS", String.valueOf(jsonObjectP));
                    ContentValues contentValuesP = new ContentValues();
                    contentValuesP.put("id", jsonObjectP.getInt("id"));
                    contentValuesP.put("nama", jsonObjectP.getString("nama"));
                    contentValuesP.put("email", jsonObjectP.getString("email"));
                    contentValuesP.put("nohp", jsonObjectP.getString("nohp"));
                    contentValuesP.put("foto", jsonObjectP.getString("foto"));
                    db = this.getWritableDatabase();
                    Long ret = db.insert(TABLE_PENGACARA, null, contentValuesP);
                    db.close();
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
                db.close();
//                Log.w("UPD", update_time + "++++" + res.getString(6));
                ContentValues contentValues = new ContentValues();
                contentValues.put("sync", update_time);
                db = this.getWritableDatabase();
                int ret = db.update(TABLE_SETTINGS, contentValues, null, null);
                db.close();
                keputusan = true;
            }else{
                keputusan = false;
            }
        }else {
            keputusan = false;
        }
        res.close();
        Log.e("le", String.valueOf(keputusan));
        return keputusan;
    }

    public boolean GantiStatus(Integer idKasus) {
        db = this.getReadableDatabase();
        Integer status = 0;
        Cursor res = db.rawQuery( "select * from "+TABLE_KASUS+ " where id_masalah = " + idKasus, null );
        res.moveToFirst();
        if (res.moveToLast()) {
            status = res.getInt(res.getColumnIndex("status"));
            Log.e("FStatus",Integer.toString(status));
        }else {
            Log.e("error not found", "data can't be found or database empty");
        }
        ContentValues contentValues = new ContentValues();
        if (status==1){
            contentValues.put("status", 0);
        }else if (status==2){
            Log.e("FStatus",Integer.toString(status));
            contentValues.put("status", 4);
        }else if(status==3){
            contentValues.put("status", 2);
        }else if (status==4){
            contentValues.put("status", 2);
        }
//        Log.e("AStatus",String.valueOf(contentValues));
        db = this.getWritableDatabase();
        String contentWhere = "id_masalah="+idKasus;
        int ret = db.update(TABLE_KASUS, contentValues, contentWhere, null);
        if (ret!=0){
            return true;
        }
        else{
            return false;
        }
    }

}
