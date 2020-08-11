package com.itcteam.advokatmonitor.ui.main.pengacara;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.itcteam.advokatmonitor.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class KasusListPengacara extends AppCompatActivity {
    ArrayList<HashMap<String, Object>> daftarPengacara;
    ListView kasuslist_pengacara;
    ProgressDialog pd;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kasus_list_pengacara);
        pd = new ProgressDialog(this);
        final Context context = this;
        pd.setCancelable(false);
        pd.setTitle("Mohon Tunggu !!!");
        pd.setMessage("Mengambil data");
        pd.show();
        daftarPengacara = new ArrayList<HashMap<String, Object>>();
        kasuslist_pengacara = this.findViewById(R.id.listview_pengacara);
        imageView = this.findViewById(R.id.fotopengacara_listview);

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://192.168.43.90/advokat/api/key/pengacara";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (pd.isShowing()){
                            try {
                                JSONArray jsonArray = new JSONArray(response);
                                for (int i = 0; i < jsonArray.length(); i++){
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    Log.e("Data", String.valueOf(jsonObject));
                                    HashMap<String,Object> pengacara = new HashMap<>();
                                    pengacara.put("id",Integer.toString(jsonObject.getInt("id")));
                                    pengacara.put("nama",jsonObject.getString("nama"));
                                    pengacara.put("email",jsonObject.getString("email"));
                                    pengacara.put("nohp",jsonObject.getString("nohp"));
                                    String urlfoto = "http://192.168.43.90/advokat/public/pengacara/foto/" + jsonObject.getString("foto");
                                    Log.e("url", urlfoto);
                                    pengacara.put("foto",urlfoto);
                                    daftarPengacara.add(pengacara);
                                }
//                                ListAdapter adapter = new SimpleAdapter(context, daftarPengacara, R.layout.listview_daftarpengacara,new String[]{"id","nama","email","nohp", "foto"}, new int[]{R.id.id_pengacara,R.id.namapengacara_listview, R.id.emailpengacara_listview, R.id.nohppengacara_listview, R.id.fotopengacara_listview});
                                AdapterPengacara adapter = new AdapterPengacara(context, daftarPengacara, R.layout.listview_daftarpengacara,new String[]{"id","nama","email","nohp", "foto"}, new int[]{R.id.id_pengacara,R.id.namapengacara_listview, R.id.emailpengacara_listview, R.id.nohppengacara_listview, R.id.fotopengacara_listview});
                                kasuslist_pengacara.setAdapter(adapter);
                                pd.dismiss();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }
        );
        queue.add(stringRequest);
    }
}

