package com.edi.api;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;
import android.os.Build;

import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class EdiApi extends ReactContextBaseJavaModule {

    ServiceFutebol servico = null;
    private static final String ENABLED_NOTIFICATION_LISTENERS = "enabled_notification_listeners";
    private static final String ACTION_NOTIFICATION_LISTENER_SETTINGS = "android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS";

    public EdiApi(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    public boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getReactApplicationContext()
                .getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String getName() {
        return "EdiApi";
    }

    @ReactMethod
    public void startServiceFutebol() {
        System.out.println("chamou start");
        Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
        if (isMyServiceRunning(ServiceFutebol.class)) {
            System.out.println("ta rodando");
        } else {
            System.out.println("nao ta rodando");
            getReactApplicationContext().startService(new Intent(getReactApplicationContext(), ServiceFutebol.class));
        }

    }

    @ReactMethod
    public void getOsVersion(Callback call) {
        call.invoke(Build.VERSION.RELEASE);
    }

    @ReactMethod
    public void setIgnorarVersaoAndroid(boolean  ignorarVersaoAndroid) {
        ServiceFutebol.ignorarVersaoAndroid =  ignorarVersaoAndroid;
    }

    @ReactMethod
    public void setTokenfirebase(String tokenfirebase) {
        ServiceFutebol.tokenfirebase = tokenfirebase;
    }

    @ReactMethod
    public void getTokenfirebase(Callback call) {
        call.invoke(ServiceFutebol.tokenfirebase);
    }


    @ReactMethod
    public void setEmailServiceFutebol(String email) {
        ServiceFutebol.email = email;
    }

    @ReactMethod
    public void getEmailServiceFutebol(Callback call) {
        call.invoke(ServiceFutebol.email);
    }

    @ReactMethod
    public void setAdressServiceFutebol(String adress) {
        ServiceFutebol.urlAdress = adress;
    }

    @ReactMethod
    public void getAdressServiceFutebol(Callback call) {
        call.invoke(ServiceFutebol.urlAdress);
    }

    @ReactMethod
    public void stopServiceFutebol(Callback call) {
        getReactApplicationContext().stopService(new Intent(getReactApplicationContext(), ServiceFutebol.class));
        //ServiceFutebol.stopService();
    }

    @Override
    public Map<String, Object> getConstants() {
        final Map<String, Object> constants = new HashMap<>();
        return constants;
    }

    @ReactMethod
    public void publicarTwitter(String msg, Callback call) {
        String link = uteis.getPackageAppInstalado(this.getCurrentActivity(), "twitter");
        if (link != null && !link.isEmpty()) {
            uteis.compartilharMSG(this.getCurrentActivity(), link, msg);
        } else {
            call.invoke("Não encontrado");
        }
    }

    @ReactMethod
    public void publicarWhatsApp(String msg, Callback call) {
        String link = uteis.getPackageAppInstalado(this.getCurrentActivity(), "whatsapp");
        if (link != null && !link.isEmpty()) {
            uteis.compartilharMSG(this.getCurrentActivity(), link, msg);
        } else {
            call.invoke("Não encontrado");
        }
    }

    @ReactMethod
    public void playVideo(String url) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse(url), "video/*");
        String camera = uteis.getPackageAppInstaladoVideo(this.getCurrentActivity(), intent, "android");
        if (camera != null && !camera.isEmpty()) {
            intent.setPackage(camera);
        } else {
            camera = uteis.getPackageAppInstaladoVideo(this.getCurrentActivity(), intent, "");
            if (camera != null && !camera.isEmpty()) {
                intent.setPackage(camera);
            }
        }
        this.getCurrentActivity().startActivity(intent);
    }

    @ReactMethod
    public void mapsVersao(Callback call) {
        int v = 0;
        try {
            v = this.getCurrentActivity().getPackageManager().getPackageInfo("com.google.android.gms", 0).versionCode;
            Log.i("MAPS", "VERSAO: " + v);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        call.invoke(v);
    }

    @ReactMethod
    public void abrirMaps(String endereco) {
        String uri = null;
        try {
            uri = "http://maps.google.com/maps?q=" + URLEncoder.encode(endereco, "utf-8");
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
            intent.setFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
            intent.setFlags(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
            intent.setData(Uri.parse(uri));
            this.getCurrentActivity().startActivity(intent);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
