package br.com.arius.pdvarius;

import android.content.Context;
import android.os.Bundle;
import android.util.TypedValue;
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

import arius.pdv.base.ProdutoCategoria;
import arius.pdv.base.ProdutoCategoriaDao;
import arius.pdv.core.AppContext;
import arius.pdv.core.FuncionaisFilters;
import arius.pdv.db.AriusCursorAdapter;

/**
 * Created by Arius on 06/10/2017.
 */

public class AriusActivityProdutoCategoria extends ActivityPadrao {

    private GridView grdProdCategoria;
    private Context context;
    private Button btnVoltar;
    private ProdutoCategoria produtoCategoriaSelecionado;

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

        if (view == null)
            grdProdCategoria = (GridView) findViewById(R.id.grdProduto_Categoria);
        else
            grdProdCategoria = view.findViewById(R.id.grdProduto_Categoria);

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
                produtoCategoriaSelecionado = (ProdutoCategoria) adapterView.getItemAtPosition(i);
                pesquisaCategoria(produtoCategoriaSelecionado.getId());
            }
        });

        btnVoltar = view.findViewById(R.id.btnProCategoriaVoltar);
        btnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (produtoCategoriaSelecionado == null)
                    return;
                if (produtoCategoriaSelecionado.getProdutoCategoria() != null){
                    pesquisaCategoria(produtoCategoriaSelecionado.getProdutoCategoria().getId());
                    produtoCategoriaSelecionado = produtoCategoriaSelecionado.getProdutoCategoria();
                } else {
                    pesquisaCategoria(0);
                    produtoCategoriaSelecionado = null;
                }
                if (pesquisaProdutoCategoria != null)
                    pesquisaProdutoCategoria.pesquisaProdutoCategoria(null);
            }
        });
    }

    private void pesquisaCategoria(final long categoria_id){
        AriusCursorAdapter adapter_item = null;
        FuncionaisFilters<ProdutoCategoria> filter = new FuncionaisFilters<ProdutoCategoria>() {
            @Override
            public boolean test(ProdutoCategoria p) {
                if (categoria_id > 0 && p.getProdutoCategoria() != null)
                    return categoria_id == p.getProdutoCategoria().getId();
                else
                    return (categoria_id <= 0 ? p.getProdutoCategoria() == null : false);
            }
        };

        List<ProdutoCategoria> lprodutocategoria;

        lprodutocategoria = AppContext.get().getDao(ProdutoCategoriaDao.class).listCache(filter);
        Map<Integer, String> campos = new HashMap<>();
        campos.put(R.id.combobox_codigo,"produto.codigo");
        campos.put(R.id.combobox_descricao,"produto.descricao");

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
                    ImageView imgaux = v.findViewById(R.id.imglayoutProdCategoria);
                    imgaux.setImageResource(R.drawable.semimagem);
                }
            });

            grdProdCategoria.setAdapter(adapter_item);
        } else {
            if (pesquisaProdutoCategoria != null && pesquisaProdutoCategoria != null)
                pesquisaProdutoCategoria.pesquisaProdutoCategoria(produtoCategoriaSelecionado);
            produtoCategoriaSelecionado = produtoCategoriaSelecionado.getProdutoCategoria();
        }
    }
}
