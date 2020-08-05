package com.itcteam.advokatmonitor;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class CekSesi {
    public String token;
    DatabaseHandler DBset;
    public static final String URL_TOKEN = "http://192.168.43.90/advokat/api/key/loginToken";
    public boolean status;
    private RequestQueue queue;
    Context kelas;

    public CekSesi(Context kelas) {
        this.kelas = kelas;
    }

    public boolean doJob(){
        DBset = new DatabaseHandler(kelas);
        queue = Volley.newRequestQueue(kelas);
        this.token = DBset.getToken();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_TOKEN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.w("Cek response token : ",response);
                        if (checkToken(response)==1){
                            status = true;
                            Log.w("status true = ", Boolean.toString(status));
                        }
                        else if (checkToken(response)==0){
                            status = false;
                            Log.w("status false = ", Boolean.toString(status));
                        }
                        else{
                            Log.w("CekSesi","Gagal menghubungkan ke server");
                            showDialog("Terjadi kesalahan saat menghubungkan ke database !! [x-TOKEN]");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                Log.w("Kirim token (cek sesi)", token);
                params.put("token",token);
                return params;
            }
        };
        queue.add(stringRequest);
        return getStatus();
    }

    public Boolean getStatus(){
        Log.w("status return", Boolean.toString(status));
        return status;
    }

    private Integer checkToken(String response) {
        JSONObject reader;
        Integer ret = null;
        try {
            reader = new JSONObject(response);
            String token = reader.getString("status_token");

            if (token=="true"){
                ret = 1;
            }
            else if (token=="false"){
                ret = 0;
            }else{
                ret = null;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.w("status_token" , Integer.toString(ret));
        return ret;
    }

    private void showDialog(String text){
        AlertDialog alertDialog = new AlertDialog.Builder(kelas).create();
        alertDialog.setTitle("Peringatan");
        alertDialog.setMessage(text);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }
}
