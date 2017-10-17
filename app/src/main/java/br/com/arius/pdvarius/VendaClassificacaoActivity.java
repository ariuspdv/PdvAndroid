package br.com.arius.pdvarius;

import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.collect.ImmutableMap;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import SwipeListView.SwipeMenu;
import arius.pdv.base.Finalizadora;
import arius.pdv.base.FinalizadoraDao;
import arius.pdv.base.PdvDao;
import arius.pdv.base.PdvService;
import arius.pdv.base.Produto;
import arius.pdv.base.ProdutoDao;
import arius.pdv.base.Venda;
import arius.pdv.base.VendaDao;
import arius.pdv.base.VendaItem;
import arius.pdv.base.VendaSituacao;
import arius.pdv.core.AppContext;
import arius.pdv.core.FuncionaisFilters;
import arius.pdv.core.UserException;
import arius.pdv.db.AndroidUtils;
import arius.pdv.db.AriusAlertDialog;
import arius.pdv.db.AriusAutoCompleteTextView;
import arius.pdv.db.AriusCursorAdapter;
import arius.pdv.db.AriusListView;

public class VendaClassificacaoActivity extends ActivityPadrao
            implements AriusCursorAdapter.MontarCamposTela, AriusCursorAdapter.AfterDataControll {

    private View.OnClickListener btnFinalizar;
    private AriusListView grdVenda_Item;
    private TextView edtVendaItemTotal;
    private Produto produto;
    private AriusAutoCompleteTextView cmbPesqProduto;

    private void preparaTela(){
        totalItens();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_venda_classificacao);

        this.edtVendaItemTotal = (TextView) findViewById(R.id.edtVendaItemTotal);

        setButtons(false, true, true);

        instanciaBotoes();

        ConstraintLayout pnlCmbPesqProduto = (ConstraintLayout) findViewById(R.id.pnlCmbPesqProduto);
        final ConstraintLayout pnlVendaItens = (ConstraintLayout) findViewById(R.id.pnlVendaItens);

        final Button btnSplitter = (Button) findViewById(R.id.btnSplitter_ProdClass);
        this.grdVenda_Item = (AriusListView) findViewById(R.id.grdVenda_Item);
        final GridView grdProd_Class = (GridView) findViewById(R.id.grdProduto_Classificacao);
        this.cmbPesqProduto = (AriusAutoCompleteTextView) findViewById(R.id.cmbPesqProduto);

        preparaTela();

        pnlCmbPesqProduto.setVisibility(View.VISIBLE);

        btnSplitter.setVisibility(View.GONE);
        grdProd_Class.setVisibility(View.GONE);

//        pnlCmbPesqProduto.setVisibility(View.GONE);
//
//        btnSplitter.setVisibility(View.VISIBLE);
//        grdProd_Class.setVisibility(View.VISIBLE);

        RelativeLayout.LayoutParams layoutParams =
                (RelativeLayout.LayoutParams) pnlVendaItens.getLayoutParams();
        layoutParams.removeRule(RelativeLayout.BELOW);
        layoutParams.removeRule(RelativeLayout.ABOVE);
        layoutParams.removeRule(RelativeLayout.ALIGN_PARENT_TOP);
        //layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP); //para funcionar por classificacao habilitar esse
        layoutParams.addRule(RelativeLayout.ABOVE);//, R.id.btnSplitter_ProdClass);
        layoutParams.addRule(RelativeLayout.BELOW, R.id.pnlCmbPesqProduto); //para funcionar por classificacao dehabilitar esse
        layoutParams.addRule(RelativeLayout.ABOVE, R.id.pnlTotal_Items); //para funcionar por classificacao dehabilitar esse
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
                    if (grdVenda_Item.getAriusCursorAdapter() != null) {
                        if (grdVenda_Item.getAriusCursorAdapter().getCount() > 3) {
                            grdVenda_Item.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
                            grdVenda_Item.setStackFromBottom(true);
                        }
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
                ImmutableMap.<Integer, String>of(R.id.combobox_codigo,"produto.codigo",
                        R.id.combobox_descricao,"descricao"),
                lproduto);

        //exemplo criação de botões no listview
//        SwipeMenuItem goodItem = new SwipeMenuItem(
//                getApplicationContext());
//        // set item background
//        goodItem.setBackground(new ColorDrawable(Color.rgb(0x30, 0xB1,
//                0xF5)));
//        // set item width
//        goodItem.setWidth(300);
//        // set a icon
//        goodItem.setIcon(R.drawable.if_circle_green);
//        // add to menu        SwipeMenuItem goodItem = new SwipeMenuItem(
//                getApplicationContext());
//        // set item background
//        goodItem.setBackground(new ColorDrawable(Color.rgb(0x30, 0xB1,
//                0xF5)));
//        // set item width
//        goodItem.setWidth(300);
//        // set a icon
//        goodItem.setIcon(R.drawable.if_circle_green);
//        // add to menu
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
//        grdVenda_Item.getMenu().addMenuItem(goodItem);

//        grdVenda_Item.setCampos_Exibir(ImmutableMap.<Integer, String>of(android.R.id.text1,"descricao"));
//        grdVenda_Item.setSwipe_Delete(true);
//        grdVenda_Item.setEntity_listview("Produto");
//        grdVenda_Item.setLayout_arius(android.R.layout.simple_spinner_item);

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

        final AriusCursorAdapter adapter = new AriusCursorAdapter(this,
                R.layout.layoutprodutoclassificacao,
                R.layout.cmbtextcombo,
                ImmutableMap.<Integer, String>of(R.id.edtCodProdClassGrid,"produto.codigo",
                        R.id.edtDesProdClassGrid,"produto.descricao"),
                lproduto);

        grdProd_Class.setAdapter(adapter);

        adapter.setCampos_filtro(new String[]{"produto.codigo","produto.descricao"});
        adapter.setExibirfiltrado_zerado(true);

        cmbPesqProduto.setAdapter(adapter);

        grdProd_Class.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                inserirProduto(adapterView, i);
            }
        });

        grdVenda_Item.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                final VendaItem vendaItem = (VendaItem) adapterView.getItemAtPosition(i);

                if (vendaItem.getVenda().getSituacao() != VendaSituacao.ABERTA)
                    return;

                AriusAlertDialog.exibirDialog(VendaClassificacaoActivity.this, R.layout.content_dialog_vendaitem);

                final EditText edtQtde = AriusAlertDialog.getGetView().findViewById(R.id.edtVendaItemDialogQtde);
                final EditText edtDesc = AriusAlertDialog.getGetView().findViewById(R.id.edtVendaItemDialogDesc);

                edtDesc.addTextChangedListener(new TextWatcher() {
                    private String texto = "";
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        if (!editable.toString().equals(texto)) {
                            edtDesc.removeTextChangedListener(this);

                            NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("pt","BR"));

                            double parsed;
                            if (editable.toString().contains(nf.getCurrency().getSymbol())) {
                                String replaceable = String.format("[%s,.\\s]", nf.getCurrency().getSymbol());
                                String  cleanString = editable.toString().replaceAll(replaceable, "");

                                try {
                                    parsed = Double.parseDouble(cleanString);
                                } catch (NumberFormatException e) {
                                    parsed = 0.00;
                                }

                                parsed = parsed / 100;
                            } else
                                parsed = Double.parseDouble(editable.toString());

                            edtDesc.setText(AndroidUtils.FormatarValor_Monetario(parsed));
                            edtDesc.setSelection(edtDesc.getText().toString().length());
                            texto = edtDesc.getText().toString();
                            edtDesc.addTextChangedListener(this);
                        }
                    }
                });

                final EditText edtAcres = AriusAlertDialog.getGetView().findViewById(R.id.edtVendaItemDialogAcres);

                edtAcres.addTextChangedListener(new TextWatcher() {
                    private String texto = null;
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        edtAcres.removeTextChangedListener(this);

                        NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("pt","BR"));

                        double parsed;
                        if (editable.toString().contains(nf.getCurrency().getSymbol())) {
                            String replaceable = String.format("[%s,.\\s]", nf.getCurrency().getSymbol());
                            String  cleanString = editable.toString().replaceAll(replaceable, "");

                            try {
                                parsed = Double.parseDouble(cleanString);
                            } catch (NumberFormatException e) {
                                parsed = 0.00;
                            }

                            parsed = parsed / 100;
                        } else
                            parsed = Double.parseDouble(editable.toString());

                        edtAcres.setText(AndroidUtils.FormatarValor_Monetario(parsed));
                        edtAcres.setSelection(edtAcres.getText().toString().length());
                        texto = edtAcres.getText().toString();
                        edtAcres.addTextChangedListener(this);
                    }
                });

                final Button btnCancel = AriusAlertDialog.getGetView().findViewById(R.id.btnItemDialogCancelar);
                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AriusAlertDialog.getAlertDialog().dismiss();
                    }
                });

                final Button btnOK = AriusAlertDialog.getGetView().findViewById(R.id.btnItemDialogOK);
                btnOK.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("pt","BR"));
                        if (Double.parseDouble(edtQtde.getText().toString().equals("") ? "0" :  edtQtde.getText().toString()) > 0) {
                            Double vQtde = Double.parseDouble(edtQtde.getText().toString());
                            vendaItem.setValorTotal(vendaItem.getValorUnitario() * vQtde);
                            vendaItem.setQtde(vQtde);
                        }
                        Double vDesc = 0.0;
                        try{
                            vDesc = nf.parse(edtDesc.getText().toString().replace(" ","")).doubleValue();
                        }catch (ParseException e){
                            e.printStackTrace();
                        }
                        vendaItem.setDesconto(vDesc);

                        Double vAcres = 0.0;
                        try{
                            vAcres = nf.parse(edtAcres.getText().toString().replace(" ","")).doubleValue();
                        }catch (ParseException e){
                            e.printStackTrace();
                        }
                        vendaItem.setAcrescimo(vAcres);

                        PdvService.get().alteraVendaItem(vendaItem);

                        ((AriusCursorAdapter) grdVenda_Item.getAdapter()).notifyDataSetChanged();

                        totalItens();

                        AriusAlertDialog.getAlertDialog().dismiss();
                    }
                });

                edtQtde.requestFocus();
                edtQtde.setText(AndroidUtils.FormataQuantidade(vendaItem.getProduto(),vendaItem.getQtde()));
                edtQtde.setSelection(edtQtde.getText().length());

                edtDesc.setText(String.valueOf(vendaItem.getDesconto()));
                edtAcres.setText(String.valueOf(vendaItem.getAcrescimo()));
            }
        });

    }

    private void gerenciaVenda(boolean inserir){
        if (inserir){
            if (PdvService.get().getVendaAtiva() == null){
                Venda vnd = new Venda();
                vnd.setDataHora(new Date());
                vnd.setSituacao(VendaSituacao.ABERTA);
                vnd.setValorTroco(0);

                PdvService.get().insereVenda(vnd);

                grdVenda_Item.setDataSource(PdvService.get().getVendaAtiva().getItens());
            }
        }

        if (PdvService.get().getVendaAtiva() != null) {
            grdVenda_Item.setDataSource(PdvService.get().getVendaAtiva().getItens());
            grdVenda_Item.setSwipe_Delete(PdvService.get().getVendaAtiva().getSituacao() == VendaSituacao.ABERTA);
            grdVenda_Item.getAriusCursorAdapter().setMontarCamposTela(this);
            grdVenda_Item.getAriusCursorAdapter().setAfterDataControll(this);
            grdVenda_Item.getAriusCursorAdapter().notifyDataSetChanged();
        } else
            grdVenda_Item.setAdapter(null);
    }

    private void inserirProduto(AdapterView<?> adapterView, int posicao){
        cmbPesqProduto.setText("");

        gerenciaVenda(true);

        if (cmbPesqProduto.getAriusCursorAdapter().getEntity_selecionada() != null)
            this.produto = (Produto) cmbPesqProduto.getAriusCursorAdapter().getEntity_selecionada();
        else
            if (this.produto == null)
                this.produto = (Produto) adapterView.getItemAtPosition(posicao);
        VendaItem vndItem = new VendaItem();
        vndItem.setVenda(PdvService.get().getVendaAtiva());
        vndItem.setProduto(this.produto);
        vndItem.setQtde(1);
        vndItem.setValorTotal(10);

        this.produto = null;
        cmbPesqProduto.getAriusCursorAdapter().setEntity_selecionada(null);

        PdvService.get().insereVendaItem(vndItem);

        grdVenda_Item.getAriusCursorAdapter().notifyDataSetChanged();

        grdVenda_Item.setSelection(grdVenda_Item.getAdapter().getCount()-1);

        totalItens();

        setImagemVendaStatus();

        progressBar(false);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.itemCancelaVenda);
        item.setVisible(false);
        if (PdvService.get().getVendaAtiva() != null)
            item.setVisible(PdvService.get().getVendaAtiva().getSituacao() == VendaSituacao.FECHADA);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    protected void onStart() {
        super.onStart();
        preparaTela();

        this.cmbPesqProduto.getAriusCursorAdapter().setMontarCamposTela(this);

        gerenciaVenda(false);
    }

    private void totalItens(){
        edtVendaItemTotal.setText(AndroidUtils.FormatarValor_Monetario(0.0));
        if (PdvService.get().getVendaAtiva() != null) {
            edtVendaItemTotal.setText(AndroidUtils.FormatarValor_Monetario(PdvService.get().getVendaAtiva().getValorLiquido()));
        }
    }

    private void instanciaBotoes(){
        final ImageButton btnIncProduto = (ImageButton) findViewById(R.id.btnCmbPesqProduto);

        btnIncProduto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cmbPesqProduto.getAriusCursorAdapter().getEntity_selecionada() != null)
                    produto = (Produto) cmbPesqProduto.getAriusCursorAdapter().getEntity_selecionada();
                if (produto == null) {
                    throw new UserException("Operação não permitida! \n Selecionar um produto para incluir na venda!");
                }
                inserirProduto(null, -1);
            }
        });


        btnFinalizar = new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (PdvService.get().getVendaAtiva() != null) {

                    Intent intent = new Intent(getBaseContext(), VendaFinalizacaoActivity.class);

                    startActivity(intent);
                } else {
                    throw new UserException("Operação não permitida.\n Informar um produto para finalizar a venda!");
                }
            }
        };

        Button btnFinalizar = (Button) findViewById(R.id.btnVenda_Finalizar);
        btnFinalizar.setOnClickListener(this.btnFinalizar);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        final Button btnCancel;
        final Button btnOK;
        final Spinner cmbFinalizadora;
        switch (item.getItemId()) {
            case R.id.itemFechar_Caixa:
                PdvService.get().fechaCaixa(PdvService.get().getOperadorAtual());
                onBackPressed();
                return true;
            case R.id.itemVendas:
                Intent intent = new Intent(getBaseContext(), VendaListagemActivity.class);

                startActivity(intent);
                return true;
            case R.id.itemFechaVendaAtiva:
                PdvService.get().getPdv().setVendaAtiva(null);
                AppContext.get().getDao(PdvDao.class).update(PdvService.get().getPdv());

                grdVenda_Item.setAdapter(null);

                totalItens();

                setImagemVendaStatus();

                return true;
            case R.id.itemCancelaVenda:
                PdvService.get().cancelaVenda(PdvService.get().getVendaAtiva());
                setImagemVendaStatus();
                return true;
            case R.id.itemSangria :
                FuncionaisFilters<Finalizadora> filtro= new FuncionaisFilters<Finalizadora>() {
                    @Override
                    public boolean test(Finalizadora p) {
                        return true;
                    }
                };

                AriusAlertDialog.exibirDialog(this, R.layout.content_dialog_sangria);

                AriusCursorAdapter adapter = new AriusCursorAdapter(this,
                        R.layout.cmbtextcombo,
                        R.layout.cmbtextcombo,
                        ImmutableMap.<Integer, String>of(R.id.lbcmbtextcombo
                                ,"finalizadora.descricao"),
                        AppContext.get().getDao(FinalizadoraDao.class).listCache(filtro)
                );

                cmbFinalizadora = AriusAlertDialog.getGetView().findViewById(R.id.cmbSangriaDialogFinalizadora);

                cmbFinalizadora.setPrompt("Selecione uma finalizadora!");
                adapter.insert(cmbFinalizadora.getPrompt(),0);
                cmbFinalizadora.setAdapter(adapter);

                final EditText edtSangriaValor = AriusAlertDialog.getGetView().findViewById(R.id.edtSangriaDialogValor);

                btnCancel = AriusAlertDialog.getGetView().findViewById(R.id.btnSangriaDialogCancelar);
                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AriusAlertDialog.getAlertDialog().dismiss();
                    }
                });

                btnOK = AriusAlertDialog.getGetView().findViewById(R.id.btnSangriaDialogOK);
                btnOK.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String itemSelecionado;
                        Finalizadora finalizadora = null;
                        if (cmbFinalizadora.getSelectedItem().getClass() == Finalizadora.class) {
                            finalizadora = (Finalizadora) cmbFinalizadora.getSelectedItem();
                            itemSelecionado = finalizadora.getDescricao();
                        }
                        else
                            itemSelecionado = (String) cmbFinalizadora.getSelectedItem();

                        if (itemSelecionado.equals(cmbFinalizadora.getPrompt())) {
                            cmbFinalizadora.requestFocus();
                            throw new UserException("Operação não permitida! \n Finalizadora não selecionada");
                        }

                        NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("pt","BR"));
                        Double v_valor = 0.0;
                        try{
                            v_valor = nf.parse(edtSangriaValor.getText().toString().replace(" ","")).doubleValue();
                        }catch (ParseException e){
                            e.printStackTrace();
                        }
                        if (edtSangriaValor.getText().toString().equals("") || v_valor <= 0) {
                            edtSangriaValor.requestFocus();
                            throw new UserException("Informar um valor para a sangria!");
                        }

                        PdvService.get().sangraCaixa(finalizadora,v_valor, PdvService.get().getOperadorAtual());

                        AriusAlertDialog.getAlertDialog().dismiss();
                    }
                });

                edtSangriaValor.addTextChangedListener(new TextWatcher() {
                    private String texto;
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        edtSangriaValor.removeTextChangedListener(this);

                        NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("pt","BR"));

                        double parsed;
                        if (editable.toString().contains(nf.getCurrency().getSymbol())) {
                            String replaceable = String.format("[%s,.\\s]", nf.getCurrency().getSymbol());
                            String  cleanString = editable.toString().replaceAll(replaceable, "");

                            try {
                                parsed = Double.parseDouble(cleanString);
                            } catch (NumberFormatException e) {
                                parsed = 0.00;
                            }

                            parsed = parsed / 100;
                        } else
                            parsed = Double.parseDouble(editable.toString());

                        edtSangriaValor.setText(AndroidUtils.FormatarValor_Monetario(parsed));
                        edtSangriaValor.setSelection(edtSangriaValor.getText().toString().length());
                        texto = edtSangriaValor.getText().toString();
                        edtSangriaValor.addTextChangedListener(this);
                    }
                });

                edtSangriaValor.setText("0");

                cmbFinalizadora.requestFocus();

                edtSangriaValor.setSelection(edtSangriaValor.getText().length());

                return true;

            case R.id.itemReforco :
                AriusAlertDialog.exibirDialog(this,R.layout.contentariusdialogreforco);

                final EditText edtReforco = AriusAlertDialog.getGetView().findViewById(R.id.edtReforcoDialog);

                btnCancel = AriusAlertDialog.getGetView().findViewById(R.id.btnReforcoDialogCancelar);
                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AriusAlertDialog.getAlertDialog().dismiss();
                    }
                });

                btnOK = AriusAlertDialog.getGetView().findViewById(R.id.btnReforcoDialogOK);
                btnOK.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("pt","BR"));
                        Double v_valor = 0.0;
                        try{
                            v_valor = nf.parse(edtReforco.getText().toString().replace(" ","")).doubleValue();
                        }catch (ParseException e){
                            e.printStackTrace();
                        }
                        if (edtReforco.getText().toString().equals("") || v_valor <= 0)
                            throw new UserException("Informar um valor para o reforço!");

                        PdvService.get().reforcoCaixa(v_valor);

                        AriusAlertDialog.getAlertDialog().dismiss();
                    }
                });

                edtReforco.addTextChangedListener(new TextWatcher() {
                    private String texto;
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        edtReforco.removeTextChangedListener(this);

                        NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("pt","BR"));

                        double parsed;
                        if (editable.toString().contains(nf.getCurrency().getSymbol())) {
                            String replaceable = String.format("[%s,.\\s]", nf.getCurrency().getSymbol());
                            String  cleanString = editable.toString().replaceAll(replaceable, "");

                            try {
                                parsed = Double.parseDouble(cleanString);
                            } catch (NumberFormatException e) {
                                parsed = 0.00;
                            }

                            parsed = parsed / 100;
                        } else
                            parsed = Double.parseDouble(editable.toString());

                        edtReforco.setText(AndroidUtils.FormatarValor_Monetario(parsed));
                        edtReforco.setSelection(edtReforco.getText().toString().length());
                        texto = edtReforco.getText().toString();
                        edtReforco.addTextChangedListener(this);
                    }
                });

                edtReforco.setText("0");

                edtReforco.requestFocus();

                edtReforco.setSelection(edtReforco.getText().length());

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void montarCamposTela(Object p, View v) {
        if (p.getClass().equals(VendaItem.class)){
            VendaItem vndItem = (VendaItem) p;
            TextView edtaux = v.findViewById(R.id.combobox_codigo);
            if (edtaux != null)
                edtaux.setText(String.valueOf(vndItem.getProduto().getCodigo()));
            edtaux = v.findViewById(R.id.combobox_descricao);
            if (edtaux != null)
                edtaux.setText(String.valueOf(vndItem.getProduto().getDescricaoReduzida().equals("") ?
                        vndItem.getProduto().getDescricao() : vndItem.getProduto().getDescricaoReduzida()));
            edtaux = v.findViewById(R.id.edtItemVendaQtde);
            if (edtaux != null)
                edtaux.setText(AndroidUtils.FormataQuantidade(vndItem.getProduto(),vndItem.getQtde()));
            edtaux = v.findViewById(R.id.edtItemVendaVrUnitario);
            if (edtaux != null)
                edtaux.setText(AndroidUtils.FormatarValor_Monetario(vndItem.getValorUnitario()));
            edtaux = v.findViewById(R.id.edtItemVendaVrTotal);
            if (edtaux != null)
                edtaux.setText(AndroidUtils.FormatarValor_Monetario(vndItem.getValorLiquido()));
            System.out.print(vndItem.getProduto().getDescricao());
        }

        if (p.getClass().equals(Produto.class)){
            TextView edtaux = v.findViewById(R.id.edtCodProdClassGrid);
            if (edtaux != null)
                edtaux.setText(String.valueOf(((Produto) p).getCodigo()));
            edtaux = v.findViewById(R.id.edtDesProdClassGrid);
            if (edtaux != null)
                edtaux.setText(String.valueOf(((Produto) p).getDescricaoReduzida().equals("") ?
                        ((Produto) p).getDescricao() : ((Produto) p).getDescricaoReduzida()));
        }
    }

    @Override
    public void afterScroll(Object object) {}

    @Override
    public void afterDelete(Object object) {
        if (PdvService.get().getVendaAtiva() != null) {
            if (PdvService.get().getVendaAtiva().getItens().size() == 0) {
                AppContext.get().getDao(VendaDao.class).delete(PdvService.get().getVendaAtiva());
                PdvService.get().getPdv().setVendaAtiva(null);
                AppContext.get().getDao(PdvDao.class).update(PdvService.get().getPdv());
                setImagemVendaStatus();
            }
        }
        totalItens();
        grdVenda_Item.setSelection(grdVenda_Item.getAdapter().getCount() - 1);
    }
}
