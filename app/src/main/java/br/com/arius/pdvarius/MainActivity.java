package br.com.arius.pdvarius;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

import arius.pdv.base.PdvDao;
import arius.pdv.base.PdvService;
import arius.pdv.base.PdvTipo;
import arius.pdv.base.UsuarioDao;
import arius.pdv.core.AppContext;
import arius.pdv.core.UserException;
import arius.pdv.db.AndroidUtils;
import arius.pdv.db.AriusAlertDialog;

public class MainActivity extends ActivityPadrao {

    private AppBarLayout appBar;

    @Override
    protected void onStart() {
        super.onStart();
        appBar.setVisibility(View.GONE);
        if (PdvService.get().getPdv() == null)
            return;

        if (PdvService.get().getPdv().getStatus() == PdvTipo.ABERTO && PdvService.get().getOperadorAtual() != null){

            //PdvService.get().fechaCaixa(AppContext.get().getDao(UsuarioDao.class).find(1));

            Intent intent = new Intent(getBaseContext(), AriusActivityPrincipal.class);
            //Intent intent = new Intent(getBaseContext(), AriusActivityProdutoCategoria.class);

            startActivity(intent);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        appBar = (AppBarLayout) findViewById(R.id.appBarLayout);

        AuxiliarCadastros auxiliarCadastros = new AuxiliarCadastros();

        if(AppContext.get().getDao(PdvDao.class).find(1) == null) {
            auxiliarCadastros.cadastrarPDV();
            auxiliarCadastros.cadastrarOperador();
            auxiliarCadastros.cadastrarFinalizadora();
            auxiliarCadastros.cadastraProdutos();
            auxiliarCadastros.cadastroUnidadeMedida();
            auxiliarCadastros.cadastroeHistorico();
            auxiliarCadastros.cadastroProdutoClassificacao();
            auxiliarCadastros.alteraProdutoClassificacao();
        }


//        Produto produto = AppContext.get().getDao(ProdutoDao.class).find(1);
//        produto.setUnidadeMedida(AppContext.get().getDao(UnidadeMedidaDao.class).find(1));
//
//        AppContext.get().getDao(ProdutoDao.class).update(produto);

//        Produto produto = AppContext.get().getDao(ProdutoDao.class).find(3);
//        Produto produto = new Produto();
//        produto.setDescricao("Cerveja Skol");
//        produto.setDescricaoReduzida("Cerveja Skol");
//        produto.setProdutoCategoria(AppContext.get().getDao(ProdutoCategoriaDao.class).find(8));
//        produto.setPrincipal(false);
//        AppContext.get().getDao(ProdutoDao.class).insert(produto);

        Button btn = (Button) findViewById(R.id.btn_Entrar);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PdvService.get().setOperadorAtual(AppContext.get().getDao(UsuarioDao.class).find(1));
                if (PdvService.get().getOperadorAtual() == null) {
                    throw new UserException("Operação não permitida! \n Operador não encontrado!");
                }
            }
        });
//                TesteComMetodos testeComMetodos = new TesteComMetodos();
//                testeComMetodos.capturaMetodo();


        setButtons(false, false, false);

    }

}
