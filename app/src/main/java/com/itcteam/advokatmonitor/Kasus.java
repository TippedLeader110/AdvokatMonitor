package com.itcteam.advokatmonitor;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.itcteam.advokatmonitor.ui.main.SectionsPagerAdapter;

public class Kasus extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CekSesi ck = new CekSesi(Kasus.this);
        ck.doJob();
        if (ck.getStatus()){
            Intent intent = new Intent(Kasus.this, Login.class);
            startActivity(intent);
            finish();
        }else{
            setContentView(R.layout.activity_kasus);
            String level = getIntent().getStringExtra("LEVEL_ACCOUNT");
            SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager(), Integer.parseInt(level));
            ViewPager viewPager = findViewById(R.id.view_pager);
            viewPager.setAdapter(sectionsPagerAdapter);
            TabLayout tabs = findViewById(R.id.tabs);
            tabs.setupWithViewPager(viewPager);
        }
    }
}