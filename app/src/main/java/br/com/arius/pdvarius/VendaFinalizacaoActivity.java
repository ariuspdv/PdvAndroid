package br.com.arius.pdvarius;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.common.collect.ImmutableMap;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

import arius.pdv.base.Finalizadora;
import arius.pdv.base.FinalizadoraDao;
import arius.pdv.base.FinalizadoraTipo;
import arius.pdv.base.PdvService;
import arius.pdv.base.VendaFinalizadora;
import arius.pdv.base.VendaFinalizadoraDao;
import arius.pdv.base.VendaItem;
import arius.pdv.base.VendaSituacao;
import arius.pdv.core.AppContext;
import arius.pdv.core.Entity;
import arius.pdv.core.FuncionaisFilters;
import arius.pdv.core.FuncionaisTela;
import arius.pdv.core.UserException;
import arius.pdv.db.AndroidUtils;
import arius.pdv.db.AriusCursorAdapter;
import arius.pdv.db.AriusListView;

public class VendaFinalizacaoActivity extends ActivityPadrao {

    private View.OnClickListener btnSpliter;
    private View.OnClickListener btnFinalizar_Finalizar;
    private AriusListView grdVenda_Finalizadoras;
    private GridView grdFinalizadoras;
    private RelativeLayout.LayoutParams layoutParams;
    private TextView edtVendaFinalizadoraTotal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.conten_venda_finalizacao);

        this.edtVendaFinalizadoraTotal = (TextView) findViewById(R.id.edtVendaFinalizadoraTotal);

        totalFinalizadora();

        setButtons(true, false, true);

        this.grdVenda_Finalizadoras = (AriusListView) findViewById(R.id.grdVenda_Finalizadoras);
        this.grdFinalizadoras = (GridView) findViewById(R.id.grdFinalizadoras);

        iniciaBotoes();

        FuncionaisFilters<Finalizadora> filter = new FuncionaisFilters<Finalizadora>() {
            @Override
            public boolean test(Finalizadora p) {
                return true;
            }
        };

        this.grdVenda_Finalizadoras.setCampos_Exibir(
                    ImmutableMap.<Integer, String>of(R.id.edtVendaFinalizadora,"finalizadora.descricao",
                                                     R.id.edtVendaFinalizadoraValor, "vendafinalizadora.valor"));
        this.grdVenda_Finalizadoras.setLayout_arius(R.layout.layoutvendafinalizadora);

        this.grdVenda_Finalizadoras.setGenericDao(new VendaFinalizadoraDao());
        this.grdVenda_Finalizadoras.setEntity(new VendaFinalizadora());

        if (PdvService.get().getVendaAtiva() != null)
            this.grdVenda_Finalizadoras.setDataSource(PdvService.get().getVendaAtiva().getFinalizadoras());

        if (grdVenda_Finalizadoras.getAdapter() != null)
            this.grdVenda_Finalizadoras.setSelection(grdVenda_Finalizadoras.getAdapter().getCount()-1);

        this.grdVenda_Finalizadoras.setSwipe_Delete(true);

//        ((AriusCursorAdapter) this.grdVenda_Finalizadoras.getAdapter()).setAfterDelete(new FuncionaisFilters() {
//            @Override
//            public boolean test(Object p) {
//                totalFinalizadora();
//                return false;
//            }
//        });

//        setAfterDeleteVendaFinalizadoras();

        setAfterScrollVendaFinalizadora();

        setMontaTela();

        AriusCursorAdapter adapter = new AriusCursorAdapter(
                this,
                R.layout.layoutfinalizadoras,
                0,
                ImmutableMap.<Integer, String>of(R.id.edtFinalizacaoFinalizadora,"finalizadora.descricao"),
                AppContext.get().getDao(FinalizadoraDao.class).listCache(filter)
        );

        grdFinalizadoras.setAdapter(adapter);

        ((AriusCursorAdapter) grdFinalizadoras.getAdapter()).setMontatela(new FuncionaisTela() {
            @Override
            public void test(Entity p, View v) {
                TextView edtaux = (TextView) v.findViewById(R.id.edtFinalizacaoFinalizadora);
                if (edtaux != null)
                    edtaux.setText(((Finalizadora) p).getDescricao());

                ImageView imgFinalizadora = (ImageView) v.findViewById(R.id.imgFinalizacaoFinalizadora);
                imgFinalizadora.setImageResource(
                        ((Finalizadora) p).getTipo() == FinalizadoraTipo.DINHEIRO ? R.mipmap.dinheiro :
                                ((Finalizadora) p).getTipo() == FinalizadoraTipo.CARTAO_CREDITO ? R.mipmap.creditcard :
                                    ((Finalizadora) p).getTipo() == FinalizadoraTipo.CARTAO_DEBITO ? R.mipmap.debitcard : 0

                );
            }
        });

        grdFinalizadoras.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, final long l) {
                if (PdvService.get().getVendaAtiva().getSituacao() != VendaSituacao.ABERTA)
                    throw new UserException("Operação não permitida! \n"+
                                            " Venda já " + PdvService.get().getVendaAtiva().getSituacao().toString() +
                                            " não é possível incluir uma finalizadora!");

                final Entity entity = (Entity) adapterView.getItemAtPosition(i);
                final VendaFinalizadora vndFinalizadora = new VendaFinalizadora();
                vndFinalizadora.setFinalizadora((Finalizadora) entity);

                final AlertDialog alertBuilder = new AlertDialog.Builder(VendaFinalizacaoActivity.this).create();
                LayoutInflater layoutInflater = LayoutInflater.from(VendaFinalizacaoActivity.this);

                final View layout = layoutInflater.inflate(R.layout.content_dialog_finalizacao, null);

                alertBuilder.setView(layout);

                final EditText edtValor = (EditText) layout.findViewById(R.id.edtFinalizadoraDialog);

                final TextView lbalert = (TextView) layout.findViewById(R.id.lbFinalizadoraDialog);

                lbalert.setText(lbalert.getText() + " " + vndFinalizadora.getFinalizadora().getDescricao());

                layout.findViewById(R.id.btnFinalizadoraDialogCancelar).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertBuilder.dismiss();
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
                            edtValor.setText(formatted);
                            edtValor.setSelection(formatted.length());
                            edtValor.addTextChangedListener(this);
                        }
                    }
                });

                layout.findViewById(R.id.btnFinalizadoraDialogOK).setOnClickListener(new View.OnClickListener() {
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

                        vndFinalizadora.setVenda(PdvService.get().getVendaAtiva());

                        vndFinalizadora.setValor(v_valor);

                        PdvService.get().insereVendaFinalizadora(vndFinalizadora);

                        grdVenda_Finalizadoras.setSelection(grdVenda_Finalizadoras.getAdapter().getCount()-1);

                        totalFinalizadora();

                        alertBuilder.dismiss();
                    }
                });

                edtValor.setText("0");

                TextView lbtitle = new TextView(VendaFinalizacaoActivity.this);
                lbtitle.setText("PdvArius");
                lbtitle.setGravity(Gravity.CENTER_HORIZONTAL);
                lbtitle.setBackgroundResource(R.color.colorPrimary);
                lbtitle.setTextSize(30);
                lbtitle.setTextColor(Color.WHITE);
                lbtitle.setTypeface(null, Typeface.BOLD);

                alertBuilder.setCustomTitle(lbtitle);

                alertBuilder.show();
            }
        });
    }

    private void totalFinalizadora(){
        edtVendaFinalizadoraTotal.setText(AndroidUtils.FormatarValor_Monetario(PdvService.get().getVendaAtiva().getValorPago()));
    }

    private void iniciaBotoes(){
        final Button btnSpliter = (Button) findViewById(R.id.btnSplitFinalizacao_Finalizacao);
        final ConstraintLayout pnlVenda_Finalizadoras = (ConstraintLayout) findViewById(R.id.pnlVenda_Finalizador);
        this.btnSpliter = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (grdFinalizadoras.getVisibility() != View.GONE) {
                    grdFinalizadoras.setVisibility(View.GONE);

                    layoutParams = (RelativeLayout.LayoutParams) btnSpliter.getLayoutParams();
                    layoutParams.removeRule(RelativeLayout.BELOW);
                    layoutParams.removeRule(RelativeLayout.ABOVE);
                    layoutParams.addRule(RelativeLayout.ABOVE, R.id.pnlTotal_Finalizadoras);
                    btnSpliter.setLayoutParams(layoutParams);

                    layoutParams = (RelativeLayout.LayoutParams) pnlVenda_Finalizadoras.getLayoutParams();
                    layoutParams.removeRule(RelativeLayout.BELOW);
                    layoutParams.removeRule(RelativeLayout.ABOVE);
                    layoutParams.addRule(RelativeLayout.ABOVE, R.id.btnSplitFinalizacao_Finalizacao);
                    pnlVenda_Finalizadoras.setLayoutParams(layoutParams);

                    // desmarcar a configuração para o ultimo item da venda
                    grdVenda_Finalizadoras.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_DISABLED);
                    grdVenda_Finalizadoras.setStackFromBottom(false);

                }else{
                    grdFinalizadoras.setVisibility(View.VISIBLE);
                    layoutParams = (RelativeLayout.LayoutParams) pnlVenda_Finalizadoras.getLayoutParams();
                    layoutParams.removeRule(RelativeLayout.BELOW);
                    layoutParams.removeRule(RelativeLayout.ABOVE);
                    pnlVenda_Finalizadoras.setLayoutParams(layoutParams);

                    layoutParams = (RelativeLayout.LayoutParams) btnSpliter.getLayoutParams();
                    layoutParams.removeRule(RelativeLayout.BELOW);
                    layoutParams.removeRule(RelativeLayout.ABOVE);

                    layoutParams.addRule(RelativeLayout.BELOW, R.id.pnlVenda_Finalizador);
                    btnSpliter.setLayoutParams(layoutParams);


                    // seta para o ultimo item da venda
                    if (grdVenda_Finalizadoras.getAdapter().getCount() > 5) {
                        grdVenda_Finalizadoras.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
                        grdVenda_Finalizadoras.setStackFromBottom(true);
                    }
                }

            }

        };
        btnSpliter.setOnClickListener(this.btnSpliter);

        final Button btnFinalizar_Finalizar = (Button) findViewById(R.id.btnFinalizar_Finalizar);
        this.btnFinalizar_Finalizar = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (PdvService.get().getVendaAtiva().getFinalizadoras().size() == 0) {
                    throw new UserException("Operação não permitida.\n Informar uma finalizadora para poder finalizar a venda!");
                } else {
                    PdvService.get().getPdv().getVendaAtiva().setPdv(PdvService.get().getPdv());
                    PdvService.get().encerraVenda(PdvService.get().getPdv().getVendaAtiva());
                    setImagemVendaStatus();
                    onBackPressed();
                }
            }
        };

        btnFinalizar_Finalizar.setOnClickListener(this.btnFinalizar_Finalizar);
    }

    private void setAfterDeleteVendaFinalizadoras(){
        if (PdvService.get().getVendaAtiva() != null) {
            grdVenda_Finalizadoras.setDataSource(PdvService.get().getVendaAtiva().getFinalizadoras());

            if (grdVenda_Finalizadoras.getAdapter() == null)
                return;

            ((AriusCursorAdapter) grdVenda_Finalizadoras.getAdapter()).setAfterDelete(
                    new FuncionaisFilters<VendaFinalizadora>() {
                        @Override
                        public boolean test(VendaFinalizadora p) {
                            if (PdvService.get().getVendaAtiva() != null) {
                                PdvService.get().geraValoresMovimentoDiario(p.getFinalizadora(),p.getValor(),3);
                            }
                            return true;
                        }
                    }
            );

            grdVenda_Finalizadoras.setSelection(grdVenda_Finalizadoras.getAdapter().getCount() - 1);
        }
    }

    private void setAfterScrollVendaFinalizadora(){
        if (PdvService.get().getVendaAtiva() != null) {
            grdVenda_Finalizadoras.setDataSource(PdvService.get().getVendaAtiva().getFinalizadoras());

            ((AriusCursorAdapter) grdVenda_Finalizadoras.getAdapter()).setAfterScroll(
                    new FuncionaisFilters<VendaFinalizadora>() {
                        @Override
                        public boolean test(VendaFinalizadora p) {
                            totalFinalizadora();
                            if (PdvService.get().getVendaAtiva() != null) {
                                grdVenda_Finalizadoras.setSwipe_Delete(p.getVenda().getSituacao() == VendaSituacao.ABERTA);
                            }
                            return true;
                        }
                    }
            );

            grdVenda_Finalizadoras.setSelection(grdVenda_Finalizadoras.getAdapter().getCount() - 1);
        }
    }

    private void setMontaTela(){
        if (PdvService.get().getVendaAtiva() != null){
            ((AriusCursorAdapter) grdVenda_Finalizadoras.getAdapter()).setMontatela(new FuncionaisTela() {

                @Override
                public void test(Entity p, View v) {
                    TextView edtaux = (TextView) v.findViewById(R.id.edtVendaFinalizadoraValor);
                    if (edtaux != null)
                        edtaux.setText(AndroidUtils.FormatarValor_Monetario(((VendaFinalizadora) p).getValor()));
                    edtaux = (TextView) v.findViewById(R.id.edtVendaFinalizadora);
                    if (edtaux != null)
                        edtaux.setText(((VendaFinalizadora) p).getFinalizadora().getDescricao());
                }
            });
        }
    }

}
