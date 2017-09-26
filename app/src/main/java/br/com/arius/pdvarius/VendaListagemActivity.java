package br.com.arius.pdvarius;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.common.collect.ImmutableMap;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import arius.pdv.base.PdvDao;
import arius.pdv.base.PdvService;
import arius.pdv.base.PdvUtil;
import arius.pdv.base.Venda;
import arius.pdv.base.VendaDao;
import arius.pdv.base.VendaSituacao;
import arius.pdv.core.AppContext;
import arius.pdv.core.Entity;
import arius.pdv.core.FuncionaisFilters;
import arius.pdv.core.FuncionaisTela;
import arius.pdv.db.AndroidUtils;
import arius.pdv.db.AriusCursorAdapter;
import arius.pdv.db.AriusListView;

public class VendaListagemActivity extends ActivityPadrao {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_venda_listagem);

        setButtons(true,false, false);
    }

    @Override
    protected void onStart() {
        super.onStart();

        progressBar(true);

        final AriusListView grdVendaListagem = (AriusListView) findViewById(R.id.grdVendaListagem);

        List vendaSituacaos = new ArrayList<>(EnumSet.allOf(VendaSituacao.class));

        vendaSituacaos.add("TODAS");

        final AriusCursorAdapter adapter = new AriusCursorAdapter(VendaListagemActivity.this,
                R.layout.layoutcmbbasico,
                R.layout.layoutcmbbasico,
                ImmutableMap.<Integer, String>of(R.id.lbcmbbasico,"Situacao"),
                vendaSituacaos);

        ((AriusCursorAdapter) grdVendaListagem.getAdapter()).setCampos_filtro(new String[]{"situacao"});
        ((AriusCursorAdapter) grdVendaListagem.getAdapter()).setExibirfiltrado_zerado(true);

        Spinner cmbVendaSituacao = (Spinner) findViewById(R.id.cmbVendaListagemSituacao);

        cmbVendaSituacao.setAdapter(adapter);

        cmbVendaSituacao.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                progressBar(true);
                final String situacao = adapterView.getItemAtPosition(i).toString();
                ((AriusCursorAdapter) grdVendaListagem.getAdapter()).getFilter().filter(situacao.equals("TODAS") ? "" : situacao.toString());
                FuncionaisFilters<Venda> filterVenda = new FuncionaisFilters<Venda>() {
                    @Override
                    public boolean test(Venda p) {
                        return situacao.equals("TODAS") ? true : p.getSituacao().equals(situacao);
                    }
                };
                AppContext.get().getDao(VendaDao.class).listCache(filterVenda);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        grdVendaListagem.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Venda entity = (Venda) adapterView.getItemAtPosition(i);
                PdvService.get().getPdv().setVendaAtiva(entity);
                AppContext.get().getDao(PdvDao.class).update(PdvService.get().getPdv());

                onBackPressed();
            }
        });

        ((AriusCursorAdapter) grdVendaListagem.getAdapter()).setMontatela(new FuncionaisTela() {
            @Override
            public void test(Entity p, View v) {
                TextView edtAux = (TextView) v.findViewById(R.id.edtVendaListagemID);
                edtAux.setText(String.valueOf(((Venda) p).getId()));

                edtAux = (TextView) v.findViewById(R.id.edtVendaListagemValor);
                edtAux.setText(AndroidUtils.FormatarValor_Monetario(((Venda) p).getValorLiquido()));

                edtAux = (TextView) v.findViewById(R.id.edtVendaListagemData);
                edtAux.setText(PdvUtil.converteData_texto(((Venda) p).getDataHora()));

                ImageView imgStatusVenda = (ImageView) v.findViewById(R.id.imgVendaListagemSituacao);
                imgStatusVenda.setImageResource(((Venda) p).getSituacao() == VendaSituacao.ABERTA ? R.mipmap.vendaaberta :
                        ((Venda) p).getSituacao() == VendaSituacao.FECHADA ? R.mipmap.vendafechada :
                                ((Venda) p).getSituacao() == VendaSituacao.CANCELADA ? R.mipmap.vendacancelada : 0);
            }
        });
    }
}
