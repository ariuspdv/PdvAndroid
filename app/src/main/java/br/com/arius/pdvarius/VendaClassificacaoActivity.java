package br.com.arius.pdvarius;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.common.collect.ImmutableMap;

import java.util.Date;
import java.util.List;

import SwipeListView.SwipeMenu;
import SwipeListView.SwipeMenuItem;
import arius.pdv.base.PdvService;
import arius.pdv.base.Produto;
import arius.pdv.base.ProdutoDao;
import arius.pdv.base.Venda;
import arius.pdv.base.VendaItem;
import arius.pdv.base.VendaSituacao;
import arius.pdv.core.AppContext;
import arius.pdv.core.Entity;
import arius.pdv.core.FuncionaisFilters;
import arius.pdv.db.AriusCursorAdapter;
import arius.pdv.db.AriusListView;

public class VendaClassificacaoActivity extends ActivityPadrao {

    private View.OnClickListener btnFinalizar;
    private AriusListView grdVenda_Item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_venda_classificacao);

        setButtons(true, true);

        instanciaBotoes();

        ConstraintLayout pnlCmbPesqProduto = (ConstraintLayout) findViewById(R.id.pnlCmbPesqProduto);
        final ConstraintLayout pnlVendaItens = (ConstraintLayout) findViewById(R.id.pnlVendaItens);


        final Button btnSplitter = (Button) findViewById(R.id.btnSplitter_ProdClass);
        grdVenda_Item = (AriusListView) findViewById(R.id.grdVenda_Item);
        final GridView grdProd_Class = (GridView) findViewById(R.id.grdProduto_Classificacao);

        pnlCmbPesqProduto.setVisibility(View.GONE);

        RelativeLayout.LayoutParams layoutParams =
                (RelativeLayout.LayoutParams)pnlVendaItens.getLayoutParams();
        layoutParams.removeRule(RelativeLayout.BELOW);
        layoutParams.removeRule(RelativeLayout.ABOVE);
        layoutParams.removeRule(RelativeLayout.ALIGN_PARENT_TOP);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        layoutParams.addRule(RelativeLayout.ABOVE);//, R.id.btnSplitter_ProdClass);
        //layoutParams.addRule(RelativeLayout.BELOW, R.id.pnlCmbPesqProduto);
        //layoutParams.addRule(RelativeLayout.ABOVE, R.id.pnlTotal);
        pnlVendaItens.setLayoutParams(layoutParams);

        btnSplitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (grdProd_Class.getVisibility() != View.GONE) {
                    grdProd_Class.setVisibility(View.GONE);

                    RelativeLayout.LayoutParams layoutParams =
                            (RelativeLayout.LayoutParams)btnSplitter.getLayoutParams();
                    layoutParams.removeRule(RelativeLayout.BELOW);
                    layoutParams.removeRule(RelativeLayout.ABOVE);
                    layoutParams.addRule(RelativeLayout.ABOVE, R.id.pnlTotal_Items);
                    btnSplitter.setLayoutParams(layoutParams);

                    RelativeLayout.LayoutParams layoutParams2 =
                            (RelativeLayout.LayoutParams)pnlVendaItens.getLayoutParams();
                    layoutParams2.removeRule(RelativeLayout.BELOW);
                    layoutParams2.removeRule(RelativeLayout.ABOVE);
                    layoutParams2.addRule(RelativeLayout.ABOVE, R.id.btnSplitter_ProdClass);
                    pnlVendaItens.setLayoutParams(layoutParams2);

                    // desmarcar a configuração para o ultimo item da venda
                    grdVenda_Item.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_DISABLED);
                    grdVenda_Item.setStackFromBottom(false);

                }else{
                    grdProd_Class.setVisibility(View.VISIBLE);
                    RelativeLayout.LayoutParams layoutParams2 =
                            (RelativeLayout.LayoutParams)pnlVendaItens.getLayoutParams();
                    layoutParams2.removeRule(RelativeLayout.BELOW);
                    layoutParams2.removeRule(RelativeLayout.ABOVE);
                    pnlVendaItens.setLayoutParams(layoutParams2);

                    RelativeLayout.LayoutParams layoutParams =
                        (RelativeLayout.LayoutParams)btnSplitter.getLayoutParams();
                    layoutParams.removeRule(RelativeLayout.BELOW);
                    layoutParams.removeRule(RelativeLayout.ABOVE);

                    layoutParams.addRule(RelativeLayout.BELOW, R.id.pnlVendaItens);
                    btnSplitter.setLayoutParams(layoutParams);


                    // seta para o ultimo item da venda
                    if (grdVenda_Item.getAdapter().getCount() > 3) {
                        grdVenda_Item.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
                        grdVenda_Item.setStackFromBottom(true);
                    }
                }
            }
        });

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

        //exemplo criação de botões no listview
        SwipeMenuItem goodItem = new SwipeMenuItem(
                getApplicationContext());
        // set item background
        goodItem.setBackground(new ColorDrawable(Color.rgb(0x30, 0xB1,
                0xF5)));
        // set item width
        goodItem.setWidth(300);
        // set a icon
        goodItem.setIcon(R.drawable.if_circle_green);
        // add to menu
        //menu.addMenuItem(goodItem);
//
//        grdVenda_Item.getMenu().addMenuItem(goodItem);
//
//        goodItem.setBackground(new ColorDrawable(Color.rgb(0x30, 0xB1,
//                0xF5)));
//        // set item width
//        goodItem.setWidth(300);
//        // set a icon
//        goodItem.setIcon(R.drawable.if_circle_red);
//        // add to menu
//        //menu.addMenuItem(goodItem);
//
        grdVenda_Item.getMenu().addMenuItem(goodItem);

//        grdVenda_Item.setCampos_Exibir(ImmutableMap.<Integer, String>of(android.R.id.text1,"descricao"));
//        grdVenda_Item.setSwipe_Delete(true);
//        grdVenda_Item.setEntity_listview("Produto");
//        grdVenda_Item.setLayout_arius(android.R.layout.simple_spinner_item);

        if (PdvService.get().getVendaAtiva() != null)
            grdVenda_Item.setDataSource(PdvService.get().getVendaAtiva().getItens());

        grdVenda_Item.setOnMenuItemClickListener(new AriusListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                if (index == 0)
                    Toast.makeText(getApplicationContext(),"botao 1",Toast.LENGTH_LONG).show();
                if (index == 1)
                    Toast.makeText(getApplicationContext(),"botao 2",Toast.LENGTH_LONG).show();
                return true;

            }
        });
//        grdVenda_Item.setAdapter(adapter_item);
//
//        grdVenda_Item.setSelection(grdVenda_Item.getAdapter().getCount()-1);

        final AriusCursorAdapter adapter = new AriusCursorAdapter(this,
                R.layout.layoutprodutoclassificacao,
                R.layout.layoutprodutoclassificacao,
                ImmutableMap.<Integer, String>of(R.id.edtCodProdClassGrid,"codigo",
                        R.id.edtDesProdClassGrid,"descricao"),
                lproduto);

        grdProd_Class.setAdapter(adapter);

        grdProd_Class.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (PdvService.get().getVendaAtiva() == null){
                    Venda vnd = new Venda();
                    vnd.setDataHora(new Date());
                    vnd.setSituacao(VendaSituacao.ABERTA);
                    vnd.setValorTroco(0);

                    PdvService.get().insereVenda(vnd);
                }

                Entity entity = (Entity) adapterView.getItemAtPosition(i);
                VendaItem vndItem = new VendaItem();
                vndItem.setVenda(PdvService.get().getVendaAtiva());
                vndItem.setProduto((Produto) entity);
                vndItem.setQtde(1);
                vndItem.setValorTotal(10);

                PdvService.get().insereVendaItem(vndItem);

                ((BaseAdapter) grdVenda_Item.getAdapter()).notifyDataSetChanged();
                String id = "Produto = " + String.valueOf(entity.getId());
                Toast.makeText(getBaseContext(), id , Toast.LENGTH_LONG).show();

                grdVenda_Item.setSelection(grdVenda_Item.getAdapter().getCount()-1);
            }
        });

    }

    private void instanciaBotoes(){
        btnFinalizar = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                ((BaseAdapter) grdVenda_Item.getAdapter()).notifyDataSetChanged();
//                //String id = "Produto = " + String.valueOf(entity.getId());
//                Toast.makeText(getBaseContext(), "Teste atualizar itens" , Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getBaseContext(), VendaFinalizacaoActivity.class);

                startActivity(intent);
            }
        };

        Button btnFinalizar = (Button) findViewById(R.id.btnVenda_Finalizar);
        btnFinalizar.setOnClickListener(this.btnFinalizar);
    }

}
