package br.com.arius.pdvarius;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import arius.pdv.base.PdvService;
import arius.pdv.base.VendaSituacao;
import arius.pdv.core.AppContext;


public class ActivityPadrao extends AppCompatActivity {

    private Toolbar toolbar;
    private boolean habilita_menu;

    @Override
    protected void onStart() {
        super.onStart();
        TextView title = (TextView) findViewById(R.id.lbTitle_ActionBar);
        title.setText(PdvService.get().getPdv(1).isAberto()? "PdvArius - Caixa Aberto" : "PdvArius - Caixa Fechado");
        ImageView imgStatusVenda = (ImageView) findViewById(R.id.imgVendaStatus);
        if (PdvService.get().getVendaAtiva() == null)
            imgStatusVenda.setImageDrawable(null);
        else
            imgStatusVenda.setImageResource(PdvService.get().getVendaAtiva().getSituacao() == VendaSituacao.FECHADA ?
                    R.drawable.if_circle_red : R.drawable.if_circle_green);
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
            case R.id.itemFechar_Caixa:
                //noinspection SimplifiableIfStatement
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void setButtons(Boolean p_btnVoltar, Boolean p_habilitaMenu){
        this.habilita_menu = p_habilitaMenu;
        LayoutInflater mInflater = LayoutInflater.from(this);
        View mCustomView = mInflater.inflate(R.layout.layouactionbar, null);
        TextView mTitleTextView = (TextView) mCustomView.findViewById(R.id.lbTitle_ActionBar);
        mTitleTextView.setText(PdvService.get().getPdv(1).isAberto()? "PdvArius - Caixa Aberto" : "PdvArius - Caixa Fechado");

        if (getSupportActionBar() == null) {
            toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
        }

        if (p_btnVoltar) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            mTitleTextView.setGravity(Gravity.START);
        } else

            mTitleTextView.setGravity(Gravity.CENTER);

        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        getSupportActionBar().setCustomView(mCustomView);
        getSupportActionBar().setDisplayShowCustomEnabled(true);

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
}
