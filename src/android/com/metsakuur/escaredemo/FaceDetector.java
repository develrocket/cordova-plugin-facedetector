package com.metsakuur.escaredemo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.content.pm.PackageManager;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.PluginResult;
import org.apache.cordova.PermissionHelper;

public class FaceDetector extends CordovaPlugin {
    private String [] permissions = { Manifest.permission.CAMERA };

    private JSONArray requestArgs;
    private CallbackContext callbackContext;

    /**
     * Constructor.
     */
    public FaceDetector() {
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) {
        this.callbackContext = callbackContext;
        this.requestArgs = args;

        Log.d("ESLOG-CordovaExecute", String.valueOf(args));

        if (action.equals("scan")) {

            //android permission auto add
            if(!hasPermisssion()) {
                requestPermissions(0);
            } else {
                scan(args);
            }
        } else {
            return false;
        }
        return true;
    }

    /**
     * Starts an intent to scan and decode a barcode.
     */
    public void scan(final JSONArray args) {

        final FaceDetector that = this;
        String type = "";
        String deviceId = "";

        try {
            type = args.getString(0);
            deviceId = args.getString(1);
        } catch (JSONException e) {

        }

        UFaceConfig.idkey = deviceId;
        UFaceConfig.INSTANCE.setTYPE(Integer.parseInt(type));

        Log.d("ESLOG-AccountId", type);
        Log.d("ESLOG-deviceId", deviceId);

        cordova.getThreadPool().execute(new Runnable() {
            public void run() {
                Intent intentScan = new Intent(that.cordova.getActivity().getBaseContext(), FaceCameraActivity.class);
                // avoid calling other phonegap apps
                intentScan.setPackage(that.cordova.getActivity().getApplicationContext().getPackageName());
                that.cordova.startActivityForResult(that, intentScan, 0);
            }
        });
    }

    /**
     * check application's permissions
     */
    public boolean hasPermisssion() {
        for(String p : permissions)
        {
            if(!PermissionHelper.hasPermission(this, p))
            {
                return false;
            }
        }
        return true;
    }

    /**
     * We override this so that we can access the permissions variable, which no longer exists in
     * the parent class, since we can't initialize it reliably in the constructor!
     *
     * @param requestCode The code to get request action
     */
    public void requestPermissions(int requestCode)
    {
        PermissionHelper.requestPermissions(this, requestCode, permissions);
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        if(resultCode == Activity.RESULT_OK){
            Bundle extras = data.getExtras();// Get data sent by the Intent
            String information = extras.getString("data"); // data parameter will be send from the other activity.
            this.callbackContext.success(information);
            return;
        }else if(resultCode == Activity.RESULT_CANCELED){
            this.callbackContext.success("");
            return;
        }
        // Handle other results if exists.
        super.onActivityResult(requestCode, resultCode, data);
    }
}
