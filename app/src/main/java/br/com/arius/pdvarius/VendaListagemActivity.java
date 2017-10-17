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
import arius.pdv.core.FuncionaisFilters;
import arius.pdv.db.AndroidUtils;
import arius.pdv.db.AriusCursorAdapter;
import arius.pdv.db.AriusListView;

public class VendaListagemActivity extends ActivityPadrao {

    private  AriusListView grdVendaListagem;
    private Spinner cmbVendaSituacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_venda_listagem);

        this.grdVendaListagem = (AriusListView) findViewById(R.id.grdVendaListagem);
        this.cmbVendaSituacao = (Spinner) findViewById(R.id.cmbVendaListagemSituacao);

        List vendaSituacaos = new ArrayList<>(EnumSet.allOf(VendaSituacao.class));

        vendaSituacaos.add("TODAS");

        grdVendaListagem.getAriusCursorAdapter().getFilter().filter(EnumSet.of(VendaSituacao.ABERTA).toString());

        final AriusCursorAdapter adapter = new AriusCursorAdapter(VendaListagemActivity.this,
                R.layout.layoutcmbbasico,
                R.layout.layoutcmbbasico,
                ImmutableMap.<Integer, String>of(R.id.lbcmbbasico,"Situacao"),
                vendaSituacaos);

        cmbVendaSituacao.setAdapter(adapter);
        ((AriusCursorAdapter) cmbVendaSituacao.getAdapter()).setMontarCamposTela(
                new AriusCursorAdapter.MontarCamposTela() {
                    @Override
                    public void montarCamposTela(Object p, View v) {
                        TextView edtaux = v.findViewById(R.id.lbcmbbasico);
                        if (edtaux != null)
                            edtaux.setText(p.toString());
                    }
                }
        );

        setButtons(true,false, false);

    }

    @Override
    protected void onStart() {
        super.onStart();

        progressBar(true);

        grdVendaListagem.getAriusCursorAdapter().setCampos_filtro(new String[]{"situacao"});
        grdVendaListagem.getAriusCursorAdapter().setExibirfiltrado_zerado(true);
        grdVendaListagem.getAriusCursorAdapter().setMontarCamposTela(
                new AriusCursorAdapter.MontarCamposTela() {
                    @Override
                    public void montarCamposTela(Object p, View v) {
                        Venda vnd = (Venda) p;
                        TextView edtaux = v.findViewById(R.id.edtVendaListagemID);
                        if (edtaux != null)
                            edtaux.setText(String.valueOf(vnd.getId()));
                        edtaux = v.findViewById(R.id.edtVendaListagemValor);
                        if (edtaux != null)
                            edtaux.setText(AndroidUtils.FormatarValor_Monetario(vnd.getValorLiquido()));
                        edtaux = v.findViewById(R.id.edtVendaListagemData);
                        if (edtaux != null)
                            edtaux.setText(PdvUtil.converteData_texto(vnd.getDataHora()));

                        ImageView imgStatusVenda = v.findViewById(R.id.imgVendaListagemSituacao);
                        imgStatusVenda.setImageResource(((Venda) p).getSituacao() == VendaSituacao.ABERTA ? R.mipmap.vendaaberta :
                                ((Venda) p).getSituacao() == VendaSituacao.FECHADA ? R.mipmap.vendafechada :
                                        ((Venda) p).getSituacao() == VendaSituacao.CANCELADA ? R.mipmap.vendacancelada : 0);

                    }
                }
        );


        cmbVendaSituacao.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                progressBar(true);
                final String situacao = adapterView.getItemAtPosition(i).toString();
                grdVendaListagem.getAriusCursorAdapter().getFilter().filter(situacao.equals("TODAS") ? "" : situacao.toString());
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
    }
}
