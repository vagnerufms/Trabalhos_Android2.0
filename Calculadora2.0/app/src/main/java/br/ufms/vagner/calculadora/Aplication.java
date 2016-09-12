package br.ufms.vagner.calculadora;

import android.app.Application;

public class Aplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        com.firebase.client.Firebase.setAndroidContext(this);
    }
}
