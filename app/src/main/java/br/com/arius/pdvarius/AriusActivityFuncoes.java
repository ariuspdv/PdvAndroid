package br.com.arius.pdvarius;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.LinearLayout;
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

    private LinearLayout btnReforco;
    private LinearLayout btnFecharCaixa;
    private LinearLayout btnSangria;
    private LinearLayout btnFechaVenda;
    private LinearLayout btnCancelarVenda;
    private LinearLayout btnListabgeVenda;
    private LinearLayout btnRetirada;
    private Context context;
    private  AriusActivityPercValor ariusActivityPercValor;

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

    public void montaFuncoes(View view, final Context context){
        this.context = context;
        ariusActivityPercValor = new AriusActivityPercValor();
        if (view == null){
            btnReforco = (LinearLayout) findViewById(R.id.btnFuncoesReforco);
            btnFecharCaixa = (LinearLayout) findViewById(R.id.btnFuncoesFecharCaixa);
            btnSangria = (LinearLayout) findViewById(R.id.btnFuncoesSangria);
            btnFechaVenda = (LinearLayout) findViewById(R.id.btnFuncoesFechaVenda);
            btnCancelarVenda = (LinearLayout) findViewById(R.id.btnFuncoesCancelaVenda);
            btnListabgeVenda = (LinearLayout) findViewById(R.id.btnFuncoesVendas);
            btnRetirada = (LinearLayout) findViewById(R.id.btnFuncoesRetirada);
        } else {
            btnReforco = view.findViewById(R.id.btnFuncoesReforco);
            btnFecharCaixa = view.findViewById(R.id.btnFuncoesFecharCaixa);
            btnSangria = view.findViewById(R.id.btnFuncoesSangria);
            btnFechaVenda = view.findViewById(R.id.btnFuncoesFechaVenda);
            btnCancelarVenda = view.findViewById(R.id.btnFuncoesCancelaVenda);
            btnListabgeVenda = view.findViewById(R.id.btnFuncoesVendas);
            btnRetirada = view.findViewById(R.id.btnFuncoesRetirada);
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

        if (btnRetirada != null) {
            btnRetirada();
        }

    }

    private void btnReforco(){

        btnReforco.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!validaStatusPDV())
                    return;

                AriusAlertDialog.exibirDialog(context,R.layout.contentariusdialogpercvalor);
                ariusActivityPercValor.montaDialog_Campos(AriusAlertDialog.getAlertDialog(), view, "Reforço");
                ariusActivityPercValor.setUtilizaPorcentagem(false);

                AriusAlertDialog.getGetView().findViewById(R.id.btnContentDialogValorConfirmar).setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (ariusActivityPercValor.getRetorno_valor() <= 0)
                                    throw new UserException("Informar um valor para o reforço!");

                                PdvService.get().reforcoCaixa(ariusActivityPercValor.getRetorno_valor());

                                AriusAlertDialog.getAlertDialog().dismiss();

                            }
                        }
                );
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
                intent.putExtra("funcaoExecutar","FecharCaixa");

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
                intent.putExtra("funcaoExecutar","Sangria");

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
                    if (PdvService.get().getVendaAtiva().getSituacao() == VendaSituacao.CANCELADA)
                        throw new UserException("Venda já cancelada!");
                    else {
                        PdvService.get().getVendaAtiva().setSituacao(VendaSituacao.CANCELADA);
                        AppContext.get().getDao(VendaDao.class).update(PdvService.get().getVendaAtiva());
                    }
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

    private void btnRetirada(){
        btnRetirada.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!validaStatusPDV())
                    return;

                Intent intent = new Intent(context, AriusActivityResumoCaixa.class);
                intent.putExtra("funcaoExecutar","Retirada");

                context.startActivity(intent);
            }
        });
    }
}
