package br.com.arius.pdvarius;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import arius.pdv.base.PdvDao;
import arius.pdv.base.PdvService;
import arius.pdv.base.Produto;
import arius.pdv.base.ProdutoCategoria;
import arius.pdv.base.ProdutoDao;
import arius.pdv.base.Venda;
import arius.pdv.base.VendaItem;
import arius.pdv.base.VendaSituacao;
import arius.pdv.core.AppContext;
import arius.pdv.core.FuncionaisFilters;
import arius.pdv.db.AndroidUtils;
import arius.pdv.db.AriusCursorAdapter;

/**
 * Created by Arius on 09/10/2017.
 */

public class AriusActivityProdutoPrincipal extends ActivityPadrao {

    private GridView grdProdPrincipal;
    private Context context;
    private TextView edttotalitem;
    private LinearLayout pnlValor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contentariusprodprincipal);

        montaPrincipal(null, getApplicationContext());

    }

    public void setPnlValor(LinearLayout pnlValor) {
        this.pnlValor = pnlValor;
    }

    public void setEdttotalitem(TextView edttotalitem) {
        this.edttotalitem = edttotalitem;
    }

    public void montaPrincipal(View view, Context context){
        this.context = context;

        if (view == null) {
            grdProdPrincipal = (GridView) findViewById(R.id.grdProduto_Principal);
            edttotalitem = (TextView) findViewById(R.id.edtlayoutItemVendaRodapeTotItem);
            pnlValor = (LinearLayout) findViewById(R.id.pnlContentAriusItemVendaRodapeValor);
        }
        else {
            grdProdPrincipal = view.findViewById(R.id.grdProduto_Principal);
            edttotalitem = view.findViewById(R.id.edtlayoutItemVendaRodapeTotItem);
            pnlValor = view.findViewById(R.id.pnlContentAriusItemVendaRodapeValor);
        }

        pesquisaPrincipa(0);

        grdProdPrincipal.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                inserirItemVenda((Produto) adapterView.getItemAtPosition(i));
            }
        });
    }

    public void pesquisaPrincipa(final long categoria_id){
        AriusCursorAdapter adapter_item;
        FuncionaisFilters<Produto> filter = new FuncionaisFilters<Produto>() {
            @Override
            public boolean test(Produto p) {
                if (categoria_id == 0)
                    return p.getPrincipal();
                else
                    return p.getProdutoCategoria() == null ? false : p.getProdutoCategoria().getId() == categoria_id;
            }
        };

        List<Produto> lproduto;

        lproduto = AppContext.get().getDao(ProdutoDao.class).listCache(filter);
        Map<Integer, String> campos = new HashMap<>();

        if (lproduto.size() > 0) {
            adapter_item = new AriusCursorAdapter(context,
                    R.layout.layoutprodprincipal,
                    R.layout.layoutprodprincipal,
                    campos,
                    lproduto);

            adapter_item.setMontarCamposTela(new AriusCursorAdapter.MontarCamposTela() {
                @Override
                public void montarCamposTela(Object p, View v) {
                    TextView edtaux = v.findViewById(R.id.edtlayoutProdPrincipal);
                    if (edtaux != null)
                        edtaux.setText(((Produto) p).getDescricao());
                    ImageView imgaux = v.findViewById(R.id.imglayoutProdPrincipal);
                    imgaux.setImageResource(R.drawable.semimagem);
                }
            });

            grdProdPrincipal.setAdapter(adapter_item);
        } else{
            if (categoria_id == 0)
                AndroidUtils.toast(this.context,"Nenhum produto encontrado como principal!");
            else
                AndroidUtils.toast(this.context,"Nenhum produto encontrado para a categoria!");
        }

        montaRodape();

    }

    public void inserirItemVenda(Produto produto){
        if (PdvService.get().getVendaAtiva() == null){
            Venda vnd = new Venda();
            vnd.setDataHora(new Date());
            vnd.setSituacao(VendaSituacao.ABERTA);
            vnd.setValorTroco(0);

            PdvService.get().insereVenda(vnd);

            PdvService.get().getPdv().setVendaAtiva(vnd);
            AppContext.get().getDao(PdvDao.class).update(PdvService.get().getPdv());
        }

        VendaItem vndItem = new VendaItem();
        vndItem.setVenda(PdvService.get().getVendaAtiva());
        vndItem.setProduto(produto);
        vndItem.setQtde(1);
        vndItem.setValorTotal(10);

        PdvService.get().insereVendaItem(vndItem);

        if (this.context == null)
            this.context = getAppContext();

        montaRodape();

        AndroidUtils.toast(this.context,vndItem.getProduto().getDescricao() + " \n Incluido na venda!");

        progressBar(false);
    }

    private void montaRodape(){
        if (pnlValor != null)
            if (pnlValor.getVisibility() == View.VISIBLE)
                    pnlValor.setVisibility(View.GONE);
        if (edttotalitem != null) {
            edttotalitem.setText("0");
            if (PdvService.get().getVendaAtiva() != null) {
                edttotalitem.setText(String.valueOf(PdvService.get().getVendaAtiva().getItens().size()));
            }
        }
    }

}
