package br.com.arius.pdvarius;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.lang.reflect.Field;

import arius.pdv.base.PdvService;
import arius.pdv.base.PdvTipo;
import arius.pdv.db.AndroidUtils;

public class AriusActivityPrincipal extends ActivityPadrao {

    private static BottomNavigationView navigation;
    private static boolean criandoTela;
    private boolean pressBack = false;
    private Fragment fragmentAtivo;
    private AppBarLayout appBar;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                String tag = getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1).getName();
                fragmentAtivo = getSupportFragmentManager().findFragmentByTag(tag);
            } else
                fragmentAtivo = null;
            switch (item.getItemId()) {
                case R.id.navigation_prodcategoria:
                    montaFragmento(FragmentActivityCategoriaPrincipal.class);
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
        FragmentManager fm = getSupportFragmentManager();
        super.onStart();
        if (PdvService.get().getPdv().getStatus() == PdvTipo.FECHADO){
            appBar.setVisibility(View.GONE);
            navigation.setVisibility(View.GONE);
            for(int i = 0; i < fm.getBackStackEntryCount(); ++i) {
                fm.popBackStack(fm.getBackStackEntryAt(i).getName(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
            fm.popBackStackImmediate();
            montaFragmento(FragmentActivityLogin.class);
        } else {
            if (navigation.getVisibility() != View.VISIBLE)
                navigation.setVisibility(View.VISIBLE);
            if (appBar.getVisibility() != View.VISIBLE)
                appBar.setVisibility(View.VISIBLE);

            for(int i = 0; i < fm.getBackStackEntryCount(); ++i) {
                String tag = fm.getBackStackEntryAt(i).getName();
                if (fm.findFragmentByTag(tag).getClass() == FragmentActivityLogin.class) {
                    fm.beginTransaction().remove(fm.findFragmentByTag(tag)).commitNow();
                    super.onBackPressed();
                }
            }

            setFragmentAtivo();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arius_principal);

        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        BottomNavigationViewHelper.disableShiftMode(navigation);
        appBar = (AppBarLayout) findViewById(R.id.appBarLayout);

        criandoTela = true;

        setButtons(false,false,false);

        AuxiliarCadastros auxiliarCadastros = new AuxiliarCadastros();

//        auxiliarCadastros.alteraProdutoClassificacao();
//        auxiliarCadastros.alteraProduto();

    }

    public static void setNavigation(int itemNavigator){
        navigation.setSelectedItemId(itemNavigator);
    }

    private void setFragmentAtivo(){
        if (getSupportFragmentManager().getBackStackEntryCount() == 0){
            if (PdvService.get().getVendaAtiva() == null)
                navigation.setSelectedItemId(R.id.navigation_prodcategoria);
            else
                navigation.setSelectedItemId(R.id.navigation_itensvenda);
        } else {
            if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                String tag = getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1).getName();
                fragmentAtivo = getSupportFragmentManager().findFragmentByTag(tag);
                montaFragmento(fragmentAtivo.getClass());
            }
        }

    }

    @Override
    public void onBackPressed() {
        String tag = getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1).getName();
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(tag);
        getSupportFragmentManager().beginTransaction().remove(fragment).commitNow();

        super.onBackPressed();

        if (getSupportFragmentManager().getBackStackEntryCount() == 0)
            finish();
        else {
            tag = getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1).getName();
            fragment = getSupportFragmentManager().findFragmentByTag(tag);

            if (fragment.getClass() == FragmentActivityLogin.class)
                onBackPressed();
            else {
                pressBack = true;

                if (fragment.getClass() == FragmentActivityCategoriaPrincipal.class)
                    navigation.setSelectedItemId(R.id.navigation_prodcategoria);
                if (fragment.getClass() == FragmentActivityItemVenda.class)
                    navigation.setSelectedItemId(R.id.navigation_itensvenda);
                if (fragment.getClass() == FragmentActivityFinalizadoraVenda.class)
                    navigation.setSelectedItemId(R.id.navigation_finalizadorasvenda);
                if (fragment.getClass() == FragmentActivityFuncoes.class)
                    navigation.setSelectedItemId(R.id.navigation_funcoes);

                pressBack = false;

            }
        }
    }

    private void montaFragmento(Class nomeClasseFragment){

        android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        if (nomeClasseFragment == FragmentActivityLogin.class){
            if (getSupportFragmentManager().findFragmentByTag("fragmentActivityLogin") == null) {
                FragmentActivityLogin fragmentActivityLogin = new FragmentActivityLogin();
                ft.replace(R.id.frameprincipal, fragmentActivityLogin,"fragmentActivityLogin");
                ft.addToBackStack("fragmentActivityLogin");
            }
        }

        if (nomeClasseFragment == FragmentActivityCategoriaPrincipal.class){
            if (fragmentAtivo != null && fragmentAtivo.getClass() == nomeClasseFragment)
                return;

            FragmentActivityCategoriaPrincipal fragmentActivityCategoriaPrincipal = new FragmentActivityCategoriaPrincipal();
            ft.replace(R.id.frameprincipal, fragmentActivityCategoriaPrincipal, "fragmentActivityCategoriaPrincipal");
            if (!pressBack)
                ft.addToBackStack("fragmentActivityCategoriaPrincipal");
        }

        if (nomeClasseFragment == FragmentActivityItemVenda.class){
            if (fragmentAtivo != null && fragmentAtivo.getClass() == nomeClasseFragment)
                return;

            FragmentActivityItemVenda fragmentActivityItemVenda = new FragmentActivityItemVenda();
            ft.replace(R.id.frameprincipal, fragmentActivityItemVenda,"fragmentActivityItemVenda");
            if (!pressBack)
                ft.addToBackStack("fragmentActivityItemVenda");
        }

        if (nomeClasseFragment == FragmentActivityFinalizadoraVenda.class){
            if (fragmentAtivo != null && fragmentAtivo.getClass() == nomeClasseFragment)
                return;

            FragmentActivityFinalizadoraVenda fragmentActivityFinalizadoraVenda = new FragmentActivityFinalizadoraVenda();
            ft.replace(R.id.frameprincipal, fragmentActivityFinalizadoraVenda, "fragmentActivityFinalizadoraVenda");
            if (!pressBack)
                ft.addToBackStack("fragmentActivityFinalizadoraVenda");
        }

        if (nomeClasseFragment == FragmentActivityFuncoes.class){
            if (fragmentAtivo != null && fragmentAtivo.getClass() == nomeClasseFragment)
                return;

           FragmentActivityFuncoes fragmentActivityFuncoes = new FragmentActivityFuncoes();
           ft.replace(R.id.frameprincipal, fragmentActivityFuncoes,"fragmentActivityFuncoes");
            if (!pressBack)
                ft.addToBackStack("fragmentActivityFuncoes");
        }

        ft.commit();
    }


    public static class BottomNavigationViewHelper {
        public static void disableShiftMode(BottomNavigationView view) {
            BottomNavigationMenuView menuView = (BottomNavigationMenuView) view.getChildAt(0);
            try {
                Field shiftingMode = menuView.getClass().getDeclaredField("mShiftingMode");
                shiftingMode.setAccessible(true);
                //shiftingMode.setBoolean(menuView, false);
                //shiftingMode.setAccessi      ble(false);
                for (int i = 0; i < menuView.getChildCount(); i++) {
                    //BottomNavigationItemView item = (BottomNavigationItemView) menuView.getChildAt(i);
                    //noinspection RestrictedApi
                    //item.setShiftingMode(false);
                    // set once again checked value, so view will be updated
                    //noinspection RestrictedApi
                    //item.setChecked(item.getItemData().isChecked());

                    final View iconView = menuView.getChildAt(i).findViewById(android.support.design.R.id.icon);
                    final ViewGroup.LayoutParams layoutParams = iconView.getLayoutParams();
                    final DisplayMetrics displayMetrics = view.getResources().getDisplayMetrics();
                    layoutParams.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 32, displayMetrics);
                    layoutParams.width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 32, displayMetrics);
                    iconView.setLayoutParams(layoutParams);

                }
            } catch (NoSuchFieldException e) {
                // Log.e("BNVHelper", "Unable to get shift mode field", e);
            } //catch (IllegalAccessException e) {
                // Log.e("BNVHelper", "Unable to change value of shift mode", e);
            //}
        }
    }

}

