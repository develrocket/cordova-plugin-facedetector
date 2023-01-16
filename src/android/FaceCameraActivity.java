package com.metsakuur.escaredemo;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.metsakuur.escaredemo.UFaceConfig;
import com.metsakuur.escaredemo.http.UFaceHttpRequestManager;
import com.metsakuur.escaredemo.http.UFaceHttpListener;
import com.metsakuur.ufacedetectorlite.UFaceDetector;
import com.metsakuur.ufacedetectorlite.UFaceDetectorListener;
import com.metsakuur.ufacedetectorlite.camerax.UFaceXPreviewView;
import com.metsakuur.ufacedetectorlite.model.UFaceDetectResult;
import com.metsakuur.ufacedetectorlite.model.UFaceError;
import com.metsakuur.ufacedetectorlite.util.UFaceLog;
import com.metsakuur.ufacedetectorlite.util.UFaceUtils;
import capacitor.android.plugins.R;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class FaceCameraActivity extends AppCompatActivity implements UFaceDetectorListener {

    public Context context;
    public UFaceXPreviewView faceXPreviewView;
    public UFaceDetector uFaceDetector;

    private AlertDialog.Builder alertDialogBuilder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.activity_face_camera);
        this.context = this;
        this.faceXPreviewView = findViewById(R.id.faceXPreviewView);

        Log.d("ESLOG", "================================== start face detection ==========================================");
        Log.d("ESLOG-UdeviceId", UFaceConfig.idkey);
        this.initFaceDetector();
    }

    private final void initFaceDetector() {
        this.uFaceDetector = new UFaceDetector();
        this.uFaceDetector.setFaceDetectorListener(this);
        String[] strExtraData = new String[]{
            "antispoof_v2_bin.dat",
            "antispoof_v2se_bin.dat",
            "eyeblink_bin.dat",
            "mobileattack_bin.dat",
            "ultraface_bin.dat"
        };

        String strFilePath = getBaseContext().getFilesDir().getPath();

        for (int i = 0; i < strExtraData.length; i ++) {
            String strDataFile = strFilePath + "/" + strExtraData[i];
            File file = new File(strDataFile);
            if (!file.exists()) {
                byte[] btFileData = this.readAsset(getBaseContext(), strExtraData[i]);
                if (btFileData != null) {
                    this.saveFile(strDataFile, btFileData);
                }
            }
        }

        this.uFaceDetector.setUFaceXPreviewView(this.faceXPreviewView);
        String licenseKey = "4F5A46527631008159DB9F7FCCB6BC3D6170E79B3F1DF734BDC103035E9264D38EFF4ECB88BE5DD2509E27199D03A183B4A8D6EC0A32E9AE";
        this.uFaceDetector.initDetector(
            this.context,
            licenseKey,
            strFilePath
        );
    }

    public void uFaceDetector(UFaceDetector uFaceDetector, UFaceDetectResult result) {
        UFaceLog.d("UFaceDetectResult : " + result);
        Log.d("ESLOG-DetectResult", String.valueOf(result));

        if (result.isFake) {
            uFaceDetector.stopDetect();

            this.showAlertDialog("Detection FAKE", (DialogInterface.OnClickListener)(new DialogInterface.OnClickListener() {
                public final void onClick(DialogInterface $noName_0, int $noName_1) {
                    finish();
                }
            }));

            return;
        }

        uFaceDetector.stopDetect();

        switch(UFaceConfig.INSTANCE.getTYPE()) {
            case 0:
                //registration
                try {
                    Log.d("ESLOG-idKEY", UFaceConfig.idkey);
                    Log.d("ESLOG-UUID", UFaceConfig.getUUID(context));
                    Log.d("ESLOG-FaceData", Arrays.toString(UFaceUtils.getInstance().getJpegByte(result.cropImage, 90)));


                    UFaceHttpRequestManager.INSTANCE.requestRegist(
                        UFaceConfig.SERVER_IP + UFaceConfig.SERVER_PORT + UFaceConfig.SERVER_URL,
                        UFaceConfig.idkey,
                        UFaceConfig.getUUID(context),
                        UFaceUtils.getInstance().getJpegByte(result.cropImage, 90),
                        (UFaceHttpListener)(new UFaceHttpListener() {
                            public void onResponse(String response) {
                                UFaceLog.d("requestRegist : " + response);
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    String msg = jsonObject.has("msg") ? jsonObject.getString("msg") : "";
                                    String code = jsonObject.has("code") ? jsonObject.getString("code") : "";
                                    String custNo = jsonObject.has("cust_no") ? jsonObject.getString("cust_no") : "";
                                    UFaceConfig.INSTANCE.setHASH(custNo);

                                    Log.d("ESLOG-Res-Msg", msg);
                                    Log.d("ESLOG-Res-copde", code);
                                    Log.d("ESLOG-Res-custNo", custNo);

                                    if (code.equals("00000")) {
                                        showAlertDialog("Success in registration face.", (DialogInterface.OnClickListener)(new DialogInterface.OnClickListener() {
                                            public final void onClick(DialogInterface dialog, int $noName_1) {
                                                finish();
                                            }
                                        }));
                                    } else {
                                        showAlertDialog(msg + "(code : " + code + ')', (DialogInterface.OnClickListener)(new DialogInterface.OnClickListener() {
                                            public final void onClick(DialogInterface dialog, int $noName_1) {
                                                finish();
                                            }
                                        }));
                                    }
                                } catch (JSONException e) {

                                }
                            }
                        })
                    );
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }

                break;

            case 1:
                //verify
                try {
                    UFaceHttpRequestManager.INSTANCE.requestVerify(
                        UFaceConfig.SERVER_IP + UFaceConfig.SERVER_PORT + UFaceConfig.SERVER_URL,
                        UFaceConfig.idkey,
                        UFaceConfig.getUUID(context),
                        UFaceUtils.getInstance().getJpegByte(result.cropImage, 90),
                        (UFaceHttpListener)(new UFaceHttpListener() {
                            public void onResponse(String response) {
                                UFaceLog.d("requestVerify : " + response);
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    String msg = jsonObject.has("msg") ? jsonObject.getString("msg") : "";
                                    String code = jsonObject.has("code") ? jsonObject.getString("code") : "";
                                    String custNo = jsonObject.has("cust_no") ? jsonObject.getString("cust_no") : "";
                                    UFaceConfig.INSTANCE.setHASH(custNo);

                                    if (code.equals("00000")) {
                                        showAlertDialog(UFaceConfig.idkey + " Success in verification.", (DialogInterface.OnClickListener)(new DialogInterface.OnClickListener() {
                                            public final void onClick(DialogInterface dialog, int $noName_1) {
                                                finish();
                                            }
                                        }));
                                    } else {
                                        showAlertDialog(msg + "(code : " + code + ')', (DialogInterface.OnClickListener)(new DialogInterface.OnClickListener() {
                                            public final void onClick(DialogInterface dialog, int $noName_1) {
                                                finish();
                                            }
                                        }));
                                    }
                                } catch (JSONException e) {

                                }
                            }
                        })
                    );
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }

                break;
            case 2:
                //1:N verification
                try {
                    UFaceHttpRequestManager.INSTANCE.requestBulkVerify(
                        UFaceConfig.SERVER_IP + UFaceConfig.SERVER_PORT + UFaceConfig.SERVER_URL,
                        UFaceConfig.getUUID(context),
                        UFaceUtils.getInstance().getJpegByte(result.cropImage, 90),
                        (UFaceHttpListener)(new UFaceHttpListener() {
                            public void onResponse(String response) {
                                UFaceLog.d("requestBulkVerify : " + response);
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    String msg = jsonObject.has("msg") ? jsonObject.getString("msg") : "";
                                    String code = jsonObject.has("code") ? jsonObject.getString("code") : "";
                                    String custNo = jsonObject.has("cust_no") ? jsonObject.getString("cust_no") : "";
                                    UFaceConfig.INSTANCE.setHASH(custNo);
                                    if (code.equals("00000")) {
                                        showAlertDialog(custNo + " Success in verification.", (DialogInterface.OnClickListener)(new DialogInterface.OnClickListener() {
                                            public final void onClick(DialogInterface dialog, int $noName_1) {
                                                finish();
                                            }
                                        }));
                                    } else {
                                        showAlertDialog(msg + "(code : " + code + ')', (DialogInterface.OnClickListener)(new DialogInterface.OnClickListener() {
                                            public final void onClick(DialogInterface dialog, int $noName_1) {
                                                finish();
                                            }
                                        }));
                                    }
                                } catch (JSONException e) {

                                }

                            }
                        })
                    );
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }

                break;
        }
    }

    public void uFaceDetector(final UFaceDetector uFaceDetector, UFaceError error) {
        UFaceLog.e(error.errorDescription);
        if (!error.errorCode.equals("72001")) {
            uFaceDetector.stopDetect();
            this.showAlertDialog(error.errorDescription + "(code " + error.errorCode + ')', (DialogInterface.OnClickListener)(new DialogInterface.OnClickListener() {
                public final void onClick(DialogInterface dialog, int which) {
                    uFaceDetector.startDetect();
                }
            }));
        }
    }

    public void uFaceDetectorSetCameraComplete() {
        UFaceLog.d("uFaceDetectorSetCameraComplete");
        this.uFaceDetector.startDetect();
    }

    @Override
    public void uFaceIsDetectFace(boolean b) {

    }

    @Override
    public void uFaceDetectSmallFace() {

    }

    @Override
    public void uFaceDetectLargeFace() {

    }


    private void showAlertDialog(final String msg, final DialogInterface.OnClickListener onClickListener) {
        this.runOnUiThread((Runnable)(new Runnable() {
            public final void run() {
                if (alertDialogBuilder == null) {
                    alertDialogBuilder = new AlertDialog.Builder(context);
                }

                alertDialogBuilder.setMessage(msg).setPositiveButton("Confirm", onClickListener ).setCancelable(false).create().show();
            }
        }));
    }

    private byte[] readAsset(Context context, String strFileName) {
        AssetManager am = context.getAssets();
        InputStream inputStream = (InputStream)null;

        try {
            inputStream = am.open(strFileName);
            int size = inputStream.available();
            if (size > 0) {
                byte[] data = new byte[size];
                int nLength = data.length;
                inputStream.read(data);
                am = (AssetManager)null;
                return data;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Exception e) {
                }
            }

        }

        return null;
    }

    private void saveFile(String strFileName, byte[] btData) {
        File file = new File(strFileName);
        OutputStream os = (OutputStream)null;
        try {
            os = (OutputStream)(new FileOutputStream(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        }

        try {
            os.write(btData);
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d("ESLOG-EndScreen", UFaceConfig.INSTANCE.getHASH());

        Intent intent = new Intent();
        intent.putExtra("data", UFaceConfig.INSTANCE.getHASH());
        this.setResult(-1, intent);
        this.finish();
    }
}
