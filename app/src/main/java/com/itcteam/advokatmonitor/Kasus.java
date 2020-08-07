package com.itcteam.advokatmonitor;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.tabs.TabLayout;

import androidx.appcompat.app.AlertDialog;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import com.itcteam.advokatmonitor.dbclass.DatabaseHandlerAppSave;
import com.itcteam.advokatmonitor.ui.main.SectionsPagerAdapter;

public class Kasus extends AppCompatActivity {
    ProgressDialog pd;
    private String sResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CekSesi ck = new CekSesi(Kasus.this);
        pd = new ProgressDialog(Kasus.this);
        ck.doJob();
        if (ck.getStatus()){
            Intent intent = new Intent(Kasus.this, Login.class);
            startActivity(intent);
            finish();
        }else{
            final DatabaseHandlerAppSave databaseHandlerAppSave = new DatabaseHandlerAppSave(Kasus.this);
            pd.setCancelable(false);
            pd.setTitle("Mohon Tunggu !!!");
            pd.setMessage("Sinkronasi dengan database");
            pd.show();
            RequestQueue queue = Volley.newRequestQueue(Kasus.this);
            String url = "http://192.168.43.90/advokat/api/key/kasus";
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            sResponse = response;
                            if (pd.isShowing()){
                                pd.dismiss();
                                if (databaseHandlerAppSave.syncKasus(sResponse)){
                                    if (databaseHandlerAppSave.getNotif()){
                                        showDialog("Sinkronasi Berhasil");
                                    }
                                    setContentView(R.layout.activity_kasus);
                                    String level = getIntent().getStringExtra("LEVEL_ACCOUNT");
                                    SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(Kasus.this, getSupportFragmentManager(), Integer.parseInt(level));
                                    ViewPager viewPager = findViewById(R.id.view_pager);
                                    viewPager.setAdapter(sectionsPagerAdapter);
                                    TabLayout tabs = findViewById(R.id.tabs);
                                    tabs.setupWithViewPager(viewPager);
                                }
                                else{
                                    showDialog("Sinkronasi Gagal");
                                    Intent intent = new Intent(Kasus.this, Login.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            error.printStackTrace();
                        }
                    });
            queue.add(stringRequest);
        }
    }

    public void showDialog(String text){
        AlertDialog alertDialog = new AlertDialog.Builder(Kasus.this).create();
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