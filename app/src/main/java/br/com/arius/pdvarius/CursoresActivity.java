package br.com.arius.pdvarius;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.common.collect.ImmutableMap;
import com.google.common.util.concurrent.Striped;

import java.util.List;
import java.util.Map;

import arius.pdv.base.Produto;
import arius.pdv.base.ProdutoDao;
import arius.pdv.core.AppContext;
import arius.pdv.core.Entity;
import arius.pdv.core.FuncionaisFilters;
import arius.pdv.db.AriusAutoCompleteTextView;
import arius.pdv.db.AriusCursorAdapter;

public class CursoresActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_cursores);


        FuncionaisFilters<Produto> filter = new FuncionaisFilters<Produto>() {
            @Override
            public boolean test(Produto p) {
                return true;
            }
        };

        List<Produto> lproduto = AppContext.get().getDao(ProdutoDao.class).listCache(filter);

        Map<Integer, String> teste = ImmutableMap.of(1,"rodrigo");

        //GridView grdProd = (GridView) findViewById(R.id.grdProdu);

//        final AriusCursorAdapter adapter = new AriusCursorAdapter(this,
//                R.layout.layoutprodutoclassificacao,
//                R.layout.layoutprodutoclassificacao,
//                ImmutableMap.<Integer, String>of(R.id.edtCodProdClassGrid,"codigo",
//                                   R.id.edtDesProdClassGrid,"descricao"),
//                lproduto);
//
//
//        grdProd.setAdapter(adapter);
//
//        grdProd.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                Entity entity = (Entity) adapterView.getItemAtPosition(i);
//                String id = "Produto = " + String.valueOf(entity.getId());
//                Toast.makeText(getBaseContext(), id , Toast.LENGTH_LONG).show();
//            }
//        });

    }
}
