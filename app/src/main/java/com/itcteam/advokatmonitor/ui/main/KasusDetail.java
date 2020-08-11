package com.itcteam.advokatmonitor.ui.main;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.itcteam.advokatmonitor.R;
import com.itcteam.advokatmonitor.dbclass.DatabaseHandlerAppSave;
import com.itcteam.advokatmonitor.ui.main.pengacara.KasusListPengacara;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Objects;

public class KasusDetail extends AppCompatActivity {
    DatabaseHandlerAppSave appsave;
    TextView judul_detail, pengirim_detail, status_text_detail, ktp_detail;
    Button pengacara, status_button;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kasus_detail);
        final Context context = this;
        judul_detail = this.findViewById(R.id.judul_detail);
        status_text_detail = this.findViewById(R.id.status_text_detail);
        ktp_detail = this.findViewById(R.id.ktp_detail);
        pengirim_detail = this.findViewById(R.id.pengirim_detail);
        appsave = new DatabaseHandlerAppSave(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        CollapsingToolbarLayout toolBarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        final Integer idKasus = Integer.parseInt(Objects.requireNonNull(getIntent().getStringExtra("id_kasus")));
        final Integer posisiFragment = Integer.parseInt(Objects.requireNonNull(getIntent().getStringExtra("posisi")));
        HashMap<String, String> hash = appsave.getDetailKasus(idKasus, posisiFragment);
        toolBarLayout.setTitle("Detail Kasus");
//      Ambil data untuk detail
        String judul = hash.get("judul");
        String pengirim = hash.get("pengirim");
        String ktp = hash.get("ktp");
        String status = hash.get("status");
        judul_detail.setText(judul);
        pengirim_detail.setText(pengirim);
        ktp_detail.setText(ktp);
        status_text_detail.setText(status);

//      Klik tombol pengacara
        pengacara = this.findViewById(R.id.pengacara_detail);
        pengacara.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, KasusListPengacara.class);
                intent.putExtra("id_kasus", idKasus);
                intent.putExtra("posisi", posisiFragment);
                startActivity(intent);
//                Snackbar.make(view, "Pengacara", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
            }
        });


    }
}