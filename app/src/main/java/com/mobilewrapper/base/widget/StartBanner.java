package com.mobilewrapper.base.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;

import com.mobilewrapper.base.R;
import com.mobilewrapper.base.communication.retrofit.beans.Banner;
import com.mobilewrapper.base.manager.AnalyticsManager;
import com.mobilewrapper.base.manager.BannerClickManager;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by ppark on 2015-08-24.
 */
public class StartBanner extends Dialog implements DialogInterface.OnShowListener, DialogInterface.OnCancelListener {

    private final static String A_CATEGORY = "startBanner";
    private final static String A_ACTION_CLICK = "click";
    private final static String A_ACTION_CLOSE = "close";
    private final static String A_ACTION_SHOW = "show";

    private final static String NO_SHOW_DATE = "NO_SHOW_DATE";
    private final static String DATE_FORMAT = "yyyyMMdd";

    @Bind(R.id.startBannerImage)
    ImageView image;
    @Bind(R.id.startBannerNoMoreSeeToday)
    Button noMoreSeeToday;
    @Bind(R.id.startBannerClose)
    Button close;

    Banner banner;

    public StartBanner(Context context, Banner banner) {
        super(context);

        this.banner = banner;

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.widget_startbanner);
        ButterKnife.bind(this);

        if(isShowToday()) {
            Picasso.with(context).load(banner.getImage()).into(image, new Callback() {
                @Override
                public void onSuccess() {
                    StartBanner.this.show();
                    setEvent();
                }

                @Override
                public void onError() {

                }
            });

            this.setOnShowListener(this);
            this.setOnCancelListener(this);
        }
    }

    @Override
    public void onShow(DialogInterface dialog) {
        AnalyticsManager.analytics(getContext(), A_CATEGORY, A_ACTION_SHOW, banner.getId());
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        AnalyticsManager.analytics(getContext(), A_CATEGORY, A_ACTION_CLOSE, banner.getId());
    }

    @OnClick(R.id.startBannerClose)
    void onCloseClick() {
        cancel();
    }

    @OnClick(R.id.startBannerNoMoreSeeToday)
    void onNoMoreSeeTodayClick() {
        setNoMoreSeeToday();
        cancel();
    }

    private void setEvent() {
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AnalyticsManager.analytics(getContext(), A_CATEGORY, A_ACTION_CLICK, banner.getId());
                BannerClickManager.clickBannerAction(StartBanner.this.getContext(), banner);
                dismiss();
            }
        });
    }

    private String getToday() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, java.util.Locale.getDefault());
        return dateFormat.format(new Date());
    }

    private boolean isShowToday() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String date = sharedPreferences.getString(NO_SHOW_DATE, null);
        if(date != null && date.equals(getToday())) {
            return false;
        }
        return true;
    }

    private void setNoMoreSeeToday() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        sharedPreferences.edit().putString(NO_SHOW_DATE, getToday()).apply();
    }
}
