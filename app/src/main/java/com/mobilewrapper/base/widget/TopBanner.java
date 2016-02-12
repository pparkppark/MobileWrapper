package com.mobilewrapper.base.widget;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.mobilewrapper.base.R;
import com.mobilewrapper.base.communication.retrofit.beans.Banner;
import com.mobilewrapper.base.manager.AnalyticsManager;
import com.mobilewrapper.base.manager.BannerClickManager;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by ppark on 2015-08-24.
 */
public class TopBanner extends RelativeLayout {

    private final static String A_CATEGORY = "topBanner";
    private final static String A_ACTION_CLICK = "click";
    private final static String A_ACTION_CLOSE = "close";
    private final static String A_ACTION_SHOW = "show";

    @Bind(R.id.topBannerImage)
    ImageView image;
    @Bind(R.id.topBannerClose)
    Button close;

    public TopBanner(Context context) {
        this(context, null);
    }

    public TopBanner(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TopBanner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        LayoutInflater.from(getContext()).inflate(R.layout.widget_topbanner, this, true);
        ButterKnife.bind(this);
    }

    public void init(final Banner banner) {
        int color = Color.TRANSPARENT;
        try {
            color = Color.parseColor(banner.getBackground());
        } catch (IllegalArgumentException e) {
        }
        image.setBackgroundColor(color);
        Picasso.with(getContext()).load(banner.getImage()).into(image, new Callback() {
            @Override
            public void onSuccess() {
                AnalyticsManager.analytics(getContext(), A_CATEGORY, A_ACTION_SHOW, banner.getId());
                setVisibility(View.VISIBLE);
                setEvent(banner);
            }

            @Override
            public void onError() {
                setVisibility(View.GONE);
            }
        });
    }

    private void setEvent(final Banner banner) {
        image.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                AnalyticsManager.analytics(getContext(), A_CATEGORY, A_ACTION_CLICK, banner.getId());
                BannerClickManager.clickBannerAction(TopBanner.this.getContext(), banner);
            }
        });
        close.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                AnalyticsManager.analytics(getContext(), A_CATEGORY, A_ACTION_CLOSE, banner.getId());
                TopBanner.this.setVisibility(View.GONE);
            }
        });
    }
}
