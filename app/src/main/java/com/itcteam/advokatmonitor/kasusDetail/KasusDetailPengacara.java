package com.itcteam.advokatmonitor.kasusDetail;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.itcteam.advokatmonitor.R;
import com.itcteam.advokatmonitor.dbclass.DatabaseHandlerAppSave;
import com.itcteam.advokatmonitor.simpletask.TampilAlertDialog;
import com.itcteam.advokatmonitor.ui.main.Login;
import com.itcteam.advokatmonitor.ui.main.kasus.Kasus;
import com.itcteam.advokatmonitor.ui.main.pengacara.KasusListPengacara;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class KasusDetailPengacara extends AppCompatActivity {

    DatabaseHandlerAppSave appsave;
    ProgressDialog pd;
    TextView judul_detail, pengirim_detail, status_text_detail, ktp_detail, nama_pengacara, tgl_lhr, tmpt_lhr, pekerjaan, tgl_lhrdo, tmpt_lhrdo, pekerjaando, waktu;
    Button kalender, status_button, edit, add;
    FloatingActionButton fab;
    String judul, pengirim, ktp, status, tmpt, tgl, pekerjaanString, waktuString;
    Context context;
    Boolean back;
    TampilAlertDialog talert;
    Integer idKasus, posisiFragment;
    DatePickerDialog.OnDateSetListener dateSetListener;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        talert = new TampilAlertDialog(this);
        setContentView(R.layout.activity_kasus_detail_pengacara);
        context = this;
        back = false;
        pd = new ProgressDialog(this);
        judul_detail = this.findViewById(R.id.judul_detail_pengacara);
        status_text_detail = this.findViewById(R.id.status_text_detail_pengacara);
        ktp_detail = this.findViewById(R.id.ktp_detail_pengacara);
        pengirim_detail = this.findViewById(R.id.namapengirim_detail_pengacara);
        tmpt_lhr = this.findViewById(R.id.tmptlahir_detail_pengacara);
        tgl_lhr = this.findViewById(R.id.tgllahir_detail_pengacara);
        pekerjaan = this.findViewById(R.id.pekerjaan_detail_pengacara);
        waktu = this.findViewById(R.id.waktu_detail_pengacara);
        nama_pengacara = this.findViewById(R.id.nama_pengacara_detail_pengacara);
        edit = this.findViewById(R.id.edit_detail_pengacara);
        add = this.findViewById(R.id.tambahdokumen_detail_pengacara);
        appsave = new DatabaseHandlerAppSave(this);
        fab  = this.findViewById(R.id.fab);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
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
        kalender = this.findViewById(R.id.date_detail_pengacara);
        kalender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dialog = new DatePickerDialog(
                        context,
                        android.R.style.Theme_Holo_Dialog_MinWidth,
                        dateSetListener,
                        year, month, day
                );
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                String date = year+"-"+month+"-"+day;
                Log.w("Date", date);
            }
        };

        status_button = this.findViewById(R.id.status_detail_pengacara);

        contentChangebyStatus();

        status_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                pd.setCancelable(false);
                pd.setTitle("Mohon Tunggu !!!");
                pd.setMessage("Sedang memproses");
                pd.show();
                if (pd.isShowing()){
                    RequestQueue queue = Volley.newRequestQueue(KasusDetailPengacara.this);
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

    public void contentChangebyStatus(){
        Log.w("stat = ", status);

        if (status!="Kasus Baru"){
            tgl_lhr.setVisibility(tgl_lhr.VISIBLE);
            tmpt_lhr.setVisibility(tmpt_lhr.VISIBLE);
            pekerjaan.setVisibility(pekerjaan.VISIBLE);
//            tgl_lhrdo.setVisibility(tgl_lhrdo.VISIBLE);
//            tmpt_lhrdo.setVisibility(tmpt_lhrdo.VISIBLE);
//            pekerjaando.setVisibility(pekerjaando.VISIBLE);
            kalender.setText("Ganti Jadwal Jumpa");
            Log.w("Pekerjaan", String.valueOf(pekerjaanString));
        }else{
            edit.setEnabled(false);
            add.setEnabled(false);
        }

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
        waktuString = hash.get("tanggal_jumpa");
        status = hash.get("status");
        tgl = hash.get("tanggal_lahir");
        tmpt = hash.get("tempat_lahir");
        pekerjaanString = hash.get("pekerjaan");
        judul_detail.setText(judul);
        nama_pengacara.setText(appsave.namaPengacara(hash.get("id_p")));
        pengirim_detail.setText(pengirim);
        ktp_detail.setText(ktp);
        status_text_detail.setText(status);
        pekerjaan.setText(pekerjaanString);
        tmpt_lhr.setText(tmpt);
        tgl_lhr.setText(tgl);
        if (waktuString!=null){
            waktu.setText(waktuString);
        }
    }

    @Override
    public void onBackPressed() {
        if (back==true){
            Intent intent = new Intent(context, Kasus.class);
            intent.putExtra("LEVEL_ACCOUNT", Integer.toString(appsave.getLevel()));
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }else{
            finish();
        }
    }
}