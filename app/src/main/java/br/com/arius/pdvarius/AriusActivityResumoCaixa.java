package br.com.arius.pdvarius;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import arius.pdv.base.Finalizadora;
import arius.pdv.base.PdvService;
import arius.pdv.base.PdvUtil;
import arius.pdv.base.PdvValor;
import arius.pdv.base.PdvValorDao;
import arius.pdv.base.PdvValorTipo;
import arius.pdv.core.AppContext;
import arius.pdv.core.UserException;
import arius.pdv.db.AndroidUtils;
import arius.pdv.db.AriusAlertDialog;
import arius.pdv.db.AriusCursorAdapter;

/**
 * Created by Arius on 16/10/2017.
 */

public class AriusActivityResumoCaixa extends ActivityPadrao {

    private ListView grdResumoCaixa;
    private boolean sangria;
    private ResumoCaixa resumoCaixa;
    private List<ResumoCaixa> lresumo = new ArrayList<>();
    private Button btnEncerrar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contentariusresumocaixa);

        setButtons(true, false, false);

        Intent intent = getIntent();
        sangria = intent.getBooleanExtra("sangria",false);

        btnEncerrar = (Button) findViewById(R.id.btnResumoCaixaFecharCaixa);

        grdResumoCaixa = (ListView) findViewById(R.id.grdResumoCaixa);

        TextView lbDataAbertura = (TextView) findViewById(R.id.lbResumoCaixaDtAbertura);
        lbDataAbertura.setText("Data Abertura Caixa: " + PdvUtil.converteData_texto(PdvService.get().getPdv().getDataAbertura()));

        resumoCaixa();
    }

    private void resumoCaixa(){
        List<PdvValor> teste = AppContext.get().getDao(PdvValorDao.class).listDatabase(
        "pdv_id = " + PdvService.get().getPdv().getId() +
                " and strftime('%d/%m/%Y',datetime(substr(data_hora,0, length(data_hora)-2),  'unixepoch', 'localtime')) = '" +
                PdvUtil.converteData_texto(PdvService.get().getPdv().getDataAbertura())+ "'");

        boolean vencontroFinalizadora;
        for (PdvValor loop : teste) {
            vencontroFinalizadora = false;
           for (ResumoCaixa loopresumo : lresumo){
               if (loopresumo.finalizadora == loop.getFinalizadora()) {
                   vencontroFinalizadora = true;

                   if (loop.getTipo() == PdvValorTipo.REFORCO){
                       loopresumo.valor = loopresumo.valor + loop.getValor();
                   }

                   if (loop.getTipo() == PdvValorTipo.RETIRADA){
                       loopresumo.valor = loopresumo.valor - loop.getValor();
                   }

                   if (loop.getTipo() == PdvValorTipo.SANGRIA){
                       loopresumo.valor = loopresumo.valor - loop.getValor();
                   }

               }
           }
           if (!vencontroFinalizadora && ((loop.getFinalizadora().isAceitaSangria() && sangria) || !sangria))
               lresumo.add(new ResumoCaixa(loop.getFinalizadora(),loop.getValor()));
        }

        AriusCursorAdapter adapter = new AriusCursorAdapter(getBaseContext(),
                R.layout.layoutresumocaixa,
                R.layout.layoutresumocaixa,
                null,
                lresumo);

        adapter.setMontarCamposTela(new AriusCursorAdapter.MontarCamposTela() {
            @Override
            public void montarCamposTela(Object p, View v) {
                resumoCaixa = (ResumoCaixa) p;
                TextView campoaux = v.findViewById(R.id.lbLayoutResumoCaixaFinalizadora);

                if (campoaux != null)
                    campoaux.setText(resumoCaixa.finalizadora.getDescricao());

                campoaux = v.findViewById(R.id.lbLayoutResumoCaixaFinalizadoraValor);

                if (campoaux != null)
                    campoaux.setText(AndroidUtils.FormatarValor_Monetario(resumoCaixa.valor - resumoCaixa.valor_sangrar));

                campoaux = v.findViewById(R.id.edtLayoutResumoCaixaFinalizadoraValorSangra);

                if (campoaux != null)
                    campoaux.setText(AndroidUtils.FormatarValor_Monetario(resumoCaixa.valor_sangrar));
            }
        });

        grdResumoCaixa.setAdapter(adapter);

        if (sangria) {
            grdResumoCaixa.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    resumoCaixa = (ResumoCaixa) adapterView.getItemAtPosition(i);
                    criarDialogSangrar();
                }
            });
        }

        setBotaoEncerrar();

    }

    private void criarDialogSangrar(){
        AriusAlertDialog.exibirDialog(AriusActivityResumoCaixa.this, R.layout.content_dialog_sangria);
        AriusAlertDialog.getGetView().findViewById(R.id.cmbSangriaDialogFinalizadora).setVisibility(View.GONE);
        AriusAlertDialog.getGetView().findViewById(R.id.lbSangriaDialogFinalizadora).setVisibility(View.GONE);

        final EditText edtSangria = AriusAlertDialog.getGetView().findViewById(R.id.edtSangriaDialogValor);

        AriusAlertDialog.getGetView().findViewById(R.id.btnSangriaDialogCancelar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AriusAlertDialog.getAlertDialog().dismiss();
            }
        });

        AriusAlertDialog.getGetView().findViewById(R.id.btnSangriaDialogOK).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("pt","BR"));
                Double v_valor = 0.0;
                try{
                    v_valor = nf.parse(edtSangria.getText().toString().replace(" ","")).doubleValue();
                }catch (ParseException e){
                    e.printStackTrace();
                }
                if (edtSangria.getText().toString().equals("") || v_valor <= 0) {
                    edtSangria.requestFocus();
                    throw new UserException("Informar um valor para a sangria!");
                }

                for (ResumoCaixa loopresumo : lresumo) {
                    if (loopresumo.finalizadora == resumoCaixa.finalizadora)
                        loopresumo.valor_sangrar += v_valor;
                }

                ((AriusCursorAdapter) grdResumoCaixa.getAdapter()).notifyDataSetChanged();

                AriusAlertDialog.getAlertDialog().dismiss();
            }
        });

        edtSangria.addTextChangedListener(new TextWatcher() {
            private String texto;
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                edtSangria.removeTextChangedListener(this);

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

                edtSangria.setText(AndroidUtils.FormatarValor_Monetario(parsed));
                edtSangria.setSelection(edtSangria.getText().toString().length());
                texto = edtSangria.getText().toString();
                edtSangria.addTextChangedListener(this);
            }
        });

        edtSangria.setText("0");

        edtSangria.setSelection(edtSangria.getText().length());
    }

    private void setBotaoEncerrar(){
        if (!sangria)
            btnEncerrar.setText("FECHAR CAIXA");
        else
            btnEncerrar.setText("EFETUAR SANGRIA");

        if (!sangria){
            btnEncerrar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PdvService.get().fechaCaixa(PdvService.get().getOperadorAtual());
                    onBackPressed();
                }
            });

        }
    }

    class ResumoCaixa{
        private Finalizadora finalizadora;
        private double valor;
        private double valor_sangrar;

        ResumoCaixa(Finalizadora finalizadora, double valor){
            this.finalizadora = finalizadora;
            this.valor = valor;
        }
    }
}
