package arius.pdv.db;

import android.content.res.Resources;

import com.google.common.collect.HashBiMap;
import com.google.common.reflect.Reflection;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import arius.pdv.core.Entity;

/**
 * Created by Arius on 30/08/2017.
 */

public class AndroidUtils {

    public static Object getValor(Field field,ArrayList<Class<?>> array,Entity entity, String campo){

        for(int w = 0; array.size() > w; w++){
            Field[] campos2 = array.get(w).getDeclaredFields();
            for(int y = 0; campos2.length > y; y++){
                if (campos2[y].getType().getGenericSuperclass() != null && campos2[y].getName().equals(campo)) {
                    if (campos2[y].getType().getGenericSuperclass().equals(Entity.class)) {
                        campos2[y].setAccessible(true);

                        try{
                            return field.get(entity);
                        }catch (IllegalAccessException e){
                            e.printStackTrace();
                        }

                    }
                };
                if (campos2[y].getName().equals(campo)) {
                    campos2[y].setAccessible(true);

                    try{
                        Field teste;
                        if (campo == "sigla") {
                            ;
                        }
                        return campos2[y].get(entity);
                    }catch (IllegalAccessException e){
                        e.printStackTrace();
                    }
                }

            }
        }

        try {
            field.setAccessible(true);
            return field.get(field);
        }catch (IllegalAccessException e){
            e.printStackTrace();
        }
        return null;
    }

    public static String valor_Campo(Entity entity, String campo){
        Map<Class<?>, String> array_test = new HashMap<>();
        ArrayList<Class<?>> entity_aux = new ArrayList<Class<?>>();
        array_test.put(entity.getClass(),entity.toString());
        entity_aux.add(entity.getClass());
        try {
            for (int j = 0; entity_aux.size() > j; j++) {
                //entity_aux.get(i) = entity.getClass();
                //while (entity_aux2.getGenericSuperclass() != null) {
                //get(j).getGenericSuperclass() != null) {
                    Field[] campos = entity_aux.get(j).getDeclaredFields();
                    //Field[] campos = entity_aux2.getDeclaredFields();

                    for (int i = 0; campos.length > i; i++) {
                        Object teste = campos[i].getType().getGenericSuperclass();
                        Object teste2 = campos[i].getType();
                        boolean supentity = campos[i].getType().getGenericSuperclass() != null;

                        if (supentity && campos[i].getType().getGenericSuperclass().equals(Entity.class)) {
                            if (!entity_aux.contains((Class<?>) campos[i].getType())) {
                                entity_aux.add(campos[i].getType());
                                array_test.put(campos[i].getType().getClass(),campos[i].getType().getName());
                            }
                            //objarius = campos[i].getType();
//                            try {
//                                ClassLoader classLoader = campos[i].getClass().getClassLoader();
//                                String classname = campos[i].getType().getName();
//                                Entity classteste = (Entity) Class.forName(classname).newInstance();
//                                Field campo_int = campos[i];
//                                campo_int.setAccessible(true);
//                                Object classeteste2 = campo_int.get(entity);
//                                objarius = campos[i].getType().newInstance();
//                                //entity_aux.add((Class<?>) campos[i].getType().newInstance());
//                                Field[] campos2 =objarius.getClass().getDeclaredFields();

//                                for (int w = 0; campos2.length > 0; w++) {
//
//                                    if (campos2[w].getName().equals("descricao")) {
//                                        Field campo_int = campos2[w];
//
//                                        campo_int.setAccessible(true);
//
//                                        String arius = campo_int.get(objarius).toString();
//                                        System.out.print(arius);
//                                    }
//                                }
//                            } catch (InstantiationException e){
//                                e.printStackTrace();
////                            } catch (NoSuchFieldException ex){
////                                ex.printStackTrace();
//                            } catch (ClassNotFoundException ew){
//                                ew.printStackTrace();
//                            }

//                            entity_aux.add(objarius);

                        } else {

                            if (campos[i].getName().equals(campo)) {

                                Field campo_int = campos[i];

                                campo_int.setAccessible(true);

                                String testevalor = getValor(campo_int,entity_aux,entity,campo).toString();

                                Field[] campos2 = entity.getClass().getDeclaredFields();
                                for(int t = 0; campos2.length > t; t++){
                                    if (campos2[t].getType().getGenericSuperclass() != null) {
                                        if (campos2[t].getType().getGenericSuperclass().equals(Entity.class)) {
                                            campos2[t].setAccessible(true);

                                            try{
                                                return campo_int.get(campos2[t].get(entity)).toString();
                                            }catch (RuntimeException e){
                                                e.printStackTrace();
                                            }

                                        }
                                    }
                                }
                                return campo_int.get(entity).toString();
                            }

                        }
                        //}

                    }
//                if (entity_aux.indexOf(entity_aux.get(j).getGenericSuperclass()) == 0)
//                    entity_aux.add((Class<?>) entity_aux.get(j).getGenericSuperclass());
                //if (supentity && campos[i].getType().getGenericSuperclass().equals(Entity.class))
                //    entity_aux2 = ((Class<?>) entity_aux2.getGenericSuperclass());
                //}
                //}
            }
        } catch(IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
}
