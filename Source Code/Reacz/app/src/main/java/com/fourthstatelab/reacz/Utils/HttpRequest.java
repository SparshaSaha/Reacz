package com.fourthstatelab.reacz.Utils;


import android.os.AsyncTask;


import com.fourthstatelab.reacz.Config;
import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by sid on 7/25/17.
 */

public class HttpRequest {
    public enum Method{
        GET,
        POST
    }
    private Method method;
    private String Url;
    private OnResponseListener onResponseListener=null;
    private Map<String,String> parameters = new HashMap<>();

    public HttpRequest(String Url,Method method){
        this.Url= Config.SERVER_BASE_URL+Url;
        this.method=method;
    }

    public HttpRequest addParam(String key,String value){
        parameters.put(key,value);
        return this;
    }
    public HttpRequest addParam(String key,int value){
        parameters.put(key,String.valueOf(value));
        return this;
    }


    public interface OnResponseListener {
        void onResponse(String response);
    }

    public HttpRequest sendRequest(OnResponseListener onResponseListener){
        this.onResponseListener=onResponseListener;
        new HttpRequestExecutor().execute();
        return this;
    }

    private class HttpRequestExecutor extends AsyncTask<String,String,String>{

        @Override
        protected String doInBackground(String... params) {

            OkHttpClient client = new OkHttpClient();

            Request.Builder requestBuilder = null;
            if(method.equals(Method.GET)) {
                HttpUrl.Builder urlBuilder = HttpUrl.parse(Url).newBuilder();
                for (String key: parameters.keySet()){
                    urlBuilder.addQueryParameter(key,parameters.get(key));
                }
                String url = urlBuilder.build().toString();
                requestBuilder= new Request.Builder()
                        .url(url)
                        .get();
            }
            else{
                MultipartBuilder multipartBuilder = new MultipartBuilder();
                for (String key: parameters.keySet()){
                    multipartBuilder.addFormDataPart(key,parameters.get(key));
                }
                RequestBody requestBody = multipartBuilder.build();
                requestBuilder= new Request.Builder()
                        .url(Url)
                        .method("POST", RequestBody.create(null, new byte[0]))
                        .post(requestBody);
            }

            Request request = requestBuilder.build();

            try {
                Response response= client.newCall(request).execute();
                return response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String response) {
            if(onResponseListener!=null) onResponseListener.onResponse(response);
            super.onPostExecute(response);
        }
    }
}
