package com.edi.api;


import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import java.util.Collection;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.text.DecimalFormat;
import java.text.Normalizer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.Drawable;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class uteis {


    public static void compartilharRedesSocial(final Context act, final String msg) throws JSONException {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        PackageManager pm = act.getPackageManager();
        final List<ResolveInfo> activityList = new ArrayList<ResolveInfo>();
        final List<JSONObject> objectos = new ArrayList<JSONObject>();

        List<ResolveInfo> lista = pm.queryIntentActivities(sharingIntent, 0);
        String name;
        for (int i = 0; i < lista.size(); i++) {
            ApplicationInfo appInfo = null;
            try {
                appInfo = pm.getApplicationInfo(lista.get(i).activityInfo.packageName, 0);
                JSONObject d = new JSONObject();
                d.put("nome", appInfo.loadLabel(pm).toString());

                //       Log.i("ss",appInfo.packageName);
                if (appInfo.packageName.equals("com.google.android.apps.plus")) {
                    activityList.add(lista.get(i));
                    objectos.add(d);
                }
                if (appInfo.packageName.contains("facebook")) {
                    activityList.add(lista.get(i));
                    objectos.add(d);
                }
                if (appInfo.packageName.contains("twitter")) {
                    activityList.add(lista.get(i));
                    objectos.add(d);
                }
                if (appInfo.packageName.contains("whatsapp")) {
                    activityList.add(lista.get(i));
                    objectos.add(d);
                }
            } catch (NameNotFoundException e) {
                e.printStackTrace();
            }
            //		            icons.add(appInfo.loadIcon(getPackageManager()));
        }
        BaseAdapter AdapterCompartilhar = new BaseAdapter() {
            @Override
            public View getView(int position, View vi, ViewGroup parent) {
                vi = new RelativeLayout(act);
                ;
                final JSONObject data = objectos.get(position);

                RelativeLayout cellview_35 = (RelativeLayout) vi;
                cellview_35.setBackgroundColor(Color.parseColor("#ffffff"));
                int altura = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 45, act.getResources().getDisplayMetrics());
                TextView label_39_37 = new TextView(act);
                label_39_37.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
                label_39_37.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17);
                label_39_37.setVisibility(View.VISIBLE);
                label_39_37.setTextColor(Color.parseColor("#444444"));
                try {
                    label_39_37.setText(data.getString("nome"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                RelativeLayout.LayoutParams lay = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT, altura);
                lay.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                lay.leftMargin = (altura / 2);
                label_39_37.setLayoutParams(lay);
                cellview_35.addView(label_39_37);
                return vi;
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public Object getItem(int position) {
                return objectos.get(position);
            }

            @Override
            public int getCount() {
                return objectos.size();
            }
        };
        // Create alert dialog box
        AlertDialog.Builder builder = new AlertDialog.Builder(act);
        builder.setTitle("Compartilhar");
        if (objectos.isEmpty()) {
            builder.setTitle("Não encontrou apps de compartilhamento...");
        }
        builder.setAdapter(AdapterCompartilhar, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
//				alert.cancel();
                ResolveInfo info = (ResolveInfo) activityList.get(item);
                Intent intent = new Intent(android.content.Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.setPackage(info.activityInfo.packageName);
                intent.putExtra(Intent.EXTRA_TEXT, msg);
//				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                ((Activity) act).startActivity(intent);
            }// end onClick
        });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    static public void abrirApp(String package_name, Context context) {
        if (package_name != null && !package_name.equals("")) {
            String[] par = package_name.split(";");
            package_name = par[0];
        } else {
            return;
        }
        //Log.i("",package_name);
        try {
            Intent LaunchIntent = context.getPackageManager().getLaunchIntentForPackage(package_name);
            context.startActivity(LaunchIntent);
        } catch (Exception e) {
            try {
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + package_name)));
            } catch (Exception e2) {
            }
        }
    }

    public static String getPackageAppInstalado(final Context context, String nome) {
        String re = null;
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> lista = pm.queryIntentActivities(sharingIntent, 0);
        for (int i = 0; i < lista.size(); i++) {
            ApplicationInfo appInfo = null;
            try {
                appInfo = pm.getApplicationInfo(lista.get(i).activityInfo.packageName, 0);
                Log.i("ss", appInfo.packageName);
                if (appInfo.packageName.equals(nome) || appInfo.packageName.contains(nome)) {
                    re = appInfo.packageName;
//                    Log.i("apps",re);
                }
            } catch (NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        return re;
    }

    public static String getPackageAppInstaladoVideo(final Context context,Intent sharingIntent, String nome) {
        String re = null;
//        Intent sharingIntent = new Intent(android.content.Intent.ACTION_VIEW);
//        sharingIntent.setType("video/*");
        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> lista = pm.queryIntentActivities(sharingIntent, 0);
        for (int i = 0; i < lista.size(); i++) {
            ApplicationInfo appInfo = null;
            try {
                appInfo = pm.getApplicationInfo(lista.get(i).activityInfo.packageName, 0);
                Log.i("ss", appInfo.packageName);
                if (appInfo.packageName.equals(nome) || appInfo.packageName.contains(nome)) {
                    re = appInfo.packageName;
//                    Log.i("apps",re);
                }
            } catch (NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        return re;
    }

    static public void compartilharMSG(final Context context, String package_nome, String msg) {
        Intent intent = new Intent(android.content.Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.setPackage(package_nome);
        intent.putExtra(Intent.EXTRA_TEXT, msg);
//		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        context.startActivity(intent);
    }
}

