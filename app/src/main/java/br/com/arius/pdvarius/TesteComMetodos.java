package br.com.arius.pdvarius;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import arius.pdv.base.Produto;
import arius.pdv.base.ProdutoDao;
import arius.pdv.core.AppContext;
import arius.pdv.core.Entity;
import arius.pdv.core.FuncionaisFilters;

/**
 * Created by Arius on 26/09/2017.
 */

public class TesteComMetodos {

    private Entity baseDados;

    private Produto produto;
    private String entity;
    private Object ultimo;
    private Map<Integer, Object> arqMetodos = new HashMap<>();

    private Object valor(){
        return this.baseDados;
    }

    private Method encontra_valor(Method[] metodos, Entity entity){
        String campo = "descricao";
        String sentity = null;
        for (Method lmetodos : metodos){

            if (!lmetodos.getReturnType().equals(Void.TYPE)){

                Object tipoMetodo = lmetodos.getReturnType();
                Object classe = lmetodos.getReturnType().getGenericSuperclass();
                String tete = lmetodos.getDeclaringClass().getSimpleName();

                if (lmetodos.getReturnType().getGenericSuperclass() == null ||
                        !lmetodos.getReturnType().getGenericSuperclass().equals(Entity.class)) {

                    String nomeMetodo = lmetodos.getName().substring(3);
                    System.out.print(nomeMetodo);
                    if (nomeMetodo.toLowerCase().equals(campo.toLowerCase()) &&
                            lmetodos.getDeclaringClass().getSimpleName().toLowerCase().equals(this.entity.toLowerCase())) {

                        Object teste = arqMetodos.get(lmetodos.hashCode());
                        if (teste == null){
                            teste = entity;
                            arqMetodos.put(lmetodos.hashCode(), teste);
                        } else {
                            teste = entity;
                            arqMetodos.put(lmetodos.hashCode(), teste);
                        }

                        return lmetodos;
                    }
                } else {
                    //this.entity = lmetodos.getReturnType().getSimpleName();
                    String nomeMetodo = lmetodos.getName().substring(3);
                    System.out.print(nomeMetodo);
                    try {
                        Object ret = lmetodos.invoke(valor(),new Object[]{});

                        Method ret2 = encontra_valor(lmetodos.getReturnType().getDeclaredMethods(),(Entity) ret);

                        ultimo = lmetodos.invoke(this.baseDados);

                        String ret3 = ret2.invoke(ret, new Object[]{}).toString();

                        Object tt = ret2.getDefaultValue();

                        return ret2;
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return null;
    }

    public void capturaMetodo(){
        try {
            this.produto = AppContext.get().getDao(ProdutoDao.class).find(1);
            this.entity = "produto";//this.produto.getClass().getSimpleName();
            Method mteste = encontra_valor(this.produto.getClass().getDeclaredMethods(), baseDados);

            for (Produto lproduto : AppContext.get().getDao(ProdutoDao.class).listCache(new FuncionaisFilters<Produto>() {
                @Override
                public boolean test(Produto p) {
                    return true;
                }
            })){
                Method mproduto = encontra_valor(lproduto.getClass().getDeclaredMethods(),(Entity) lproduto);
                Object obj = arqMetodos.get(mproduto.hashCode());
                Object obj2 = mproduto.invoke(obj);
                String st = obj2.toString();
            }


//            Method m1 = this.produto.getClass().getDeclaredMethod("getUnidadeMedida");
//            Method m2 = m1.getReturnType().getDeclaredMethod("getDescricao");
//            Method m3 = m2.getDeclaringClass().getSuperclass().getDeclaredMethod("getId");
//
//            Object t1 = m1.invoke(this.produto,new Object[]{});
//            Object t2 = m2.invoke(m1.invoke(this.produto,new Object[]{}), new Object[]{});
//            Object t3 = m3.invoke(
//                    //m2.invoke(
//                            m1.invoke(this.produto,new Object[]{}),
//                    //new Object[]{}),
//            new Object[]{});
//
            mteste.setAccessible(true);
            Object vt = mteste.getDeclaringClass().getDeclaringClass();
            Object valor = mteste.invoke(null, new Object[]{});
            String v = mteste.toString();
            System.out.print("teste");
        }catch (InvocationTargetException e1) {
        }catch( IllegalAccessException e){
//        } catch (NoSuchMethodException e) {
//            e.printStackTrace();
        }
    }

}
