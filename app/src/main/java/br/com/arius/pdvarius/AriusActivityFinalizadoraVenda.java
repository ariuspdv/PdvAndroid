package br.com.arius.pdvarius;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DecimalFormat;
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
    private Button btnFinalizaVenda;
    private Button btnDescontoTotal;
    private Button btnAcrescimoTotal;
    private boolean vdescontototal = false;
    private boolean valtpercnovalor = false;
    private View.OnClickListener clickDesconto_Acrescimo_Total;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contentariusfinalizadoravenda);

        montaFinalizadoraVenda(null, getApplicationContext());
    }

    public void montaFinalizadoraVenda(View view, final Context context){
        this.context = context;

        if (view == null){
            grdFinalidora_Venda = (AriusListView) findViewById(R.id.grdFinalizadoraVenda);
            edtValorRestante = (TextView) findViewById(R.id.edtlayoutFinalizaVendaRodapeValor);
            lbValorRestante = (TextView) findViewById(R.id.lblayoutFinalizaVendaRodapeValor);
            btnFinalizaVenda = (Button) findViewById(R.id.btnlayoutFinalizaVendaRodapeFinalizar);
            btnDescontoTotal = (Button) findViewById(R.id.btnlayoutFinalizaVendaRodapeDesconto);
            btnAcrescimoTotal = (Button) findViewById(R.id.btnlayoutFinalizaVendaRodapeAcrescimo);
        } else {
            grdFinalidora_Venda = view.findViewById(R.id.grdFinalizadoraVenda);
            edtValorRestante = view.findViewById(R.id.edtlayoutFinalizaVendaRodapeValor);
            lbValorRestante = view.findViewById(R.id.lblayoutFinalizaVendaRodapeValor);
            btnFinalizaVenda = view.findViewById(R.id.btnlayoutFinalizaVendaRodapeFinalizar);
            btnDescontoTotal = view.findViewById(R.id.btnlayoutFinalizaVendaRodapeDesconto);
            btnAcrescimoTotal = view.findViewById(R.id.btnlayoutFinalizaVendaRodapeAcrescimo);
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
                    AriusActivityPrincipal.setNavigation();
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

                    ImageView imgaux = v.findViewById(R.id.imgFinalizadoraVendaDelete);
                    if (vendaFinalizadora.getVenda().getSituacao() == VendaSituacao.ABERTA) {
                        imgaux.setVisibility(View.VISIBLE);
                        imgaux.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                grdFinalidora_Venda.getAriusCursorAdapter().remove(p);
                                deleteVendaFinalizadora(p);
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
    }

    private void deleteVendaFinalizadora(Object object){
        VendaFinalizadora vendaFinalizadora = (VendaFinalizadora) object;
        if (AppContext.get().getDao(VendaFinalizadoraDao.class).find(vendaFinalizadora.getId()) != null){
            AppContext.get().getDao(VendaFinalizadoraDao.class).delete(vendaFinalizadora);
        }

        montaRodape();
    }

    private void montaDialogFinalizadoraVenda() {
        AriusAlertDialog.exibirDialog(context, R.layout.layoutdialogfinalizadoravenda);

        Button btnCancel = AriusAlertDialog.getGetView().findViewById(R.id.btnlayoutDialogFinalizadoraVendaCancelar);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AriusAlertDialog.getAlertDialog().dismiss();
            }
        });

        final EditText edtValor = AriusAlertDialog.getGetView().findViewById(R.id.edtlayoutDialogFinalizadoraVendaValor);
        final EditText edtDesconto = AriusAlertDialog.getGetView().findViewById(R.id.edtlayoutDialogFinalizadoraVendaDesconto);
        final EditText edtJuros = AriusAlertDialog.getGetView().findViewById(R.id.edtlayoutDialogFinalizadoraVendaJuros);


        final TextView lbalert = AriusAlertDialog.getGetView().findViewById(R.id.lblayoutDialogFinalizadoraVendaFinalizadora);

        lbalert.setText(vendaFinalizadora.getFinalizadora().getDescricao());

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
                    edtValor.setText(formatted);
                    edtValor.setSelection(formatted.length());
                    edtValor.addTextChangedListener(this);
                }
            }
        });

        edtValor.setText(AndroidUtils.FormatarValor_Monetario(vendaFinalizadora.getValor()));

        edtValor.requestFocus();

        edtDesconto.addTextChangedListener(new TextWatcher() {
            private String current = "";
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().equals(current)) {
                    edtDesconto.removeTextChangedListener(this);

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
                    edtDesconto.setText(formatted);
                    edtDesconto.setSelection(formatted.length());
                    edtDesconto.addTextChangedListener(this);
                }
            }
        });

        edtDesconto.setText("0");

        edtJuros.addTextChangedListener(new TextWatcher() {
            private String current = "";
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().equals(current)) {
                    edtJuros.removeTextChangedListener(this);

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
                    edtJuros.setText(formatted);
                    edtJuros.setSelection(formatted.length());
                    edtJuros.addTextChangedListener(this);
                }
            }
        });

        edtJuros.setText("0");

        AriusAlertDialog.getAlertDialog().findViewById(R.id.btnlayoutDialogFinalizadoraVendaConfirmar).setOnClickListener(
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

                        vendaFinalizadora.setValor(v_valor);

                        PdvService.get().alteraVendaFinalizadora(vendaFinalizadora);

                        grdFinalidora_Venda.getAriusCursorAdapter().notifyDataSetChanged();

                        AriusAlertDialog.getAlertDialog().dismiss();

                        montaRodape();
                    }
                });
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
        }
    }



    private void setBtnDescontoTotal(){

        clickDesconto_Acrescimo_Total = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AriusAlertDialog.exibirDialog(context, R.layout.contentariusdialogpercvalor);

                Button btnCancel = AriusAlertDialog.getGetView().findViewById(R.id.btnContentDialogValorCancelar);
                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AriusAlertDialog.getAlertDialog().dismiss();
                    }
                });

                final EditText edtValor = AriusAlertDialog.getGetView().findViewById(R.id.edtContentDialogPercValorValor);
                final EditText edtPerc = AriusAlertDialog.getGetView().findViewById(R.id.edtContentDialogPercValorPerc);

                final TextView lbtitulo = AriusAlertDialog.getGetView().findViewById(R.id.lbContentDialogPercValorTitulo);
                if (vdescontototal){
                    lbtitulo.setText("Desconto Total Venda");
                } else {
                    lbtitulo.setText("Acréscimo Total Venda");
                }

                edtPerc.addTextChangedListener(new TextWatcher() {
                    private String current = "";

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {}

                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count,
                                                  int after) {}

                    @Override
                    public void afterTextChanged(Editable s) {
                        if (!s.toString().equals(current)) {
                            edtPerc.removeTextChangedListener(this);

                            String cleanString = s.toString().replace(".","");
                            if (cleanString.contains("%"))
                                cleanString = cleanString.replace("%","");
                            else
                                cleanString = String.copyValueOf(cleanString.toCharArray(),0,cleanString.length() - 1);

                            double parsed;
                            try {
                                parsed = Double.parseDouble(cleanString);
                            } catch (NumberFormatException e) {
                                parsed = 0.00;
                            }

                            current = new DecimalFormat("####0.00").format(parsed / 100) + "%";
                            edtPerc.setText(current);
                            edtPerc.setSelection(current.length());

                            if (!valtpercnovalor) {
                                double v_valor = 0;
                                double v_porc = (parsed / 100);
                                double vnd_valor = PdvService.get().getVendaAtiva().getValorLiquido() +
                                        PdvService.get().getVendaAtiva().getDesconto();

                                v_valor = vnd_valor - ((vnd_valor * (parsed / 100)) / 100);

                                PdvService.get().getVendaAtiva().setDesconto(v_valor);

                                edtValor.setText(String.valueOf(v_valor));
                            }

                            edtPerc.addTextChangedListener(this);
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
                            edtValor.setText(formatted);
                            edtValor.setSelection(formatted.length());

                            valtpercnovalor = true;

                            double v_perc = 0;
                            double vnd_valor = PdvService.get().getVendaAtiva().getValorLiquido() +
                                    PdvService.get().getVendaAtiva().getDesconto();
                            v_perc = parsed / vnd_valor;
                            edtPerc.setText(new DecimalFormat("####0.00").format(v_perc) + "%");

                            valtpercnovalor = false;

                            edtValor.addTextChangedListener(this);
                        }
                    }
                });

                edtValor.setText(AndroidUtils.FormatarValor_Monetario(PdvService.get().getVendaAtiva().getDesconto()));

                edtValor.requestFocus();

                AriusAlertDialog.getAlertDialog().findViewById(R.id.btnContentDialogValorConfirmar).setOnClickListener(
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
                                if (edtValor.getText().toString().equals("") || v_valor <= 0) {
                                    if (vdescontototal)
                                        throw new UserException("Informar um valor para o desconto!");
                                    else
                                        throw new UserException("Informar um valor para o acréscimo!");
                                }

                                if (vdescontototal)
                                    PdvService.get().getVendaAtiva().setDesconto(v_valor);
                                else
                                    PdvService.get().getVendaAtiva().setAcrescimo(v_valor);

                                AppContext.get().getDao(VendaDao.class).update(PdvService.get().getVendaAtiva());

                                AriusAlertDialog.getAlertDialog().dismiss();

                                montaRodape();
                            }
                });
            }
        };

        btnDescontoTotal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vdescontototal = true;
                clickDesconto_Acrescimo_Total.onClick(view);
            }
        });
        btnAcrescimoTotal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vdescontototal = false;
                clickDesconto_Acrescimo_Total.onClick(view);
            }
        });
    }
}
