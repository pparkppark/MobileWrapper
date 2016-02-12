package com.mobilewrapper.base.manager;

import android.content.Context;

import com.mobilewrapper.base.communication.retrofit.beans.Banner;
import com.mobilewrapper.base.utils.Utilities;

/**
 * Created by ppark on 2015-08-24.
 */
public class BannerClickManager {

    enum LinkType { inlink, outlink };

    public static void clickBannerAction(Context context, Banner banner) {
        if(banner != null) {
            if(banner.getLinkType().equals(LinkType.inlink.name())) {

            }
            else if(banner.getLinkType().equals(LinkType.outlink.name())) {
                Utilities.openURLOnBrowser(context, banner.getUrl());
            }
        }
    }
}
