package com.mobilewrapper.base.communication.retrofit;

import com.mobilewrapper.base.communication.retrofit.beans.Initialize;

import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by ppark on 2015-08-20.
 */
public interface IWrapperAPIService {

    String domain = "http://www.mobilewrapper.com";

    @GET("/api/initialize.json")
    void initilize(@Query("oldToken") String oldToken, Callback<Initialize> callback);

    @GET("/api/analytics.json")
    void analytics(@Query("data") String data, Callback<Response> callback);
}
