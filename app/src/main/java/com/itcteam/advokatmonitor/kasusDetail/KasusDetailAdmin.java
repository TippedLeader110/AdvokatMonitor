package com.itcteam.advokatmonitor.kasusDetail;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.itcteam.advokatmonitor.R;
import com.itcteam.advokatmonitor.dbclass.DatabaseHandlerAppSave;
import com.itcteam.advokatmonitor.simpletask.TampilAlertDialog;
import com.itcteam.advokatmonitor.ui.main.Login;
import com.itcteam.advokatmonitor.ui.main.kasus.Kasus;
import com.itcteam.advokatmonitor.ui.main.pengacara.KasusListPengacara;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class KasusDetailAdmin extends AppCompatActivity {
    DatabaseHandlerAppSave appsave;
    ProgressDialog pd;
    TextView judul_detail, pengirim_detail, status_text_detail, ktp_detail, nama_pengacara;
    Button pengacara, status_button;
    FloatingActionButton fab;
    String judul, pengirim, ktp, status;
    Context context;
    Boolean back;
    TampilAlertDialog talert;
    Integer idKasus, posisiFragment;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        talert = new TampilAlertDialog(this);
        setContentView(R.layout.activity_kasus_detail);
        context = this;
        back = false;
        pd = new ProgressDialog(this);
        judul_detail = this.findViewById(R.id.judul_detail);
        status_text_detail = this.findViewById(R.id.status_text_detail);
        ktp_detail = this.findViewById(R.id.ktp_detail);
        pengirim_detail = this.findViewById(R.id.namapengirim_detail);
        nama_pengacara = this.findViewById(R.id.nama_pengacara_detail);
        appsave = new DatabaseHandlerAppSave(this);
        fab  = this.findViewById(R.id.fab);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        CollapsingToolbarLayout toolBarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        idKasus = Integer.parseInt(Objects.requireNonNull(getIntent().getStringExtra("id_kasus")));
        posisiFragment = Integer.parseInt(Objects.requireNonNull(getIntent().getStringExtra("posisi")));

        toolBarLayout.setTitle("Detail Kasus");
//      Ambil data untuk detail

        fetchData();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Title");
                // Set up the input
                final EditText input = new EditText(context);
                // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
                builder.setView(input);

                // Set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(context, input.getText().toString(), Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });

//      Klik tombol pengacara
        pengacara = this.findViewById(R.id.pengacara_detail);
        pengacara.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, KasusListPengacara.class);
                Log.e("KIRIMAAAN", Integer.toString(idKasus));
                intent.putExtra("id_kasus", Integer.toString(idKasus));
//                intent.putExtra("posisi", posisiFragment);
                startActivity(intent);
//                Snackbar.make(view, "Pengacara", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
            }
        });

        status_button = this.findViewById(R.id.status_detail);

        statusButtonChange();

        status_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                pd.setCancelable(false);
                pd.setTitle("Mohon Tunggu !!!");
                pd.setMessage("Sedang memproses");
                pd.show();
                if (pd.isShowing()){
                    RequestQueue queue = Volley.newRequestQueue(KasusDetailAdmin.this);
                    String url = getString(R.string.base_url)+"gantiStatus";
                    StringRequest objR = new StringRequest(Request.Method.POST, url,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    if (pd.isShowing())
                                        pd.dismiss();
                                    try {
                                        JSONObject jb = new JSONObject(response);
                                        if (jb.getString("error")=="false"){
                                            back = true;
                                            AlertDialog alertDialog = new AlertDialog.Builder(context).create();
                                            alertDialog.setTitle("Berhasil");
                                            alertDialog.setMessage("Status Kasus berhasil diubah");
                                            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                                    new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            Intent intent = new Intent(context, Kasus.class);
                                                            intent.putExtra("LEVEL_ACCOUNT", Integer.toString(appsave.getLevel()));
                                                            finish();
                                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                            startActivity(intent);
                                                            dialog.dismiss();
                                                        }
                                                    });
                                            alertDialog.show();
                                        }else if(jb.getString("error")=="fail"){
                                            talert.tampilDialogDefault("Kesalahan", "sesi telah habis, silahkan login kembali");
                                            Intent intent = new Intent(context, Login.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            finish();
                                            startActivity(intent);
                                        }
                                        else{
                                            talert.tampilDialogDefault("Kesalahan", "Terjadi kesalahan");
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    error.printStackTrace();
                                }
                            }
                    ){
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> params = new HashMap<String, String>();
                            params.put("id",Integer.toString(idKasus));
                            params.put("token",appsave.getToken());
                            return params;
                        }
                    };
                    queue.add(objR);
                }
            }
        });
    }

    public void statusButton(Integer logo, String text){
//        Drawable drawable = getResources().getDrawable(logo);
        status_button.setCompoundDrawablesWithIntrinsicBounds(0, logo, 0, 0);
        status_button.setText(text);
    }

    public void statusButtonChange(){
        Log.w("stat = ", status);
        if(status=="Kasus Baru"){
            statusButton(R.drawable.ic_baseline_block_24, "Tolak Kasus");
        }else if(status=="Kasus Berjalan"){
            statusButton(R.drawable.ic_baseline_block_24, "Batalkan Kasus");
        }else if(status=="Kasus Selesai"){
            statusButton(R.drawable.ic_baseline_check_24, "Buka Kembali Kasus");
        }
        else{
            statusButton(R.drawable.ic_baseline_check_24, "Buka Kasus");
        }
    }

    public void fetchData(){
        HashMap<String, String> hash = appsave.getDetailKasus(idKasus, posisiFragment);
        judul = hash.get("judul");
        pengirim = hash.get("pengirim");
        ktp = hash.get("ktp");
        status = hash.get("status");
        judul_detail.setText(judul);
        nama_pengacara.setText(appsave.namaPengacara(hash.get("id_p")));
        pengirim_detail.setText(pengirim);
        ktp_detail.setText(ktp);
        status_text_detail.setText(status);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(context, Kasus.class);
        intent.putExtra("LEVEL_ACCOUNT", Integer.toString(appsave.getLevel()));
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        Intent intent = new Intent(context, Kasus.class);
        intent.putExtra("LEVEL_ACCOUNT", Integer.toString(appsave.getLevel()));
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
        return true;
    }
}