package br.com.arius.pdvarius;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
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
import arius.pdv.db.AriusProdutoCategoriaNavigator;

/**
 * Created by Arius on 06/10/2017.
 */

public class AriusActivityProdutoCategoria extends ActivityPadrao {

    private GridView grdProdCategoria;
    private Context context;
    private Button btnVoltar;
    private boolean produtosCarregados;
    private ProdutoCategoria produtoCategoriaSelecionado;
    private TextView edttotalitem;
    private TextView edttotalvenda;
    private TextView lbProdutos;
    private TesteGroupNavigation pnlProdutoCategoriaNavigator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contentariusprodcategoria);

        montaCategorias(null, getApplicationContext());
    }

    public void montaCategorias(View view, final Context context){
        this.context = context;

        if (view == null) {
            grdProdCategoria = (GridView) findViewById(R.id.grdProduto_Categoria);
            edttotalitem = (TextView) findViewById(R.id.edtlayoutItemVendaRodapeTotItem);
            edttotalvenda = (TextView) findViewById(R.id.edtlayoutItemVendaRodapeValorVenda);
            lbProdutos = (TextView) findViewById(R.id.lbContentAriusCategoriaProduto);
            pnlProdutoCategoriaNavigator = (TesteGroupNavigation) findViewById(R.id.pnlProdutoCategoriaNavigator);
        } else {
            grdProdCategoria = view.findViewById(R.id.grdProduto_Categoria);
            edttotalitem = view.findViewById(R.id.edtlayoutItemVendaRodapeTotItem);
            edttotalvenda = view.findViewById(R.id.edtlayoutItemVendaRodapeValorVenda);
            lbProdutos = view.findViewById(R.id.lbContentAriusCategoriaProduto);
            pnlProdutoCategoriaNavigator = view.findViewById(R.id.pnlProdutoCategoriaNavigator);
        }

        pnlProdutoCategoriaNavigator.setItemNavigatorAcoes(new TesteGroupNavigation.ItemNavigatorAcoes() {
            @Override
            public void onClickItemNavigator(Object object) {
                pesquisaCategoria(((ProdutoCategoria) object).getId());
                if (lbProdutos.getVisibility() == View.VISIBLE)
                    lbProdutos.setVisibility(View.GONE);
            }
        });

        lbProdutos.setVisibility(View.GONE);

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

                    if (produtoCategoriaSelecionado != null)
                        pnlProdutoCategoriaNavigator.incluirItemNavegacao(produtoCategoriaSelecionado.getDescricao(),
                                produtoCategoriaSelecionado);
                } else {
                    AriusActivityProdutoPrincipal ariusActivityProdutoPrincipal = new AriusActivityProdutoPrincipal();
                    ariusActivityProdutoPrincipal.setEdttotalitem(edttotalitem);
                    ariusActivityProdutoPrincipal.setEdttotalvenda(edttotalvenda);
                    ariusActivityProdutoPrincipal.inserirItemVenda((Produto) adapterView.getItemAtPosition(i));

                    ((BaseAdapter) grdProdCategoria.getAdapter()).notifyDataSetChanged();
                }
            }
        });

        btnVoltar = view.findViewById(R.id.btnProCategoriaVoltar);
        btnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (produtoCategoriaSelecionado == null)
                    return;

                pnlProdutoCategoriaNavigator.limparItemNavigator();

                pesquisaCategoria(0);
                produtoCategoriaSelecionado = null;

                lbProdutos.setVisibility(View.GONE);
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

                    ConstraintLayout pnlQtde = v.findViewById(R.id.pnlTotalProdutoVenda);
                    pnlQtde.setVisibility(View.GONE);

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

                lbProdutos.setVisibility(View.VISIBLE);

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

                        ConstraintLayout pnlQtde = v.findViewById(R.id.pnlTotalProdutoVenda);
                        pnlQtde.setVisibility(View.VISIBLE);

                        edtaux =  v.findViewById(R.id.edtTotalProdutoVenda);
                        if(edtaux != null) {
                            AriusActivityProdutoPrincipal ariusActivityProdutoPrincipal = new AriusActivityProdutoPrincipal();
                            edtaux.setText(AndroidUtils.FormataQuantidade(((Produto) p),
                                    ariusActivityProdutoPrincipal.getQtdeProdutoVenda((Produto) p)));
                        }
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
                produtoCategoriaSelecionado = produtoCategoriaSelecionado.getProdutoCategoria();
                AndroidUtils.toast(this.context,"Nenhum produto encontrado para a categoria!");
            }
        }
    }
}
