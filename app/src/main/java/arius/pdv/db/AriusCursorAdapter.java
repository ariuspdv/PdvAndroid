package arius.pdv.db;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import SwipeListView.SwipeMenu;
import SwipeListView.SwipeMenuLayout;
import SwipeListView.SwipeMenuView;
import arius.pdv.core.Entity;
import br.com.arius.pdvarius.ActivityPadrao;

public class AriusCursorAdapter extends ArrayAdapter {

    private int layout_listar;
    private int layout_dropdown;
    private boolean exibirfiltrado_zerado = false;
    private Map<Integer, String> componentes;
    private List<Entity> filterEntity, filtrado;
    private String[] campos_exibir;
    private String[] campos_filtro;
    private Entity entity_selecionada;

    private MontarCamposTela montarCamposTela;
    private AfterDataControll afterDataControll;
    private Filtros filtros;

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
        this.filterEntity = new ArrayList<>((List<Entity>) array);
        this.filtrado = new ArrayList<>();

    }

    public interface MontarCamposTela{
        void montarCamposTela(Object p, View v);
    }

    public interface AfterDataControll{
        void afterScroll(Object object);
        void afterDelete(Object object);
    }

    public interface Filtros{
        boolean filtroCampos(Object object, CharSequence texto);
        String exibircampoSelecionado(Object object);
    }

    public void setCampos_exibir_filtro(String[] campos_exibir) {
        this.campos_exibir = campos_exibir;
    }

    public void setCampos_filtro(String[] campos_filtro) {
        this.campos_filtro = campos_filtro;
    }

    public void setExibirfiltrado_zerado(boolean exibirfiltrado_zerado) {
        this.exibirfiltrado_zerado = exibirfiltrado_zerado;
    }

    public Entity getEntity_selecionada() {
        return entity_selecionada;
    }

    public void setEntity_selecionada(Entity entity_selecionada) {
        this.entity_selecionada = entity_selecionada;
    }

    public void setMontarCamposTela(MontarCamposTela montarCamposTela) {
        this.montarCamposTela = montarCamposTela;
    }

    public void setAfterDataControll(AfterDataControll afterDataControll) {
        this.afterDataControll = afterDataControll;
    }

    public AfterDataControll getAfterDataControll() {
        return afterDataControll;
    }

    public void setFiltros(Filtros filtros) {
        this.filtros = filtros;
    }

    public Filtros getFiltros() {
        return filtros;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        SwipeMenuLayout layout = null;
        // Get the data item for this positio
        try {
            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(this.layout_listar, parent, false);
            } else {
                if (parent.getClass() == AriusListView.class && convertView.getClass() == SwipeMenuLayout.class) {
                    layout = (SwipeMenuLayout) convertView;
                    layout.closeMenu();
                    layout.setPosition(position);
                    convertView = layout;
                }
            }
            if (montarCamposTela != null) {
                montarCamposTela.montarCamposTela(getItem(position), convertView);
            }

            if (parent.getClass() == AriusListView.class && layout == null) {
                final AriusListView listView = (AriusListView) parent;
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

        }catch(NullPointerException e){
            e.printStackTrace();
        }
        if (afterDataControll != null)
            afterDataControll.afterScroll(super.getItem(position));

        return convertView;
    }

    private View initView(int position, View convertView) {
        if(convertView == null)
            convertView = View.inflate(getContext(), this.layout_dropdown, null);

        if (montarCamposTela != null) {
            montarCamposTela.montarCamposTela(getItem(position), convertView);
        }

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
            if (filtros != null){
                str = filtros.exibircampoSelecionado(entity);
            } else {
                if (campos_exibir != null) {
                    for (String lcampos : campos_exibir) {
                        str = AndroidUtils.valor_Campo(entity, lcampos);
                    }
                }
            }
            return str;
        }

        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            if (charSequence != null) {
                filtrado.clear();
                for (Entity entity : filterEntity) {
                    if (filtros != null){
                        if (filtros.filtroCampos(entity, charSequence))
                            filtrado.add(entity);
                    } else {
                        if (campos_filtro != null) {
                            for (String lcampo : campos_filtro) {
                                String valorcampo = AndroidUtils.valor_Campo(entity, lcampo).toLowerCase();
                                if (valorcampo.toLowerCase().contains(charSequence.toString().toLowerCase())) {
                                    filtrado.add(entity);
                                    break;
                                }
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
            if ((filterResults != null && filterResults.count > 0) || (exibirfiltrado_zerado && filterList != null)) {
                clear();
                if (filterResults.count == 0)
                    entity_selecionada = null;
                for (Entity entity : filterList) {
                    add(entity);
                    notifyDataSetChanged();
                }
            } else {
                notifyDataSetChanged();
            }
            ActivityPadrao.progressBar(false);
        }
    };
}
