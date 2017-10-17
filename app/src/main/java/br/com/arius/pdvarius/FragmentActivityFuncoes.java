package br.com.arius.pdvarius;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import arius.pdv.base.ProdutoCategoria;
import br.com.arius.pdvarius.AriusActivityProdutoCategoria;
import br.com.arius.pdvarius.AriusActivityProdutoPrincipal;
import br.com.arius.pdvarius.R;

/**
 * Created by Arius on 06/10/2017.
 */

public class FragmentActivityFuncoes extends android.support.v4.app.Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragmentariusfuncoes, container,false);

        AriusActivityFuncoes activityFuncoes = new AriusActivityFuncoes();
        activityFuncoes.montaFuncoes(view, getContext());

        return view;
    }
}
