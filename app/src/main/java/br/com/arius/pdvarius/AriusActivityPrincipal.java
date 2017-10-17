package br.com.arius.pdvarius;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentTransaction;
import android.view.MenuItem;

public class AriusActivityPrincipal extends ActivityPadrao {

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_prodcategoria:
                    montaFragmento(FragmentActivityProdCategoria.class);
                    return true;
                case R.id.navigation_itensvenda:
                    montaFragmento(FragmentActivityItemVenda.class);
                    return true;
                case R.id.navigation_finalizadorasvenda:
                    montaFragmento(FragmentActivityFinalizadoraVenda.class);
                    return true;
                case R.id.navigation_funcoes:
                    montaFragmento(FragmentActivityFuncoes.class);
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arius_principal);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

    }

    private void montaFragmento(Class nomeClasseFragment){

        android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        fm.popBackStack();

        if (nomeClasseFragment == FragmentActivityProdCategoria.class){
            FragmentActivityProdCategoria teste = new FragmentActivityProdCategoria();
            ft.replace(R.id.frameprincipal, teste, "FragmentActivityProdCategoria");
            ft.addToBackStack("FragmentActivityProdCategoria");
        }

        if (nomeClasseFragment == FragmentActivityItemVenda.class){
            FragmentActivityItemVenda teste = new FragmentActivityItemVenda();
            ft.replace(R.id.frameprincipal, teste, "FragmentActivityItemVenda");
            ft.addToBackStack("FragmentActivityItemVenda");
        }

        if (nomeClasseFragment == FragmentActivityFinalizadoraVenda.class){
            FragmentActivityFinalizadoraVenda teste = new FragmentActivityFinalizadoraVenda();
            ft.replace(R.id.frameprincipal, teste, "FragmentActivityFinalizadoraVenda");
            ft.addToBackStack("FragmentActivityFinalizadoraVenda");
        }

        if (nomeClasseFragment == FragmentActivityFuncoes.class){
            FragmentActivityFuncoes teste = new FragmentActivityFuncoes();
            ft.replace(R.id.frameprincipal, teste, "FragmentActivityFuncoes");
            ft.addToBackStack("FragmentActivityFuncoes");

        }

        ft.commit();
    }

}
