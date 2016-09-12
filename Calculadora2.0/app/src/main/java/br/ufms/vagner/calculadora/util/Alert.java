package br.ufms.vagner.calculadora.util;

import android.content.Context;
import android.widget.Toast;

public class Alert {

    public static void information(Context context, int resourceId,
                                   Object... message) {
        String stringResourceId = context.getResources().getString(resourceId);
        showToast(context, String.format(stringResourceId, message));
    }

    public static void information(Context context, String message) {
        showToast(context, message);
    }

    public static void confirmation(Context context, String title, String messsage) {

    }

    private static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}