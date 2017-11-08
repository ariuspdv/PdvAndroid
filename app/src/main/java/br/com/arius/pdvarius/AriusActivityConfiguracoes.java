package br.com.arius.pdvarius;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import arius.pdv.base.Configuracao;
import arius.pdv.base.ConfiguracaoDao;
import arius.pdv.base.PdvService;
import arius.pdv.core.AppContext;
import arius.pdv.db.AndroidUtils;
import arius.pdv.db.AriusAlertDialog;

public class AriusActivityConfiguracoes extends ActivityPadrao {

    private Configuracao configuracao;

    private CheckBox chkPermitirFundoTrocoZerado;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contentariusconfiguracoes);

        setButtons(true, false, false);

        carregandoComponentes();

        Button btnGravar = (Button) findViewById(R.id.btnContetAriusConfiguracoesGravar);
        btnGravar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gravarConfiguracoes();
            }
        });

        this.configuracao = PdvService.get().getConfiguracao();

        carregarConfiguracoes();
    }

    private void carregandoComponentes(){
        chkPermitirFundoTrocoZerado = (CheckBox) findViewById(R.id.chkPermitir_Fundo_Troco_Zerado);
    }

    private void carregarConfiguracoes(){
        chkPermitirFundoTrocoZerado.setChecked(configuracao.getPermitir_fundo_troco_zerado() == "T");
    }

    private void gravarConfiguracoes(){
        try {
            this.configuracao.setPermitir_fundo_troco_zerado(chkPermitirFundoTrocoZerado.isChecked() ? "T" : "F");

            AppContext.get().getDao(ConfiguracaoDao.class).update(this.configuracao);

            AriusAlertDialog.exibirDialog(this, R.layout.contentariusdialogdelete);
            ((TextView) AriusAlertDialog.getAlertDialog().findViewById(R.id.edtContentDialogDeleteTexto)).setText("Configurações gravada com sucesso!");
            AriusAlertDialog.getAlertDialog().findViewById(R.id.btnContentDialogDeleteNao).setVisibility(View.GONE);
            ((Button) AriusAlertDialog.getAlertDialog().findViewById(R.id.btnContentDialogDeleteSim)).setText("OK");
            AriusAlertDialog.getAlertDialog().findViewById(R.id.btnContentDialogDeleteSim).setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AriusAlertDialog.getAlertDialog().dismiss();
                        }
                    }
            );
        } catch (Exception e){
            AndroidUtils.toast(this, "Problema ao gravar as configurações! \n" + e.getMessage());
        }
    }
}
