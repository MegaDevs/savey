package com.megadevs.savey.machinecommon;

import com.google.gson.Gson;
import com.megadevs.savey.machinecommon.data.*;

import java.io.Serializable;

public class GsonWrapper {

    private static final Gson instance = new Gson();

    public static Gson getInstance() {
        return instance;
    }

    public static String toJson(Serializable obj) {
        return getInstance().toJson(obj);
    }

    public static APIRequest getAPIRequest(String json) {
        return getInstance().fromJson(json, APIRequest.class);
    }

    public static APIResponse getAPIResponse(String json) {
        return getInstance().fromJson(json, APIResponse.class);
    }

    public static Request getRequest(String json) {
        return getInstance().fromJson(json, Request.class);
    }

    public static Response getResponse(String json) {
        return getInstance().fromJson(json, Response.class);
    }

    public static QrCodeData getQrCodeData(String json) {
        return getInstance().fromJson(json, QrCodeData.class);
    }

}
