package br.com.arius.pdvarius;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

import arius.pdv.base.PdvDao;
import arius.pdv.base.PdvService;
import arius.pdv.base.PdvTipo;
import arius.pdv.base.VendaDao;
import arius.pdv.base.VendaSituacao;
import arius.pdv.core.AppContext;
import arius.pdv.core.UserException;
import arius.pdv.db.AndroidUtils;
import arius.pdv.db.AriusAlertDialog;

public class AriusActivityFuncoes extends ActivityPadrao {

    private ImageButton btnReforco;
    private ImageButton btnFecharCaixa;
    private ImageButton btnSangria;
    private ImageButton btnFechaVenda;
    private ImageButton btnCancelarVenda;
    private ImageButton btnListabgeVenda;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contentariusfuncoes);

        montaFuncoes(null, getApplicationContext());

    }

    private boolean validaStatusPDV(){
        if (PdvService.get().getPdv().getStatus() != PdvTipo.ABERTO){
            AndroidUtils.toast(context,"Não é possível efetuar a operação \nPDV " +
                    PdvService.get().getPdv().getStatus().name());
            return false;
        }

        return true;
    }

    public void montaFuncoes(View view, Context context){
        this.context = context;
        if (view == null){
            btnReforco = (ImageButton) findViewById(R.id.btnFuncoesReforco);
            btnFecharCaixa = (ImageButton) findViewById(R.id.btnFuncoesFecharCaixa);
            btnSangria = (ImageButton) findViewById(R.id.btnFuncoesSangria);
            btnFechaVenda = (ImageButton) findViewById(R.id.btnFuncoesFechaVenda);
            btnCancelarVenda = (ImageButton) findViewById(R.id.btnFuncoesCancelaVenda);
            btnListabgeVenda = (ImageButton) findViewById(R.id.btnFuncoesVendas);
        } else {
            btnReforco = view.findViewById(R.id.btnFuncoesReforco);
            btnFecharCaixa = view.findViewById(R.id.btnFuncoesFecharCaixa);
            btnSangria = view.findViewById(R.id.btnFuncoesSangria);
            btnFechaVenda = view.findViewById(R.id.btnFuncoesFechaVenda);
            btnCancelarVenda = view.findViewById(R.id.btnFuncoesCancelaVenda);
            btnListabgeVenda = view.findViewById(R.id.btnFuncoesVendas);
        }

        if (btnReforco != null){
            btnReforco();
        }

        if (btnFecharCaixa != null){
            btnFecharCaixa();
        }

        if (btnSangria != null){
            btnSangria();
        }

        if (btnFechaVenda != null){
            btnFechaVenda();
        }

        if (btnCancelarVenda != null){
            btnCancelarVenda();
        }

        if (btnListabgeVenda != null) {
            btnListabgeVenda();
        }

    }

    private void btnReforco(){

        btnReforco.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!validaStatusPDV())
                    return;

                AriusAlertDialog.exibirDialog(context,R.layout.contentariusdialogreforco);

                final EditText edtReforco = AriusAlertDialog.getGetView().findViewById(R.id.edtReforcoDialog);

                AriusAlertDialog.getGetView().findViewById(R.id.btnReforcoDialogCancelar).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AriusAlertDialog.getAlertDialog().dismiss();
                    }
                });

                AriusAlertDialog.getGetView().findViewById(R.id.btnReforcoDialogOK).setOnClickListener(new View.OnClickListener() {
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
                            throw new UserException("Informar um valor para o reforço!");

                        PdvService.get().reforcoCaixa(v_valor);

                        AriusAlertDialog.getAlertDialog().dismiss();

                    }
                });

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

                edtReforco.setText("0");

                edtReforco.requestFocus();

                edtReforco.setSelection(edtReforco.getText().length());

            }
        });

    }

    private void btnFecharCaixa(){

        btnFecharCaixa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!validaStatusPDV())
                    return;

                Intent intent = new Intent(context, AriusActivityResumoCaixa.class);
                intent.putExtra("sangria",false);

                context.startActivity(intent);
            }
        });

    }

    private void btnSangria(){
        btnSangria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!validaStatusPDV())
                    return;

                Intent intent = new Intent(context, AriusActivityResumoCaixa.class);
                intent.putExtra("sangria",true);

                context.startActivity(intent);
            }
        });
    }

    private void btnFechaVenda(){
        btnFechaVenda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!validaStatusPDV())
                    return;

                if (PdvService.get().getVendaAtiva() != null){
                    PdvService.get().getPdv().setVendaAtiva(null);
                    AppContext.get().getDao(PdvDao.class).update(PdvService.get().getPdv());

                    AndroidUtils.toast(context,"Venda Fechada!");
                }
            }
        });

    }

    private void btnCancelarVenda(){
        btnCancelarVenda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!validaStatusPDV())
                    return;

                if (PdvService.get().getVendaAtiva() == null){
                    AndroidUtils.toast(context,"Nenhuma venda ativa para cancelar!");
                } else {
                    PdvService.get().getVendaAtiva().setSituacao(VendaSituacao.CANCELADA);
                    AppContext.get().getDao(VendaDao.class).update(PdvService.get().getVendaAtiva());
                }
            }
        });
    }

    private void btnListabgeVenda(){
        btnListabgeVenda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!validaStatusPDV())
                    return;

                Intent intent = new Intent(context, AriusActivityListagemVenda.class);

                context.startActivity(intent);
            }
        });
    }
}
