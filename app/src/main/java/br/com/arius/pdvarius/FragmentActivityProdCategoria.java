package br.com.arius.pdvarius;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import arius.pdv.base.ProdutoCategoria;

/**
 * Created by Arius on 06/10/2017.
 */

public class FragmentActivityProdCategoria extends android.support.v4.app.Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragmentariusprodutocategoria, container,false);

        AriusActivityProdutoCategoria produtoCategoria = new AriusActivityProdutoCategoria();
        produtoCategoria.montaCategorias(view, getContext());

        final AriusActivityProdutoPrincipal produtoPrincipal = new AriusActivityProdutoPrincipal();
        produtoPrincipal.montaPrincipal(view, getContext());

        produtoCategoria.setPesquisaProdutoCategoria(new AriusActivityProdutoCategoria.PesquisaProdutoCategoria() {
            @Override
            public void pesquisaProdutoCategoria(ProdutoCategoria produtoCategoria) {
                produtoPrincipal.pesquisaPrincipa(produtoCategoria == null ? 0 : produtoCategoria.getId());
            }
        });

        return view;
    }
}
