package com.itcteam.advokatmonitor.ui.main;

import android.content.Context;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.itcteam.advokatmonitor.R;
import com.itcteam.advokatmonitor.kasus_fragment.FragKasusBaru;
import com.itcteam.advokatmonitor.kasus_fragment.FragKasusBerjalan;

import java.util.HashMap;
import java.util.Map;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    private  String respon;
    @StringRes
    private static final int[] TAB_TITLES = new int[]{R.string.tab_text_1, R.string.tab_text_2, R.string.tab_text_3, R.string.tab_text_4};
    private final Context mContext;
    private final Integer mLevel;

    public SectionsPagerAdapter(Context context, FragmentManager fm, Integer level) {
        super(fm);
        mContext = context;
        mLevel = level;
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
//        return PlaceholderFragment.newInstance(position + 1);
        Fragment fragment = null;
        Log.w("Posisi", Integer.toString(position));

        switch (position){
            case 0:
                fragment = FragKasusBerjalan.newInstance(position+1);
                break;
            case 1:
                fragment = FragKasusBerjalan.newInstance(position+1);
                break;
            case 2:
                fragment = FragKasusBerjalan.newInstance(position+1);
            case 3:
                fragment = FragKasusBerjalan.newInstance(position+1);
        }
        return fragment;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getResources().getString(TAB_TITLES[position]);
    }
//
//    public Void getKasusAPI(String status){
//        RequestQueue queue = Volley.newRequestQueue(mContext);
//
//        String url = "http://192.168.43.90/advokat/api/key/kasus";
//        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        respon = response;
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        error.getStackTrace();
//                    }
//                })
//        {
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                Map<String, String> params = new HashMap<String, String>();
//                params.put("status",status);
//                return params;
//            }
//        };
//        queue.add(stringRequest);
//    }

    @Override
    public int getCount() {
        return 4;
    }
}