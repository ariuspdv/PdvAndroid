package br.com.arius.pdvarius;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import arius.pdv.base.PdvService;
import arius.pdv.base.UsuarioDao;
import arius.pdv.core.AppContext;
import arius.pdv.db.AndroidUtils;
import arius.pdv.db.AriusAlertDialog;

/**
 * Created by Arius on 25/10/2017.
 */

public class AriusActivityLogin extends ActivityPadrao {

    private Context context;
    private Button btnLogin;
    private AriusActivityPercentualValor ariusActivityPercentualValor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        montaLogin(null, getApplicationContext());

    }

    public void montaLogin(View view, final Context context){
        this.context = context;

        ariusActivityPercentualValor = new AriusActivityPercentualValor();

        if (view == null){
            btnLogin = (Button) findViewById(R.id.btn_Entrar);
        } else {
            btnLogin = view.findViewById(R.id.btn_Entrar);
        }

        if (btnLogin != null)
            setButtonLogin();
    }

    private void setButtonLogin(){
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AriusAlertDialog.exibirDialog(context,R.layout.dialog_arius_perccentual_valor);
                ariusActivityPercentualValor.montaDialog_Campos(AriusAlertDialog.getAlertDialog(), v, "Valor Inicial do Caixa");
                ariusActivityPercentualValor.setUtilizaPorcentagem(false);

                AriusAlertDialog.getGetView().findViewById(R.id.btnDialogAriusPerccentualValorConfirmar).setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (ariusActivityPercentualValor.getRetorno_valor() == 0 &&
                                        PdvService.get().getConfiguracao().getPermitir_fundo_troco_zerado() == "F")
                                    AndroidUtils.toast(context,"Informar um valor para a abertura do caixa!");
                                else {
                                    PdvService.get().setOperadorAtual(AppContext.get().getDao(UsuarioDao.class).find(1));
                                    PdvService.get().abreCaixa(
                                            PdvService.get().getOperadorAtual(),
                                            ariusActivityPercentualValor.getRetorno_valor()
                                    );

                                    AriusAlertDialog.getAlertDialog().dismiss();

                                    ((AriusActivityPrincipal) context).onStart();
                                }
                            }
                        }
                );

            }
        });
    }
}
