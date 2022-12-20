package com.metsakuur.escaredemo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.provider.Settings;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class UFaceConfig {
    //Camera scene type 0 register, 1 verify
    public int TYPE = 0;
    public String hash = "";
    //id
    public static String idkey = "";
    //server ip
    public static String SERVER_IP = "http://61.74.179.47";
    //server port
    public static String SERVER_PORT = ":18080";
    public static String SERVER_URL = "/uface_api/face/common";

    //channel
    public static String channel = "ESC";
    //type
    public static String registType = "REGIST";
    public static String verifyType = "VERIFY";
    public static String bulkVerifyType = "BULKVERIFY";
    public static String deleteType = "DELETE";
    public static String checkType = "CHECK";

    public static final UFaceConfig INSTANCE;

    public final int getTYPE() {
        return TYPE;
    }

    public final void setTYPE(int var1) {
        TYPE = var1;
    }

    public final String getHASH() {
        return hash;
    }

    public final void setHASH(String var1) {
        hash = var1;
    }


    public static byte[] getSHA(String input) throws NoSuchAlgorithmException
    {
        // Static getInstance method is called with hashing SHA
        MessageDigest md = MessageDigest.getInstance("SHA-256");

        // digest() method called
        // to calculate message digest of an input
        // and return array of byte
        return md.digest(input.getBytes(StandardCharsets.UTF_8));
    }

    public static String toHexString(byte[] hash)
    {
        // Convert byte array into signum representation
        BigInteger number = new BigInteger(1, hash);

        // Convert message digest into hex value
        StringBuilder hexString = new StringBuilder(number.toString(16));

        // Pad with leading zeros
        while (hexString.length() < 64)
        {
            hexString.insert(0, '0');
        }

        return hexString.toString();
    }


    public static String getUUID(Context context) throws NoSuchAlgorithmException {
        @SuppressLint("HardwareIds") String androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        return toHexString(getSHA(androidId));
    }

    private UFaceConfig() {
    }

    static {
        INSTANCE = new UFaceConfig();
        idkey = "";
        SERVER_IP = "http://61.74.179.47";
        SERVER_PORT = ":18080";
        SERVER_URL = "/uface_api/face/common";
        channel = "ESC";
        registType = "REGIST";
        verifyType = "VERIFY";
        bulkVerifyType = "BULKVERIFY";
        deleteType = "DELETE";
        checkType = "CHECK";
    }
}
