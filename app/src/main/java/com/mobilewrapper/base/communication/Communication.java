package com.mobilewrapper.base.communication;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.mobilewrapper.base.communication.retrofit.IWrapperAPIService;
import com.mobilewrapper.base.communication.retrofit.beans.AnalyticsReqBean;
import com.mobilewrapper.base.communication.retrofit.beans.Initialize;
import com.mobilewrapper.base.gcm.QuickstartPreferences;
import com.mobilewrapper.base.utils.Utilities;

import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by ppark on 2015-08-20.
 */
public class Communication {

    private RestAdapter restAdapter;
    private IWrapperAPIService wrapperAPIService;

    public Communication(Context context) {
        restAdapter = new RestAdapter.Builder()
                .setEndpoint(IWrapperAPIService.domain)
                .setRequestInterceptor(getRequestInterceptor(context))
                .build();
        wrapperAPIService = restAdapter.create(IWrapperAPIService.class);
    }

    public void initialize(String oldToken, Callback<Initialize> callback) {
        wrapperAPIService.initilize(oldToken, callback);
    }
    public void initialize(Callback<Initialize> callback) {
        initialize(null, callback);
    }

    public void analytics(String category, String action, String label) {
        AnalyticsReqBean bean = new AnalyticsReqBean(category, action, label);
        wrapperAPIService.analytics(new Gson().toJson(bean), new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {

            }

            @Override
            public void failure(RetrofitError error) {

            }
        });

        // analytics의 경우 실새 성공 관련 응답은 없는것으로 한다.
    }

    private RequestInterceptor getRequestInterceptor(final Context context) {
        return new RequestInterceptor() {
            @Override
            public void intercept(RequestInterceptor.RequestFacade request) {
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                String Token = sharedPreferences.getString(QuickstartPreferences.REGISTRATION_TOKEN, null);

                request.addQueryParam("package", Utilities.getPackageName(context));
                request.addQueryParam("deviceToken", Token);

            }
        };
    }
}
