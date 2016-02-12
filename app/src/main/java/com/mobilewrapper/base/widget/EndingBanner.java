package com.mobilewrapper.base.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
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

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by ppark on 2015-08-24.
 */
public class EndingBanner extends Dialog implements DialogInterface.OnShowListener, DialogInterface.OnCancelListener {

    private final static String A_CATEGORY = "endingBanner";
    private final static String A_ACTION_CLICK = "click";
    private final static String A_ACTION_CLOSE = "close";
    private final static String A_ACTION_SHOW = "show";

    @Bind(R.id.endingBannerImage)
    ImageView image;
    @Bind(R.id.endingBannerCancel)
    Button cancel;
    @Bind(R.id.endingBannerOk)
    Button ok;

    Banner banner;

    public EndingBanner(Context context, final Banner banner) {
        super(context);

        this.banner = banner;

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.widget_endingbanner);
        ButterKnife.bind(this);

        Picasso.with(context).load(banner.getImage()).into(image, new Callback() {
            @Override
            public void onSuccess() {

                setEvent();
            }

            @Override
            public void onError() {

            }
        });
    }

    @Override
    public void onShow(DialogInterface dialog) {
        AnalyticsManager.analytics(getContext(), A_CATEGORY, A_ACTION_SHOW, banner.getId());
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        AnalyticsManager.analytics(getContext(), A_CATEGORY, A_ACTION_CLOSE, banner.getId());
    }

    @OnClick(R.id.endingBannerOk)
    void onOkClick() {
        this.dismiss();
    }

    @OnClick(R.id.endingBannerCancel)
    void onCancelClick() {
        this.cancel();
    }

    private void setEvent() {
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AnalyticsManager.analytics(getContext(), A_CATEGORY, A_ACTION_CLICK, banner.getId());
                BannerClickManager.clickBannerAction(EndingBanner.this.getContext(), banner);
                onCancelClick();
            }
        });
    }
}
