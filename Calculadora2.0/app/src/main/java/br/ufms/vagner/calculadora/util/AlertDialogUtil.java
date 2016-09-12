package br.ufms.vagner.calculadora.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
public class AlertDialogUtil {

    android.support.v7.app.AlertDialog.Builder alert;
    boolean confirmarAcao;

    public AlertDialogUtil() {

    }

    public boolean getYesNoWithExecutionStop(String title, String message, Context context) {
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message mesg) {
                throw new RuntimeException();
            }
        };
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setTitle(title);
        alert.setMessage(message);
        alert.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                confirmarAcao = true;
                handler.sendMessage(handler.obtainMessage());
            }
        });
        alert.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                confirmarAcao = false;
                handler.sendMessage(handler.obtainMessage());
            }
        });
        alert.show();

        try {
            Looper.loop();
        } catch (RuntimeException e2) {
        }

        return confirmarAcao;
    }
}
