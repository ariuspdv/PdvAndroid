package br.com.arius.pdvarius;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

import arius.pdv.base.Finalizadora;
import arius.pdv.base.FinalizadoraDao;
import arius.pdv.base.FinalizadoraTipo;
import arius.pdv.base.PdvService;
import arius.pdv.base.VendaFinalizadora;
import arius.pdv.base.VendaSituacao;
import arius.pdv.core.AppContext;
import arius.pdv.core.FuncionaisFilters;
import arius.pdv.core.UserException;
import arius.pdv.db.AndroidUtils;
import arius.pdv.db.AriusAlertDialog;
import arius.pdv.db.AriusCursorAdapter;
import arius.pdv.db.AriusListView;

/**
 * Created by Arius on 10/10/2017.
 */

public class AriusActivityFinalizadora extends ActivityPadrao {

    private Context context;
    private GridView grdFinalidoras;
    private Finalizadora finalizadora;
    private VendaFinalizadora vendaFinalizadora;
    private DataControll dataControll;
    private TextView edtValorRestante;
    private TextView lbValorRestante;
    private AriusActivityPercValor ariusActivityPercValor;
    private AlertDialog alertFinalizadora;
    private AlertDialog alertDescJuro;

    /*Campos rodapé dialog finalizadora*/
    private TextView edtVrBruto;
    private TextView edtDesconto;
    private TextView edtJuros;
    private TextView edtVrLiquido;

    public interface DataControll{
        void afterScroll();
    }

    public void setDataControll(DataControll dataControll) {
        this.dataControll = dataControll;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contentariusfinalizadora);

        montaFinalizadoraVenda(null, getApplicationContext());
    }

    public void montaFinalizadoraVenda(View view, final Context context){
        this.context = context;

        if (view == null){
            grdFinalidoras = (GridView) findViewById(R.id.grdFinalizadoras);
            edtValorRestante = (TextView) findViewById(R.id.edtlayoutFinalizaVendaRodapeValor);
            lbValorRestante = (TextView) findViewById(R.id.lblayoutFinalizaVendaRodapeValor);
        } else {
            grdFinalidoras = view.findViewById(R.id.grdFinalizadoras);
            edtValorRestante = view.findViewById(R.id.edtlayoutFinalizaVendaRodapeValor);
            lbValorRestante = view.findViewById(R.id.lblayoutFinalizaVendaRodapeValor);
        }

        carregaFinalizadoras();

        ariusActivityPercValor = new AriusActivityPercValor();
    }

    private void carregaFinalizadoras(){
        final AriusCursorAdapter finalizadoras = new AriusCursorAdapter(context,
                R.layout.layoutfinalizadora,
                R.layout.layoutfinalizadora,
                null,
                AppContext.get().getDao(FinalizadoraDao.class).listCache(new FuncionaisFilters<Finalizadora>() {
                    @Override
                    public boolean test(Finalizadora p) {
                        return true;
                    }
                })
        );

        finalizadoras.setMontarCamposTela(new AriusCursorAdapter.MontarCamposTela() {
            @Override
            public void montarCamposTela(Object p, View v) {
                Finalizadora finalizadora = (Finalizadora) p;
                TextView campoaux = v.findViewById(R.id.edtlayoutFinalizadora);
                if (campoaux != null)
                    campoaux.setText(finalizadora.getDescricao());
                ImageView imgaux = v.findViewById(R.id.imglayoutFinalizadora);
                if (imgaux != null) {
                    if (finalizadora.getTipo() == FinalizadoraTipo.DINHEIRO)
                        imgaux.setImageResource(R.mipmap.dinheiro);
                    if (finalizadora.getTipo() == FinalizadoraTipo.CARTAO_CREDITO)
                        imgaux.setImageResource(R.mipmap.creditcard);
                    if (finalizadora.getTipo() == FinalizadoraTipo.CARTAO_DEBITO)
                        imgaux.setImageResource(R.mipmap.debitcard);
                }
            }
        });

        grdFinalidoras.setAdapter(finalizadoras);

        grdFinalidoras.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (PdvService.get().getVendaAtiva().getSituacao() != VendaSituacao.ABERTA)
                    throw new UserException("Operação não permitida! \n" +
                            " Venda já " + PdvService.get().getVendaAtiva().getSituacao().toString() +
                            " não é possível incluir uma finalizadora!");

                finalizadora = (Finalizadora) adapterView.getItemAtPosition(i);
                vendaFinalizadora = new VendaFinalizadora();
                vendaFinalizadora.setFinalizadora(finalizadora);

                montaDialogFinalizadoraVenda();

            }
        });

        montaRodape();
    }

    private void montaDialogFinalizadoraVenda(){
        AriusAlertDialog.exibirDialog(context, R.layout.layoutdialogfinalizadoravenda);
        alertFinalizadora = AriusAlertDialog.getAlertDialog();

        final EditText edtValor = (EditText) alertFinalizadora.findViewById(R.id.edtlayoutDialogFinalizadoraVendaValor);

        final TextView lbalert = (TextView) alertFinalizadora.findViewById(R.id.lblayoutDialogFinalizadoraVendaFinalizadora);

        lbalert.setText(vendaFinalizadora.getFinalizadora().getDescricao());

        alertFinalizadora.findViewById(R.id.btnlayoutDialogFinalizadoraVendaCancelar).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertFinalizadora.dismiss();
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
                    edtValor.removeTextChangedListener(this);

                    NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("pt","BR"));

                    String replaceable = String.format("[%s,.\\s]", nf.getCurrency().getSymbol());
                    String cleanString = s.toString().replaceAll(replaceable, "");

                    double parsed;
                    try {
                        parsed = Double.parseDouble(cleanString);
                    } catch (NumberFormatException e) {
                        parsed = 0.00;
                    }

                    String formatted =  String.copyValueOf(nf.format(parsed/100).toCharArray(),0,2);
                    int len = nf.format(parsed/100).length();
                    formatted = formatted + " " + String.copyValueOf(nf.format(parsed/100).toCharArray(),2,len-2);
                    current = formatted;
                    vendaFinalizadora.setValor((parsed / 100));
                    edtValor.setText(formatted);
                    edtValor.setSelection(formatted.length());
                    edtValor.addTextChangedListener(this);
                    montaRodape();
                }
            }
        });

        alertFinalizadora.findViewById(R.id.btnlayoutDialogFinalizadoraVendaConfirmar).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("pt","BR"));
                        Double v_valor = 0.0;
                        try{
                            v_valor = nf.parse(edtValor.getText().toString().replace(" ","")).doubleValue();
                        }catch (ParseException e){
                            e.printStackTrace();
                        }
                        if (edtValor.getText().toString().equals("") || v_valor <= 0)
                            throw new UserException("Informar um valor para a finalizadora!");

                        vendaFinalizadora.setVenda(PdvService.get().getVendaAtiva());

                        PdvService.get().insereVendaFinalizadora(vendaFinalizadora);

                        if (dataControll != null)
                            dataControll.afterScroll();

                        montaRodape();

                        alertFinalizadora.dismiss();
                    }
        });

        alertFinalizadora.findViewById(R.id.btnlayoutDialogFinalizadoraVendaDesconto).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (vendaFinalizadora.getValor() > 0) {
                            AriusAlertDialog.exibirDialog(context, R.layout.contentariusdialogpercvalor);
                            alertDescJuro = AriusAlertDialog.getAlertDialog();
                            ariusActivityPercValor.montaDialog_Campos(alertDescJuro, v, "Desconto");
                            ariusActivityPercValor.setValor(vendaFinalizadora.getValor());
                            ariusActivityPercValor.setAceitaporcentagem(false);

                            alertDescJuro.findViewById(R.id.btnContentDialogValorConfirmar).setOnClickListener(
                                    new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            vendaFinalizadora.setDesconto(ariusActivityPercValor.getRetorno_valor());
                                            alertDescJuro.dismiss();

                                            montaRodape();
                                        }
                                    }
                            );

                        } else {
                            edtValor.requestFocus();
                            AndroidUtils.toast(context, "Informar o valor antes de aplicar o desconto!");
                        }
                    }
                }
        );

        alertFinalizadora.findViewById(R.id.btnlayoutDialogFinalizadoraVendaJuros).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (vendaFinalizadora.getValor() > 0) {
                            AriusAlertDialog.exibirDialog(context, R.layout.contentariusdialogpercvalor);
                            alertDescJuro = AriusAlertDialog.getAlertDialog();
                            ariusActivityPercValor.montaDialog_Campos(alertDescJuro, v, "Juro");
                            ariusActivityPercValor.setValor(vendaFinalizadora.getValor());
                            ariusActivityPercValor.setAceitaporcentagem(true);

                            alertDescJuro.findViewById(R.id.btnContentDialogValorConfirmar).setOnClickListener(
                                    new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            vendaFinalizadora.setJuro(ariusActivityPercValor.getRetorno_valor());
                                            alertDescJuro.dismiss();

                                            montaRodape();
                                        }
                                    }
                            );

                        } else {
                            edtValor.requestFocus();
                            AndroidUtils.toast(context, "Informar o valor antes de aplicar o juro!");
                        }
                    }
                }
        );

        edtVrBruto = (TextView) alertFinalizadora.findViewById(R.id.edtlayoutDialogFinalizadoraVendaValorBruto);
        edtDesconto = (TextView)  alertFinalizadora.findViewById(R.id.edtlayoutDialogFinalizadoraVendaDesconto);
        edtJuros = (TextView) alertFinalizadora.findViewById(R.id.edtlayoutDialogFinalizadoraVendaJuros);
        edtVrLiquido = (TextView) alertFinalizadora.findViewById(R.id.edtlayoutDialogFinalizadoraVendaValorLiquido);

        montaRodape();
    }

    private void montaRodape(){
        edtValorRestante.setText(AndroidUtils.FormatarValor_Monetario(0.0));
        lbValorRestante.setText("Valor Restante:");
        if (PdvService.get().getVendaAtiva() != null) {
            edtValorRestante.setText(AndroidUtils.FormatarValor_Monetario(PdvService.get().getVendaAtiva().getValorRestante()));
            if (PdvService.get().getVendaAtiva().getValorRestante() < 0){
                lbValorRestante.setText("Valor Troco:");
                edtValorRestante.setText(AndroidUtils.FormatarValor_Monetario(PdvService.get().getVendaAtiva().getValorRestante() * -1));
            }

            if (edtVrBruto != null)
                edtVrBruto.setText(AndroidUtils.FormatarValor_Monetario(vendaFinalizadora.getValor()));
            if (edtDesconto != null)
                edtDesconto.setText(AndroidUtils.FormatarValor_Monetario(vendaFinalizadora.getDesconto()));
            if (edtJuros != null)
                edtJuros.setText(AndroidUtils.FormatarValor_Monetario(vendaFinalizadora.getJuro()));
            if (edtVrLiquido != null)
                edtVrLiquido.setText(AndroidUtils.FormatarValor_Monetario(vendaFinalizadora.getValorLiquido()));
        }
    }

}
