package br.com.arius.pdvarius;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentTransaction;
import android.view.MenuItem;

import java.lang.reflect.Field;

import arius.pdv.base.PdvService;
import arius.pdv.base.PdvTipo;
import arius.pdv.db.AndroidUtils;

public class AriusActivityPrincipal extends ActivityPadrao {

    private static BottomNavigationView navigation;
    private static boolean criandoTela;

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
                    if (PdvService.get().getVendaAtiva() == null) {
                        AndroidUtils.toast(getAppContext(),"Não existe venda ativa para finalizar!");
                        return false;
                    } else {
                        montaFragmento(FragmentActivityFinalizadoraVenda.class);
                        return true;
                    }
                case R.id.navigation_funcoes:
                    montaFragmento(FragmentActivityFuncoes.class);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onStart(){
        super.onStart();
        if (getPesquisaVenda()) {
            setNavigation();
            setPesquisaVenda(false);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arius_principal);

        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        BottomNavigationViewHelper.disableShiftMode(navigation);

        criandoTela = true;
        setAbaNavigator();

    }

    public static void setNavigation(){
        setAbaNavigator();
    }

    private static void setAbaNavigator(){
        if (criandoTela) {
            navigation.setSelectedItemId(R.id.navigation_prodcategoria);
            criandoTela = false;
        } else {
            if (PdvService.get().getVendaAtiva() == null)
                navigation.setSelectedItemId(R.id.navigation_prodcategoria);
            else
                navigation.setSelectedItemId(R.id.navigation_itensvenda);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (PdvService.get().getPdv().getStatus() != PdvTipo.FECHADO) {
            setNavigation();
            super.onStart();
        }
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


    public static class BottomNavigationViewHelper {
        public static void disableShiftMode(BottomNavigationView view) {
            BottomNavigationMenuView menuView = (BottomNavigationMenuView) view.getChildAt(0);
            try {
                Field shiftingMode = menuView.getClass().getDeclaredField("mShiftingMode");
                shiftingMode.setAccessible(true);
                shiftingMode.setBoolean(menuView, false);
                shiftingMode.setAccessible(false);
                for (int i = 0; i < menuView.getChildCount(); i++) {
                    BottomNavigationItemView item = (BottomNavigationItemView) menuView.getChildAt(i);
                    //noinspection RestrictedApi
                    item.setShiftingMode(false);
                    // set once again checked value, so view will be updated
                    //noinspection RestrictedApi
                    item.setChecked(item.getItemData().isChecked());
                }
            } catch (NoSuchFieldException e) {
                // Log.e("BNVHelper", "Unable to get shift mode field", e);
            } catch (IllegalAccessException e) {
                // Log.e("BNVHelper", "Unable to change value of shift mode", e);
            }
        }
    }

}

