package com.edi.api;


import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class EdiApi extends ReactContextBaseJavaModule {


    public EdiApi(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @Override
    public String getName() {
        return "EdiApi";
    }

    @Override
    public Map<String, Object> getConstants() {
        final Map<String, Object> constants = new HashMap<>();
        return constants;
    }

    @ReactMethod
    public void publicarTwitter( String msg, Callback call) {
        String link = uteis.getPackageAppInstalado(this.getCurrentActivity(),"twitter");
        if (link!=null && !link.isEmpty()){
            uteis.compartilharMSG(this.getCurrentActivity(),link,msg);
        }else{
            call.invoke("Não encontrado");
        }
    }

    @ReactMethod
    public void getHttp( final String url, final Callback callback) {
       // url = "http://192.168.4.1/read/status";
        //Log.i("dd", "getHttp:"+url);


//        class ConectarNT extends AsyncTask<String, Integer, String> {
//            protected String doInBackground(String... config) {
//                try {
//                    OkHttpClient client = new OkHttpClient();
//                    Request request = new Request.Builder()  .url(url) .build();
//                    Response response = client.newCall(request).execute();
//                    String msg =  response.body().string();
//                   // call.invoke(msg);
//                   // Log.i("dd", "getHttp:"+msg);
//                    return msg;
//
//                } catch (IOException e) {
//                    Log.i("dd", "getHttp:"+e.toString());
//                    return "{}";
//                }
//            }
//
//            protected void onProgressUpdate(Integer... progress) {
//
//            }
//
//            protected void onPostExecute(String result) {
//                callback.invoke(result);
//            }
//        }
//        new ConectarNT().execute("");


        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    try {
                        OkHttpClient client = new OkHttpClient();
                        Request request = new Request.Builder()  .url(url) .build();
                        Response response = client.newCall(request).execute();
                        String msg =  response.body().string();
                        // call.invoke(msg);
                         Log.i("dd", "getHttp:"+msg);
                        callback.invoke( msg);

                    } catch (IOException e) {
                        Log.i("dd", "getHttp:"+e.toString());
                        callback.invoke( "{}");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        thread.start();

    }


    @ReactMethod
    public void publicarWhatsApp( String msg , Callback call) {
        String link = uteis.getPackageAppInstalado(this.getCurrentActivity(),"whatsapp");
        if (link!=null && !link.isEmpty()){
            uteis.compartilharMSG(this.getCurrentActivity(),link,msg);
        }else{
            call.invoke("Não encontrado");
        }
    }

    @ReactMethod
    public void playVideo(String url){
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse(url), "video/*");
        String camera = uteis.getPackageAppInstaladoVideo(this.getCurrentActivity(),intent,"android");
        if(camera!=null && !camera.isEmpty()){
            intent.setPackage(camera);
        }else{
            camera = uteis.getPackageAppInstaladoVideo(this.getCurrentActivity(),intent,"");
            if(camera!=null && !camera.isEmpty()){
                intent.setPackage(camera);
            }
        }
        this.getCurrentActivity().startActivity(intent);
    }

    @ReactMethod
    public void mapsVersao(  Callback call) {
        int v = 0;
        try {
            v = this.getCurrentActivity().getPackageManager().getPackageInfo("com.google.android.gms", 0 ).versionCode;
            Log.i("MAPS","VERSAO: "+v);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        call.invoke(v);
     }


    @ReactMethod
    public void abrirMaps( String endereco ) {
        String uri = null;
        try {
            uri = "http://maps.google.com/maps?q="+ URLEncoder.encode(endereco,"utf-8");
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
