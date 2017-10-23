package br.com.arius.pdvarius;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.reflect.Method;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

import arius.pdv.base.PdvDao;
import arius.pdv.base.PdvService;
import arius.pdv.base.Produto;
import arius.pdv.base.ProdutoDao;
import arius.pdv.base.VendaDao;
import arius.pdv.base.VendaItem;
import arius.pdv.base.VendaItemDao;
import arius.pdv.base.VendaSituacao;
import arius.pdv.core.AppContext;
import arius.pdv.core.FuncionaisFilters;
import arius.pdv.core.UserException;
import arius.pdv.db.AndroidUtils;
import arius.pdv.db.AriusAlertDialog;
import arius.pdv.db.AriusAutoCompleteTextView;
import arius.pdv.db.AriusCursorAdapter;
import arius.pdv.db.AriusListView;

/**
 * Created by Arius on 10/10/2017.
 */

public class AriusActivityItemVenda extends ActivityPadrao {

    private Context context;
    private AriusListView grdItemVenda;
    private AriusAutoCompleteTextView cmbPesqProduto;
    private ImageButton btnInserirItem;
    private Produto produto;
    private VendaItem vendaItem;
    private TextView edttotalvenda;
    private TextView edttotalitem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contentariusitemvenda);

        montaItemVenda(null, getApplicationContext());
    }

    public void montaItemVenda(View view, final Context context){
        this.context = context;

        if (view == null) {
            grdItemVenda = (AriusListView) findViewById(R.id.grdItemVenda);
            cmbPesqProduto = (AriusAutoCompleteTextView) findViewById(R.id.cmbItemVendaPesqProduto);
            btnInserirItem = (ImageButton) findViewById(R.id.btnItemVendaInserirItem);
            edttotalvenda = (TextView) findViewById(R.id.edtlayoutItemVendaRodapeValorVenda);
            edttotalitem = (TextView) findViewById(R.id.edtlayoutItemVendaRodapeTotItem);
        } else {
            grdItemVenda = view.findViewById(R.id.grdItemVenda);
            cmbPesqProduto = view.findViewById(R.id.cmbItemVendaPesqProduto);
            btnInserirItem = view.findViewById(R.id.btnItemVendaInserirItem);
            edttotalvenda = view.findViewById(R.id.edtlayoutItemVendaRodapeValorVenda);
            edttotalitem = view.findViewById(R.id.edtlayoutItemVendaRodapeTotItem);
        }

        grdItemVenda.setSwipe_Delete(true);

        carregaVenda();

        carregaPesqProduto();

        grdItemVenda.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                vendaItem = (VendaItem) adapterView.getItemAtPosition(i);
                if (vendaItem.getVenda().getSituacao() != VendaSituacao.ABERTA)
                    return;

                montaDialogItemVenda();
            }
        });

        btnInserirItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AriusActivityProdutoPrincipal activityProdutoPrincipal = new AriusActivityProdutoPrincipal();
                if (cmbPesqProduto.getAriusCursorAdapter().getEntity_selecionada() != null)
                    produto = (Produto) cmbPesqProduto.getAriusCursorAdapter().getEntity_selecionada();
                if (produto == null) {
                    throw new UserException("Operação não permitida! \n Selecionar um produto para incluir na venda!");
                }
                activityProdutoPrincipal.inserirItemVenda(produto);

                cmbPesqProduto.setText("");

                if (grdItemVenda.getAriusCursorAdapter() != null) {
                    if (grdItemVenda.getAriusCursorAdapter().getCount() == 0)
                        carregaVenda();
                } else
                    carregaVenda();

                grdItemVenda.getAriusCursorAdapter().notifyDataSetChanged();

                montaRodape();
            }
        });
    }

    private void carregaVenda(){
        if (PdvService.get().getVendaAtiva() != null){
            AriusCursorAdapter venda_itens = new AriusCursorAdapter(context,
                    R.layout.layoutitemvenda,
                    R.layout.layoutitemvenda,
                    null,
                    PdvService.get().getVendaAtiva().getItens());

            venda_itens.setMontarCamposTela(new AriusCursorAdapter.MontarCamposTela() {
                @Override
                public void montarCamposTela(final Object p, View v) {
                    final VendaItem vendaItem = (VendaItem) p;
                    TextView campoAux = v.findViewById(R.id.lbLayoutItemVendaProduto);
                    if (campoAux != null)
                        campoAux.setText(String.valueOf(vendaItem.getProduto().getDescricaoReduzida().equals("") ?
                                vendaItem.getProduto().getDescricao() : vendaItem.getProduto().getDescricaoReduzida()));

                    campoAux = v.findViewById(R.id.lbLayoutItemVendaQtde);
                    if (campoAux != null)
                        campoAux.setText(AndroidUtils.FormataQuantidade(vendaItem.getProduto(),vendaItem.getQtde()));

                    campoAux = v.findViewById(R.id.lbLayoutItemVendaVrUnitario);
                    if (campoAux != null)
                        campoAux.setText(AndroidUtils.FormatarValor_Monetario(vendaItem.getValorUnitario()));

                    campoAux = v.findViewById(R.id.lbLayoutItemVendaVrTotal);
                    if (campoAux != null)
                        campoAux.setText(AndroidUtils.FormatarValor_Monetario(vendaItem.getValorLiquido()));

                    ImageView imgaux = v.findViewById(R.id.imgItemVendaDelete);
                    if (vendaItem.getVenda().getSituacao() == VendaSituacao.ABERTA) {
                        imgaux.setVisibility(View.VISIBLE);
                        imgaux.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                grdItemVenda.getAriusCursorAdapter().remove(p);
                                deleteVendaItem(p);
                            }
                        });
                    } else
                        imgaux.setVisibility(View.GONE);
                }
            });

            venda_itens.setAfterDataControll(new AriusCursorAdapter.AfterDataControll() {
                @Override
                public void afterScroll(Object object) {

                }

                @Override
                public void afterDelete(Object object) {
                    deleteVendaItem(object);
                }
            });

            grdItemVenda.setAdapter(venda_itens);

            grdItemVenda.setSwipe_Delete(PdvService.get().getVendaAtiva().getSituacao() == VendaSituacao.ABERTA);
        }
        montaRodape();
    }

    private void deleteVendaItem(Object object){
        VendaItem vendaItem = (VendaItem) object;
        if (AppContext.get().getDao(VendaItemDao.class).find(vendaItem.getId()) != null){
            AppContext.get().getDao(VendaItemDao.class).delete(vendaItem);
        }
        if (PdvService.get().getVendaAtiva().getItens().size() == 0){
            AppContext.get().getDao(VendaDao.class).delete(PdvService.get().getVendaAtiva());
            PdvService.get().getPdv().setVendaAtiva(null);
            AppContext.get().getDao(PdvDao.class).update(PdvService.get().getPdv());
        }

        montaRodape();
    }

    private void carregaPesqProduto(){
        AriusCursorAdapter produto_pesq = new AriusCursorAdapter(context,
                R.layout.layoutcmbbasico,
                R.layout.layoutcmbbasico,
                null,
                AppContext.get().getDao(ProdutoDao.class).listCache(new FuncionaisFilters<Produto>() {
                    @Override
                    public boolean test(Produto p) {
                        return true;
                    }
                }));

        produto_pesq.setExibirfiltrado_zerado(true);

        produto_pesq.setMontarCamposTela(new AriusCursorAdapter.MontarCamposTela() {
            @Override
            public void montarCamposTela(Object p, View v) {
                Produto produto = (Produto) p;
                TextView campoaux = v.findViewById(R.id.lbcmbbasico);
                if (campoaux != null)
                    campoaux.setText(produto.getDescricaoReduzida().equals("") ?
                        produto.getDescricao() : produto.getDescricaoReduzida());
            }
        });

        produto_pesq.setFiltros(new AriusCursorAdapter.Filtros() {
            @Override
            public boolean filtroCampos(Object object, CharSequence texto) {Produto produto = (Produto) object;
                return produto.getDescricaoReduzida().toLowerCase().contains(texto.toString().toLowerCase()) ||
                        produto.getDescricao().toLowerCase().contains(texto.toString().toLowerCase()) ||
                         String.valueOf(produto.getCodigo()).toLowerCase().contains(texto.toString().toLowerCase());
            }

            @Override
            public String exibircampoSelecionado(Object object) {
                Produto produto = (Produto) object;
                return (produto.getDescricaoReduzida().equals("") ? produto.getDescricao() : produto.getDescricaoReduzida());
            }
        });

        cmbPesqProduto.setAdapter(produto_pesq);
    }

    private void montaDialogItemVenda(){
        AriusAlertDialog.exibirDialog(context,R.layout.layoutdialogitemvenda);

        Button btnCancel = AriusAlertDialog.getGetView().findViewById(R.id.btnlayoutDialogItemVendaCancelar);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AriusAlertDialog.getAlertDialog().dismiss();
            }
        });

        final EditText edtVrUnitario = AriusAlertDialog.getGetView().findViewById(R.id.edtlayoutDialogItemVendaVrUnitario);

        edtVrUnitario.addTextChangedListener(new TextWatcher() {
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
                    edtVrUnitario.removeTextChangedListener(this);

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

                    edtVrUnitario.setText(AndroidUtils.FormatarValor_Monetario(parsed));
                    edtVrUnitario.setSelection(edtVrUnitario.getText().toString().length());
                    texto = edtVrUnitario.getText().toString();
                    edtVrUnitario.addTextChangedListener(this);
                }
            }
        });

        edtVrUnitario.setText(AndroidUtils.FormatarValor_Monetario(vendaItem.getValorUnitario()));

        final EditText edtAcrecsimo = AriusAlertDialog.getGetView().findViewById(R.id.edtlayoutDialogItemVendaAcrescimo);

        edtAcrecsimo.addTextChangedListener(new TextWatcher() {
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
                    edtAcrecsimo.removeTextChangedListener(this);

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

                    edtAcrecsimo.setText(AndroidUtils.FormatarValor_Monetario(parsed));
                    edtAcrecsimo.setSelection(edtAcrecsimo.getText().toString().length());
                    texto = edtAcrecsimo.getText().toString();
                    edtAcrecsimo.addTextChangedListener(this);
                }
            }
        });

        edtAcrecsimo.setText(AndroidUtils.FormatarValor_Monetario(vendaItem.getAcrescimo()));

        final EditText edtDesconto = AriusAlertDialog.getGetView().findViewById(R.id.edtlayoutDialogItemVendaDesconto);

        edtDesconto.addTextChangedListener(new TextWatcher() {
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
                    edtDesconto.removeTextChangedListener(this);

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

                    edtDesconto.setText(AndroidUtils.FormatarValor_Monetario(parsed));
                    edtDesconto.setSelection(edtDesconto.getText().toString().length());
                    texto = edtDesconto.getText().toString();
                    edtDesconto.addTextChangedListener(this);
                }
            }
        });

        edtDesconto.setText(AndroidUtils.FormatarValor_Monetario(vendaItem.getDesconto()));

        final EditText edtQtde = AriusAlertDialog.getGetView().findViewById(R.id.edtlayoutDialogItemVendaQtde);

        edtQtde.addTextChangedListener(new TextWatcher() {
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
                    edtQtde.removeTextChangedListener(this);

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

                    edtQtde.setText(AndroidUtils.FormataQuantidade(vendaItem.getProduto(),parsed));
                    edtQtde.setSelection(edtQtde.getText().toString().length());
                    texto = edtQtde.getText().toString();
                    edtQtde.addTextChangedListener(this);
                }
            }
        });

        edtQtde.setText(AndroidUtils.FormataQuantidade(vendaItem.getProduto(),vendaItem.getQtde()));

        edtQtde.requestFocus();

        ImageButton btnDiminui = AriusAlertDialog.getGetView().findViewById(R.id.btnlayoutDialogItemVendaDiminuiQtde);
        btnDiminui.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                double vqtde = Double.parseDouble(edtQtde.getText().toString());
                if (vqtde > 0)
                    vqtde --;
                edtQtde.setText(AndroidUtils.FormataQuantidade(vendaItem.getProduto(),vqtde));
            }
        });

        ImageButton btnAumentar = AriusAlertDialog.getGetView().findViewById(R.id.btnlayoutDialogItemVendaAumentaQtde);
        btnAumentar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                double vqtde = Double.parseDouble(edtQtde.getText().toString());
                    vqtde ++;
                edtQtde.setText(AndroidUtils.FormataQuantidade(vendaItem.getProduto(),vqtde));
            }
        });

        Button btnConfirma = AriusAlertDialog.getGetView().findViewById(R.id.btnlayoutDialogItemVendaConfirmar);
        btnConfirma.setOnClickListener(new View.OnClickListener() {
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
                    vDesc = nf.parse(edtDesconto.getText().toString().replace(" ","")).doubleValue();
                }catch (ParseException e){
                    e.printStackTrace();
                }
                vendaItem.setDesconto(vDesc);

                Double vAcres = 0.0;
                try{
                    vAcres = nf.parse(edtAcrecsimo.getText().toString().replace(" ","")).doubleValue();
                }catch (ParseException e){
                    e.printStackTrace();
                }
                vendaItem.setAcrescimo(vAcres);

                Double vVrUnitario = 0.0;
                try{
                    vVrUnitario = nf.parse(edtVrUnitario.getText().toString().replace(" ","")).doubleValue();
                }catch (ParseException e){
                    e.printStackTrace();
                }
                Double vQtde = Double.parseDouble(edtQtde.getText().toString());
                vendaItem.setValorTotal(vVrUnitario * vQtde);

                PdvService.get().alteraVendaItem(vendaItem);

                grdItemVenda.getAriusCursorAdapter().notifyDataSetChanged();

                vendaItem = null;
                AriusAlertDialog.getAlertDialog().dismiss();

                montaRodape();
            }
        });
    }

    private void montaRodape(){
        edttotalvenda.setText(AndroidUtils.FormatarValor_Monetario(0.0));
        edttotalitem.setText("0");
        if (PdvService.get().getVendaAtiva() != null) {
            edttotalvenda.setText(AndroidUtils.FormatarValor_Monetario(PdvService.get().getVendaAtiva().getValorLiquido()));
            edttotalitem.setText(String.valueOf(PdvService.get().getVendaAtiva().getItens().size()));
        }
    }
}
