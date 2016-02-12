package com.mobilewrapper.base.communication.retrofit.beans;

/**
 * Created by ppark on 2015-08-24.
 */
public class AnalyticsReqBean {

    String category;
    String action;
    String label;

    public AnalyticsReqBean(String category, String action, String label) {
        this.category = category;
        this.action = action;
        this.label = label;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
