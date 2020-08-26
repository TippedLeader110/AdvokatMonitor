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

public class DialogEditKasus extends DialogFragment {
    private EditText tgl, tempat, pekerjaan;
    DatePickerDialog.OnDateSetListener dateSetListener;
    private EditDialogListener listener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder= new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_edit_kasus, null);
        
        builder.setView(view)
                .setTitle("Edit Kasus")
                .setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dismiss();
                    }
                })
                .setPositiveButton("Simpan", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String tanggalString = tgl.getText().toString();
                        String tempatString = tempat.getText().toString();
                        String pekerjaanString = pekerjaan.getText().toString();
                        listener.terimaDataDialog(tanggalString, tempatString, pekerjaanString);
                    }
                });

        tgl = view.findViewById(R.id.tgllahir_edit);
        tempat = view.findViewById(R.id.tmptlahir_edit);
        pekerjaan = view.findViewById(R.id.pekerjaan_edit);

        tgl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dialog = new DatePickerDialog(
                        getContext(),
                        android.R.style.Theme_Holo_Light_Dialog,
                        dateSetListener,
                        year, month, day
                );
//                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                String monthShow = "0";
                if (month>=10){
                    monthShow = Integer.toString(month);
                }else{
                    monthShow = "0"+month;
                }
                String date = year+"-"+monthShow+"-"+day;
                tgl.setText(date);
            }
        };
        
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
        void terimaDataDialog(String tanggalString, String tempatString, String pekerjaanString);
    }

}
