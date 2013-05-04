package com.megadevs.savey.machineserver;

import com.google.gson.Gson;
import com.megadevs.savey.machineserver.data.APIRequest;
import com.megadevs.savey.machineserver.data.APIResponse;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class RealWebService implements WebService {

    private static WebService instance;
    private static Endpoint endpoint;

    private HttpClient client = new DefaultHttpClient();
    private Gson gson = new Gson();

    public static WebService getInstance() {
        if (instance == null) {
            instance = new RealWebService();
            setEndpoint(Endpoint.DEFAULT);
        }
        return instance;
    }

    public static void setEndpoint(Endpoint endpoint) {
        RealWebService.endpoint = endpoint;
    }

    private RealWebService() {}

    private HttpGet createRequest(Page page, APIRequest apiRequest) {
        HttpGet request = null;
        String paramString = "";
        if (apiRequest != null) {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("value", gson.toJson(apiRequest)));
            paramString = "?" + URLEncodedUtils.format(params, "utf-8");
        }
        switch (page) {
            case GET_CREDIT:
                request = new HttpGet(endpoint.getValue() + page.getValue() + paramString);
                break;
        }
        return request;
    }

    synchronized private APIResponse execute(HttpGet request) {
        try {
            HttpResponse response = client.execute(request);
            if (response.getStatusLine().getStatusCode() == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                String json = reader.readLine();
                Logg.v("API json: %s", json);
                return gson.fromJson(json, APIResponse.class);
            } else {
                handleError(response);
            }
        } catch (IOException e) {
            Logg.e("Error while executing request");
            e.printStackTrace();
        }
        return null;
    }

    private void handleError(HttpResponse response) {
        Logg.e("Error while executing request - StatusCode: %d, ReasonPhrase: %s", response.getStatusLine().getStatusCode(), response.getStatusLine().getReasonPhrase());
    }

    @Override
    public void getMachineQrCode(final int id, final OnWebServiceResponse listener) {
        performOnBackground(new Runnable() {
            @Override
            public void run() {
                APIRequest apiRequest = new APIRequest();
                apiRequest.id = id;
                HttpGet request = createRequest(Page.GET_MACHINE, apiRequest);
                APIResponse response = execute(request);
                if (listener != null) {
                    listener.onWebServiceResponse(response);
                }
            }
        });
    }

    @Override
    public void getCredit(final int taskId, final OnWebServiceResponse listener) {
        performOnBackground(new Runnable() {
            @Override
            public void run() {
                APIRequest apiRequest = new APIRequest();
                apiRequest.id = taskId;
                HttpGet request = createRequest(Page.GET_CREDIT, apiRequest);
                APIResponse response = execute(request);
                if (listener != null) {
                    listener.onWebServiceResponse(response);
                }
            }
        });
    }

    private void performOnBackground(Runnable runnable) {
        new Thread(runnable).start();
    }

    public enum Endpoint {
        DEFAULT("http://ec2-54-244-109-143.us-west-2.compute.amazonaws.com:8080/SaveyAPIs/");

        String value;
        Endpoint(String value) {
            this.value = value;
        }

        String getValue() {
            return value;
        }
    }

    public enum Page {
        GET_MACHINE("get_machine"),
        GET_CREDIT("get_credit");

        String value;
        Page(String value) {
            this.value = value;
        }

        String getValue() {
            return value;
        }
    }

}