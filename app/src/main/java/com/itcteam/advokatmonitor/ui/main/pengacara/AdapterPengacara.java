package com.itcteam.advokatmonitor.ui.main.pengacara;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.itcteam.advokatmonitor.R;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdapterPengacara extends SimpleAdapter {
    private Context context;
    public LayoutInflater layoutInflater = null;

    public AdapterPengacara(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to) {
        super(context, data, resource, from, to);
        this.context = context;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
//        return super.getView(position, convertView, parent);
        View vi = convertView;
        if (convertView == null)
            vi = layoutInflater.inflate(R.layout.listview_daftarpengacara, null);
        HashMap<String, Object> data = (HashMap<String, Object>) getItem(position);
        Picasso.get().load((String) data.get("foto")).into((ImageView) vi.findViewById(R.id.fotopengacara_listview));
        TextView id = vi.findViewById(R.id.id_pengacara);
        id.setText((String) data.get("id"));
        TextView nama = vi.findViewById(R.id.namapengacara_listview);
        nama.setText((String) data.get("nama"));
        TextView email = vi.findViewById(R.id.emailpengacara_listview);
        email.setText((String) data.get("email"));
        TextView nohp = vi.findViewById(R.id.nohppengacara_listview);
        nohp.setText((String) data.get("nohp"));
        return vi;
    }
}
