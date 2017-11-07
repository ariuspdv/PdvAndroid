package br.com.arius.pdvarius;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Arius on 06/10/2017.
 */

public class FragmentActivityItemVenda extends android.support.v4.app.Fragment {

    private static AriusActivityItemVenda ariusActivityItemVenda;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_arius_item_venda, container,false);

        ariusActivityItemVenda = new AriusActivityItemVenda();
        ariusActivityItemVenda.montaItemVenda(view, getContext());

        return view;
    }

    public static AriusActivityItemVenda getAriusActivityItemVenda() {
        return ariusActivityItemVenda;
    }
}
