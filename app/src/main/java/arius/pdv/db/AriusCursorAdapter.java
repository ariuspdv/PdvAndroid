package arius.pdv.db;

import android.content.Context;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import SwipeListView.SwipeMenu;
import SwipeListView.SwipeMenuItem;
import SwipeListView.SwipeMenuLayout;
import SwipeListView.SwipeMenuView;
import arius.pdv.base.PdvDao;

import arius.pdv.core.AppContext;
import arius.pdv.core.Entity;

public class AriusCursorAdapter extends ArrayAdapter {

    private ListAdapter adapter;
    private int layout_listar;
    private int layout_dropdown;
    private int componete;
    private Map<Integer, String> componentes;
    private List<Entity> filterEntity, filtrado;
    private String[] campos_exibir;
    private String[] campos_filtro;

    public AriusCursorAdapter(Context context,
                              int layout_listar,
                              int layout_dropdown,
                              Map<Integer, String> componentes,
                              List<?> array) {
        super(context,
              layout_listar,
              array);
        setDropDownViewResource(layout_dropdown);
        this.layout_listar = layout_listar;
        this.layout_dropdown = layout_dropdown;
        this.componentes = componentes;
        this.filterEntity = new ArrayList<Entity>((List<Entity>) array);
        this.filtrado = new ArrayList<Entity>();
    }

    private void encontra_campo(View view, Entity entity){
        Class<?> entity_aux = entity.getClass();


        for(Map.Entry<Integer, String> tloop : this.componentes.entrySet()){
            componete = tloop.getKey().intValue();

            TextView tvName = (TextView) view.findViewById(componete);

            if (tvName != null) {
                tvName.setText(AndroidUtils.valor_Campo(entity,tloop.getValue()));
            }
        }
    }

    public void setCampos_exibir_filtro(String[] campos_exibir) {
        this.campos_exibir = campos_exibir;
    }

    public void setCampos_filtro(String[] campos_filtro) {
        this.campos_filtro = campos_filtro;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        SwipeMenuLayout layout = null;
        // Get the data item for this positio
        Entity entity = (Entity) getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(this.layout_listar, parent, false);
        } else {
            if (parent.getClass() == AriusListView.class && convertView.getClass() == SwipeMenuLayout.class){
                layout = (SwipeMenuLayout) convertView;
                layout.closeMenu();
                layout.setPosition(position);
                convertView = layout;
            }
        }
        encontra_campo(convertView, entity);
        if (parent.getClass() == AriusListView.class && layout == null) {
            final AriusListView listView = (AriusListView) parent;
            //SwipeMenu menu = new SwipeMenu(getContext());
            //SwipeMenu menu = listView.getmMenu();
            //menu.setViewType(this.getItemViewType(position));
            //createMenu(menu);
            if (listView.getMenu() != null) {
                SwipeMenuView menuView = new SwipeMenuView(listView.getMenu(), (AriusListView) parent);
                menuView.setOnSwipeItemClickListener(new SwipeMenuView.OnSwipeItemClickListener() {
                    @Override
                    public void onItemClick(SwipeMenuView view, SwipeMenu menu, int index) {
                        listView.onItemClick(view, menu, index);
                    }
                });
                layout = new SwipeMenuLayout(convertView, menuView,
                        listView.getCloseInterpolator(),
                        listView.getOpenInterpolator());
                layout.setPosition(position);
                convertView = layout;
            }
        }
        return convertView;
    }

    private View initView(int position, View convertView) {
        Entity entity = (Entity) getItem(position);
        if(convertView == null)
            convertView = View.inflate(getContext(), this.layout_dropdown, null);
        encontra_campo(convertView, entity);
        return convertView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return initView(position, convertView);
    }

    @Override
    public Filter getFilter(){
        return nameFilter;
    }

    Filter nameFilter = new Filter() {
        @Override
        public CharSequence convertResultToString(Object resultValue) {
            Entity entity = (Entity) resultValue;
            String str = "";
            if (campos_exibir != null) {
                for (int i = 0; campos_exibir.length > i; i++) {
                    str = AndroidUtils.valor_Campo(entity, campos_exibir[i]);
                }
            }
            return str;
        }

        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            if (charSequence != null) {
                filtrado.clear();
                for (Entity entity : filterEntity) {
                    if (campos_filtro != null){
                        for(int i = 0; campos_filtro.length > i; i++){
                            String valorcampo = AndroidUtils.valor_Campo(entity,campos_filtro[i]).toLowerCase();
                            if (valorcampo.contains(charSequence.toString().toLowerCase())){
                                filtrado.add(entity);
                                break;
                            }
                        }
                    }
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = filtrado;
                filterResults.count = filtrado.size();
                return filterResults;
            } else {
                return new FilterResults();
            }
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            List<Entity> filterList = (List<Entity>) filterResults.values;
            if (filterResults != null && filterResults.count > 0) {
                clear();
                for (Entity entity : filterList) {
                    add(entity);
                    notifyDataSetChanged();
                }
            }

        }
    };
}
