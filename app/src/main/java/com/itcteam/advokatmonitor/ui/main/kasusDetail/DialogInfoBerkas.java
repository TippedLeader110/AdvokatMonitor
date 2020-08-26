package com.itcteam.advokatmonitor.ui.main.kasusDetail;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.itcteam.advokatmonitor.R;

import java.util.Calendar;

public class DialogInfoBerkas extends DialogFragment {
    private EditText berkas;
    private EditDialogListener listener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder= new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_info_berkas, null);
        berkas = view.findViewById(R.id.nama_berkas);
        builder.setView(view)
                .setTitle("Upload Berkas Pendukung")
                .setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dismiss();
                    }
                })
                .setPositiveButton("Simpan", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String namaBerkas = berkas.getText().toString();
                        listener.terimaDataDialogBerkas(namaBerkas);
                    }
                });
        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (EditDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " Kelasnya wajib ada ");
        }
    }

    public interface EditDialogListener{
        void terimaDataDialogBerkas(String namafile);
    }

}
