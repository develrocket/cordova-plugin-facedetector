package com.metsakuur.escaredemo.http;

import android.content.ContentValues;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.CookieManager;

import com.metsakuur.ufacedetectorlite.util.UFaceLog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

/**
 * This is communication sample. 
 */
public class UFaceHttpManager {

    public static final String POST = "POST";
    public static final String GET = "GET";

    int timeOut = 5000;

    public static volatile UFaceHttpManager instance;

    public static UFaceHttpManager getInstance() {
        if (instance == null) instance = new UFaceHttpManager();
        return instance;
    }

    public void requestUrl(String apiUrl, String method, ContentValues params, UFaceHttpListener UFaceHttpUrlListener) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                URL url = null;
                HttpURLConnection urlConnection = null;

                // the parameter that is added to the back of URL to send.
                StringBuffer sbParams = new StringBuffer();
                /**
                 * 1. connect StringBuffer to parameter
                 **/
                // If no data to send, empty parameter.
                if (params == null)
                    sbParams.append("");
                    // If data to send, input parameter.
                else {
                    try {
                        // If parameters are more than 2, varient is created to switch for add &.
                        boolean isAnd = false;
                        // parameter key and value.
                        String key;
                        String value;

                        for (Map.Entry<String, Object> parameter : params.valueSet()) {
                            key = parameter.getKey();
                            value = parameter.getValue().toString();

                            // 
                            if (isAnd)
                                sbParams.append("&");

                            sbParams.append(key).append("=").append(value);

                            // 
                            if (!isAnd)
                                if (params.size() >= 2)
                                    isAnd = true;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        sbParams.append("");
                    }
                }

                try {
                    url = new URL(apiUrl);
                    urlConnection = (HttpURLConnection) url.openConnection();

                    // [2-1]. urlConn setting.
                    urlConnection.setReadTimeout(timeOut);
                    urlConnection.setConnectTimeout(timeOut);
                    urlConnection.setRequestMethod(method);
                    urlConnection.setDoInput(true);
                    urlConnection.setDoOutput(true);
                    urlConnection.setUseCaches(false);
                    urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");

                    // cookie
                    CookieManager cookieManager = CookieManager.getInstance();
                    String cookie = cookieManager.getCookie(urlConnection.getURL().toString());

                    if (cookie != null) {
                        UFaceLog.e("cookie is not null");
                        urlConnection.setRequestProperty("Cookie", cookie);
                    }

                    UFaceLog.d("---- getURL =" + urlConnection.getURL());

                    // [2-2]. send parameter and read data.
                    String strParams = sbParams.toString(); //save parameters in sbParams to string. ex)id=id1&pw=123;
                    OutputStream os = urlConnection.getOutputStream();
                    os.write(strParams.getBytes("UTF-8")); // print in output stream.
                    os.flush(); // flush output stream.
                    os.close(); // 

                    if (urlConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                        Log.i("http : ", "" + urlConnection.getResponseCode());
                        if (UFaceHttpUrlListener != null)
                            UFaceHttpUrlListener.onResponse("{\"code\":\"" + urlConnection.getResponseCode() + "\"}");
                        return;
                    }

                    BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8"));

                    String line;
                    String page = "";

                    while ((line = reader.readLine()) != null) {
                        page += line;
                    }
                    UFaceLog.d("response page : " + page);
                    if (!TextUtils.isEmpty(page)) {
                        String code = getValueFromJson(page, "code");
                        if (code.equals("00000")) {
                            if (checkHash(page)) {
                                if (UFaceHttpUrlListener != null) {
                                    UFaceHttpUrlListener.onResponse(page);
                                    return;
                                }
                            }
                        } else {
                            if (UFaceHttpUrlListener != null) {
                                UFaceHttpUrlListener.onResponse(page);
                                return;
                            }
                        }
                    }

                    if (UFaceHttpUrlListener != null)
                        UFaceHttpUrlListener.onResponse("{\"code\":\"99999\"}");

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    if (UFaceHttpUrlListener != null)
                        UFaceHttpUrlListener.onResponse("{\"code\":\"99999\"}");
                } catch (IOException e) {
                    e.printStackTrace();
                    if (UFaceHttpUrlListener != null)
                        UFaceHttpUrlListener.onResponse("{\"code\":\"99999\"}");
                } catch (Exception e) {
                    if (urlConnection != null)
                        urlConnection.disconnect();
                    e.printStackTrace();
                    if (UFaceHttpUrlListener != null)
                        UFaceHttpUrlListener.onResponse("{\"code\":\"99999\"}");
                } finally {
                    if (urlConnection != null)
                        urlConnection.disconnect();
                }
            }
        });
        thread.start();
    }

    /**
     *
     * @param strResponse
     * @return
     */
    public boolean checkHash(String strResponse) {
        try {
            JSONObject obj = new JSONObject(strResponse);
            if (obj.has("hash")) {
                String strHash = obj.getString("hash");
                obj.remove("hash");
                obj.put("metsakuur_uface", "metsakuur_uface");
                UFaceLog.d("checkHash : " + obj.toString());
                if (strHash != null && strHash.length() > 0) {
                    String strGenHash = sha256(obj.toString());
                    if (strGenHash.equals(strHash)) {
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    return false;
                }
            } else {
                return false;
            }

        } catch (JSONException e) {
            e.printStackTrace();

            return false;
        }
    }

    /**
     *
     * @param JsonObj
     * @param strKey
     * @return
     */
    private String getValueFromJson(JSONObject JsonObj, String strKey) {
        String strValue = "";
        if (!JsonObj.has(strKey)) {
            return strValue;
        }
        try {
            strValue = (String) JsonObj.getString(strKey);
        } catch (JSONException e) {
            try {
                int nValue = JsonObj.getInt(strKey);
                strValue = String.format("%d", nValue);
            } catch (JSONException e1) {
                e1.printStackTrace();
                return "";
            }
        }
        return strValue;
    }


    /**
     *
     * @param strJsonData
     * @param strKey
     * @return
     */
    public String getValueFromJson(String strJsonData, String strKey) {
        if (TextUtils.isEmpty(strJsonData)) return "";
        try {
            JSONObject jsonObject = new JSONObject(strJsonData);
            return getValueFromJson(jsonObject, strKey);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private static String sha256(String str) {
        String SHA = "";
        try {
            MessageDigest sh = MessageDigest.getInstance("SHA-256");
            sh.update(str.getBytes());
            byte byteData[] = sh.digest();
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < byteData.length; i++)
                sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
            SHA = sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            SHA = null;
        }

        return SHA;
    }
}
