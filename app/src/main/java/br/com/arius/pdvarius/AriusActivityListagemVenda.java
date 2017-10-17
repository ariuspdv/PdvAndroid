package br.com.arius.pdvarius;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

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
                Dialog datePickerDialog = new DatePickerDialog(AriusActivityListagemVenda.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {

                                dtInicio.setText(String.valueOf(dayOfMonth)+"/"+
                                                 String.valueOf(monthOfYear)+"/"+
                                                 String.valueOf(year));

                            }
                        },mYear, mMonth, mDay);

                datePickerDialog.show();
            }
        });
    }
}
