package br.com.arius.pdvarius;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import arius.pdv.base.Finalizadora;
import arius.pdv.base.FinalizadoraTipo;
import arius.pdv.base.Historico;
import arius.pdv.base.HistoricoDao;
import arius.pdv.base.HistoricoTipo;
import arius.pdv.base.PdvService;
import arius.pdv.base.PdvUtil;
import arius.pdv.base.PdvValor;
import arius.pdv.base.PdvValorDao;
import arius.pdv.base.Venda;
import arius.pdv.base.VendaDao;
import arius.pdv.base.VendaSituacao;
import arius.pdv.core.AppContext;
import arius.pdv.core.FuncionaisFilters;
import arius.pdv.core.UserException;
import arius.pdv.db.AndroidUtils;
import arius.pdv.db.AriusAlertDialog;
import arius.pdv.db.AriusCursorAdapter;

/**
 * Created by Arius on 16/10/2017.
 */

public class AriusActivityResumoCaixa extends ActivityPadrao {

    private ListView grdResumoCaixa;
    private String funcaoExecutar;
    private ResumoCaixa resumoCaixa;
    private List<ResumoCaixa> lresumo = new ArrayList<>();
    private Button btnEncerrar;
    double vrTotalVendido = 0;
    double vrTroco = 0;

    private AriusActivityPercentualValor ariusActivityPercentualValor;
    private AlertDialog dialogAux;

    /*Campos Rodapé*/
    private TextView lbCampo1;
    private TextView edtCampo1;
    private TextView lbCampo2;
    private TextView edtCampo2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contentariusresumocaixa);

        ariusActivityPercentualValor = new AriusActivityPercentualValor();

        setButtons(true, false, false);

        Intent intent = getIntent();
        funcaoExecutar = intent.getStringExtra("funcaoExecutar");

        btnEncerrar = (Button) findViewById(R.id.btnResumoCaixaFecharCaixa);

        grdResumoCaixa = (ListView) findViewById(R.id.grdResumoCaixa);

        lbCampo1 = (TextView) findViewById(R.id.lbResumoCaixaRodapeCapo1);
        edtCampo1 = (TextView) findViewById(R.id.edtResumoCaixaRodapeCapo1);
        lbCampo2 = (TextView) findViewById(R.id.lbResumoCaixaRodapeCapo2);
        edtCampo2 = (TextView) findViewById(R.id.edtResumoCaixaRodapeCapo2);

        TextView lbDataAbertura = (TextView) findViewById(R.id.lbResumoCaixaDtAbertura);
        lbDataAbertura.setText("Data Abertura: " + PdvUtil.converteData_texto(PdvService.get().getPdv().getDataAbertura()));

        resumoCaixa();
    }

    private void resumoCaixa(){
        for(Venda loopvendas : AppContext.get().getDao(VendaDao.class).listCache(new FuncionaisFilters<Venda>() {
            @Override
            public boolean test(Venda p) {
                return PdvUtil.comparar_Datas(p.getDataHora(),PdvService.get().getPdv().getDataAbertura())
                        && p.getSituacao() == VendaSituacao.FECHADA;
            }
        })){
            vrTotalVendido += loopvendas.getValorLiquido();
            vrTroco += loopvendas.getValorTroco();
        };

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
                   break;
               }
           }
           if (!vencontroFinalizadora && ((loop.getFinalizadora().isAceitaSangria() && (funcaoExecutar.toLowerCase().equals("sangria"))) ||
                   !funcaoExecutar.toLowerCase().equals("sangria")))
               lresumo.add(new ResumoCaixa(loop.getFinalizadora(),
                       (PdvService.get().calculaSaldoFinalizadora(loop.getFinalizadora())) -
                               (loop.getFinalizadora().getTipo() == FinalizadoraTipo.DINHEIRO ? vrTroco : 0)));
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
                    campoaux.setText(AndroidUtils.FormatarValor_Monetario(resumoCaixa.valor - resumoCaixa.valor_sangrar_retirar));

                campoaux = v.findViewById(R.id.edtLayoutResumoCaixaVrSangrar_Retirar);

                if (campoaux != null)
                    campoaux.setText(AndroidUtils.FormatarValor_Monetario(resumoCaixa.valor_sangrar_retirar));

                if (funcaoExecutar.toLowerCase().equals("sangria")){
                    campoaux = v.findViewById(R.id.lbLayoutResumoCaixaVrSangrar_Retirar);
                    campoaux.setText("Valor Sangrar:");
                }
                if (funcaoExecutar.toLowerCase().equals("fecharcaixa")) {
                    campoaux = v.findViewById(R.id.lbLayoutResumoCaixaVrSangrar_Retirar);
                    campoaux.setVisibility(View.GONE);
                    campoaux = v.findViewById(R.id.edtLayoutResumoCaixaVrSangrar_Retirar);
                    campoaux.setVisibility(View.GONE);
                }
                if (funcaoExecutar.toLowerCase().equals("retirada")){
                    campoaux = v.findViewById(R.id.lbLayoutResumoCaixaVrSangrar_Retirar);
                    campoaux.setText("Valor Retirar:");
                }
            }
        });

        grdResumoCaixa.setAdapter(adapter);

        if (funcaoExecutar.toLowerCase().equals("sangria") || funcaoExecutar.toLowerCase().equals("retirada")) {
            grdResumoCaixa.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    resumoCaixa = (ResumoCaixa) adapterView.getItemAtPosition(i);
                    if (funcaoExecutar.toLowerCase().equals("sangria"))
                        criarDialogSangrar(view);
                    if (funcaoExecutar.toLowerCase().equals("retirada"))
                        criarDialogRetirada();
                }
            });
        }

        setBotaoEncerrar();

        setRodape();
    }

    private void criarDialogSangrar(View view){
        AriusAlertDialog.exibirDialog(AriusActivityResumoCaixa.this, R.layout.dialog_arius_perccentual_valor);
        ariusActivityPercentualValor.montaDialog_Campos(AriusAlertDialog.getAlertDialog(), view, "Sangria");
        ariusActivityPercentualValor.setUtilizaPorcentagem(false);

        AriusAlertDialog.getGetView().findViewById(R.id.btnDialogAriusPerccentualValorConfirmar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ariusActivityPercentualValor.getRetorno_valor() <= 0) {
                    throw new UserException("Informar um valor para a sangria!");
                }

                for (ResumoCaixa loopresumo : lresumo) {
                    if (loopresumo.valor - loopresumo.valor_sangrar_retirar < ariusActivityPercentualValor.getRetorno_valor())
                        throw new UserException("Saldo insuficiente da Finalizadora : " + loopresumo.finalizadora.getDescricao() +
                        " para efeutar a sangria!");

                    if (loopresumo.finalizadora == resumoCaixa.finalizadora)
                        loopresumo.valor_sangrar_retirar += ariusActivityPercentualValor.getRetorno_valor();
                }

                ((AriusCursorAdapter) grdResumoCaixa.getAdapter()).notifyDataSetChanged();

                AriusAlertDialog.getAlertDialog().dismiss();

                setRodape();
            }
        });
    }

    private void criarDialogRetirada() {
        AriusAlertDialog.exibirDialog(AriusActivityResumoCaixa.this,R.layout.dialog_arius_retirada,false);
        dialogAux = AriusAlertDialog.getAlertDialog();
        dialogAux.show();

        dialogAux.findViewById(R.id.btnDialogAriusRetiradaCancelar).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialogAux.dismiss();
                    }
        });

        final Spinner cmbHistorico = (Spinner) dialogAux.findViewById(R.id.cmbDialogAriusRetiradaHistorico);
        List array_historicos = AppContext.get().getDao(HistoricoDao.class).listCache(new FuncionaisFilters<Historico>() {
            @Override
            public boolean test(Historico p) {
                return true;
            }
        });

        Historico selecionar = new Historico();
        selecionar.setId(-1);
        selecionar.setDescricao("Selecionar um histórico");
        selecionar.setTipo(HistoricoTipo.RETIRADA);

        array_historicos.add(0,selecionar);

        AriusCursorAdapter adapter_historico = new AriusCursorAdapter(getAppContext(),
                R.layout.layoutcmbbasico,
                R.layout.layoutcmbbasico,
                null,
                array_historicos);
        adapter_historico.setMontarCamposTela(new AriusCursorAdapter.MontarCamposTela() {
            @Override
            public void montarCamposTela(Object p, View v) {
                Historico historico = (Historico) p;
                TextView campoaux = v.findViewById(R.id.lbcmbbasico);
                if (campoaux != null) {
                    campoaux.setPadding(10, 10, 10, 10);
                    campoaux.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.dropdown, 0);
                    campoaux.setText(historico.getDescricao());
                    campoaux.setTextColor(Color.parseColor("#000000"));
                }

                campoaux = v.findViewById(android.R.id.text1);
                if (campoaux != null){
                    campoaux.setText(historico.getDescricao());
                    campoaux.setTextColor(Color.parseColor("#000000"));
                }

            }
        });
        cmbHistorico.setAdapter(adapter_historico);

        final EditText edtRetirada = (EditText) dialogAux.findViewById(R.id.edtDialogAriusRetiradaValor);

        dialogAux.findViewById(R.id.btnDialogAriusRetiradaConfirmar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("pt","BR"));
                Double v_valor = 0.0;
                try{
                    v_valor = nf.parse(edtRetirada.getText().toString().replace(" ","")).doubleValue();
                }catch (ParseException e){
                    e.printStackTrace();
                }
                if (edtRetirada.getText().toString().equals("") || v_valor <= 0) {
                    edtRetirada.requestFocus();
                    throw new UserException("Informar um valor para a retirada!");
                }

                if (((Historico) cmbHistorico.getSelectedItem()).getId() == -1){
                    cmbHistorico.requestFocus();
                    throw new UserException("Selecionar um histórico para a retirada!");
                }

                for (ResumoCaixa loopresumo : lresumo) {
                    if (loopresumo.valor - loopresumo.valor_sangrar_retirar < v_valor)
                        throw new UserException("Saldo insuficiente da Finalizadora : " + loopresumo.finalizadora.getDescricao() +
                                " para efeutar a retirada!");

                    if (loopresumo.finalizadora == resumoCaixa.finalizadora) {
                        loopresumo.valor_sangrar_retirar += v_valor;
                        Historico historico = (Historico) cmbHistorico.getSelectedItem();
                        loopresumo.historico = historico;
                    }
                }

                ((AriusCursorAdapter) grdResumoCaixa.getAdapter()).notifyDataSetChanged();

                dialogAux.dismiss();

                setRodape();
            }
        });

        edtRetirada.addTextChangedListener(new TextWatcher() {
            private String texto;
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                edtRetirada.removeTextChangedListener(this);

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

                edtRetirada.setText(AndroidUtils.FormatarValor_Monetario(parsed));
                edtRetirada.setSelection(edtRetirada.getText().toString().length());
                texto = edtRetirada.getText().toString();
                edtRetirada.addTextChangedListener(this);
            }
        });

        edtRetirada.setText("0");
        edtRetirada.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((EditText) v).setSelection(((EditText) v).getText().length());
            }
        });

        edtRetirada.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus){
                    final View campoEdit = v;
                    v.post(new Runnable() {
                        @Override
                        public void run() {
                            ((EditText) campoEdit).setSelection(((EditText) campoEdit).getText().length());
                        }
                    });
                }
            }
        });

        edtRetirada.setSelection(edtRetirada.getText().length());
    }

    private void setBotaoEncerrar(){
        if (funcaoExecutar.toLowerCase().equals("sangria")){
            btnEncerrar.setText("EFETUAR SANGRIA");
        }
        if (funcaoExecutar.toLowerCase().equals("fecharcaixa")) {
            btnEncerrar.setText("FECHAR CAIXA");
        }
        if (funcaoExecutar.toLowerCase().equals("retirada")){
            btnEncerrar.setText("EFETUAR RETIRADA");
        }
        btnEncerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean efetuouSangria_Retirada = false;
                if (funcaoExecutar.toLowerCase().equals("fecharcaixa")) {
                    PdvService.get().fechaCaixa(PdvService.get().getOperadorAtual());
                } else {
                    for (ResumoCaixa loopRetirada : lresumo) {
                        if (loopRetirada.valor_sangrar_retirar > 0) {
                            if (funcaoExecutar.toLowerCase().equals("retirada"))
                                PdvService.get().retiradaCaixa(loopRetirada.finalizadora,
                                        loopRetirada.historico,
                                        PdvService.get().getOperadorAtual(),
                                        loopRetirada.valor_sangrar_retirar);
                            if (funcaoExecutar.toLowerCase().equals("sangria"))
                                PdvService.get().sangraCaixa(loopRetirada.finalizadora,
                                        loopRetirada.valor_sangrar_retirar,
                                        PdvService.get().getOperadorAtual());
                            efetuouSangria_Retirada = true;
                        }
                    }
                }
                if (!efetuouSangria_Retirada && !funcaoExecutar.toLowerCase().equals("fecharcaixa"))
                    AndroidUtils.toast(AriusActivityResumoCaixa.this,
                            "Clicar sobre uma finalizador para informar o valor e efetuar a " + funcaoExecutar);
                else {
                    AriusAlertDialog.exibirDialog(AriusActivityResumoCaixa.this, R.layout.dialog_arius_delete);
                    ((TextView) AriusAlertDialog.getAlertDialog().findViewById(R.id.edtDialogAriusDeleteTexto)).setText(
                            (funcaoExecutar.toLowerCase().equals("fecharcaixa") ? "Fechamento de caixa" : funcaoExecutar) + " efetuada com sucesso!");
                    AriusAlertDialog.getAlertDialog().findViewById(R.id.btnDialogAriusDeleteNao).setVisibility(View.GONE);

                    ((Button) AriusAlertDialog.getAlertDialog().findViewById(R.id.btnDialogAriusDeleteSim)).setText("OK");

                    AriusAlertDialog.getAlertDialog().findViewById(R.id.btnDialogAriusDeleteSim).setOnClickListener(
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    AriusAlertDialog.getAlertDialog().dismiss();
                                    onBackPressed();
                                }
                            }
                    );


                }
            }
        });
    }

    private void setRodape(){

        double vrSangrar_Retirar = 0;
        double vrDiario = 0;

        for(ResumoCaixa loop : lresumo) {
            vrSangrar_Retirar += loop.valor_sangrar_retirar;
            vrDiario += loop.valor;
        }
        edtCampo1.setText(AndroidUtils.FormatarValor_Monetario(vrDiario));
        edtCampo2.setText(AndroidUtils.FormatarValor_Monetario(vrSangrar_Retirar));

        if (funcaoExecutar.toLowerCase().equals("sangria")){
            lbCampo2.setText("Total a sangrar");
        }
        if (funcaoExecutar.toLowerCase().equals("fecharcaixa")) {
            lbCampo2.setText("Total diário vendido");

            edtCampo2.setText(AndroidUtils.FormatarValor_Monetario(vrTotalVendido));
        }
        if (funcaoExecutar.toLowerCase().equals("retirada")){
            lbCampo2.setText("Total a retirar");

        }

    }

    class ResumoCaixa{
        private Finalizadora finalizadora;
        private double valor;
        private double valor_sangrar_retirar;
        private Historico historico;

        ResumoCaixa(Finalizadora finalizadora, double valor){
            this.finalizadora = finalizadora;
            this.valor = valor;
        }
    }
}
