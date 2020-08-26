package com.itcteam.advokatmonitor.ui.main.kasus;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
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

import com.itcteam.advokatmonitor.R;
import com.itcteam.advokatmonitor.dbclass.DatabaseHandlerAppSave;
import com.itcteam.advokatmonitor.ui.main.Login;
import com.itcteam.advokatmonitor.ui.main.kasus.kasus_fragment.SectionsPagerAdapter;
import com.itcteam.advokatmonitor.simpletask.CekSesi;

import java.util.HashMap;
import java.util.Map;

public class Kasus extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {
    ProgressDialog pd;
    private String sResponse;
    DatabaseHandlerAppSave databaseHandlerAppSave;
    TextView judulBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CekSesi ck = new CekSesi(Kasus.this);
        pd = new ProgressDialog(Kasus.this);
        ck.doJob();
        setContentView(R.layout.activity_kasus);
        judulBar = this.findViewById(R.id.title);
        if (ck.getStatus()){
            Intent intent = new Intent(Kasus.this, Login.class);
            startActivity(intent);
            finish();
        }else{
            databaseHandlerAppSave = new DatabaseHandlerAppSave(Kasus.this);
            pd.setCancelable(false);
            pd.setTitle("Mohon Tunggu !!!");
            pd.setMessage("Memperbaharui daftar kasus");
            pd.show();
            RequestQueue queue = Volley.newRequestQueue(Kasus.this);
            String url = getString(R.string.base_url)+"kasus";
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            sResponse = response;
                            if (pd.isShowing()){
                                pd.dismiss();

                                if (databaseHandlerAppSave.syncKasus(sResponse)){
                                    if (databaseHandlerAppSave.getNotif()){
//                                        showDialog("Sinkronasi Berhasil");
                                    }
                                    if (databaseHandlerAppSave.getLevel()==1){
                                        judulBar.setText("Advokat Monitor (ADMIN)");
                                    }
                                    else{
                                        judulBar.setText("Advokat Monitor (Pengacara)");
                                    }

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
                    }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("level",Integer.toString(databaseHandlerAppSave.getLevel()));
                    params.put("token",databaseHandlerAppSave.getToken());
                    return params;
                }
            };
            queue.add(stringRequest);
        }
    }

    public void dothePopup(View view){
        PopupMenu pop = new PopupMenu(this, view);
        pop.setOnMenuItemClickListener(this);
        pop.inflate(R.menu.menu_logout);
        pop.show();
    }



    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.item1:
                Toast.makeText(this, "Logout !!!", Toast.LENGTH_SHORT).show();
                databaseHandlerAppSave.clearDB(3);
                databaseHandlerAppSave.clearDB(4);
                databaseHandlerAppSave.clearDB(2);
                databaseHandlerAppSave.clearDB(1);
                Intent intent = new Intent(this, Login.class);
                startActivity(intent);
                finish();
                return true;
            default:
                return false;

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