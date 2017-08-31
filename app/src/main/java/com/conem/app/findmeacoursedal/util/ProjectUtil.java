package com.conem.app.findmeacoursedal.util;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashSet;
import java.util.Set;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * Created by mj on 5/26/2017.
 */

public class ProjectUtil {

    private static final String SET_KEY = "set_key";
    protected static final String DAL_PREFERENCE = "DAL_PREFERENCE";

    public static void setSharedSet(Context context, Set<String> savedSet) {
        context.getSharedPreferences(DAL_PREFERENCE, Context.MODE_PRIVATE).edit().clear().apply();
        context.getSharedPreferences(DAL_PREFERENCE, Context.MODE_PRIVATE).edit()
                .putStringSet(SET_KEY, savedSet).apply();
    }

    public static Set<String> getSharedPreferencesString(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(DAL_PREFERENCE, Context.MODE_PRIVATE);
        return sharedPreferences.getStringSet(SET_KEY, new HashSet<>());
    }

    /**
     * Read url text
     *
     * @param url url to read
     * @return return Url body in string
     */
    public static String readUrl(String url) {

        TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            @Override
            public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
                // Not implemented
            }

            @Override
            public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
                // Not implemented
            }
        } };

        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        StringBuilder stringBuilder = new StringBuilder();
        try {
            URL url1 = new URL(url);

            HttpsURLConnection urlConnection =
                    (HttpsURLConnection)url1.openConnection();
            String inputLine;
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(urlConnection.getInputStream()));
            while ((inputLine = in.readLine()) != null)
                stringBuilder.append(inputLine);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return stringBuilder.toString();
    }
}
