package br.com.arius.pdvarius;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import arius.pdv.core.AppContext;

/**
 * Created by Arius on 03/11/2017.
 */

public class AriusActivitySplashScreen extends ActivityPadrao {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Handler handle = new Handler();
        handle.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Esse create faz o trabalho de inicialization da aplicação
                AppContext.get();

                Intent intent = new Intent(getApplicationContext(), AriusActivityPrincipal.class);

                startActivity(intent);
                finish();
            }
        }, 2450);
    }

}
