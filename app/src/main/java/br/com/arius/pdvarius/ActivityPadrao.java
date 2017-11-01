package br.com.arius.pdvarius;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import FloatingActionMenu.FloatingActionMenu;
import arius.pdv.base.Pdv;
import arius.pdv.base.PdvDao;
import arius.pdv.base.PdvService;
import arius.pdv.base.PdvTipo;
import arius.pdv.base.VendaSituacao;
import arius.pdv.core.AppContext;
import arius.pdv.core.FuncionaisFilters;
import arius.pdv.core.UserException;
import arius.pdv.db.AriusAlertDialog;


public class ActivityPadrao extends AppCompatActivity {

    private Toolbar toolbar;
    private boolean habilita_menu;
    private boolean iconeVendaStatus;
    private ImageView imgVensaStatus;
    private static ActivityPadrao activityPadrao;
    private static boolean pesquisaVenda;
    private FloatingActionMenu floatingActionMenu;
    private static BottomNavigationView navigation;

    public Context getAppContext(){
        try {
            return activityPadrao.getApplicationContext();
        } catch (Exception e){
            return null;
        }
    }

    public void setPesquisaVenda(boolean pesquisaVendal){
        pesquisaVenda = pesquisaVendal;
    }

    public boolean getPesquisaVenda(){
        return pesquisaVenda;
    }

    @Override
    protected void onPause() {
        progressBar(true);

        super.onPause();
    }

    @Override
    protected void onStart() {
        super.onStart();

        activityPadrao = this;

        progressBar(false);

        List<Pdv> lpdvs = AppContext.get().getDao(PdvDao.class).listCache(new FuncionaisFilters<Pdv>() {
            @Override
            public boolean test(Pdv p) {
                return p.getCodigo_pdv() == 1;
            }
        });
        if (lpdvs.size() > 0)
            PdvService.get().setPdv(lpdvs.get(0));  //pode haver apenas um pdv com um código

        if (PdvService.get().getPdv() == null){
            throw new UserException("Operação não permitida! \n" +
                                    "PDV não configurado ou não existente!");
        }

        setTitleToolbar();

        if (floatingActionMenu == null)
            floatingActionMenu = (FloatingActionMenu) findViewById(R.id.floatingActionMenu);
        if (navigation ==  null)
            navigation = (BottomNavigationView) findViewById(R.id.navigation);
    }

    public FloatingActionMenu getFloatingActionMenu() {
        return floatingActionMenu;
    }

    public static BottomNavigationView getNavigation() {
        return navigation;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //Esse create faz o trabalho de inicialization da aplicação
        AppContext.get();
        super.onCreate(savedInstanceState);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    //Método para criar o toolbar customizado no app
    public void setTitleToolbar() {
        TextView title = (TextView) findViewById(R.id.lbTitle_ActionBar);
        if (title != null && PdvService.get().getPdv() != null) {
            TextView txtStatus = (TextView) findViewById(R.id.lbStatus);
            txtStatus.setText(PdvService.get().getPdv().getStatus().toString());
            txtStatus.setBackgroundColor(PdvService.get().getPdv().getStatus() != PdvTipo.ABERTO ?
                    Color.parseColor("#bc2d2d"): Color.parseColor("#29840f"));
//            ImageView imgPDVVenda = (ImageView) findViewById(R.id.imgPDVStatus);
//            imgPDVVenda.setImageResource(PdvService.get().getPdv().getStatus() != PdvTipo.ABERTO ?
//                    R.drawable.ic_circle_red : R.drawable.ic_circle_green);
            this.imgVensaStatus = (ImageView) findViewById(R.id.imgVendaStatus);
            setImagemVendaStatus();
        }

    }

    public void setButtons(boolean p_btnVoltar, boolean p_habilitaMenu, boolean iconeVendaStatus){
        this.habilita_menu = p_habilitaMenu;
        this.iconeVendaStatus = iconeVendaStatus;
        LayoutInflater mInflater = LayoutInflater.from(this);
        View mCustomView = mInflater.inflate(R.layout.layouactionbar, null);

        if (getSupportActionBar() == null) {
            toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
        }

        if (p_btnVoltar) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        getSupportActionBar().setCustomView(mCustomView);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.toolbar));

        toolbar = (Toolbar) mCustomView.getParent();
        toolbar.setContentInsetsAbsolute(0,0);
        toolbar.setContentInsetStartWithNavigation(0);
        toolbar.setContentInsetEndWithActions(0);
        toolbar.setPadding(0, 0, 0, 0);


        //ImageView imgVendaStatus = (ImageView) findViewById(R.id.imgVendaStatus);
        //imgVendaStatus.setVisibility(PdvService.get().getPdv(1).isAberto() ? View.VISIBLE : View.GONE);

//        try {
//            Field declaredField = toolbar.getClass().getDeclaredField("mTitleTextView");
//            declaredField.setAccessible(true);
//            TextView titleTextView = (TextView) declaredField.get(toolbar);
//            ViewGroup.LayoutParams layoutParams = titleTextView.getLayoutParams();
//            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
//            titleTextView.setLayoutParams(layoutParams);
//            titleTextView.setTypeface(null, Typeface.BOLD);
//            titleTextView.setGravity(Gravity.CENTER_HORIZONTAL);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        //ImageButton imageButton = (ImageButton) mCustomView
        //        .findViewById(R.id.imageButton);
        //imageButton.setOnClickListener(new OnClickListener() {

        //    @Override
        //    public void onClick(View view) {
        //        Toast.makeText(getApplicationContext(), "Refresh Clicked!",
        //                Toast.LENGTH_LONG).show();
        //  }
        //});

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return (this.habilita_menu && super.onPrepareOptionsMenu(menu));
    }

    public void setImagemVendaStatus(){
        if (!this.iconeVendaStatus)
            this.imgVensaStatus.setVisibility(View.GONE);
        else {
            this.imgVensaStatus.setVisibility(View.VISIBLE);
            if (PdvService.get().getVendaAtiva() != null) {
                this.imgVensaStatus.setImageResource(
                        PdvService.get().getVendaAtiva().getSituacao() == VendaSituacao.ABERTA ? R.mipmap.vendaaberta :
                                PdvService.get().getVendaAtiva().getSituacao() == VendaSituacao.FECHADA ? R.mipmap.vendafechada :
                                        PdvService.get().getVendaAtiva().getSituacao() == VendaSituacao.CANCELADA ? R.mipmap.vendacancelada :
                0);
            } else
                this.imgVensaStatus.setImageResource(0);
        }
    }

    public static void progressBar(boolean exibir){
        if (!exibir) {
            if (AriusAlertDialog.getGetView() != null)
                if (AriusAlertDialog.getGetView().getVisibility() == View.VISIBLE )
                    AriusAlertDialog.getAlertDialog().dismiss();
            return;
        } else
            if (AriusAlertDialog.getGetView() != null)
                if (AriusAlertDialog.getGetView().getVisibility() == View.VISIBLE )
                    return;

        AriusAlertDialog.exibirDialog(activityPadrao,R.layout.layoutprogress);
        AriusAlertDialog.getAlertDialog().setCancelable(exibir);
        AriusAlertDialog.getAlertDialog().show();
    }

}
