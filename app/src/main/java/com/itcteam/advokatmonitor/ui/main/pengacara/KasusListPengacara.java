package com.itcteam.advokatmonitor.ui.main.pengacara;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.itcteam.advokatmonitor.R;
import com.itcteam.advokatmonitor.dbclass.DatabaseHandlerAppSave;
import com.itcteam.advokatmonitor.simpletask.TampilAlertDialog;
import com.itcteam.advokatmonitor.ui.main.kasus.Kasus;
import com.itcteam.advokatmonitor.ui.main.Login;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class KasusListPengacara extends AppCompatActivity {
    ArrayList<HashMap<String, Object>> daftarPengacara;
    ListView kasuslist_pengacara;
    ProgressDialog pd;
    Context context;
    ImageView imageView;
    DatabaseHandlerAppSave appSave;
    TampilAlertDialog talert;
    Integer id_kasus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kasus_list_pengacara);
        assert getSupportActionBar() != null;   //null check
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);   //show back button
        pd = new ProgressDialog(this);
        context = this;
        id_kasus = Integer.parseInt(Objects.requireNonNull(getIntent().getStringExtra("id_kasus")));
        appSave = new DatabaseHandlerAppSave(context);
        pd.setCancelable(false);
        talert = new TampilAlertDialog(context);
        pd.setTitle("Mohon Tunggu !!!");
        pd.setMessage("Mengambil data");
        pd.show();
        daftarPengacara = new ArrayList<HashMap<String, Object>>();
        kasuslist_pengacara = this.findViewById(R.id.listview_pengacara);
        imageView = this.findViewById(R.id.fotopengacara_listview);

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = getString(R.string.base_url)+"pengacara";
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
                                    String urlfoto =  getString(R.string.base_url_public) + "pengacara/foto/" + jsonObject.getString("foto");
                                    Log.e("url", urlfoto);
                                    pengacara.put("foto",urlfoto);
                                    daftarPengacara.add(pengacara);
                                }
//                                ListAdapter adapter = new SimpleAdapter(context, daftarPengacara, R.layout.listview_daftarpengacara,new String[]{"id","nama","email","nohp", "foto"}, new int[]{R.id.id_pengacara,R.id.namapengacara_listview, R.id.emailpengacara_listview, R.id.nohppengacara_listview, R.id.fotopengacara_listview});
                                AdapterPengacara adapter = new AdapterPengacara(context, daftarPengacara, R.layout.listview_daftarpengacara,new String[]{"id","nama","email","nohp", "foto"}, new int[]{R.id.id_pengacara,R.id.namapengacara_listview, R.id.emailpengacara_listview, R.id.nohppengacara_listview, R.id.fotopengacara_listview});
                                kasuslist_pengacara.setAdapter(adapter);
                                dotheJob();
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

    private void dotheJob() {
        kasuslist_pengacara.setClickable(true);
        kasuslist_pengacara.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView id = view.findViewById(R.id.id_pengacara);
                final String idval = (String) id.getText();
                Toast.makeText(context, "ID NYA : " + idval, Toast.LENGTH_SHORT).show();
                pd.setTitle("Mohon Tunggu !!!");
                pd.setMessage("Memproses");
                pd.show();
                String url = getString(R.string.base_url)+"setPengacara";
                RequestQueue queue = Volley.newRequestQueue(context);
                StringRequest objS = new StringRequest(Request.Method.POST, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONObject jb = new JSONObject(response);
                                    if (jb.getString("error")=="false"){
                                        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
                                        alertDialog.setTitle("Berhasil");
                                        alertDialog.setMessage("Pengacara Kasus berhasil dipilih");
                                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        Intent intent = new Intent(context, Kasus.class);
                                                        intent.putExtra("LEVEL_ACCOUNT", Integer.toString(appSave.getLevel()));
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
                        params.put("token", appSave.getToken());
                        params.put("pengacara", idval);
                        params.put("id_masalah", Integer.toString(id_kasus));
                        return params;
                    }
                };
                queue.add(objS);
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}

