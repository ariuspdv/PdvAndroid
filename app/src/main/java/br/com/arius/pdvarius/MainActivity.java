package br.com.arius.pdvarius;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

import arius.pdv.base.PdvService;
import arius.pdv.base.UsuarioDao;
import arius.pdv.core.AppContext;
import arius.pdv.core.UserException;
import arius.pdv.db.AndroidUtils;
import arius.pdv.db.AriusAlertDialog;

public class MainActivity extends ActivityPadrao {

    @Override
    protected void onStart() {
        super.onStart();

        if (PdvService.get().getPdv() == null)
            return;

        if (PdvService.get().getPdv().isAberto() && PdvService.get().getOperadorAtual() != null){

            Intent intent = new Intent(getBaseContext(), VendaClassificacaoActivity.class);

            startActivity(intent);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        AuxiliarCadastros auxiliarCadastros = new AuxiliarCadastros();
//        auxiliarCadastros.cadastrarPDV();
//        auxiliarCadastros.cadastrarOperador();
//        auxiliarCadastros.cadastrarFinalizadora();
//        auxiliarCadastros.cadastraProdutos();

        Button btn = (Button) findViewById(R.id.btn_Entrar);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PdvService.get().setOperadorAtual(AppContext.get().getDao(UsuarioDao.class).find(1));
                if (PdvService.get().getOperadorAtual() == null){
                    throw new UserException("Operação não permitida! \n Operador não encontrado!");
                }


                AriusAlertDialog.exibirDialog(MainActivity.this,R.layout.content_dialog_reforco);

                final Button btnOK = (Button) AriusAlertDialog.getGetView().findViewById(R.id.btnReforcoDialogOK);
                final Button btnCancel = (Button) AriusAlertDialog.getGetView().findViewById(R.id.btnReforcoDialogCancelar);
                final TextView lbRefornco = (TextView) AriusAlertDialog.getGetView().findViewById(R.id.lbReforcoDialog);
                final EditText edtReforco = (EditText) AriusAlertDialog.getGetView().findViewById(R.id.edtReforcoDialog);

                lbRefornco.setText("Valor abertura caixa");

                edtReforco.addTextChangedListener(new TextWatcher() {
                    private String texto;
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        edtReforco.removeTextChangedListener(this);

                        NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("pt","BR"));

                        double parsed;
                        if (editable.toString().contains(nf.getCurrency().getSymbol())) {
                            String replaceable = String.format("[%s,.\\s]", nf.getCurrency().getSymbol());
                            String  cleanString = editable.toString().replaceAll(replaceable, "");

                            try {
                                parsed = Double.parseDouble(cleanString);
                            } catch (NumberFormatException e) {
                                parsed = 0.00;
                            }

                            parsed = parsed / 100;
                        } else
                            parsed = Double.parseDouble(editable.toString());

                        edtReforco.setText(AndroidUtils.FormatarValor_Monetario(parsed));
                        edtReforco.setSelection(edtReforco.getText().toString().length());
                        texto = edtReforco.getText().toString();
                        edtReforco.addTextChangedListener(this);
                    }
                });

                btnOK.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("pt","BR"));
                        Double v_valor = 0.0;
                        try{
                            v_valor = nf.parse(edtReforco.getText().toString().replace(" ","")).doubleValue();
                        }catch (ParseException e){
                            e.printStackTrace();
                        }
                        if (edtReforco.getText().toString().equals("") || v_valor <= 0)
                            throw new UserException("Informar um valor para a abertura do caixa!");

                        //iniciaBarraProgresso();

                        PdvService.get().abreCaixa(AppContext.get().getDao(UsuarioDao.class).find(1),v_valor);

                        Intent intent = new Intent(getBaseContext(), VendaClassificacaoActivity.class);

                        startActivity(intent);

                        AriusAlertDialog.getAlertDialog().dismiss();
                    }
                });

                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AriusAlertDialog.getAlertDialog().dismiss();
                    }
                });

                edtReforco.setText("0");
                edtReforco.requestFocus();
                edtReforco.setSelection(edtReforco.getText().length());
            }
        });

        setButtons(false, false, false);

    }

}
