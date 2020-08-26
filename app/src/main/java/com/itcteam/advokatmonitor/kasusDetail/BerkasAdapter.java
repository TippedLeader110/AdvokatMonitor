package com.itcteam.advokatmonitor.kasusDetail;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.itcteam.advokatmonitor.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BerkasAdapter extends SimpleAdapter {

    private Context context;
    public LayoutInflater layoutInflater = null;

    public BerkasAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to) {
        super(context, data, resource, from, to);
        this.context = context;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Bitmap thumb;
        View vi = convertView;
        if (convertView == null)
            vi = layoutInflater.inflate(R.layout.listview_daftarberkas, null);
        HashMap<String, Object> data = (HashMap<String, Object>) getItem(position);
        final TextView id_berkas = vi.findViewById(R.id.id_berkas);
        TextView namaberkas = vi.findViewById(R.id.namaberkas);
        TextView urlberkas = vi.findViewById(R.id.urlberkas);
        ImageView imageView = vi.findViewById(R.id.file_thumb);

        imageView.setImageResource(R.drawable.ic_baseline_insert_drive_file_24);
        namaberkas.setText((String) data.get("namaberkas"));
        id_berkas.setText((String) data.get("id"));
        urlberkas.setText((String) data.get("urlberkas"));
        Button hapus_berkas = vi.findViewById(R.id.hapus_berkas);
        Button download_berkas = vi.findViewById(R.id.download_berkas);
        download_berkas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Log.w("ID SEL : " , (String) id_berkas.getText());
            }
        });

        return vi;
    }
}
