package com.metsakuur.escaredemo.http;

import android.content.ContentValues;
import android.os.Build;
import android.util.ArrayMap;

import com.metsakuur.escaredemo.UFaceConfig;
import com.metsakuur.ufacedetectorlite.util.UFaceLog;
import com.metsakuur.ufacefrclient.UFaceFRClient;

public class UFaceHttpRequestManager {

    public static final UFaceHttpRequestManager INSTANCE;
    private static String os = getDeviceInfo();

    private UFaceHttpRequestManager() {
    }

    public final String getOs() {
        return os;
    }

    public final void setOs(String var1) {
        os = var1;
    }

    static {
        UFaceHttpRequestManager a = new UFaceHttpRequestManager();
        INSTANCE = a;
        os = getDeviceInfo();
    }

    public static final String getDeviceInfo() {
        String strAndroid = "android";
        String strVer = Build.VERSION.RELEASE;
        String strBrand = Build.BRAND;
        String strModel = Build.MODEL;
        return String.format("%s;%s;%s;%s", strAndroid, strVer, strBrand, strModel);
    }


    public void requestRegist(String url, String custNo, String uuid, byte[] image, UFaceHttpListener uFaceHttpListener) {
        String data = UFaceFRClient.getApiData(UFaceConfig.registType, custNo, UFaceConfig.channel, custNo, os, image, uuid);
        ContentValues contentValues = new ContentValues();

        contentValues.put("data", data);
        contentValues.put("type", UFaceConfig.registType);
        UFaceHttpManager.getInstance().requestUrl(url, UFaceHttpManager.POST, contentValues, uFaceHttpListener);
    }


    public void requestVerify(String url, String uuid, String custNo, byte[] image, UFaceHttpListener uFaceHttpListener) {
        String data = UFaceFRClient.getApiData(UFaceConfig.verifyType, custNo, UFaceConfig.channel, custNo, os, image, uuid);
        ContentValues contentValues = new ContentValues();

        contentValues.put("data", data);
        contentValues.put("type", UFaceConfig.verifyType);
        UFaceHttpManager.getInstance().requestUrl(url, UFaceHttpManager.POST, contentValues, uFaceHttpListener);
    }

    public void requestBulkVerify(String url, String uuid, byte[] image, UFaceHttpListener uFaceHttpListener) {
        String data = UFaceFRClient.getApiData(UFaceConfig.bulkVerifyType, "", UFaceConfig.channel, "", os, image, uuid);
        ContentValues contentValues = new ContentValues();

        contentValues.put("data", data);
        contentValues.put("type", UFaceConfig.bulkVerifyType);
        UFaceHttpManager.getInstance().requestUrl(url, UFaceHttpManager.POST, contentValues, uFaceHttpListener);
    }

    public void requestDelete(String url, String custNo, String uuid, UFaceHttpListener uFaceHttpListener) {
        byte[] byteArray = new byte[0];
        String data = UFaceFRClient.getApiData(UFaceConfig.deleteType, custNo, UFaceConfig.channel, custNo, os, byteArray, uuid);
        ContentValues contentValues = new ContentValues();

        contentValues.put("data", data);
        contentValues.put("type", UFaceConfig.deleteType);
        UFaceHttpManager.getInstance().requestUrl(url, UFaceHttpManager.POST, contentValues, uFaceHttpListener);
    }

    public void requestCheck(String url, String custNo, String uuid, UFaceHttpListener uFaceHttpListener) {
        byte[] byteArray = new byte[0];
        String data = UFaceFRClient.getApiData(UFaceConfig.checkType, custNo, UFaceConfig.channel, custNo, os, byteArray, uuid);
        ContentValues contentValues = new ContentValues();

        contentValues.put("data", data);
        contentValues.put("type", UFaceConfig.checkType);
        UFaceHttpManager.getInstance().requestUrl(url, UFaceHttpManager.POST, contentValues, uFaceHttpListener);
    }
}
