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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        pd = new ProgressDialog(Login.this);
        DBSet = new DatabaseHandler(this);
        do_login = (Button) findViewById(R.id.but_login);
        v_username = (EditText) findViewById(R.id.val_username);
        v_password = (EditText) findViewById(R.id.val_password);

//        loadDB();


        do_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pd.setCancelable(false);
                pd.setTitle("Mohon Tunggu !!!");
                pd.setMessage("Mencoba login");
                pd.show();
//                pd.setContentView(R.layout.pd_login);
                RequestQueue queue = Volley.newRequestQueue(Login.this);
                String url = "http://192.168.43.90/advokat/api/key/login";

                StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                if(pd.isShowing()){
                                    pd.dismiss();
                                    if (!pd.isShowing()){
                                        if (DBSet.autoLogin(response)){
                                            showDialog("Berhasil");
                                        }
                                        else{
                                            showDialog("Terjadi kesalahan saat menghubungkan ke database !!");
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
                                        showDialog("Username atau Password Salah");
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
        });
    }

    private void loadDB() {

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