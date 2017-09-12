package br.com.arius.pdvarius;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Looper;

import arius.pdv.core.UserException;

/**
 * Created by S-Shustikov on 08.06.14.
 */
public class ReportHelper implements Thread.UncaughtExceptionHandler {
    private AlertDialog dialog;
    private Context context;
    private Throwable throwable;

    public ReportHelper(Context context) {
        this.context = context;
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        this.throwable = ex;
        showToastInThread("OOPS!");
    }

    public void showToastInThread(final String str){
        new Thread() {
            @Override
            public void run() {
                Looper.myLooper().prepare();

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Application was stopped...")
                        .setPositiveButton("Report to developer about this problem.", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                            }
                        })
                        .setNegativeButton("Fechar", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // Not worked!
                                if (throwable.getClass() == UserException.class) {
                                    dialog.dismiss();
                                    Looper.myLooper().quit();
                                } else {
                                    dialog.dismiss();
                                    System.exit(0);
                                    android.os.Process.killProcess(android.os.Process.myPid());
                                }
                            }
                        });

                dialog = builder.create();

                if(!dialog.isShowing())
                    dialog.show();
                Looper.myLooper().loop();

            }
        }.start();
    }
}