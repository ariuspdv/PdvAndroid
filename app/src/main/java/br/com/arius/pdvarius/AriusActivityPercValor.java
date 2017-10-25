package br.com.arius.pdvarius;

import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import arius.pdv.db.AndroidUtils;

/**
 * Created by Arius on 25/10/2017.
 */

public class AriusActivityPercValor {

    private String error = "";
    private TextView lbTitulo;
    private EditText edtValor;
    private EditText edtPorc;
    private View.OnClickListener clickbotao;
    private boolean digitadoPorc = false;
    private boolean digitadoValor = false;
    private boolean aceitaporcentagem;
    private String titulo = "";

    private double valor;
    private double retorno_valor;

    public AriusActivityPercValor(){
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public double getRetorno_valor() {
        return retorno_valor;
    }

    public void setAceitaporcentagem(boolean aceitaporcentagem) {
        this.aceitaporcentagem = aceitaporcentagem;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
        if (lbTitulo != null) {
            lbTitulo.setText(titulo);
            lbTitulo.setVisibility(View.VISIBLE);
        }
    }

    public void setEdtValor(double valor){
        if (valor > 0)
            edtValor.setText(AndroidUtils.FormatarValor_Monetario(valor));
    }

    public void montaDialog_Campos(final AlertDialog alertDialog, View view){
        lbTitulo = (TextView) alertDialog.findViewById(R.id.lbContentDialogPercValorTitulo);

        edtValor = (EditText) alertDialog.findViewById(R.id.edtContentDialogPercValorValor);
        edtPorc = (EditText) alertDialog.findViewById(R.id.edtContentDialogPercValorPerc);

        clickbotao = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edtPorc.addTextChangedListener(new TextWatcher() {
                    private String current = "";

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {}

                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count,
                                                  int after) {}

                    @Override
                    public void afterTextChanged(Editable s) {
                        if (!s.toString().equals(current)) {
                            error = current;
                            edtPorc.removeTextChangedListener(this);

                            String cleanString = s.toString().replace(".","");
                            if (cleanString.contains("%"))
                                cleanString = cleanString.replace("%","");
                            else
                                if (cleanString.length() > 1)
                                    cleanString = String.copyValueOf(cleanString.toCharArray(),0,cleanString.length() - 1);

                            double parsed;
                            try {
                                parsed = Double.parseDouble(cleanString);
                            } catch (NumberFormatException e) {
                                parsed = 0.00;
                            }

                            if ((parsed / 100) >= 100 && !aceitaporcentagem) {
                                edtPorc.setText(error);
                                current = error;
                                edtPorc.setSelection(edtPorc.getText().length());
                                AndroidUtils.toast(alertDialog.getContext(), titulo + " concedido maior que o permitido!");
                            } else {
                                current = new DecimalFormat("####0.00").format(parsed / 100) + "%";
                                edtPorc.setText(current);
                                edtPorc.setSelection(edtPorc.getText().length());

                                digitadoPorc = true;

                                if (!digitadoValor) {
                                    double v_valor;

                                    if (parsed == 0)
                                        v_valor = 0;
                                    else
                                        v_valor = ((valor * (parsed / 100)) / 100);

                                    if (v_valor > 0.01 || v_valor == 0) {
                                        retorno_valor = v_valor;
                                        NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
                                        edtValor.setText(String.valueOf(nf.format(v_valor)));
                                    }
                                }
                            }
                            digitadoPorc = false;

                            edtPorc.addTextChangedListener(this);
                        }
                    }
                });

                edtValor.addTextChangedListener(new TextWatcher() {
                    private String current = "";
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {}

                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count,
                                                  int after) {}

                    @Override
                    public void afterTextChanged(Editable s) {
                        if (!s.toString().equals(current)) {
                            error = current;
                            edtValor.removeTextChangedListener(this);

                            NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

                            String replaceable = String.format("[%s,.\\s]", nf.getCurrency().getSymbol());
                            String cleanString = s.toString().replaceAll(replaceable, "");

                            double parsed;
                            try {
                                parsed = Double.parseDouble(cleanString);
                            } catch (NumberFormatException e) {
                                parsed = 0.00;
                            }

                            String formatted = String.copyValueOf(nf.format(parsed / 100).toCharArray(), 0, 2);
                            int len = nf.format(parsed / 100).length();
                            formatted = formatted + " " + String.copyValueOf(nf.format(parsed / 100).toCharArray(), 2, len - 2);
                            current = formatted;
                            retorno_valor = (parsed / 100);
                            edtValor.setText(formatted);
                            edtValor.setSelection(formatted.length());

                            digitadoValor = true;

                            if (!digitadoPorc) {

                                double v_perc;

                                v_perc = (valor * (parsed / 100));
                                if (v_perc >= 100 && !aceitaporcentagem) {
                                    edtValor.setText(error);
                                    current = error;
                                    replaceable = String.format("[%s,.\\s]", nf.getCurrency().getSymbol());
                                    if (!error.equals("")) {
                                        cleanString = error.replaceAll(replaceable, "");
                                        v_perc = (valor * (Double.parseDouble(cleanString) / 100));
                                    }
                                    edtValor.setSelection(edtValor.getText().length());
                                    AndroidUtils.toast(alertDialog.getContext(), titulo + " concedido maior que o permitido!");
                                } else
                                    edtPorc.setText(new DecimalFormat("####0.00").format(v_perc) + "%");

                                edtPorc.setText(new DecimalFormat("####0.00").format(v_perc) + "%");
                                edtPorc.setSelection(edtPorc.getText().length());
                            }
                            digitadoValor = false;

                            edtValor.addTextChangedListener(this);
                        }
                    }
                });
            }
        };

        clickbotao.onClick(view);

        alertDialog.findViewById(R.id.btnContentDialogValorCancelar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        if (!titulo.equals("")) {
            lbTitulo.setText(this.titulo);
            lbTitulo.setVisibility(View.VISIBLE);
        }
        else
            lbTitulo.setVisibility(View.GONE);
    }

}
