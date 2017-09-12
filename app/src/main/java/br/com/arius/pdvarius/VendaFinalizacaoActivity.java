package br.com.arius.pdvarius;

import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.google.common.collect.ImmutableMap;

import java.util.List;

import arius.pdv.base.Produto;
import arius.pdv.base.ProdutoDao;
import arius.pdv.core.AppContext;
import arius.pdv.core.FuncionaisFilters;
import arius.pdv.db.AriusCursorAdapter;

public class VendaFinalizacaoActivity extends ActivityPadrao {

    private View.OnClickListener btnSpliter;
    private ListView grdVenda_Finalizadoras;
    private GridView grdFinalizadoras;
    private RelativeLayout.LayoutParams layoutParams;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.conten_venda_finalizacao);

        setButtons(true, false);

        this.grdVenda_Finalizadoras = (ListView) findViewById(R.id.grdVenda_Finalizadoras);
        this.grdFinalizadoras = (GridView) findViewById(R.id.grdFinalizadoras);

        iniciaBotoes();

        FuncionaisFilters<Produto> filter = new FuncionaisFilters<Produto>() {
            @Override
            public boolean test(Produto p) {
                return true;
            }
        };

        List<Produto> lproduto = AppContext.get().getDao(ProdutoDao.class).listCache(filter);

        final AriusCursorAdapter adapter_item = new AriusCursorAdapter(this,
                R.layout.layout_combobox,
                R.layout.layout_combobox,
                ImmutableMap.<Integer, String>of(R.id.combobox_codigo,"codigo",
                        R.id.combobox_descricao,"descricao"),
                lproduto);


        grdVenda_Finalizadoras.setAdapter(adapter_item);

        grdVenda_Finalizadoras.setSelection(grdVenda_Finalizadoras.getAdapter().getCount()-1);

        final AriusCursorAdapter adapter = new AriusCursorAdapter(this,
                R.layout.layoutprodutoclassificacao,
                R.layout.layoutprodutoclassificacao,
                ImmutableMap.<Integer, String>of(R.id.edtCodProdClassGrid,"codigo",
                        R.id.edtDesProdClassGrid,"descricao"),
                lproduto);

        grdFinalizadoras.setAdapter(adapter);
    }

    private void iniciaBotoes(){
        final Button btnSpliter = (Button) findViewById(R.id.btnSplitFinalizacao_Finalizacao);
        final ConstraintLayout pnlVenda_Finalizadoras = (ConstraintLayout) findViewById(R.id.pnlVenda_Finalizador);
        this.btnSpliter = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (grdFinalizadoras.getVisibility() != View.GONE) {
                    grdFinalizadoras.setVisibility(View.GONE);

                    layoutParams = (RelativeLayout.LayoutParams) btnSpliter.getLayoutParams();
                    layoutParams.removeRule(RelativeLayout.BELOW);
                    layoutParams.removeRule(RelativeLayout.ABOVE);
                    layoutParams.addRule(RelativeLayout.ABOVE, R.id.pnlTotal_Finalizadoras);
                    btnSpliter.setLayoutParams(layoutParams);

                    layoutParams = (RelativeLayout.LayoutParams) pnlVenda_Finalizadoras.getLayoutParams();
                    layoutParams.removeRule(RelativeLayout.BELOW);
                    layoutParams.removeRule(RelativeLayout.ABOVE);
                    layoutParams.addRule(RelativeLayout.ABOVE, R.id.btnSplitFinalizacao_Finalizacao);
                    pnlVenda_Finalizadoras.setLayoutParams(layoutParams);

                    // desmarcar a configuração para o ultimo item da venda
                    grdVenda_Finalizadoras.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_DISABLED);
                    grdVenda_Finalizadoras.setStackFromBottom(false);

                }else{
                    grdFinalizadoras.setVisibility(View.VISIBLE);
                    layoutParams = (RelativeLayout.LayoutParams) pnlVenda_Finalizadoras.getLayoutParams();
                    layoutParams.removeRule(RelativeLayout.BELOW);
                    layoutParams.removeRule(RelativeLayout.ABOVE);
                    pnlVenda_Finalizadoras.setLayoutParams(layoutParams);

                    layoutParams = (RelativeLayout.LayoutParams) btnSpliter.getLayoutParams();
                    layoutParams.removeRule(RelativeLayout.BELOW);
                    layoutParams.removeRule(RelativeLayout.ABOVE);

                    layoutParams.addRule(RelativeLayout.BELOW, R.id.pnlVenda_Finalizador);
                    btnSpliter.setLayoutParams(layoutParams);


                    // seta para o ultimo item da venda
                    grdVenda_Finalizadoras.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
                    grdVenda_Finalizadoras.setStackFromBottom(true);
                }

            }
        };
        btnSpliter.setOnClickListener(this.btnSpliter);
    }
}
