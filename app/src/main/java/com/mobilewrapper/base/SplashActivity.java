package com.mobilewrapper.base;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.mobilewrapper.base.common.BaseActivity;
import com.mobilewrapper.base.communication.retrofit.beans.Banner;
import com.mobilewrapper.base.communication.retrofit.beans.Initialize;
import com.mobilewrapper.base.gcm.QuickstartPreferences;
import com.mobilewrapper.base.gcm.RegistrationIntentService;
import com.mobilewrapper.base.utils.Utilities;

import java.util.Timer;
import java.util.TimerTask;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class SplashActivity extends BaseActivity {

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;

    private static final int CHECK_NETWORK = 0;
    private static final int REQUEST_INITIAL_INFO = 1;
    private static final int START_APP = 100;

    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private Handler requestHandler = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                boolean sentToken = sharedPreferences.getBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, false);
                if (sentToken) {
                    // go
                    requestHandler = new RequestHandler();
                    doAsyncStartActivity();
                } else {
                    // error
                }
            }
        };




    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(QuickstartPreferences.REGISTRATION_COMPLETE));

        if (checkPlayServices()) {
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }
        else {
            //
        }
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    private boolean checkPlayServices() {
        final int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                Dialog dialog = GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICES_RESOLUTION_REQUEST);
                if (dialog != null) {
                    dialog.show();
                    dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        public void onDismiss(DialogInterface dialog) {
                            if (ConnectionResult.SERVICE_INVALID == resultCode) finish();
                        }
                    });
                    dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            finish();
                        }
                    });
                    return false;
                }
            }
            new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Google Play Services Error")
                    .setContentText("This device is not supported for required Goole Play Services")
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            finish();
                        }
                    })
                    .show();

            return false;
        }
        return true;
    }

    private void doAsyncStartActivity() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                requestData();
            }
        }, 500);
    }

    private void requestData() {
        requestHandler.sendEmptyMessage(CHECK_NETWORK);
    }

    private void checkNetwork() {
        if (Utilities.checkStatus(this) == Utilities.NETWORK_NONE) {
            new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Network Connection Error")
                    .setContentText("네트워크 연결이 원활하지 않습니다.\n네트워크 설정을 확인한 후\n다시 시도해 주시기 바랍니다.")
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            finish();
                        }
                    })
                    .show();
        }
        else {
            requestHandler.sendEmptyMessage(REQUEST_INITIAL_INFO);
        }
    }

    private void requestInitializeInfo() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String oldToken = sharedPreferences.getString(QuickstartPreferences.OLD_TOKEN, null);


        /*
        new Communication(this).initialize(oldToken, new Callback<Initialize>() {
            @Override
            public void success(Initialize initialize, Response response) {
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(SplashActivity.this);
                sharedPreferences.edit().putString(QuickstartPreferences.OLD_TOKEN, null).apply();

                WrapperApplication application = (WrapperApplication) getApplication();
                WrapperApplication.setInitialize(initialize);
                requestHandler.sendEmptyMessage(START_APP);
            }

            @Override
            public void failure(RetrofitError error) {
                requestHandler.sendEmptyMessage(START_APP);
            }
        });
        */


        // TODO : api 완성되면 제거
        // testCode
        WrapperApplication application = (WrapperApplication) getApplication();
        Initialize initialize = application.getInitialize();
        Banner topBanner = new Banner();
        topBanner.setId(1);
        topBanner.setBackground("#ff0000");
        topBanner.setImage("http://image.momswiz.com/share/campaign_banner/201508193658_156685.png");
        topBanner.setLinkType("outlink");
        topBanner.setUrl("http://www.naver.com");
        initialize.setTopBanner(topBanner);

        Banner startBanner = new Banner();
        startBanner.setId(2);
        startBanner.setImage("http://image.momswiz.com/share/campaign_banner/201508190821_446071.png");
        startBanner.setLinkType("outlink");
        startBanner.setUrl("http://www.naver.com");
        initialize.setStartBanner(startBanner);

        Banner endingBanner = new Banner();
        endingBanner.setId(3);
        endingBanner.setImage("http://image.momswiz.com/share/campaign_banner/201508190821_446071.png");
        endingBanner.setLinkType("outlink");
        endingBanner.setUrl("http://www.naver.com");
        initialize.setEndingBanner(endingBanner);

        WrapperApplication.setInitialize(initialize);

        requestHandler.sendEmptyMessage(START_APP);
    }

    private void startApp() {
        Intent intent = new Intent(this, WebViewActivity.class);
        if (getIntent().getExtras() != null) {
            intent.putExtras(getIntent().getExtras());
        }
        startActivityWithTransition(intent);
        finish();
    }


    class RequestHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case CHECK_NETWORK:
                    checkNetwork();
                    break;
                case REQUEST_INITIAL_INFO:
                    requestInitializeInfo();
                    break;
                case START_APP:
                    startApp();
                    break;
            }
        }
    }
}
