package br.com.arius.pdvarius;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import arius.pdv.base.PdvDao;
import arius.pdv.base.PdvService;
import arius.pdv.base.PdvUtil;
import arius.pdv.base.Produto;
import arius.pdv.base.ProdutoCategoria;
import arius.pdv.base.ProdutoCategoriaDao;
import arius.pdv.base.ProdutoDao;
import arius.pdv.base.Venda;
import arius.pdv.base.VendaDao;
import arius.pdv.base.VendaItem;
import arius.pdv.base.VendaSituacao;
import arius.pdv.core.AppContext;
import arius.pdv.core.FuncionaisFilters;
import arius.pdv.db.AndroidUtils;
import arius.pdv.db.AriusAlertDialog;
import arius.pdv.db.AriusAutoCompleteTextView;
import arius.pdv.db.AriusCursorAdapter;

/**
 * Created by Arius on 16/10/2017.
 */

public class AriusActivityListagemVenda extends ActivityPadrao {

    private SimpleDateFormat dateFormatter;
    private int mYear, mMonth, mDay;
    private boolean cancelDatePichker = false;
    private boolean focusDataInicio = false;
    private EditText dtInicio;
    private EditText dtFim;
    private Calendar newDate;
    private ListView grdListagemVenda;
    private Button btnPesquisar;
    private ImageButton btnFiltros;
    private TabHost host;
    private Date dataInicio;
    private Date dataFim;

    private ProdutoCategoria produtoCategoria;
    private Produto produto;
    private String situacao = null;

    private AriusAutoCompleteTextView cmbCategoria;
    private AriusAutoCompleteTextView cmbProduto;
    private Spinner cmbSituacao;
    private LinearLayout pnlVendasStatus;
    private AlertDialog dialogFiltro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contentariuslistagemvenda);

        setPesquisaVenda(false);
        setButtons(true, false, false);

        grdListagemVenda = (ListView) findViewById(R.id.grdListagemVenda);
        btnPesquisar = (Button) findViewById(R.id.btnListagemVendaPesquisar);
        pnlVendasStatus = (LinearLayout) findViewById(R.id.pnlContentAriusListagemVendaLegendaVendas);
        pnlVendasStatus.setVisibility(View.GONE);

        btnPesquisar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pesquisaVendas();
            }
        });

        btnFiltros = (ImageButton) findViewById(R.id.btnListagemVendaFiltro);
        btnFiltros.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AriusAlertDialog.exibirDialog(AriusActivityListagemVenda.this,R.layout.dialog_arius_listagemvenda_filtros,false);
                dialogFiltro = AriusAlertDialog.getAlertDialog();
                dialogFiltro.show();
                carregaSituacoes();
                carregaProdutos();
                carregaCategorias(0);

                dialogFiltro.findViewById(R.id.btnlayoutDialogListagemVendaCancelar).setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialogFiltro.dismiss();
                            }
                        }
                );

                dialogFiltro.findViewById(R.id.btnlayoutDialogListagemVendaConfirmar).setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (cmbSituacao.getSelectedItem().toString().equals(""))
                                    situacao = null;
                                if (cmbCategoria.getText().toString().equals(""))
                                    produtoCategoria = null;
                                if (cmbProduto.getText().toString().equals(""))
                                    produto = null;
                                dialogFiltro.dismiss();
                                grdListagemVenda.setAdapter(null);
                            }
                        }
                );
            }
        });

        host = (TabHost)findViewById(R.id.tabhost);
        host.setup();

//        //Tab 1
        TabHost.TabSpec spec = host.newTabSpec("Produto");
        spec.setContent(R.id.tbsListagemVendaProduto);
        spec.setIndicator("Produtos");
        host.addTab(spec);

        //Tab 2
        spec = host.newTabSpec("Venda");
        spec.setContent(R.id.tbsListagemVendaVenda);
        spec.setIndicator("Vendas");
        host.addTab(spec);

        carregaCamposData();

        host.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String s) {
                if(s.toLowerCase().equals("venda"))
                    pnlVendasStatus.setVisibility(View.VISIBLE);
                else
                    pnlVendasStatus.setVisibility(View.GONE);
                grdListagemVenda.setAdapter(null);
            }
        });

        host.setCurrentTab(0);

        grdListagemVenda.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Venda entity = (Venda) adapterView.getItemAtPosition(i);
                PdvService.get().getPdv().setVendaAtiva(entity);
                AppContext.get().getDao(PdvDao.class).update(PdvService.get().getPdv());

                onBackPressed();

                setPesquisaVenda(true);
            }
        });
    }

    private void carregaCamposData(){
        dateFormatter = new SimpleDateFormat("dd/MM/yyyy", Locale.US);

        dtInicio = (EditText) findViewById(R.id.edtcontentariusListagemVendaDataInicio);
        dtFim = (EditText) findViewById(R.id.edtcontentariusListagemVendaDataFim);

        newDate = Calendar.getInstance(TimeZone.getDefault());

        newDate.set(newDate.get(Calendar.YEAR), newDate.get(Calendar.MONTH), newDate.get(Calendar.DAY_OF_MONTH));
        dtInicio.setText(dateFormatter.format(newDate.getTime()));
        dtFim.setText(dateFormatter.format(newDate.getTime()));
        /*O comando abaixo coloca o campo em readonly*/
        dtInicio.setKeyListener(null);
        dtFim.setKeyListener(null);
        dataInicio = newDate.getTime();
        dataFim = newDate.getTime();

        dtInicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                focusDataInicio = true;
                criarDialogDataPicker();
            }
        });

        dtFim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                focusDataInicio = false;
                criarDialogDataPicker();
            }
        });
    }

    private void criarDialogDataPicker(){
        cancelDatePichker = false;
        try {
            newDate.setTime(dateFormatter.parse(dtInicio.getText().toString()));
            mYear = newDate.get(Calendar.YEAR);
            mMonth = newDate.get(Calendar.MONTH);
            mDay = newDate.get(Calendar.DAY_OF_MONTH);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        final DatePickerDialog datePickerDialog = new DatePickerDialog(AriusActivityListagemVenda.this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        if (!cancelDatePichker) {
                            if (focusDataInicio) {
                                dtInicio.setText(String.valueOf(dayOfMonth) + "/" +
                                        String.valueOf(monthOfYear + 1) + "/" +
                                        String.valueOf(year));
                                newDate.set(year,monthOfYear,dayOfMonth);
                                dataInicio = newDate.getTime();
                            } else {
                                dtFim.setText(String.valueOf(dayOfMonth) + "/" +
                                        String.valueOf(monthOfYear + 1) + "/" +
                                        String.valueOf(year));
                                newDate.set(year,monthOfYear,dayOfMonth);
                                dataFim = newDate.getTime();
                            }
                            grdListagemVenda.setAdapter(null);
                        }
                    }
                },mYear, mMonth, mDay);

        // Create a TextView programmatically.
        TextView tv = new TextView(datePickerDialog.getContext());

        // Create a TextView programmatically
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, // Width of TextView
                RelativeLayout.LayoutParams.WRAP_CONTENT); // Height of TextView
        tv.setLayoutParams(lp);
        tv.setPadding(10, 10, 10, 10);
        tv.setGravity(Gravity.CENTER);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP,20);
        if (focusDataInicio)
            tv.setText("Data Inicio");
        else
            tv.setText("Data Fim");
        tv.setTextColor(Color.parseColor("#ffffff"));
        tv.setBackgroundColor(Color.parseColor("#0e4777"));
        tv.setTextSize(30);
        datePickerDialog.setCustomTitle(tv);
        datePickerDialog.getDatePicker().setSpinnersShown(false);

        datePickerDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Confirmar", datePickerDialog);

        datePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                cancelDatePichker = true;
                datePickerDialog.dismiss();
            }
        });

        datePickerDialog.show();
    }

    private void carregaCategorias(final long categoria_id){
        AriusCursorAdapter adapter_categoria = new AriusCursorAdapter(getApplicationContext(),
                R.layout.layoutcmbbasico,
                android.R.layout.simple_dropdown_item_1line,
                null,
                AppContext.get().getDao(ProdutoCategoriaDao.class).listCache(new FuncionaisFilters<ProdutoCategoria>() {
                    @Override
                    public boolean test(ProdutoCategoria p) {
                        return categoria_id == 0 ?  p.getProdutoCategoria() == null : p.getProdutoCategoria().getId() == categoria_id;
                    }
                }));

        adapter_categoria.setMontarCamposTela(new AriusCursorAdapter.MontarCamposTela() {
            @Override
            public void montarCamposTela(Object p, View v) {
                produtoCategoria = (ProdutoCategoria) p;
                TextView campoaux = v.findViewById(R.id.lbcmbbasico);
                if (campoaux != null){
                    campoaux.setPadding(10, 10, 10, 10);
                    campoaux.setText(produtoCategoria.getDescricao());
                    campoaux.setTextColor(Color.parseColor("#000000"));
                }

                campoaux = v.findViewById(android.R.id.text1);
                if (campoaux != null){
                    campoaux.setText(produtoCategoria.getDescricao());
                    campoaux.setTextColor(Color.parseColor("#000000"));
                }

            }
        });

        adapter_categoria.setFiltros(new AriusCursorAdapter.Filtros() {
            @Override
            public boolean filtroCampos(Object object, CharSequence texto) {
                return ((ProdutoCategoria) object).getDescricao().toLowerCase().contains(texto.toString().toLowerCase());
            }

            @Override
            public String exibircampoSelecionado(Object object) {
                return ((ProdutoCategoria) object).getDescricao();
            }
        });

        cmbCategoria = (AriusAutoCompleteTextView) dialogFiltro.findViewById(R.id.cbmListageVendaCategoria);

        cmbCategoria.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                produtoCategoria = (ProdutoCategoria) parent.getItemAtPosition(position);
            }
        });

        adapter_categoria.setExibirfiltrado_zerado(true);
        cmbCategoria.setAdapter(adapter_categoria);

        if (produtoCategoria != null)
            cmbCategoria.setText(produtoCategoria.getDescricao());

    }

    private void carregaProdutos(){
        AriusCursorAdapter adapter_produto = new AriusCursorAdapter(getApplicationContext(),
                R.layout.layoutcmbbasico,
                android.R.layout.simple_dropdown_item_1line,
                null,
                AppContext.get().getDao(ProdutoDao.class).listCache(new FuncionaisFilters<Produto>() {
                    @Override
                    public boolean test(Produto p) {
                        return true;
                    }
                }));

        adapter_produto.setMontarCamposTela(new AriusCursorAdapter.MontarCamposTela() {
            @Override
            public void montarCamposTela(Object p, View v) {
                Produto produto = (Produto) p;
                TextView campoaux = v.findViewById(R.id.lbcmbbasico);

                if (campoaux != null){
                    campoaux.setPadding(10, 10, 10, 10);
                    campoaux.setText(produto.getDescricao());
                    campoaux.setTextColor(Color.parseColor("#000000"));
                }

                campoaux = v.findViewById(android.R.id.text1);
                if (campoaux != null){
                    campoaux.setText(produto.getDescricao());
                    campoaux.setTextColor(Color.parseColor("#000000"));
                }
            }
        });

        adapter_produto.setFiltros(new AriusCursorAdapter.Filtros() {
            @Override
            public boolean filtroCampos(Object object, CharSequence texto) {
                return ((Produto) object ).getDescricaoReduzida().toLowerCase().contains(texto.toString().toLowerCase()) ||
                        ((Produto) object ).getDescricao().toLowerCase().contains(texto.toString().toLowerCase()) ||
                        String.valueOf(((Produto) object ).getCodigo()).toLowerCase().contains(texto.toString().toLowerCase());
            }

            @Override
            public String exibircampoSelecionado(Object object) {
                Produto produto = (Produto) object;
                return (produto.getDescricaoReduzida().equals("") ? produto.getDescricao() : produto.getDescricaoReduzida());
            }
        });

        cmbProduto = (AriusAutoCompleteTextView) dialogFiltro.findViewById(R.id.cbmListageVendaProduto);

        cmbProduto.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                produto = (Produto) parent.getItemAtPosition(position);
            }
        });

        adapter_produto.setExibirfiltrado_zerado(true);
        cmbProduto.setAdapter(adapter_produto);

        if (produto != null)
            cmbProduto.setText(produto.getDescricao());
    }

    private void carregaSituacoes(){
        List list_situacao = new ArrayList<>();

        list_situacao.add("TODAS");
        list_situacao.addAll(EnumSet.allOf(VendaSituacao.class));

        final AriusCursorAdapter adapter_situacao = new AriusCursorAdapter(getApplicationContext(),
                R.layout.layoutcmbbasico,
                android.R.layout.simple_dropdown_item_1line,
                null,
                list_situacao);

        adapter_situacao.setMontarCamposTela(new AriusCursorAdapter.MontarCamposTela() {
            @Override
            public void montarCamposTela(Object p, View v) {
                String sitacao = String.valueOf(p);
                TextView campoaux = v.findViewById(R.id.lbcmbbasico);
                if (campoaux != null){
                    campoaux.setPadding(10, 10, 10, 10);
                    campoaux.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.dropdown, 0);
                    campoaux.setText(sitacao);
                    campoaux.setTextColor(Color.parseColor("#000000"));
                }

                campoaux = v.findViewById(android.R.id.text1);
                if (campoaux != null){
                    campoaux.setText(sitacao);
                    campoaux.setTextColor(Color.parseColor("#000000"));
                }


            }
        });

        cmbSituacao = (Spinner) dialogFiltro.findViewById(R.id.cbmListageVendaSituacao);
        cmbSituacao.setAdapter(adapter_situacao);

        cmbSituacao.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (!adapterView.getItemAtPosition(i).toString().equals("TODAS"))
                    situacao = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void pesquisaVendas(){
        progressBar(true);
        final List vendas = AppContext.get().getDao(VendaDao.class).listCache(new FuncionaisFilters<Venda>() {
            @Override
            public boolean test(Venda p) {
                if (PdvUtil.entre_Datas(p.getDataHora(),dataInicio,dataFim) &&
                        (situacao == null || p.getSituacao().toString().toLowerCase().equals(situacao.toLowerCase()))){
                    if (produtoCategoria == null && produto == null)
                        return true;
                    else{
                        for(VendaItem loopItens : p.getItens()){
                            if (produto != null && loopItens.getProduto().getId() == produto.getId())
                                return true;
                            if (produtoCategoria !=  null && loopItens.getProduto().getProdutoCategoria().equals(produtoCategoria))
                                return true;
                        }
                    }

                }
                return false;
            }
        });

        AriusCursorAdapter adapter_vendas = null;

        if (host.getCurrentTab() == 1) {

            adapter_vendas = new AriusCursorAdapter(getAppContext(),
                    R.layout.layoutvendalistagemvenda,
                    R.layout.layoutvendalistagemvenda,
                    null,
                    vendas);

            adapter_vendas.setMontarCamposTela(new AriusCursorAdapter.MontarCamposTela() {
                @Override
                public void montarCamposTela(Object p, View v) {
                    Venda venda = (Venda) p;
                    TextView campoaux = v.findViewById(R.id.edtVendaListagemID);
                    if (campoaux != null)
                        campoaux.setText(String.valueOf(venda.getId()));
                    campoaux = v.findViewById(R.id.edtVendaListagemValor);
                    if (campoaux != null)
                        campoaux.setText(AndroidUtils.FormatarValor_Monetario(venda.getValorLiquido()));
                    campoaux = v.findViewById(R.id.edtVendaListagemData);
                    if (campoaux != null)
                        campoaux.setText(PdvUtil.converteData_texto(venda.getDataHora()));

                    ImageView imgStatusVenda = v.findViewById(R.id.imgVendaListagemSituacao);
                    imgStatusVenda.setImageResource(((Venda) p).getSituacao() == VendaSituacao.ABERTA ? R.mipmap.edit :
                            ((Venda) p).getSituacao() == VendaSituacao.FECHADA ? R.mipmap.correct :
                                    ((Venda) p).getSituacao() == VendaSituacao.CANCELADA ? R.mipmap.cancel : 0);

                }
            });

        } else {
            class rel{
                private Produto produto;
                private double qtde;
                private double valor;

                private List vendaItens = new ArrayList();

                public List getVendaItens() {
                    return vendaItens;
                }

                private void insertItem(Produto produto, double qtde, double valor){
                    rel linha = new rel();
                    if (vendaItens.size() == 0){
                        linha.produto = produto;
                        linha.qtde = qtde;
                        linha.valor = valor;
                        vendaItens.add(linha);
                    } else {
                        for (Object loopRel : vendaItens) {
                            if (((rel) loopRel).produto.getId() == produto.getId()){
                                ((rel) loopRel).qtde = ((rel) loopRel).qtde + qtde;
                                ((rel) loopRel).valor = ((rel) loopRel).valor + valor;
                                break;
                            } else {
                                linha.produto = produto;
                                linha.qtde = qtde;
                                linha.valor = valor;
                                vendaItens.add(linha);
                                break;
                            }
                        }
                    }
                }
            }

            rel vendaItens = new rel();
            for(Object loopVendas : vendas){
                for(VendaItem loopItens : ((Venda) loopVendas).getItens()){
                    if (produto == null || loopItens.getProduto().getId() == produto.getId())
                        vendaItens.insertItem(loopItens.getProduto(),
                                loopItens.getQtde(), loopItens.getValorLiquido());
                    else
                        if (produtoCategoria == null ||
                                    loopItens.getProduto().getProdutoCategoria().getId() == produtoCategoria.getId())
                                vendaItens.insertItem(loopItens.getProduto(),
                                        loopItens.getQtde(), loopItens.getValorLiquido());
                }
            }

            adapter_vendas = new AriusCursorAdapter(getAppContext(),
                    R.layout.layoutvendalistagemproduto,
                    R.layout.layoutvendalistagemproduto,
                    null,
                    vendaItens.getVendaItens());

            adapter_vendas.setMontarCamposTela(new AriusCursorAdapter.MontarCamposTela() {
                @Override
                public void montarCamposTela(Object p, View v) {
                    rel venda = (rel) p;
                    TextView campoaux = v.findViewById(R.id.edtVendaListagemID);
                    if (campoaux != null) {
                        campoaux.setText(venda.produto.getDescricao());
                    }
                    campoaux = v.findViewById(R.id.edtVendaListagemValor);
                    if (campoaux != null) {
                        campoaux.setText(AndroidUtils.FormataQuantidade(venda.produto, venda.qtde));
                    }
                    campoaux = v.findViewById(R.id.edtVendaListagemData);
                    if (campoaux != null) {
                        campoaux.setText(AndroidUtils.FormatarValor_Monetario(venda.valor));
                    }
                    ImageView imgStatusVenda = v.findViewById(R.id.imgVendaListagemSituacao);
                    imgStatusVenda.setVisibility(View.GONE);
                }
            });

        }

        grdListagemVenda.setAdapter(adapter_vendas);

        progressBar(false);
    }
}
