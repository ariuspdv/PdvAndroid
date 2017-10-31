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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import arius.pdv.base.Produto;
import arius.pdv.base.ProdutoCategoria;
import arius.pdv.base.ProdutoCategoriaDao;
import arius.pdv.base.ProdutoDao;
import arius.pdv.core.AppContext;
import arius.pdv.core.FuncionaisFilters;
import arius.pdv.db.AndroidUtils;
import arius.pdv.db.AriusCursorAdapter;

/**
 * Created by Arius on 06/10/2017.
 */

public class AriusActivityProdutoCategoria extends ActivityPadrao {

    private GridView grdProdCategoria;
    private Context context;
    private Button btnVoltar;
    private TextView edtNavegacao;
    private boolean produtosCarregados;
    private boolean precionadoVoltar = false;
    private ProdutoCategoria produtoCategoriaSelecionado;
    private TextView edttotalitem;
    private LinearLayout pnlValor;

    private PesquisaProdutoCategoria pesquisaProdutoCategoria;

    public interface PesquisaProdutoCategoria{
        void pesquisaProdutoCategoria(ProdutoCategoria produtoCategoria);
    }

    public void setPesquisaProdutoCategoria(PesquisaProdutoCategoria pesquisaProdutoCategoria) {
        this.pesquisaProdutoCategoria = pesquisaProdutoCategoria;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contentariusprodcategoria);

        montaCategorias(null, getApplicationContext());
    }

    public void montaCategorias(View view, Context context){
        this.context = context;

        if (view == null) {
            grdProdCategoria = (GridView) findViewById(R.id.grdProduto_Categoria);
            edtNavegacao = (TextView) findViewById(R.id.edtContentAriusCategoriaNavegacao);
            edttotalitem = (TextView) findViewById(R.id.edtlayoutItemVendaRodapeTotItem);
            pnlValor = (LinearLayout) findViewById(R.id.pnlContentAriusItemVendaRodapeValor);
        } else {
            grdProdCategoria = view.findViewById(R.id.grdProduto_Categoria);
            edtNavegacao = view.findViewById(R.id.edtContentAriusCategoriaNavegacao);
            edttotalitem = view.findViewById(R.id.edtlayoutItemVendaRodapeTotItem);
            pnlValor = view.findViewById(R.id.pnlContentAriusItemVendaRodapeValor);
        }

        edtNavegacao.setText("");

        pesquisaCategoria(0);

        /*Comando abaixo fixa a quantidade de categorias que será exibida em tela*/
        // comentado para ver o que irá fazer quando muda a orientação.
//        LinearLayout lteste = view.findViewById(R.id.frmProdCategoria);
//        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) lteste.getLayoutParams();
//        lp.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,65 + (90 * 0), this.context.getResources().getDisplayMetrics());
//        lteste.setLayoutParams(lp);

        produtoCategoriaSelecionado = null;

        grdProdCategoria.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (!produtosCarregados) {
                    produtoCategoriaSelecionado = (ProdutoCategoria) adapterView.getItemAtPosition(i);
                    pesquisaCategoria(produtoCategoriaSelecionado.getId());
                } else {
                    AriusActivityProdutoPrincipal ariusActivityProdutoPrincipal = new AriusActivityProdutoPrincipal();
                    ariusActivityProdutoPrincipal.setPnlValor(pnlValor);
                    ariusActivityProdutoPrincipal.setEdttotalitem(edttotalitem);
                    ariusActivityProdutoPrincipal.inserirItemVenda((Produto) adapterView.getItemAtPosition(i));
                }
            }
        });

        btnVoltar = view.findViewById(R.id.btnProCategoriaVoltar);
        btnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (produtoCategoriaSelecionado == null)
                    return;

                precionadoVoltar = true;

                edtNavegacao.setText(edtNavegacao.getText().toString().replace(" > " + produtoCategoriaSelecionado.getDescricao(),""));

                if (produtoCategoriaSelecionado.getProdutoCategoria() != null){
                    pesquisaCategoria(produtoCategoriaSelecionado.getProdutoCategoria().getId());
                    produtoCategoriaSelecionado = produtoCategoriaSelecionado.getProdutoCategoria();
                } else {
                    pesquisaCategoria(0);
                    produtoCategoriaSelecionado = null;
                    edtNavegacao.setText("");
                }
                if (pesquisaProdutoCategoria != null)
                    pesquisaProdutoCategoria.pesquisaProdutoCategoria(null);

                precionadoVoltar = false;
            }
        });
    }

    private void pesquisaCategoria(final long categoria_id){
        AriusCursorAdapter adapter_item;
        FuncionaisFilters<ProdutoCategoria> filterProdutoCategoria = new FuncionaisFilters<ProdutoCategoria>() {
            @Override
            public boolean test(ProdutoCategoria p) {
                if (categoria_id > 0 && p.getProdutoCategoria() != null)
                    return categoria_id == p.getProdutoCategoria().getId();
                else
                    return (categoria_id <= 0 ? p.getProdutoCategoria() == null : false);
            }
        };

        List<ProdutoCategoria> lprodutocategoria;

        lprodutocategoria = AppContext.get().getDao(ProdutoCategoriaDao.class).listCache(filterProdutoCategoria);
        Map<Integer, String> campos = new HashMap<>();

        if (lprodutocategoria.size() > 0) {
            if (produtoCategoriaSelecionado != null)
                if (!precionadoVoltar)
                    edtNavegacao.setText(edtNavegacao.getText() +
                        (edtNavegacao.getText().toString().equals("") ? "" : " > ") +  produtoCategoriaSelecionado.getDescricao());

            adapter_item = new AriusCursorAdapter(context,
                    R.layout.layoutprodcategoria,
                    R.layout.layoutprodcategoria,
                    campos,
                    lprodutocategoria);

            adapter_item.setMontarCamposTela(new AriusCursorAdapter.MontarCamposTela() {
                @Override
                public void montarCamposTela(Object p, View v) {
                    TextView edtaux = v.findViewById(R.id.edtlayoutProdCategoria);
                    if (edtaux != null)
                        edtaux.setText(((ProdutoCategoria) p).getDescricao());
                    ImageView imgaux = v.findViewById(R.id.imglayoutProdCategoria);

                    if(((ProdutoCategoria) p).getId() == 1) {
                        imgaux.setImageResource(R.mipmap.hamburger_icon);
                    } else if(((ProdutoCategoria) p).getId() == 2) {
                        imgaux.setImageResource(R.mipmap.drinks_icon);
                    } else if(((ProdutoCategoria) p).getId() == 3) {
                        imgaux.setImageResource(R.mipmap.dessert_icon);
                    } else if(((ProdutoCategoria) p).getId() == 4 ) {
                        imgaux.setImageResource(R.mipmap.no_alcohol_icon);
                    } else if(((ProdutoCategoria) p).getId() == 6) {
                        imgaux.setImageResource(R.mipmap.soda_icon);
                    } else if(((ProdutoCategoria) p).getId() == 7) {
                        imgaux.setImageResource(R.mipmap.juice_icon);
                    } else if(((ProdutoCategoria) p).getId() == 8) {
                        imgaux.setImageResource(R.mipmap.beer_icon);
                    } else if(((ProdutoCategoria) p).getId() == 9) {
                        imgaux.setImageResource(R.mipmap.wine_icon);
                    } else if(((ProdutoCategoria) p).getId() == 10 || ((ProdutoCategoria) p).getId() == 5 ) {
                        imgaux.setImageResource(R.mipmap.alcohol_icon);
                    } else {
                        imgaux.setImageResource(R.drawable.semimagem);
                    }


                }
            });

            grdProdCategoria.setAdapter(adapter_item);
            produtosCarregados = false;
        } else {
            FuncionaisFilters<Produto> filterProduto = new FuncionaisFilters<Produto>() {
                @Override
                public boolean test(Produto p) {
                    if (categoria_id == 0)
                        return p.getPrincipal();
                    else
                        return p.getProdutoCategoria() == null ? false : p.getProdutoCategoria().getId() == categoria_id;
                }
            };

            List<Produto> lproduto;

            lproduto = AppContext.get().getDao(ProdutoDao.class).listCache(filterProduto);

            if (lproduto.size() > 0) {

                if (produtoCategoriaSelecionado != null)
                    edtNavegacao.setText(edtNavegacao.getText() +
                            (edtNavegacao.getText().toString().equals("") ? "" : " > ") +  produtoCategoriaSelecionado.getDescricao());

                adapter_item = new AriusCursorAdapter(context,
                        R.layout.layoutprodcategoria,
                        R.layout.layoutprodcategoria,
                        campos,
                        lproduto);

                adapter_item.setMontarCamposTela(new AriusCursorAdapter.MontarCamposTela() {
                    @Override
                    public void montarCamposTela(Object p, View v) {
                        TextView edtaux = v.findViewById(R.id.edtlayoutProdCategoria);
                        if (edtaux != null)
                            edtaux.setText(((Produto) p).getDescricao());
                        ImageView imgaux = v.findViewById(R.id.imglayoutProdCategoria);

                        if(((Produto) p).getId() == 6 || ((Produto) p).getId() == 13) {
                            imgaux.setImageResource(R.mipmap.skol_icon);
                        } else if(((Produto) p).getId() == 7 || ((Produto) p).getId() == 14) {
                            imgaux.setImageResource(R.mipmap.brahma_icon);
                        } else if(((Produto) p).getId() == 8 || ((Produto) p).getId() == 15) {
                            imgaux.setImageResource(R.mipmap.antarctica_icon);
                        } else if(((Produto) p).getId() == 9 || ((Produto) p).getId() == 16) {
                            imgaux.setImageResource(R.mipmap.wine_icon);
                        } else if(((Produto) p).getId() == 10 || ((Produto) p).getId() == 17) {
                            imgaux.setImageResource(R.mipmap.cocktail_green_icon);
                        } else {
                            imgaux.setImageResource(R.drawable.semimagem);
                        }

                    }
                });

                grdProdCategoria.setAdapter(adapter_item);
                produtosCarregados = true;
            } else {
                AndroidUtils.toast(this.context,"Nenhum produto encontrado para a categoria!");
            }
//            if (pesquisaProdutoCategoria != null && pesquisaProdutoCategoria != null)
//                pesquisaProdutoCategoria.pesquisaProdutoCategoria(produtoCategoriaSelecionado);
//            produtoCategoriaSelecionado = produtoCategoriaSelecionado.getProdutoCategoria();
        }
    }
}
