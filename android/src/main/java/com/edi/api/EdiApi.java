package com.edi.api;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

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
        if (isMyServiceRunning(ServiceFutebol.class)) {
            System.out.println("ta rodando");
        }else{
            System.out.println("nao ta rodando");
            getReactApplicationContext().startService(new Intent(getReactApplicationContext(), ServiceFutebol.class));
        }

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
