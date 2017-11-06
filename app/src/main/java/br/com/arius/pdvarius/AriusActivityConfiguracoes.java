package br.com.arius.pdvarius;

import android.os.Bundle;

public class AriusActivityConfiguracoes extends ActivityPadrao {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contentariusconfiguracoes);

        setButtons(true, false, false);
    }
}
