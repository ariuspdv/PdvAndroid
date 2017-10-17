package br.com.arius.pdvarius;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Arius on 06/10/2017.
 */

public class FragmentActivityFinalizadoraVenda extends android.support.v4.app.Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragmentariusfinalizadoravenda, container,false);

        AriusActivityFinalizadora ariusActivityFinalizadora = new AriusActivityFinalizadora();
        ariusActivityFinalizadora.montaFinalizadoraVenda(view, getContext());

        final AriusActivityFinalizadoraVenda ariusActivityFinalizadoraVenda = new AriusActivityFinalizadoraVenda();
        ariusActivityFinalizadoraVenda.montaFinalizadoraVenda(view, getContext());

        ariusActivityFinalizadora.setDataControll(new AriusActivityFinalizadora.DataControll() {
            @Override
            public void afterScroll() {
                ariusActivityFinalizadoraVenda.afteScroll();
            }
        });

        return view;
    }
}
