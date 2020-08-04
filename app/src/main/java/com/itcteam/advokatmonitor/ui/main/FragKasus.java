package com.itcteam.advokatmonitor.ui.main;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.itcteam.advokatmonitor.DatabaseHandler;
import com.itcteam.advokatmonitor.Login;
import com.itcteam.advokatmonitor.R;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragKasus#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragKasus extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "tab";
    private static final String ARG_PARAM2 = "level";
//
    // TODO: Rename and change types of parameters
    private Integer mParam1;
    private Integer mParam2;
    private String Return, status;
    TextView textTest;
    DatabaseHandler DBset;
    public FragKasus() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragKasus.
     */
    // TODO: Rename and change types and number of parameters
    public static FragKasus newInstance(Integer param1, Integer param2) {
        FragKasus fragment = new FragKasus();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, param1);
        args.putInt(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getInt(ARG_PARAM1);
            mParam2 = getArguments().getInt(ARG_PARAM2);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_frag_kasus, container, false);
        TextView textView = root.findViewById(R.id.testText);
        textView.setText(getKasusAPI());
        return root;
    }

    public String getKasusAPI(){
        if (mParam1==1){
            status = "baru";
        }
        else if ((mParam1==2)){
            status = "berjalan";
        }
        else{
            status = "tutup";
        }
        RequestQueue queue = Volley.newRequestQueue(getActivity());

        String url = "http://192.168.43.90/advokat/api/key/kasus";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Return = response;
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Return = "ERROR";
                    }
                })
                {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("status",status);
                        return params;
                    }
                };
        queue.add(stringRequest);
        return Return;
    }
}