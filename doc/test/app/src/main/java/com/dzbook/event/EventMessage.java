package com.dzbook.event;

import android.os.Bundle;

/**
 * EventMessage
 *
 * @author victor on 2014/10/15 0015.
 */
public class EventMessage {
    private int requestCode;
    private String type;
    private Bundle bundle;

    /**
     * 构造
     *
     * @param requestCode requestCode
     * @param type        type
     * @param bundle      bundle
     */
    public EventMessage(int requestCode, String type, Bundle bundle) {
        this.requestCode = requestCode;
        this.type = type;
        this.bundle = bundle;
    }

    /**
     * 构造
     *
     * @param requestCode requestCode
     */
    public EventMessage(int requestCode) {
        this.requestCode = requestCode;
    }

    public int getRequestCode() {
        return requestCode;
    }

    public void setRequestCode(int requestCode) {
        this.requestCode = requestCode;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Bundle getBundle() {
        return bundle;
    }

    public void setBundle(Bundle bundle) {
        this.bundle = bundle;
    }
}
