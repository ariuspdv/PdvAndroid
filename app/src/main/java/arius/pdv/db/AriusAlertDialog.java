package arius.pdv.db;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import br.com.arius.pdvarius.R;

/**
 * Created by Arius on 21/09/2017.
 */

public class AriusAlertDialog {
    private static View view;
    private static AlertDialog alertDialog;

    public static View getGetView() {
        return view;
    }

    public static AlertDialog getAlertDialog() {
        return alertDialog;
    }

    public static void exibirDialog(Context context, int layout){

        alertDialog = new AlertDialog.Builder(context).create();
        LayoutInflater layoutInflater = LayoutInflater.from(context);

        if (layout != 0) {
            view = layoutInflater.inflate(layout, null);
            alertDialog.setView(view);
        }

        TextView lbtitle = new TextView(context);
        lbtitle.setText("PdvArius");
        lbtitle.setGravity(Gravity.CENTER_HORIZONTAL);
        lbtitle.setBackgroundResource(R.color.colorPrimary);
        lbtitle.setTextSize(30);
        lbtitle.setTextColor(Color.WHITE);
        lbtitle.setTypeface(null, Typeface.BOLD);

        alertDialog.setCustomTitle(lbtitle);

        alertDialog.show();

    }

}
