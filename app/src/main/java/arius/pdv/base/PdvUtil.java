package arius.pdv.base;

import java.text.SimpleDateFormat;
import java.util.Date;

import arius.pdv.core.ApplicationException;

public class PdvUtil {
	
	public static String converteData_texto(Date data){
		return new SimpleDateFormat("dd/MM/yyyy").format(data);
	}
	
    public static boolean comparar_Datas(Date data1, Date data2) {
        SimpleDateFormat vsdf = new SimpleDateFormat("dd/MM/yyyy");
        try{
        	return (vsdf.parse(vsdf.format(data1)).equals(vsdf.parse(vsdf.format(data2))));
        }catch (Exception ex){
        	new ApplicationException();
        }
		return false;
    }

	public static boolean entre_Datas(Date data,Date data1, Date data2) {
		SimpleDateFormat vsdf = new SimpleDateFormat("dd/MM/yyyy");
		try{
			if ((vsdf.parse(vsdf.format(data)).after(vsdf.parse(vsdf.format(data1))) &&
					vsdf.parse(vsdf.format(data)).before(vsdf.parse(vsdf.format(data2)))) ||
				(vsdf.parse(vsdf.format(data)).equals(vsdf.parse(vsdf.format(data1))) ||
					vsdf.parse(vsdf.format(data)).equals(vsdf.parse(vsdf.format(data2)))))
					return true;


		}catch (Exception ex){
			new ApplicationException();
		}
		return false;
	}

}
