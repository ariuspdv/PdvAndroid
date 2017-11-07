package br.com.arius.pdvarius;

import android.content.Context;
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
import android.view.inputmethod.InputMethodManager;
import java.lang.reflect.Field;
import FloatingActionMenu.FloatingActionButton;
import arius.pdv.base.PdvDao;
import arius.pdv.base.PdvService;
import arius.pdv.base.PdvTipo;
import arius.pdv.base.VendaDao;
import arius.pdv.base.VendaSituacao;
import arius.pdv.core.AppContext;
import arius.pdv.core.UserException;
import arius.pdv.db.AndroidUtils;

public class AriusActivityPrincipal extends ActivityPadrao {

    private boolean pressBack = false;
    private Fragment fragmentAtivo;
    private AppBarLayout appBar;
    private FloatingActionButton btnFloatingNovaVenda;
    private FloatingActionButton btnFloatingFechaVendaAtiva;
    private FloatingActionButton btnFloatingCancelaVenda;


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            progressBar(true);
            if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                String tag = getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1).getName();
                fragmentAtivo = getSupportFragmentManager().findFragmentByTag(tag);
            } else
                fragmentAtivo = null;
            View view = getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
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

        getNavigation().setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        BottomNavigationViewHelper.disableShiftMode(getNavigation());

        if (PdvService.get().getPdv().getStatus() == PdvTipo.FECHADO){
            appBar.setVisibility(View.GONE);
            getNavigation().setVisibility(View.GONE);
            getFloatingActionMenu().setVisibility(View.GONE);
            for(int i = 0; i < fm.getBackStackEntryCount(); ++i) {
                fm.popBackStack(fm.getBackStackEntryAt(i).getName(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
            fm.popBackStackImmediate();
            montaFragmento(FragmentActivityLogin.class);
        } else {
            if (getNavigation().getVisibility() != View.VISIBLE)
                getNavigation().setVisibility(View.VISIBLE);
            if (appBar.getVisibility() != View.VISIBLE)
                appBar.setVisibility(View.VISIBLE);
            if (getFloatingActionMenu().getVisibility() != View.VISIBLE)
                getFloatingActionMenu().setVisibility(View.VISIBLE);

            for(int i = 0; i < fm.getBackStackEntryCount(); ++i) {
                String tag = fm.getBackStackEntryAt(i).getName();
                if (fm.findFragmentByTag(tag).getClass() == FragmentActivityLogin.class) {
                    fm.beginTransaction().remove(fm.findFragmentByTag(tag)).commitNow();
                    super.onBackPressed();
                }
            }

            setFragmentAtivo();

            setBtnFloatingNovaVenda();

            setBtnFloatingFechaVendaAtiva();

            setBtnFloatingCancelaVenda();

            if (getPesquisaVenda())
                getNavigation().setSelectedItemId(R.id.navigation_itensvenda);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arius_principal);

        appBar = (AppBarLayout) findViewById(R.id.appBarLayout);

        setButtons(false,false,false);

        AuxiliarCadastros auxiliarCadastros = new AuxiliarCadastros();

        if (AppContext.get().getDao(PdvDao.class).find(1) == null) {
            auxiliarCadastros.cadastrarPDV();
            auxiliarCadastros.cadastrarOperador();
            auxiliarCadastros.cadastrarFinalizadora();
            auxiliarCadastros.cadastroeHistorico();
            auxiliarCadastros.cadastroUnidadeMedida();
            auxiliarCadastros.cadastroProdutoClassificacao();
            auxiliarCadastros.cadastraProdutos();
        }

        //auxiliarCadastros.performanceProduto();

        //AndroidUtils.toast(getAppContext(),getAppContext().getCacheDir().toString());

    }

    private void setFragmentAtivo(){
        if (getSupportFragmentManager().getBackStackEntryCount() == 0){
            if (PdvService.get().getVendaAtiva() == null)
                getNavigation().setSelectedItemId(R.id.navigation_prodcategoria);
            else
                getNavigation().setSelectedItemId(R.id.navigation_itensvenda);
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
                    getNavigation().setSelectedItemId(R.id.navigation_prodcategoria);
                if (fragment.getClass() == FragmentActivityItemVenda.class)
                    getNavigation().setSelectedItemId(R.id.navigation_itensvenda);
                if (fragment.getClass() == FragmentActivityFinalizadoraVenda.class)
                    getNavigation().setSelectedItemId(R.id.navigation_finalizadorasvenda);
                if (fragment.getClass() == FragmentActivityFuncoes.class)
                    getNavigation().setSelectedItemId(R.id.navigation_funcoes);

                pressBack = false;

            }
        }

        progressBar(false);
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
        progressBar(false);
    }

    private void setBtnFloatingNovaVenda(){
        btnFloatingNovaVenda = (FloatingActionButton) findViewById(R.id.btnFloatingNovaVenda);
        btnFloatingNovaVenda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PdvService.get().getVendaAtiva() != null){
                    PdvService.get().getPdv().setVendaAtiva(null);
                    AppContext.get().getDao(PdvDao.class).update(PdvService.get().getPdv());

                    getNavigation().setSelectedItemId(R.id.navigation_prodcategoria);

                    AndroidUtils.toast(getApplicationContext(),"Iniciado Venda!");
                }
                if (getFloatingActionMenu().isOpened())
                    getFloatingActionMenu().close(getFloatingActionMenu().isAnimated());
            }
        });
    }

    private void setBtnFloatingFechaVendaAtiva(){
        btnFloatingFechaVendaAtiva = (FloatingActionButton) findViewById(R.id.btnFloatingFechaVendaAtiva);
        btnFloatingFechaVendaAtiva.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PdvService.get().getVendaAtiva() != null){
                    PdvService.get().getPdv().setVendaAtiva(null);
                    AppContext.get().getDao(PdvDao.class).update(PdvService.get().getPdv());

                    getNavigation().setSelectedItemId(R.id.navigation_prodcategoria);

                    AndroidUtils.toast(getApplicationContext(),"Venda Fechada!");
                }
                if (getFloatingActionMenu().isOpened())
                    getFloatingActionMenu().close(getFloatingActionMenu().isAnimated());
            }
        });
    }

    private void setBtnFloatingCancelaVenda(){
        btnFloatingCancelaVenda = (FloatingActionButton) findViewById(R.id.btnFloatingCancelaVenda);
        btnFloatingCancelaVenda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PdvService.get().getVendaAtiva() == null){
                    AndroidUtils.toast(getApplicationContext(),"Nenhuma venda ativa para cancelar!");
                } else {
                    if (PdvService.get().getVendaAtiva().getSituacao() == VendaSituacao.CANCELADA)
                        throw new UserException("Venda já cancelada!");
                    else {
                        PdvService.get().getVendaAtiva().setSituacao(VendaSituacao.CANCELADA);
                        AppContext.get().getDao(VendaDao.class).update(PdvService.get().getVendaAtiva());

                        if (getSupportFragmentManager().findFragmentByTag("fragmentActivityItemVenda").isVisible())
                            FragmentActivityItemVenda.getAriusActivityItemVenda().carregaVenda();
                        else
                            getNavigation().setSelectedItemId(R.id.navigation_itensvenda);
                    }
                }
                if (getFloatingActionMenu().isOpened())
                    getFloatingActionMenu().close(getFloatingActionMenu().isAnimated());
            }
        });
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

