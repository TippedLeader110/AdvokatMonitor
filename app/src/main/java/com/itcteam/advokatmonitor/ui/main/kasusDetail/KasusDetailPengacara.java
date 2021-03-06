package com.itcteam.advokatmonitor.ui.main.kasusDetail;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.nbsp.materialfilepicker.MaterialFilePicker;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

public class KasusDetailPengacara extends AppCompatActivity implements DialogEditKasus.EditDialogListener
        , BerkasRecyclerAdapter.ListenerRecyclerBerkas, DialogInfoBerkas.EditDialogListener{

    private static final int PERMISSION_STORAGE_CODE_READ = 1000;
    private static final int PERMISSION_STORAGE_CODE_WRITE = 1001;
    private static final int PERMISSION_STORAGE_CODE = 1002;
    private static final int CALL_CODE_PERMISSION = 1004;
    DatabaseHandlerAppSave appsave;
    ProgressDialog pd;
    TextView judul_detail, pengirim_detail, status_text_detail, ktp_detail, nama_pengacara, tgl_lhr, tmpt_lhr, pekerjaan, tgl_lhrdo, tmpt_lhrdo, pekerjaando, waktu, nohp, email;
    Button kalender, status_button, edit, add, sukses, emailmanual, call;
    FloatingActionButton fab;
    RecyclerView berkasList;
    String judul, pengirim, ktp, status, tmpt, tgl, pekerjaanString, waktuString;
    Context context;
    Boolean back;
    TampilAlertDialog talert;
    FrameLayout frameLayout;
    Integer idKasus, posisiFragment;
    DatePickerDialog.OnDateSetListener dateSetListener;
    private String namaberkas, url;
    private String filePath;
    private boolean sendDone;
    private String namafileUpload;
    private String nohpString;
    private String emailString;



    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        talert = new TampilAlertDialog(this);
        setContentView(R.layout.activity_kasus_detail_pengacara);
        context = this;
        back = false;
        checkFilePermission();
        pd = new ProgressDialog(this);
        frameLayout = findViewById(R.id.overlay);
        judul_detail = this.findViewById(R.id.judul_detail_pengacara);
        status_text_detail = this.findViewById(R.id.status_text_detail_pengacara);
        ktp_detail = this.findViewById(R.id.ktp_detail_pengacara);
        pengirim_detail = this.findViewById(R.id.namapengirim_detail_pengacara);
        tmpt_lhr = this.findViewById(R.id.tmptlahir_detail_pengacara);
        tgl_lhr = this.findViewById(R.id.tgllahir_detail_pengacara);
        emailmanual = this.findViewById(R.id.emailmanual_detail_pengacara);
        call = this.findViewById(R.id.call_detail_pengacara);
        pekerjaan = this.findViewById(R.id.pekerjaan_detail_pengacara);
        berkasList = this.findViewById(R.id.listview_berkas);
        sukses = this.findViewById(R.id.sukses_kasus);
        waktu = this.findViewById(R.id.waktu_detail_pengacara);
        nohp = this.findViewById(R.id.nohp_detail_pengacara);
        email = this.findViewById(R.id.email_detail_pengacara);
        nama_pengacara = this.findViewById(R.id.nama_pengacara_detail_pengacara);
        edit = this.findViewById(R.id.edit_detail_pengacara);
        add = this.findViewById(R.id.tambahdokumen_detail_pengacara);
        appsave = new DatabaseHandlerAppSave(this);
        fab  = this.findViewById(R.id.fab);
        status_button = this.findViewById(R.id.status_detail_pengacara);
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
                AlertDialog alertDialog = new AlertDialog.Builder(context).create();
                alertDialog.setTitle("Perhatian");
                if (!waktuString.equals("null")){
                    if (status.equals("Kasus Berjalan")){
                        alertDialog.setMessage("Apakah anda ingin mengirim email otomatis ke client perkara jadwal pertemuan dan kontak pengacara anda ?");
                        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Tidak",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                    }
                                });
                        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Ya",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                    sendMail();
                                    }
                                });
                    }else{
                        alertDialog.setMessage("Ubah status kasus menjadi berjalan untuk mengirim email ke client !!!");
                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Ok",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                    }
                                });
                    }
                }else{
                    alertDialog.setMessage("Tentukan jadwal pertemuan terlebih dahulu untuk mengirim email otomatis ke client !!!");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Ok",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            });
                }
                alertDialog.show();

            }
        });

        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkCallPermission();
            }
        });

        emailmanual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("message/rfc822");
                i.putExtra(Intent.EXTRA_EMAIL  , new String[]{emailString});
                try {
                    startActivity(Intent.createChooser(i, "Kirim email dengan aplikasi"));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(KasusDetailPengacara.this, "Aplikasi email tidak terinstall di perangkat anda !!.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogInfoBerkas dialogInfoBerkas = new DialogInfoBerkas();
                dialogInfoBerkas.show(getSupportFragmentManager(), "Upload berkas baru");
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
                        dateSetListener,
                        year, month, day
                );
//                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                String monthShow = "0";
                if (month>=10){
                    monthShow = Integer.toString(month);
                }else{
                    monthShow = "0"+month;
                }
                String date = year+"-"+monthShow+"-"+day;
                Log.w("Date", date);
                changeDate(date);
            }
        };



        contentChangebyStatus();

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogEditKasus dialogEditKasus = new DialogEditKasus();
                dialogEditKasus.show(getSupportFragmentManager(), "Edit Kasus");
            }
        });

        sukses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog alertDialog = new AlertDialog.Builder(context).create();
                alertDialog.setTitle("Perhatian");
                alertDialog.setMessage("Apakah anda ingin menyelesaikan kasus ini ?");
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Mungkin tidak",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Ya",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                kasusSelesai();
                            }
                        });
                alertDialog.show();
            }
        });

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
//                                                            afterChangeData();
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

        fetchBerkas();
    }

    private void sendMail() {
        pd.setTitle("Mohon Tunggu !!!");
        pd.setMessage("Sedang memproses");
        pd.show();
        if (pd.isShowing()){
            RequestQueue queue = Volley.newRequestQueue(KasusDetailPengacara.this);
            String url = getString(R.string.base_url)+"emailSend";
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
                                    alertDialog.setMessage("Email berhasil dikirim");
                                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
//                                                            afterChangeData();
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

    private void fetchBerkas() {
        frameLayout.setVisibility(frameLayout.VISIBLE);
        final List daftarBerkas = new ArrayList();
        RequestQueue queue = Volley.newRequestQueue(KasusDetailPengacara.this);
        String url = getString(R.string.base_url)+"getBerkas";
        StringRequest objR = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (pd.isShowing())
                            pd.dismiss();
                        try {
                            JSONObject jb = new JSONObject(response);
                            if (jb.getString("error")=="false"){
                                JSONArray ja = new JSONArray(jb.getString("berkas"));
                                frameLayout.setVisibility(frameLayout.GONE);

                                if (ja.length()!=0){
                                    for (int i = 0; i < ja.length(); i++){
                                        HashMap<String,Object> berkasObj = new HashMap<>();
                                        JSONObject getData = ja.getJSONObject(i);
                                        berkasObj.put("id", getData.getString("id_berkas"));
                                        berkasObj.put("namaberkas", getData.getString("nama_berkas"));
                                        berkasObj.put("urlberkas", getData.getString("file"));
                                        daftarBerkas.add(berkasObj);
                                    }
//                                    BerkasAdapter adapter = new BerkasAdapter(context, daftarBerkas, R.layout.listview_daftarberkas,new String[]{"id","namaberkas", "urlberkas"}, new int[]{R.id.id_berkas, R.id.namaberkas, R.id.urlberkas});
                                    BerkasRecyclerAdapter adapter = new BerkasRecyclerAdapter(context, daftarBerkas);
                                    berkasList.setAdapter(adapter);
                                    berkasList.setLayoutManager(new LinearLayoutManager(context));
                                }

                            }else if(jb.getString("error")=="fail"){
                                talert.tampilDialogDefault("Kesalahan", "Terjadi kesalahan");

                            }
                            else{
                                talert.tampilDialogDefault("Kesalahan", "sesi telah habis, silahkan login kembali");
                                Intent intent = new Intent(context, Login.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                finish();
                                startActivity(intent);
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

    private void  kasusSelesai(){
        pd.setTitle("Mohon Tunggu !!!");
        pd.setMessage("Sedang memproses");
        pd.show();
        if (pd.isShowing()){
            RequestQueue queue = Volley.newRequestQueue(KasusDetailPengacara.this);
            String url = getString(R.string.base_url)+"kasusSelesai";
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
                                    alertDialog.setMessage("Status sudah selesai");
                                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                    Intent intent = new Intent(context, Kasus.class);
                                                    intent.putExtra("LEVEL_ACCOUNT", Integer.toString(appsave.getLevel()));
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                    startActivity(intent);
                                                    finish();
                                                }
                                            });
                                    alertDialog.show();
                                }else if(jb.getString("error")=="fail"){
                                    talert.tampilDialogDefault("Kesalahan", "Terjadi kesalahan");

                                }
                                else{
                                    talert.tampilDialogDefault("Kesalahan", "sesi telah habis, silahkan login kembali");
                                    Intent intent = new Intent(context, Login.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    finish();
                                    startActivity(intent);
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

    private void changeDate(final String date) {
        pd.setTitle("Mohon Tunggu !!!");
        pd.setMessage("Sedang memproses");
        pd.show();
        if (pd.isShowing()){
            RequestQueue queue = Volley.newRequestQueue(KasusDetailPengacara.this);
            String url = getString(R.string.base_url)+"gantiTanggal";
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
                                    if (status=="Kasus Baru"){
                                        alertDialog.setMessage("Tanggal jumpa dengan client berhasil diubah. Status kasus berubah menjadi kasus berjalan");
                                    }else{
                                        alertDialog.setMessage("Tanggal jumpa dengan client berhasil diubah");
                                    }
                                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                    afterChangeData();
                                                }
                                            });
                                    alertDialog.show();
                                }else if(jb.getString("error")=="fail"){
                                    talert.tampilDialogDefault("Kesalahan", "Terjadi kesalahan");

                                }
                                else{
                                    talert.tampilDialogDefault("Kesalahan", "sesi telah habis, silahkan login kembali");
                                    Intent intent = new Intent(context, Login.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    finish();
                                    startActivity(intent);
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
                    params.put("tanggal",date);
                    return params;
                }
            };
            queue.add(objR);
        }
    }

    private void afterChangeData() {
        pd.setCancelable(false);
        pd.setTitle("Mohon Tunggu !!!");
        pd.setMessage("Memperbaharui data kasus");
        pd.show();
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = getString(R.string.base_url)+"kasus";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String sResponse = response;
                        if (pd.isShowing()){
                            if (appsave.syncKasus(sResponse)){
                                pd.dismiss();
                                finish();
                                Intent intent = new Intent(context, KasusDetailPengacara.class);
                                intent.putExtra("id_kasus", Integer.toString(idKasus));
                                intent.putExtra("posisi",  Integer.toString(posisiFragment));
                                startActivity(intent);
//                                Intent intent = new Intent(context, KasusDetailPengacara.class);
//                                intent.putExtra("id_kasus", Integer.toString(idKasus));
//                                intent.putExtra("posisi",  Integer.toString(posisiFragment));
//                                startActivity(intent);
                            }
                            else{
//                                talert.tampilDialogDefault("Kesalahan", "sesi telah habis, silahkan login kembali");
                                Intent intent = new Intent(context, Login.class);
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
                params.put("level",Integer.toString(appsave.getLevel()));
                params.put("token",appsave.getToken());
                return params;
            }
        };
        queue.add(stringRequest);
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

        }

        if (status=="Kasus Berjalan"){
            edit.setEnabled(true);
            add.setEnabled(true);
        }else{
            edit.setEnabled(false);
            add.setEnabled(false);
        }

        if(status=="Kasus Baru"){
            statusButton(R.drawable.ic_baseline_block_24, "Tolak Kasus");
        }else if(status=="Kasus Berjalan"){
            sukses.setVisibility(sukses.VISIBLE);
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
        nohpString = hash.get("nohp");
        nohp.setText(hash.get("nohp"));
        emailString = hash.get("email");
        email.setText(hash.get("email"));
        status_text_detail.setText(status);
        if (!pekerjaanString.equals("null")){
            pekerjaan.setText(pekerjaanString);
        }
        if (!tmpt.equals("null")){
            tmpt_lhr.setText(tmpt);
        }
        if (!tgl.equals("null")){
            tgl_lhr.setText(tgl);
        }
        Log.w("waktu", String.valueOf(waktuString));
        if (!waktuString.equals("null")){
            waktu.setText(waktuString);
//            fab.setEnabled(true);
        }else{
//            fab.setEnabled(false);
        }
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

    @Override
    public void hapusBerkas(String id) {
        final String idBerkas = id;
        pd.setCancelable(false);
        pd.setTitle("Mohon Tunggu !!!");
        pd.setMessage("Menghapus berkas");
        pd.show();
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = getString(R.string.base_url)+"hapusBerkas";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (pd.isShowing()){
                            try {
                                JSONObject jb = new JSONObject(response);
                                if (jb.getString("error")=="false"){
                                    AlertDialog alertDialog = new AlertDialog.Builder(context).create();
                                    alertDialog.setTitle("Berhasil");
                                    alertDialog.setMessage("Berkas berhasil dihapus !!!");
                                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
//                                                    afterChangeData();
                                                    fetchBerkas();
                                                }
                                            });
                                    alertDialog.show();
                                }else if(jb.getString("error")=="fail"){
                                    talert.tampilDialogDefault("Kesalahan", "Terjadi kesalahan");
                                }
                                else{
                                    talert.tampilDialogDefault("Kesalahan", "sesi telah habis, silahkan login kembali");
//                                    Intent intent = new Intent(context, Login.class);
//                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                                    finish();
//                                    startActivity(intent);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
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
                params.put("id", idBerkas);
                params.put("token",appsave.getToken());
                return params;
            }
        };
        queue.add(stringRequest);
    }

    @Override
    public void terimaDataDialog(final String tanggalString, final String tempatString, final String pekerjaanString) {
//        Log.w("DDD", tanggalString + "+" + tempatString + "+" + pekerjaanString);
        pd.setCancelable(false);
        pd.setTitle("Mohon Tunggu !!!");
        pd.setMessage("Memperbaharui data kasus");
        pd.show();
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = getString(R.string.base_url)+"editKasus";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (pd.isShowing()){
                            try {
                                JSONObject jb = new JSONObject(response);
                                if (jb.getString("error")=="false"){
                                    back = true;
                                    AlertDialog alertDialog = new AlertDialog.Builder(context).create();
                                    alertDialog.setTitle("Berhasil");
                                    alertDialog.setMessage("Data berhasil diperbaharui !!!");
                                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                    afterChangeData();
                                                }
                                            });
                                    alertDialog.show();
                                }else if(jb.getString("error")=="fail"){
                                    talert.tampilDialogDefault("Kesalahan", "Terjadi kesalahan");
                                }
                                else{
                                    talert.tampilDialogDefault("Kesalahan", "sesi telah habis, silahkan login kembali");
//                                    Intent intent = new Intent(context, Login.class);
//                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                                    finish();
//                                    startActivity(intent);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
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
                params.put("level",Integer.toString(appsave.getLevel()));
                params.put("token",appsave.getToken());
                params.put("id",Integer.toString(idKasus));
                params.put("tempat",tempatString);
                params.put("tanggal",tanggalString);
                params.put("pekerjaan",pekerjaanString);
                return params;
            }
        };
        queue.add(stringRequest);
    }



    public void downloadFile(String url, String namaberkas) {
        this.url = url;
        this.namaberkas = namaberkas;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (context.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                    PackageManager.PERMISSION_DENIED){
                String[] permission = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                requestPermissions(permission, PERMISSION_STORAGE_CODE);
            }else{
                doDownload(url, namaberkas);
            }
        }else{
            doDownload(url, namaberkas);
        }
    }

    public void checkCallPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (context.checkSelfPermission(Manifest.permission.CALL_PHONE) ==
                    PackageManager.PERMISSION_DENIED){
                String[] permission = {Manifest.permission.CALL_PHONE};
                requestPermissions(permission, CALL_CODE_PERMISSION);
            }else{
                createCallIntent();
            }
        }else{
            createCallIntent();
        }
    }

    public void checkFilePermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (context.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                    PackageManager.PERMISSION_DENIED){
                String[] permission = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                requestPermissions(permission, PERMISSION_STORAGE_CODE_WRITE);
            }
            if (context.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) ==
                    PackageManager.PERMISSION_DENIED){
                String[] permission = {Manifest.permission.READ_EXTERNAL_STORAGE};
                requestPermissions(permission, PERMISSION_STORAGE_CODE_WRITE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case PERMISSION_STORAGE_CODE_WRITE: {
                if (grantResults.length > 0 && grantResults[0] ==
                        PackageManager.PERMISSION_GRANTED) {
                }else{
                    Toast.makeText(context, "Akses Penyimpanan External Ditolak", Toast.LENGTH_SHORT).show();
                }
            }
            case PERMISSION_STORAGE_CODE_READ: {
                if (grantResults.length > 0 && grantResults[0] ==
                        PackageManager.PERMISSION_GRANTED) {
                }else{
                    Toast.makeText(context, "Akses Penyimpanan External Ditolak", Toast.LENGTH_SHORT).show();
                }
            }
            case PERMISSION_STORAGE_CODE: {
                if (grantResults.length > 0 && grantResults[0] ==
                        PackageManager.PERMISSION_GRANTED) {
                    downloadFile(url, filePath);
                }else{
                    Toast.makeText(context, "Akses Penyimpanan External Ditolak", Toast.LENGTH_SHORT).show();
                }
            }
            case CALL_CODE_PERMISSION: {
                if (grantResults.length > 0 && grantResults[0] ==
                        PackageManager.PERMISSION_GRANTED) {
                    createCallIntent();
                }else{
                    Toast.makeText(context, "Akses Telepon Ditolak", Toast.LENGTH_SHORT).show();
                }
            }

        }
    }

    private void createCallIntent() {
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + nohpString));
        startActivity(intent);
    }

    public String getRealPath(Uri uri){
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
        if (cursor==null){
            return uri.getPath();
        }else{
            cursor.moveToFirst();
            int id = cursor.getColumnIndex(MediaStore.Images.ImageColumns._ID);
            return cursor.getString(id);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable final Intent data) {
        switch (requestCode) {
            case 10: {
                if (resultCode == RESULT_OK) {
                    sendDone = false;
                    pd.setTitle("Mengirim file !!");
                    pd.setMessage("Harap menunggu.....");
                    pd.show();
                    String filepath = data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH);
                    File file = new File(filepath);

                    if (getMimeType(filepath)!=null){
                        Thread t = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    String extFile = getMimeType(filepath);
                                    Log.w("MIME : ", getMimeType(filepath));

                                    OkHttpClient client = new OkHttpClient();
                                    RequestBody file_body = RequestBody.create(MediaType.parse(extFile),file);
                                    RequestBody requestBody = new MultipartBody.Builder()
                                            .setType(MultipartBody.FORM)
                                            .addFormDataPart("id", Integer.toString(idKasus))
                                            .addFormDataPart("type", extFile)
                                            .addFormDataPart("nama_file", namafileUpload)
                                            .addFormDataPart("token", appsave.getToken())
                                            .addFormDataPart("file", filepath.substring
                                                    (filepath.lastIndexOf("/")+1), file_body)
                                            .build();

                                    String urlPHP = getString(R.string.base_url)+"upload";
                                    okhttp3.Request request = new okhttp3.Request.Builder()
                                            .url(urlPHP)
                                            .post(requestBody)
                                            .build();

                                    try {
                                        okhttp3.Response response = client.newCall(request).execute();
                                        pd.dismiss();
                                        sendDone = true;
                                        if (!response.isSuccessful()) {
                                            throw new IOException("Unexpected code " + response);
                                        }
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                AlertDialog alertDialog = new AlertDialog.Builder(context).create();
                                                alertDialog.setTitle("Berhasil");
                                                alertDialog.setMessage("Upload file berhasil !!!");
                                                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                                        new DialogInterface.OnClickListener() {
                                                            public void onClick(DialogInterface dialog, int which) {
//                                                                afterChangeData();
                                                                fetchBerkas();
                                                            }
                                                        });
                                                alertDialog.show();
                                            }
                                        });
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                } finally {

                                }
                            }
                        });

                        t.start();

                    }else{
                        if (pd.isShowing()){
                            pd.dismiss();
                            talert.tampilDialogDefault("Kesalahan", "Format file atau judul tidak didukung. Harap gunakan File Document / File Gambar dan nama file yang pendek");
                        }
                    }
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public static String getMimeType(String urlD) {
        String type = null;
        String url = urlD.substring(urlD.lastIndexOf("."));
        Log.w("Substring : ", url);
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type;
    }

    @Override
    public void terimaDataDialogBerkas(String namafile) {
        this.namafileUpload = namafile;
        new MaterialFilePicker()
                .withActivity(KasusDetailPengacara.this)
                .withRequestCode(10)
                .start();
    }

    private void doDownload(String urlFile, String namaberkas) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlFile));
        startActivity(browserIntent);
//        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(urlFile));
//        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
//        request.setTitle("Download");
//        request.setDescription("Downloading : " + namaberkas);
//        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
//        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "filefile.jpg");
//        DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
//        manager.enqueue(request);
//        Toast.makeText(context, "Downloading : " + namaberkas, Toast.LENGTH_SHORT).show();
    }
}