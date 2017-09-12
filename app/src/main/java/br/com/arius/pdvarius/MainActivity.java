package br.com.arius.pdvarius;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import arius.pdv.base.Pdv;
import arius.pdv.base.PdvDao;
import arius.pdv.base.PdvService;
import arius.pdv.base.Produto;
import arius.pdv.base.ProdutoDao;
import arius.pdv.base.UnidadeMedida;
import arius.pdv.base.UnidadeMedidaDao;
import arius.pdv.base.VendaDao;
import arius.pdv.core.AppContext;
import arius.pdv.core.FuncionaisFilters;

public class MainActivity extends ActivityPadrao {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AppContext.get();

        FuncionaisFilters<Produto> filters = new FuncionaisFilters<Produto>() {
            @Override
            public boolean test(Produto p) {
                return true;
            }
        };

//        UnidadeMedida unidadeMedida = new UnidadeMedida();
//        unidadeMedida.setDescricao("Unidade");
//        unidadeMedida.setFracionada(false);
//        unidadeMedida.setSigla("UN");
//
//        AppContext.get().getDao(UnidadeMedidaDao.class).insert(unidadeMedida);
//
//        Produto produto = AppContext.get().getDao(ProdutoDao.class).find(1);
//        produto.setUnidadeMedida(unidadeMedida);
//        AppContext.get().getDao(ProdutoDao.class).update(produto);
//
//        produto = AppContext.get().getDao(ProdutoDao.class).find(2);
//        produto.setUnidadeMedida(unidadeMedida);
//        AppContext.get().getDao(ProdutoDao.class).update(produto);

        Button btn = (Button) findViewById(R.id.btn_Entrar);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //throw new ApplicationException("Teste application exceprion");

                //Teste teste = new Teste();
                //teste.iniciaTeste();

//                Produto pdv = new Produto();
//                pdv.setDescricao("Produto 3");
//                pdv.setCodigo(3);
//
//                ProdutoDao pdvDao = new ProdutoDao();
//
//                AppContext.get().getDao(ProdutoDao.class).insert(pdv);
//
//                pdv.setDescricao("Produto 4");
//                pdv.setCodigo(4);
//
//                AppContext.get().getDao(ProdutoDao.class).insert(pdv);
//
//                pdv.setDescricao("Produto 4");
//                pdv.setCodigo(4);
//
//                AppContext.get().getDao(ProdutoDao.class).insert(pdv);


                PdvService.get().getPdv(1).setAberto(true);
                //PdvService.get().getPdv(1).setVendaAtiva(AppContext.get().getDao(VendaDao.class).find(4));
                AppContext.get().getDao(PdvDao.class).update(PdvService.get().getPdv(1));

                Intent intent = new Intent(getBaseContext(), VendaClassificacaoActivity.class);

                startActivity(intent);
            }
        });

        setButtons(false, false);

//        PdvService.get().getPdv(1).setAberto(false);
//        AppContext.get().getDao(PdvDao.class).update(PdvService.get().getPdv(1));
    }

}
