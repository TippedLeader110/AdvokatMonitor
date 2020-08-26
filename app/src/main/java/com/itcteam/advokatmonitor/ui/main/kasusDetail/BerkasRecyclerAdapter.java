package com.itcteam.advokatmonitor.ui.main.kasusDetail;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.itcteam.advokatmonitor.R;

import java.util.HashMap;
import java.util.List;

public class BerkasRecyclerAdapter extends RecyclerView.Adapter<BerkasRecyclerAdapter.BerkasViewHolder> {

    private static final int PERMISSION_STORAGE_CODE = 1000;
    List dataBerkas;
    Context context;
    String namaberkas;
    private KasusDetailPengacara listener;
    private String urlfile;

    public BerkasRecyclerAdapter(Context context, List dataBerkas){
        this.context = context;
        this.dataBerkas = dataBerkas;
        try {
            this.listener =(KasusDetailPengacara) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " Kelasnya wajib ada ");
        }
    }

    public void setListener(KasusDetailPengacara listener){
        this.listener = listener;
    }

    @NonNull
    @Override
    public BerkasViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.listview_daftarberkas, null);
        return new BerkasViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return  dataBerkas.size();
    }

    @Override
    public void onBindViewHolder(@NonNull BerkasViewHolder holder, int position) {
        final HashMap<String,String> value = (HashMap<String, String>) dataBerkas.get(position);
        holder.namaberkas.setText(value.get("namaberkas"));
        holder.idfile.setText(value.get("id"));
        holder.urlfile.setText(value.get("urlberkas"));
        holder.thumb.setImageResource(R.drawable.ic_baseline_insert_drive_file_24);

        holder.download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = context.getString(R.string.base_url_public);
                url = url + "kasus/berkas/" + value.get("urlberkas");
                Log.w("URL NAMA : ", url+"  "+value.get("namaberkas"));
                listener.downloadFile(url, value.get("urlberkas"));
            }
        });

        holder.hapus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.hapusBerkas(value.get("id"));
            }
        });
    }

    public class BerkasViewHolder extends RecyclerView.ViewHolder{

        TextView namaberkas, urlfile, idfile;
        ImageView thumb;
        Button download, hapus;

        public BerkasViewHolder(@NonNull View itemView) {
            super(itemView);
            namaberkas = itemView.findViewById(R.id.namaberkas);
            download = itemView.findViewById(R.id.download_berkas);
            hapus = itemView.findViewById(R.id.hapus_berkas);
            urlfile = itemView.findViewById(R.id.urlberkas);
            idfile = itemView.findViewById(R.id.id_berkas);
            thumb = itemView.findViewById(R.id.file_thumb);
        }
    }

    public interface ListenerRecyclerBerkas{
        void downloadFile(String url, String filename);
        void hapusBerkas(String id);
    }

}
