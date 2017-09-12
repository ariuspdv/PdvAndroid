package arius.pdv.base;

import java.text.SimpleDateFormat;
import java.util.Date;

import arius.pdv.core.ApplicationException;

public class PdvUtil {

    public static String converteData_texto(Date data){
        return new SimpleDateFormat("dd/mm/yyyy").format(data);
    }

    public static boolean comparar_Datas(Date data1, Date data2) {
        SimpleDateFormat vsdf = new SimpleDateFormat("dd/mm/yyyy");
        try{
            if (vsdf.parse(vsdf.format(data1)).before(vsdf.parse(vsdf.format(data2))) ||
                    vsdf.parse(vsdf.format(data1)).after(vsdf.parse(vsdf.format(data2))))
                return true;
            else
                return false;
        }catch (Exception ex){
            new ApplicationException();
        }
        return false;
    }

}
