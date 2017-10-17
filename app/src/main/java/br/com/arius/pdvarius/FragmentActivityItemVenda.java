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

public class FragmentActivityItemVenda extends android.support.v4.app.Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragmentariusitemvenda, container,false);

        AriusActivityItemVenda ariusActivityItemVenda = new AriusActivityItemVenda();
        ariusActivityItemVenda.montaItemVenda(view, getContext());

        return view;
    }
}
