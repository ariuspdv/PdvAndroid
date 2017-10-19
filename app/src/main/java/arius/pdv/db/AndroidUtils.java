package arius.pdv.db;


import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

import arius.pdv.base.Produto;
import arius.pdv.core.Entity;

/**
 * Created by Arius on 30/08/2017.
 */

public class AndroidUtils {

    private static String vaux_entity;
    private static ArrayList<Class<?>> ventity_verificar = new ArrayList<>();
    private static ArrayList<Object> varray_objeto = new ArrayList<>();

    private static Method m1;

    private static String encontra_valor_campo(Entity entity, Field[] campos, String nome_campo,String entity_campo){
        Class entity_fields = campos[0].getDeclaringClass();
        ventity_verificar.add(entity_fields);
        for (int i = 0; campos.length > i; i++){
            if (campos[i].getType().getGenericSuperclass() != null &&
                    campos[i].getType().getGenericSuperclass().equals(Entity.class)) {
                vaux_entity = campos[i].getType().getSimpleName();
                Object v1 = null;
                if (!ventity_verificar.contains(campos[i].getType())) {
                    try {
                    if (varray_objeto.size() > 0 && entity.getClass() != entity_fields) {
                        campos[i].setAccessible(true);
                        if (campos[i].get(varray_objeto.get(varray_objeto.size() - 1)) != null) {
                            varray_objeto.add(campos[i].get(varray_objeto.get(varray_objeto.size() - 1)));
                            v1 = varray_objeto.get(varray_objeto.size() - 1);
                        }
                    } else {
                        campos[i].setAccessible(true);
                        if (campos[i].get(entity) != null) {
                            v1 = campos[i].get(entity);
                            varray_objeto.add(v1);
                        }
                    }

                    if (v1 != null){
                        String vreturn = encontra_valor_campo(entity, ((Class) campos[i].getType()).getDeclaredFields(), nome_campo, entity_campo);

                        if (!vreturn.isEmpty()) {
                            return vreturn;
                        } else {
                            if (entity.getClass() == entity_fields)
                                varray_objeto.remove(campos[i].get(entity));
                            else
                                varray_objeto.remove(varray_objeto.get(varray_objeto.size()-1));
                        }
                    }
                    }catch (IllegalAccessException e){
                        e.printStackTrace();
                    }
                }
            } else {
                try {
                    String fieldName = campos[i].getName();
                    if (campos[i].getName().toLowerCase().equals(nome_campo.toLowerCase()) &&
                            (vaux_entity.toLowerCase().equals(entity_campo.toLowerCase())
                                || entity_campo.isEmpty()
                            )) {
                        Field campo_int = campos[i];

                        campo_int.setAccessible(true);

                        String vreturn = null;
                        if (varray_objeto.size() > 0)
                            vreturn = campo_int.get(varray_objeto.get(varray_objeto.size()-1)).toString();
                        else
                            vreturn = campo_int.get(entity).toString();
                        return vreturn;
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            vaux_entity = entity_fields.getSimpleName();
        }
        if (entity_fields.getGenericSuperclass().equals(Entity.class)) {
            String vreturn = encontra_valor_campo(entity, ((Class) entity_fields.getGenericSuperclass()).getDeclaredFields(), nome_campo, entity_campo);
            if (!vreturn.isEmpty())
                return vreturn;
        }
        return "";
    }

    public static Method encontra_valor_campo(Entity entity, Method[] campos, String nome_campo, String entity_campo) {
        String teste = null;
        Class entity_fields = campos[0].getDeclaringClass();
        for(Method litem : campos){
            try {
                Object tteste1 = litem.getReturnType().getGenericSuperclass();
                boolean t1 = litem.isAccessible();

                if (tteste1 != null && litem.getReturnType().getGenericSuperclass().equals(Entity.class)) {
                    m1 = litem;
                    return encontra_valor_campo(entity, litem.getReturnType().getDeclaredMethods(), nome_campo, entity_campo);
                    //m1 = null;
                }
                if (!litem.getReturnType().equals(Void.TYPE) && m1 == null) {
                    Object validacao = litem.invoke(entity, new Object[]{});
                    if (validacao != null) {
                        if(nome_campo.toLowerCase().equals(litem.getName().toLowerCase().substring(3))) {
                            teste = validacao.toString();
                            return litem;
                        }
                    }
                } else {
                    if (!litem.getReturnType().equals(Void.TYPE)){
                        Object validacao = litem.invoke(m1.invoke(entity, new Object[]{}), new Object[]{});
                        if (validacao != null) {
                            if(nome_campo.toLowerCase().equals(litem.getName().toLowerCase().substring(3))) {
                                teste = validacao.toString();
                                //m1.get
                                //return (Method)  m1.
                            }
                        }
                    }
                }
            }catch (InvocationTargetException e){
                e.printStackTrace();
            } catch (IllegalAccessException er){
                er.printStackTrace();
            }
        }
        return null;
    }

    public static String valor_Campo(Entity entity, String campo){

        String entity_campo = "";
        String nome_campo = campo;
        Field[] campos = entity.getClass().getDeclaredFields();
        //Method[] metodos = entity.getClass().getDeclaredMethods();
        //encontra_valor_campo(entity,metodos,nome_campo,entity_campo);
        if (campo.contains(".")) {
            entity_campo = campo.contains(".") ? campo.substring(0, campo.indexOf(".")) : campo;
            nome_campo = campo.contains(".") ? campo.substring(campo.indexOf(".") + 1, campo.length()) : campo;
        }
        vaux_entity = entity_campo;
        varray_objeto.clear();
        ventity_verificar.clear();

        return encontra_valor_campo(entity,campos,nome_campo,entity_campo);
    }

    public static String FormatarValor_Monetario(Double pvalor){
        BigDecimal valor = new BigDecimal(pvalor);
        NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("pt","BR"));
        return nf.format(valor).substring(0,2)+ " " + nf.format(valor).substring(2);
    }

    public static String FormataQuantidade(Produto produto, Double quantidade){
        NumberFormat fn;
        if (produto.getUnidadeMedida() != null && produto.getUnidadeMedida().isFracionada())
            fn = new DecimalFormat("#0.0##");
        else
            fn = NumberFormat.getIntegerInstance();
        return fn.format(quantidade);
    }

    public static void toast(Context context, String mensagem){
        Toast toast = Toast.makeText(context, mensagem, Toast.LENGTH_SHORT);
        //the default toast view group is a relativelayout
        LinearLayout toastLayout = (LinearLayout) toast.getView();
        TextView toastTV = (TextView) toastLayout.getChildAt(0);
        toastTV.setTextSize(30);
        toastTV.setGravity(Gravity.CENTER);
        toast.show();
    }
}
