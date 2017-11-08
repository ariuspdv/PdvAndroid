package arius.pdv.db;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Filterable;
import android.widget.ListAdapter;

import java.util.HashMap;
import java.util.Map;
import arius.pdv.core.AppContext;
import arius.pdv.core.Entity;
import arius.pdv.core.FuncionaisFilters;
import arius.pdv.core.GenericDao;
import br.com.arius.pdvarius.R;

/**
 * Created by Arius on 28/08/2017.
 */

public class AriusAutoCompleteTextView extends AppCompatAutoCompleteTextView {

    private int layout_arius;
    private int layout_arius_dropdown;
    private Entity entity;
    private GenericDao<?> genericDao;
    private FuncionaisFilters<Entity> filtraTodos;
    private Map<Integer, String> campos = new HashMap<>();
    private String[] campos_exibir;
    private String[] campos_filtro;
    private boolean deleteAtivo = false;

    public AriusAutoCompleteTextView(Context context) {
        super(context);
    }

    public AriusAutoCompleteTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setThreshold(1);

        final TypedArray vAttr = getContext().obtainStyledAttributes(attrs, R.styleable.AriusAutoCompleteTextView);

        this.layout_arius = vAttr.getResourceId(R.styleable.AriusAutoCompleteTextView_layout_arius_autocomplete,0);
        this.layout_arius_dropdown = vAttr.getResourceId(R.styleable.AriusAutoCompleteTextView_layout_arius_dropdown_autocomplete,0);

        int idCampos = vAttr.getResourceId(R.styleable.AriusAutoCompleteTextView_comp_campos_autocomplete,0);
        if (idCampos != 0) {
            TypedArray campos = vAttr.getResources().obtainTypedArray(idCampos);
            for (int i = 0; campos.length() > i; i++) {
                int idaux = campos.getResourceId(i, -1);
                if (idaux != 0) {
                    TypedArray tela = campos.getResources().obtainTypedArray(idaux);
                    String[] tabela = campos.getResources().getStringArray(idaux);
                    for (int t = 0; tela.length() > t; t = t + 2) {
                        this.campos.put(tela.getResourceId(t+1, -1), tabela[t]);
                    }
                }
            }
        }

        idCampos = vAttr.getResourceId(R.styleable.AriusAutoCompleteTextView_campos_exibir_autocomplete,0);
        if (idCampos != 0) {
            campos_exibir = vAttr.getResources().getStringArray(idCampos);
        }

        idCampos = vAttr.getResourceId(R.styleable.AriusAutoCompleteTextView_campos_fitlro,0);
        if (idCampos != 0) {
            campos_filtro = vAttr.getResources().getStringArray(idCampos);
        }

        try {

            entity = (Entity) Class.forName("arius.pdv.base." + vAttr.getString(R.styleable.AriusAutoCompleteTextView_entity_autocomplete)).newInstance();
            genericDao = (GenericDao<?>) Class.forName("arius.pdv.base." + vAttr.getString(R.styleable.AriusAutoCompleteTextView_entity_autocomplete) + "Dao").newInstance();

            this.filtraTodos = new FuncionaisFilters<Entity>() {
                @Override
                public boolean test(Entity p) {
                    return true;
                }
            };

        } catch (ClassNotFoundException e){
            e.printStackTrace();
        } catch (InstantiationException x){
            x.printStackTrace();
        } catch (IllegalAccessException y){
            y.printStackTrace();
        }

        vAttr.recycle();

        setAdapter(montaAdapter());

        this.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                entity = (Entity) adapterView.getItemAtPosition(i);
                getAriusCursorAdapter().setEntity_selecionada(entity);
                String vreturn = "";
                if (getAriusCursorAdapter().getFiltros() != null){
                    vreturn = getAriusCursorAdapter().getFiltros().exibircampoSelecionado(entity);
                } else {
                    if (campos_exibir != null)
                        for (int vi = 0; campos_exibir.length > vi; vi++)
                            vreturn = AndroidUtils.valor_Campo(entity, campos_exibir[vi]);
                }
                setText(vreturn);
            }
        });

        this.setCompoundDrawablesWithIntrinsicBounds(null, null, imgClearButton, null);
        this.setPadding(10,10,10,10);

        // if the clear button is pressed, fire up the handler. Otherwise do nothing
        this.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                AriusAutoCompleteTextView et = AriusAutoCompleteTextView.this;

                if (et.getCompoundDrawables()[2] == null)
                    return false;

                if (event.getAction() != MotionEvent.ACTION_UP)
                    return false;

                if (event.getX() > et.getWidth() - et.getPaddingRight()	- imgClearButton.getIntrinsicWidth()) {
                    if (deleteAtivo) {
                        et.setText("");
                    } else
                        et.showDropDown();
                }
                return false;
            }
        });
    }

    public AriusAutoCompleteTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    // The image we defined for the clear button
    private Drawable imgClearButton = getResources().getDrawable(R.mipmap.dropdown_autocomplete);

    public void setLayout_arius(int layout_arius) {
        this.layout_arius = layout_arius;
        setAdapter(montaAdapter());
    }

    public void setLayout_arius_dropdown(int layout_arius_dropdown) {
        this.layout_arius_dropdown = layout_arius_dropdown;
        setAdapter(montaAdapter());
    }

    public void setEntity(Entity entity) {
        setAdapter(montaAdapter());
        this.entity = entity;
    }

    public void setGenericDao(GenericDao<?> genericDao) {
        setAdapter(montaAdapter());
        this.genericDao = genericDao;
    }

    private AriusCursorAdapter montaAdapter(){
        if (genericDao != null && entity != null && this.layout_arius != 0 && this.layout_arius_dropdown != 0) {
            AriusCursorAdapter adapter = new AriusCursorAdapter(getContext(), this.layout_arius, this.layout_arius_dropdown,
                    campos,
                    AppContext.get().getDao(genericDao.getClass()).listCache(filtraTodos));
            adapter.setCampos_exibir_filtro(campos_exibir);
            adapter.setCampos_filtro(campos_filtro);
            return adapter;
        }
        return null;
    }

    public AriusCursorAdapter getAriusCursorAdapter(){
        return (AriusCursorAdapter) getAdapter();
    }

    @Override
    public <T extends ListAdapter & Filterable> void setAdapter(T adapter) {
        super.setAdapter(adapter);
    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        if (!text.toString().equals("")) {
            imgClearButton = getResources().getDrawable(R.mipmap.delete_autocomplete);
            deleteAtivo = true;
        } else {
            imgClearButton = getResources().getDrawable(R.mipmap.dropdown_autocomplete);
            deleteAtivo = false;
        }
        this.setCompoundDrawablesWithIntrinsicBounds(null, null, imgClearButton, null);
        super.onTextChanged(text, start, lengthBefore, lengthAfter);
    }

    public void hideClearButton() {
        this.setCompoundDrawables(null, null, null, null);
    }

    public void showClearButton() {
        this.setCompoundDrawablesWithIntrinsicBounds(null, null, imgClearButton, null);
    }
}
