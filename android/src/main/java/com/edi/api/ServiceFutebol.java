package com.edi.api;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;
import android.content.res.Resources;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;



/**
 * Created by gdk1a on 23/03/2018.
 */

public class ServiceFutebol extends Service {

    public IBinder onBind(Intent intent) {
        return null;
    }

    public Thread service = null;
    public Threadzin myrunnable = null;
    public static String email = "";
    //private String urlAdress = "http://clube-futebol.1app.com.br:7015/v1/user_local/pesquisa";
    public static String urlAdress = "http://192.168.25.194:7015/v1/alertas/pesquisa_ultimos";
    SharedPreferences settings = null;
    HttpURLConnection con = null;
    URL url = null;
    int lastid = 0;
    JSONObject jsonParam = null;
    JSONObject item = null;
    OutputStreamWriter wr;
    StringBuilder sb;
    int HttpResult;
    BufferedReader br;
    String line;
    JSONArray retorno;
    SharedPreferences.Editor editor;
    NotificationManager notificationManager = null;

    public String getUrlAdress() {
        return urlAdress;
    }

    public void setUrlAdress(String urlAdress) {
        this.urlAdress = urlAdress;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void notificarRunning() {
        //Intent intent = new Intent(getBaseContext(), MainActivity.class);
        Intent intent = new Intent();
        // use System.currentTimeMillis() to have a unique ID for the pending intent
        PendingIntent pIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, 0);
        int id = Integer.parseInt((System.currentTimeMillis() / 1000) + "");
        // build notification
        // the addAction re-use the same intent to keep the example short
        Notification.Builder n = new Notification.Builder(this).setContentTitle("O serviço esta rodando" + id)
                .setContentText("Serviço de notificações esta rodando").setSmallIcon(android.R.drawable.star_big_on)
                .setContentIntent(pIntent).setOngoing(true).setAutoCancel(false);

        // notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(1, n.build());

    }

    public static int randInt(int min, int max) {

        // Usually this can be a field rather than a method variable
        Random rand = new Random();

        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        int randomNum = rand.nextInt((max - min) + 1) + min;

        return randomNum;
    }

    public void notificar(String title, String content) {

        //Intent intent = new Intent(getBaseContext(), MainActivity.class);
        Intent intent = new Intent();

        PendingIntent pIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, 0);
        int id = Integer.parseInt((System.currentTimeMillis() / 1000) + "") + randInt(1, 30000);

        Notification.Builder n = new Notification.Builder(this).setContentTitle(title).setContentText(content)
               .setContentIntent(pIntent).setAutoCancel(true);
                n.setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 });
                n.setSound(Uri.parse("android.resource://com.m1app.clubefm/raw/entrada"));

        Resources res = getApplicationContext().getResources();
        String packageName = getApplicationContext().getPackageName();
        int smallIconResId = res.getIdentifier("ic_notification", "mipmap", packageName);
        n.setSmallIcon(smallIconResId);

        
        //    notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(id, n.build());

    }

    public void cancelNot() {
        //    notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(1);
    }

    public void buscaInfos() throws Exception {
        if (email.equalsIgnoreCase("")) {
            return;
        }

        try {
            settings = getApplicationContext().getSharedPreferences("localsave", 0);
            lastid = settings.getInt("lastid", 0);
        } catch (Exception e) {
            lastid = 0;
        }

        url = new URL(getUrlAdress());

        con = (HttpURLConnection) url.openConnection();
        con.setDoOutput(true);
        con.setDoInput(true);
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestProperty("Accept", "application/json");
        con.setRequestProperty("token_api", "0d7366a14e0f0fd2df0161c69kl1779983");
        con.setRequestMethod("POST");

        jsonParam = new JSONObject();
        jsonParam.put("id", lastid);
        jsonParam.put("email", email);

        wr = new OutputStreamWriter(con.getOutputStream());
        wr.write(jsonParam.toString());
        wr.flush();
        sb = new StringBuilder();
        HttpResult = con.getResponseCode();
        if (HttpResult == HttpURLConnection.HTTP_OK) {
            br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"));
            String line = null;
            while ((line = br.readLine()) != null) {
                sb.append(line + "\n");
            }
            br.close();
        }
        con.disconnect();
        retorno = new JSONArray(sb.toString());

        for (int i = 0; i < retorno.length(); i++) {
            item = retorno.getJSONObject(i);
            lastid = Integer.parseInt(item.get("_id").toString());
            notificar(item.get("title").toString(), item.get("body").toString());
            Thread.sleep(100);
        }

        settings = getApplicationContext().getSharedPreferences("localsave", 0);
        editor = settings.edit();
        editor.putInt("lastid", lastid);
        editor.apply();

    }

    public void stopService() {
        System.out.println("antes do if do mata servico");
        notificationManager.cancel(1);
        if (service != null && service.isAlive()) {
            System.out.println("entrou no if do mata servico");
            myrunnable.stop();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Let it continue running until it is stopped.
        System.out.println("entrou no start do  serviço");
         if (Build.VERSION.RELEASE.startsWith("6")) {

        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        System.out.println("Comecçou serviço");
        stopService();
        Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();
        //notificarRunning();
        myrunnable = new Threadzin();
        service = new Thread(myrunnable);
        service.start();
        return START_STICKY;
         } else {
             System.out.println("Serviço nao iniciado. Versão do android não é 6");
             Toast.makeText(this, "Serviço nao iniciado. Versão do android não é 6", Toast.LENGTH_LONG).show();
             return START_STICKY;
         }

    }

    @Override
    public void onDestroy() {
        stopService();
        super.onDestroy();
        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();
    }

    class Threadzin implements Runnable {
        private volatile boolean exit = false;

        public void run() {
            while (!exit) {
                try {
                    Thread.sleep(5000);
                    buscaInfos();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        public void stop() {
            exit = true;
        }
    }

}
