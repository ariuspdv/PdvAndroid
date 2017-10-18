package br.com.arius.pdvarius;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import arius.pdv.base.Produto;
import arius.pdv.base.ProdutoCategoria;
import arius.pdv.base.ProdutoCategoriaDao;
import arius.pdv.base.ProdutoDao;
import arius.pdv.base.VendaSituacao;
import arius.pdv.core.AppContext;
import arius.pdv.core.FuncionaisFilters;
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

    private ProdutoCategoria produtoCategoria;
    private Produto produto;

    private Spinner cmbCategoria;
    private Spinner cmbProduto;
    private Spinner cmbSituacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contentariuslistagemvenda);

        setButtons(true, false, false);

        TabHost host = (TabHost)findViewById(R.id.tabhost);
        host.setup();

        //Tab 1
        TabHost.TabSpec spec = host.newTabSpec("Categoria");
        spec.setContent(R.id.tbsListagemVendaCategoria);
        spec.setIndicator("Categoria");
        host.addTab(spec);

        //Tab 2
        spec = host.newTabSpec("Produtos");
        spec.setContent(R.id.tbsListagemVendaProduto);
        spec.setIndicator("Produtos");
        host.addTab(spec);

        //Tab 3
        spec = host.newTabSpec("Vendas");
        spec.setContent(R.id.tbsListagemVendaVenda);
        spec.setIndicator("Vendas");
        host.addTab(spec);

        carregaCamposData();

        carregaCategorias(0);
        carregaProdutos();
        carregaSituacoes();

        host.setCurrentTab(3);
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
                            if (focusDataInicio)
                                dtInicio.setText(String.valueOf(dayOfMonth) + "/" +
                                        String.valueOf(monthOfYear + 1) + "/" +
                                        String.valueOf(year));
                            else
                                dtFim.setText(String.valueOf(dayOfMonth) + "/" +
                                        String.valueOf(monthOfYear + 1) + "/" +
                                        String.valueOf(year));
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
        tv.setTextColor(Color.parseColor("#ff0000"));
        tv.setBackgroundColor(Color.parseColor("#FFD2DAA7"));
        tv.setTextSize(30);
        datePickerDialog.setCustomTitle(tv);
        datePickerDialog.getDatePicker().setSpinnersShown(false);

        datePickerDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                cancelDatePichker = false;
            }
        });
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
                R.layout.layoutcmbbasico,
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
                if (campoaux != null)
                    campoaux.setText(produtoCategoria.getDescricao());
            }
        });

        cmbCategoria = (Spinner) findViewById(R.id.cbmListageVendaCategoria);
        cmbCategoria.setAdapter(adapter_categoria);

    }

    private void carregaProdutos(){
        AriusCursorAdapter adapter_produto = new AriusCursorAdapter(getApplicationContext(),
                R.layout.layoutcmbbasico,
                R.layout.layoutcmbbasico,
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
                produto = (Produto) p;
                TextView campoaux = v.findViewById(R.id.lbcmbbasico);
                if (campoaux != null)
                    campoaux.setText(produto.getDescricao());
            }
        });

        cmbProduto = (Spinner) findViewById(R.id.cbmListageVendaProduto);
        cmbProduto.setAdapter(adapter_produto);

    }

    private void carregaSituacoes(){
        List list_situacao = new ArrayList<>();

        list_situacao.add("TODAS");
        list_situacao.addAll(EnumSet.allOf(VendaSituacao.class));

        final AriusCursorAdapter adapter_situacao = new AriusCursorAdapter(getApplicationContext(),
                R.layout.layoutcmbbasico,
                R.layout.layoutcmbbasico,
                null,
                list_situacao);

        adapter_situacao.setMontarCamposTela(new AriusCursorAdapter.MontarCamposTela() {
            @Override
            public void montarCamposTela(Object p, View v) {
                String sitacao = String.valueOf(p);
                TextView campoaux = v.findViewById(R.id.lbcmbbasico);
                if (campoaux != null)
                    campoaux.setText(sitacao);
            }
        });

        cmbSituacao = (Spinner) findViewById(R.id.cbmListageVendaSituacao);
        cmbSituacao.setAdapter(adapter_situacao);

    }
}
