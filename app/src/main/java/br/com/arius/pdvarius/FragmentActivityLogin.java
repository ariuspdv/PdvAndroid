package br.com.arius.pdvarius;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Arius on 06/10/2017.
 */

public class FragmentActivityLogin extends android.support.v4.app.Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragmentariuslogin, container,false);

        AriusActivityLogin activityLogin = new AriusActivityLogin();
        activityLogin.montaLogin(view,getContext());

        return view;
    }
}
