package br.com.arius.pdvarius;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.AlertDialogLayout;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Arius on 16/10/2017.
 */

public class AriusActivityListagemVenda extends ActivityPadrao {

    private SimpleDateFormat dateFormatter;
    private int mYear, mMonth, mDay, mHour, mMinute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contentariuslistagemvenda);

        setButtons(true, false, false);

        carregaCamposData();
    }

    private void carregaCamposData(){
        dateFormatter = new SimpleDateFormat("dd/MM/yyyy", Locale.US);

        final EditText dtInicio = (EditText) findViewById(R.id.edtcontentariusListagemVendaDataInicio);
        EditText dtFim = (EditText) findViewById(R.id.edtcontentariusListagemVendaDataFim);

        Calendar newDate = Calendar.getInstance();
        newDate.set(newDate.get(Calendar.YEAR), newDate.get(Calendar.MONTH), newDate.get(Calendar.DAY_OF_MONTH));
        dtInicio.setText(dateFormatter.format(newDate.getTime()));
        dtFim.setText(dateFormatter.format(newDate.getTime()));

        mYear = 2017;
        mMonth = 10;
        mDay = 16;

        dtInicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(AriusActivityListagemVenda.this,
                        AlertDialog.THEME_HOLO_LIGHT,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {

                                dtInicio.setText(String.valueOf(dayOfMonth)+"/"+
                                                 String.valueOf(monthOfYear)+"/"+
                                                 String.valueOf(year));

                            }
                        },mYear, mMonth, mDay);


                // Create a TextView programmatically.
                TextView tv = new TextView(datePickerDialog.getContext());

                // Create a TextView programmatically
                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT, // Width of TextView
                        RelativeLayout.LayoutParams.WRAP_CONTENT); // Height of TextView
                tv.setLayoutParams(lp);
                tv.setPadding(10, 10, 10, 10);
                tv.setGravity(Gravity.CENTER);
                tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP,20);
                tv.setText("Data Inicio");
                tv.setTextColor(Color.parseColor("#ff0000"));
                tv.setBackgroundColor(Color.parseColor("#FFD2DAA7"));
                tv.setTextSize(30);
                datePickerDialog.setCustomTitle(tv);
                datePickerDialog.getDatePicker().setSpinnersShown(false);

                datePickerDialog.setButton(DialogInterface.BUTTON_POSITIVE,"Confirmar",datePickerDialog);
                datePickerDialog.show();
            }
        });
    }
}
