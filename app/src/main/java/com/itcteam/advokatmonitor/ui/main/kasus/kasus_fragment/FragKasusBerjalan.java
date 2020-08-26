package com.itcteam.advokatmonitor.ui.main.kasus.kasus_fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.itcteam.advokatmonitor.ui.main.kasusDetail.KasusDetailAdmin;
import com.itcteam.advokatmonitor.dbclass.DatabaseHandlerAppSave;
import com.itcteam.advokatmonitor.R;
import com.itcteam.advokatmonitor.ui.main.kasusDetail.KasusDetailPengacara;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragKasusBerjalan#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragKasusBerjalan extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "level";
    private static final String status = "berjalan";
//
    // TODO: Rename and change types of parameters

    private Integer mParam1;
    private String Return;
    ListView listView;
    private AdapterView.OnItemClickListener itemKlik;
    List listJudul;
    DatabaseHandlerAppSave DBset;
    public FragKasusBerjalan() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment FragKasus.
     */
    // TODO: Rename and change types and number of parameters
    public static FragKasusBerjalan newInstance(Integer param1) {
        FragKasusBerjalan fragment = new FragKasusBerjalan();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getInt(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_frag_kasus, container, false);
        listView = root.findViewById(R.id.listViewBerjalan);
        final DatabaseHandlerAppSave databaseHandlerAppSave = new DatabaseHandlerAppSave(getContext());
        ArrayList<HashMap<String, String>> daftarKasus;
        if(databaseHandlerAppSave.getLevel()==1){
            daftarKasus = databaseHandlerAppSave.getKasus(mParam1);
        }
        else{
            daftarKasus = databaseHandlerAppSave.getKasusPengacara(mParam1);
        }
        ListAdapter adapter = new SimpleAdapter(root.getContext(), daftarKasus, R.layout.listview_fragmentkasus,new String[]{"id","judul","pengirim","ktp"}, new int[]{R.id.id_listview,R.id.judul_listview, R.id.pengirim_listview, R.id.ktp});
        listView.setAdapter(adapter);
        listView.setClickable(true);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView id = view.findViewById(R.id.id_listview);
                Intent intent;
                String idval = (String) id.getText();
                if (databaseHandlerAppSave.getLevel()==1){
                     intent = new Intent(getContext(), KasusDetailAdmin.class);
                }
                else{
                    intent = new Intent(getContext(), KasusDetailPengacara.class);
                }
                intent.putExtra("id_kasus", idval);
                intent.putExtra("posisi",  Integer.toString(mParam1));
                startActivity(intent);
                getActivity().finish();
            }
        });
        return root;
    }

    public String getKasusAPI(){
        RequestQueue queue = Volley.newRequestQueue(getActivity());

        String url = getString(R.string.base_url)+"kasus";
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