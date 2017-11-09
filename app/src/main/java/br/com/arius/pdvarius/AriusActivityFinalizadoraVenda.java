package br.com.arius.pdvarius;

import android.support.v7.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;
import arius.pdv.base.PdvService;
import arius.pdv.base.VendaDao;
import arius.pdv.base.VendaFinalizadora;
import arius.pdv.base.VendaFinalizadoraDao;
import arius.pdv.base.VendaSituacao;
import arius.pdv.core.AppContext;
import arius.pdv.core.UserException;
import arius.pdv.db.AndroidUtils;
import arius.pdv.db.AriusAlertDialog;
import arius.pdv.db.AriusCursorAdapter;
import arius.pdv.db.AriusListView;

/**
 * Created by Arius on 10/10/2017.
 */

public class AriusActivityFinalizadoraVenda extends ActivityPadrao {

    private Context context;
    private AriusListView grdFinalidora_Venda;
    private VendaFinalizadora vendaFinalizadora;
    private TextView edtValorRestante;
    private TextView lbValorRestante;
    private LinearLayout btnFinalizaVenda;
    private Button btnDescontoTotal;
    private Button btnAcrescimoTotal;
    private TextView edtDescontoTotalVenda;
    private TextView edtAcrescimoTotalVenda;
    private AlertDialog dialogFinalizadora;
    private AlertDialog dialogDesc_Juros;
    private AriusActivityPercentualValor ariusActivityPercentualValor;
    private TextView edtValorTotalVenda;
    private TextView edtValorTotalLiquido;
    private TextView edtVrRecebido;

    /*Campos rodapé dialog finalizadora*/
    private TextView edtVrBruto;
    private TextView edtDesconto;
    private TextView edtJuros;
    private TextView edtVrLiquido;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contentariusfinalizadoravenda);

        montaFinalizadoraVenda(null, getApplicationContext());
    }

    public void montaFinalizadoraVenda(View view, final Context context){
        this.context = context;

        ariusActivityPercentualValor = new AriusActivityPercentualValor();

        if (view == null){
            grdFinalidora_Venda = (AriusListView) findViewById(R.id.grdFinalizadoraVenda);
            edtValorRestante = (TextView) findViewById(R.id.edtlayoutFinalizaVendaRodapeValor);
            lbValorRestante = (TextView) findViewById(R.id.lblayoutFinalizaVendaRodapeValor);
            btnFinalizaVenda = (LinearLayout) findViewById(R.id.btnlayoutFinalizaVendaRodapeFinalizar);
            btnDescontoTotal = (Button) findViewById(R.id.btnlayoutFinalizaVendaRodapeDesconto);
            btnAcrescimoTotal = (Button) findViewById(R.id.btnlayoutFinalizaVendaRodapeAcrescimo);
            edtDescontoTotalVenda = (TextView) findViewById(R.id.edtlayoutFinalizaVendaRodapeDesconto);
            edtAcrescimoTotalVenda = (TextView) findViewById(R.id.edtlayoutFinalizaVendaRodapeAcrescimo);
            edtValorTotalVenda = (TextView) findViewById(R.id.edtlayoutFinalizaVendaRodapeValorVenda);
            edtValorTotalLiquido = (TextView) findViewById(R.id.edtlayoutFinalizaVendaValorLiquido);
            edtVrRecebido = (TextView) findViewById(R.id.edtcontentariusfinalizadoravendaVrRecebido);
        } else {
            grdFinalidora_Venda = view.findViewById(R.id.grdFinalizadoraVenda);
            edtValorRestante = view.findViewById(R.id.edtlayoutFinalizaVendaRodapeValor);
            lbValorRestante = view.findViewById(R.id.lblayoutFinalizaVendaRodapeValor);
            btnFinalizaVenda = view.findViewById(R.id.btnlayoutFinalizaVendaRodapeFinalizar);
            btnDescontoTotal = view.findViewById(R.id.btnlayoutFinalizaVendaRodapeDesconto);
            btnAcrescimoTotal = view.findViewById(R.id.btnlayoutFinalizaVendaRodapeAcrescimo);
            edtDescontoTotalVenda = view.findViewById(R.id.edtlayoutFinalizaVendaRodapeDesconto);
            edtAcrescimoTotalVenda = view.findViewById(R.id.edtlayoutFinalizaVendaRodapeAcrescimo);
            edtValorTotalVenda = view.findViewById(R.id.edtlayoutFinalizaVendaRodapeValorVenda);
            edtValorTotalLiquido = view.findViewById(R.id.edtlayoutFinalizaVendaValorLiquido);
            edtVrRecebido = view.findViewById(R.id.edtcontentariusfinalizadoravendaVrRecebido);
        }

        grdFinalidora_Venda.setSwipe_Delete(true);

        carregaFinalizadora_Venda();

        grdFinalidora_Venda.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                vendaFinalizadora = (VendaFinalizadora) adapterView.getItemAtPosition(i);
                if (vendaFinalizadora.getVenda().getSituacao() != VendaSituacao.ABERTA)
                    return;

                montaDialogFinalizadoraVenda();
            }
        });

        btnFinalizaVenda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (PdvService.get().getVendaAtiva().getFinalizadoras().size() == 0) {
                    throw new UserException("Operação não permitida.\n Informar uma finalizadora para poder finalizar a venda!");
                } else {
                    PdvService.get().getPdv().getVendaAtiva().setPdv(PdvService.get().getPdv());
                    PdvService.get().encerraVenda(PdvService.get().getPdv().getVendaAtiva());
                    getNavigation().setSelectedItemId(R.id.navigation_prodcategoria);
                }
            }
        });

        setBtnDescontoTotal();
    }

    private void carregaFinalizadora_Venda(){
        if (PdvService.get().getVendaAtiva() != null){

            AriusCursorAdapter venda_finalizadoras = new AriusCursorAdapter(context,
                    R.layout.layoutfinalizadoravenda,
                    R.layout.layoutfinalizadoravenda,
                    null,
                    PdvService.get().getVendaAtiva().getFinalizadoras());

            venda_finalizadoras.setMontarCamposTela(new AriusCursorAdapter.MontarCamposTela() {
                @Override
                public void montarCamposTela(final Object p, View v) {
                    final VendaFinalizadora vendaFinalizadora = (VendaFinalizadora) p;
                    TextView campoAux = v.findViewById(R.id.lbLayoutFinalizadoraVendaFinalizadora);
                    if (campoAux != null)
                        campoAux.setText(vendaFinalizadora.getFinalizadora().getDescricao());

                    campoAux = v.findViewById(R.id.lbLayoutFinalizadoraVendaValor);
                    if (campoAux != null)
                        campoAux.setText(AndroidUtils.FormatarValor_Monetario(vendaFinalizadora.getValor()));

                    campoAux = v.findViewById(R.id.edtLayoutFinalizadoraVendaDesconto);
                    if (campoAux != null) {
                        campoAux.setVisibility(vendaFinalizadora.getDesconto() == 0 ? View.GONE : View.VISIBLE);
                        campoAux.setText("- " + AndroidUtils.FormatarValor_Monetario(vendaFinalizadora.getDesconto()));
                    }
                    campoAux = v.findViewById(R.id.edtLayoutFinalizadoraVendaJuros);
                    if (campoAux != null) {
                        campoAux.setVisibility(vendaFinalizadora.getJuro() == 0 ? View.GONE : View.VISIBLE);
                        campoAux.setText("+ " + AndroidUtils.FormatarValor_Monetario(vendaFinalizadora.getJuro()));
                    }

                    ImageView imgaux = v.findViewById(R.id.imgFinalizadoraVendaDelete);
                    if (vendaFinalizadora.getVenda().getSituacao() == VendaSituacao.ABERTA) {
                        imgaux.setVisibility(View.VISIBLE);
                        imgaux.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                AriusAlertDialog.exibirDialog(context,R.layout.dialog_arius_delete);
                                ((TextView) AriusAlertDialog.getGetView().findViewById(R.id.edtDialogAriusDeleteTexto)).setText(
                                        "Deseja realmente excluir a forma de pagamento?");

                                AriusAlertDialog.getAlertDialog().findViewById(R.id.btnDialogAriusDeleteNao).setOnClickListener(
                                        new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                AriusAlertDialog.getAlertDialog().dismiss();
                                            }
                                        }
                                );

                                AriusAlertDialog.getAlertDialog().findViewById(R.id.btnDialogAriusDeleteSim).setOnClickListener(
                                        new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                grdFinalidora_Venda.getAriusCursorAdapter().remove(p);
                                                deleteVendaFinalizadora(p);
                                                AriusAlertDialog.getAlertDialog().dismiss();
                                                montaRodape();
                                            }
                                        }
                                );
                            }
                        });
                    } else
                        imgaux.setVisibility(View.GONE);
                }
            });

            venda_finalizadoras.setAfterDataControll(new AriusCursorAdapter.AfterDataControll() {
                @Override
                public void afterScroll(Object object) {

                }

                @Override
                public void afterDelete(Object object) {
                    deleteVendaFinalizadora(object);
                }
            });

            grdFinalidora_Venda.setAdapter(venda_finalizadoras);

            grdFinalidora_Venda.setSwipe_Delete(PdvService.get().getVendaAtiva().getSituacao() == VendaSituacao.ABERTA);
        }

        montaRodape();

    }

    public void afteScroll(){
        if (grdFinalidora_Venda.getAriusCursorAdapter() != null) {
            if (grdFinalidora_Venda.getAriusCursorAdapter().getCount() == 0)
                carregaFinalizadora_Venda();
        } else
            carregaFinalizadora_Venda();

        grdFinalidora_Venda.getAriusCursorAdapter().notifyDataSetChanged();
        montaRodape();
    }

    private void deleteVendaFinalizadora(Object object){
        VendaFinalizadora vendaFinalizadora = (VendaFinalizadora) object;
        if (AppContext.get().getDao(VendaFinalizadoraDao.class).find(vendaFinalizadora.getId()) != null){
            AppContext.get().getDao(VendaFinalizadoraDao.class).delete(vendaFinalizadora);
        }

        montaRodape();
    }

    private void montaDialogFinalizadoraVenda() {
        AriusAlertDialog.exibirDialog(context, R.layout.dialog_arius_finalizadora_pagamento);

        dialogFinalizadora = AriusAlertDialog.getAlertDialog();

        dialogFinalizadora.findViewById(R.id.btnlayoutDialogFinalizadoraVendaCancelar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogFinalizadora.dismiss();
                dialogFinalizadora = null;
            }
        });

        final EditText edtValor = (EditText) dialogFinalizadora.findViewById(R.id.edtlayoutDialogFinalizadoraVendaValor);

        final TextView lbalert = (TextView) dialogFinalizadora.findViewById(R.id.lblayoutDialogFinalizadoraVendaFinalizadora);

        lbalert.setText(vendaFinalizadora.getFinalizadora().getDescricao());

        edtValor.setText(AndroidUtils.FormatarValor_Monetario(vendaFinalizadora.getValor()));

        edtValor.setEnabled(false);
        edtValor.setFocusable(false);

        dialogFinalizadora.findViewById(R.id.btnlayoutDialogFinalizadoraVendaDesconto).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AriusAlertDialog.exibirDialog(context,R.layout.dialog_arius_perccentual_valor);
                        dialogDesc_Juros = AriusAlertDialog.getAlertDialog();
                        ariusActivityPercentualValor.montaDialog_Campos(dialogDesc_Juros,v, "Desconto");
                        ariusActivityPercentualValor.setValor(vendaFinalizadora.getValor());
                        ariusActivityPercentualValor.setAceitaporcentagem(false);
                        ariusActivityPercentualValor.setEdtValor(vendaFinalizadora.getDesconto());

                        dialogDesc_Juros.findViewById(R.id.btnDialogAriusPerccentualValorConfirmar).setOnClickListener(
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        vendaFinalizadora.setDesconto(ariusActivityPercentualValor.getRetorno_valor());

                                        dialogDesc_Juros.dismiss();

                                        montaRodape();

                                    }
                                }
                        );
                    }
        });

        dialogFinalizadora.findViewById(R.id.btnlayoutDialogFinalizadoraVendaJuros).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AriusAlertDialog.exibirDialog(context,R.layout.dialog_arius_perccentual_valor);
                        dialogDesc_Juros = AriusAlertDialog.getAlertDialog();
                        ariusActivityPercentualValor.montaDialog_Campos(dialogDesc_Juros,v, "Juro");
                        ariusActivityPercentualValor.setValor(vendaFinalizadora.getValor());
                        ariusActivityPercentualValor.setAceitaporcentagem(true);
                        ariusActivityPercentualValor.setEdtValor(vendaFinalizadora.getJuro());

                        dialogDesc_Juros.findViewById(R.id.btnDialogAriusPerccentualValorConfirmar).setOnClickListener(
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        vendaFinalizadora.setJuro(ariusActivityPercentualValor.getRetorno_valor());

                                        dialogDesc_Juros.dismiss();

                                        montaRodape();

                                    }
                                }
                        );

                    }
                });

        dialogFinalizadora.findViewById(R.id.btnlayoutDialogFinalizadoraVendaConfirmar).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
                        Double v_valor = 0.0;
                        try {
                            v_valor = nf.parse(edtValor.getText().toString().replace(" ", "")).doubleValue();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        if (edtValor.getText().toString().equals("") || v_valor <= 0)
                            throw new UserException("Informar um valor para a finalizadora!");

                        vendaFinalizadora.setVenda(PdvService.get().getVendaAtiva());

                        vendaFinalizadora.setValor(v_valor);

                        PdvService.get().alteraVendaFinalizadora(vendaFinalizadora);

                        grdFinalidora_Venda.getAriusCursorAdapter().notifyDataSetChanged();

                        dialogFinalizadora.dismiss();

                        montaRodape();

                        dialogFinalizadora = null;
                    }
        });

        edtVrBruto = (TextView) dialogFinalizadora.findViewById(R.id.edtlayoutDialogFinalizadoraVendaValorBruto);
        edtDesconto = (TextView)  dialogFinalizadora.findViewById(R.id.edtlayoutDialogFinalizadoraVendaDesconto);
        edtJuros = (TextView) dialogFinalizadora.findViewById(R.id.edtlayoutDialogFinalizadoraVendaJuros);
        edtVrLiquido = (TextView) dialogFinalizadora.findViewById(R.id.edtlayoutDialogFinalizadoraVendaValorLiquido);

        montaRodape();
    }

    private void montaRodape(){
        edtValorRestante.setText(AndroidUtils.FormatarValor_Monetario(0.0));
        edtVrRecebido.setText(AndroidUtils.FormatarValor_Monetario(0.0));
        lbValorRestante.setText("Valor Restante:");
        if (PdvService.get().getVendaAtiva() != null) {
            edtValorTotalVenda.setText(AndroidUtils.FormatarValor_Monetario(PdvService.get().getVendaAtiva().getValorLiquido()
                    + PdvService.get().getVendaAtiva().getDesconto() - PdvService.get().getVendaAtiva().getAcrescimo()));

            edtValorRestante.setText(AndroidUtils.FormatarValor_Monetario(PdvService.get().getVendaAtiva().getValorRestante()));
            if (PdvService.get().getVendaAtiva().getValorRestante() < 0){
                lbValorRestante.setText("Valor Troco:");
                edtValorRestante.setText(AndroidUtils.FormatarValor_Monetario(PdvService.get().getVendaAtiva().getValorRestante() * -1));
            }
            edtDescontoTotalVenda.setText(" - "+AndroidUtils.FormatarValor_Monetario(PdvService.get().getVendaAtiva().getDesconto()));
            edtAcrescimoTotalVenda.setText(" + "+AndroidUtils.FormatarValor_Monetario(PdvService.get().getVendaAtiva().getAcrescimo()));
            edtValorTotalLiquido.setText(AndroidUtils.FormatarValor_Monetario(PdvService.get().getVendaAtiva().getValorLiquido()));
            if (edtVrBruto != null)
                edtVrBruto.setText(AndroidUtils.FormatarValor_Monetario(vendaFinalizadora.getValor()));
            if (edtDesconto != null)
                edtDesconto.setText(" - " + AndroidUtils.FormatarValor_Monetario(vendaFinalizadora.getDesconto()));
            if (edtJuros != null)
                edtJuros.setText(" + " + AndroidUtils.FormatarValor_Monetario(vendaFinalizadora.getJuro()));
            if (edtVrLiquido != null)
                edtVrLiquido.setText(AndroidUtils.FormatarValor_Monetario(vendaFinalizadora.getValorLiquido()));
            if (edtVrRecebido != null)
                edtVrRecebido.setText(AndroidUtils.FormatarValor_Monetario(PdvService.get().getVendaAtiva().getValorPago()));
        }
    }

    private void setBtnDescontoTotal(){
        btnDescontoTotal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AriusAlertDialog.exibirDialog(context,R.layout.dialog_arius_perccentual_valor);
                dialogDesc_Juros = AriusAlertDialog.getAlertDialog();
                ariusActivityPercentualValor.montaDialog_Campos(dialogDesc_Juros,view, "Desconto");
                ariusActivityPercentualValor.setValor(PdvService.get().getVendaAtiva().getValorLiquido() +
                                                PdvService.get().getVendaAtiva().getDesconto() -
                                                PdvService.get().getVendaAtiva().getAcrescimo());
                ariusActivityPercentualValor.setAceitaporcentagem(false);
                ariusActivityPercentualValor.setEdtValor(PdvService.get().getVendaAtiva().getDesconto());

                dialogDesc_Juros.findViewById(R.id.btnDialogAriusPerccentualValorConfirmar).setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                PdvService.get().getVendaAtiva().setDesconto(ariusActivityPercentualValor.getRetorno_valor());

                                AppContext.get().getDao(VendaDao.class).update(PdvService.get().getVendaAtiva());

                                dialogDesc_Juros.dismiss();

                                montaRodape();

                            }
                        }
                );
            }
        });

        btnAcrescimoTotal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AriusAlertDialog.exibirDialog(context,R.layout.dialog_arius_perccentual_valor);
                dialogDesc_Juros = AriusAlertDialog.getAlertDialog();
                ariusActivityPercentualValor.montaDialog_Campos(dialogDesc_Juros,view, "Acréscimo");
                ariusActivityPercentualValor.setValor(PdvService.get().getVendaAtiva().getValorLiquido() +
                        PdvService.get().getVendaAtiva().getDesconto() -
                        PdvService.get().getVendaAtiva().getAcrescimo());
                ariusActivityPercentualValor.setAceitaporcentagem(true);
                ariusActivityPercentualValor.setEdtValor(PdvService.get().getVendaAtiva().getAcrescimo());

                dialogDesc_Juros.findViewById(R.id.btnDialogAriusPerccentualValorConfirmar).setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                PdvService.get().getVendaAtiva().setAcrescimo(ariusActivityPercentualValor.getRetorno_valor());

                                AppContext.get().getDao(VendaDao.class).update(PdvService.get().getVendaAtiva());

                                dialogDesc_Juros.dismiss();

                                montaRodape();

                            }
                        }
                );
            }
        });
    }
}
