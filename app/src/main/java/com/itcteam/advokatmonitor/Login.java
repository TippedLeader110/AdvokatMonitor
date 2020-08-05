package com.itcteam.advokatmonitor;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Login extends AppCompatActivity {
    DatabaseHandler DBSet;
    Button do_login;
    EditText v_username, v_password;
    ProgressDialog pd;
    RequestQueue queue;
    boolean failedLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        failedLogin = false;
        setContentView(R.layout.activity_login);
        pd = new ProgressDialog(Login.this);
        DBSet = new DatabaseHandler(this);
        queue = Volley.newRequestQueue(Login.this);
        do_login = (Button) findViewById(R.id.but_login);
        v_username = (EditText) findViewById(R.id.val_username);
        v_password = (EditText) findViewById(R.id.val_password);
        if (DBSet.checkLogin()==true){
            autoLogin();
        }
        do_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (DBSet.checkLogin()==false || failedLogin==true){
                    failedLogin = false;
                    postLogin();
                }
            }
        });
    }

    private void autoLogin() {
        pd.setCancelable(false);
        pd.setTitle("Mohon Tunggu !!!");
        pd.setMessage("Mencoba login");
        pd.show();
//                pd.setContentView(R.layout.pd_login);
        String url = "http://192.168.43.90/advokat/api/key/loginToken";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(pd.isShowing()){
                            pd.dismiss();
                            if (!pd.isShowing()){
                                if (checkToken(response)==1){
                                    AlertDialog alertDialog = new AlertDialog.Builder(Login.this).create();
                                    alertDialog.setTitle("Info");
                                    alertDialog.setMessage("Login otomatis berhasil !!");
                                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                    Intent intent = new Intent(Login.this, Kasus.class);
                                                    intent.putExtra("LEVEL_ACCOUNT", Integer.toString(DBSet.getLevel()));
                                                    startActivity(intent);
                                                    dialog.dismiss();
                                                    finish();
                                                }
                                            });
                                    alertDialog.show();

                                }
                                else if (checkToken(response)==0){
                                    showDialog("Sesi anda sudah habis, sialhkan login kembali !!");
                                    DBSet.reLogin();
                                }
                                else{
                                    showDialog("Terjadi kesalahan saat menghubungkan ke database !! [x-02]");
                                }
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if(pd.isShowing()){
                            pd.dismiss();
                            if (!pd.isShowing()){
                                failedLogin = true;
                                showDialog("Tidak dapat terhubung ke server !!! [x-02]");
                            }
                        }
                    }
                }
        ){
            @Override
            protected java.util.Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("token",DBSet.getToken());
                return params;
            }
        };
        queue.add(stringRequest);
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
        return ret;
    }

    private void postLogin() {
        pd.setCancelable(false);
        pd.setTitle("Mohon Tunggu !!!");
        pd.setMessage("Mencoba login");
        pd.show();
        String url = "http://192.168.43.90/advokat/api/key/login";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(pd.isShowing()){
                            pd.dismiss();
                            if (!pd.isShowing()){
                                try {
                                    JSONObject reader = new JSONObject(response);
                                    if (reader.getString("error")=="false"){
                                        if (DBSet.autoLogin(response)){
                                            AlertDialog alertDialog = new AlertDialog.Builder(Login.this).create();
                                            alertDialog.setTitle("Peringatan");
                                            alertDialog.setMessage("Selamat Datang");
                                            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                                    new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            Intent intent = new Intent(Login.this, Kasus.class);
                                                            intent.putExtra("LEVEL_ACCOUNT", Integer.toString(DBSet.getLevel()));
                                                            startActivity(intent);
                                                            dialog.dismiss();
                                                            finish();
                                                        }
                                                    });
                                            alertDialog.show();

                                        }
                                        else{
                                            showDialog("Terjadi kesalahan saat menghubungkan ke database !! [x-01]");
                                        }
                                    }
                                    else{
                                        showDialog("Username atau Password tidak valid !!");
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if(pd.isShowing()){
                            pd.dismiss();
                            if (!pd.isShowing()){
                                failedLogin = true;
                                showDialog("Tidak dapat terhubung dengan server !!! [x-01]");
                            }
                        }
                    }
                }
        ){
            @Override
            protected java.util.Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("username",v_username.getText().toString());
                params.put("password",v_password.getText().toString());
                return params;
            }
        };
        queue.add(stringRequest);
    }

    public void showDialog(String text){
        AlertDialog alertDialog = new AlertDialog.Builder(Login.this).create();
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