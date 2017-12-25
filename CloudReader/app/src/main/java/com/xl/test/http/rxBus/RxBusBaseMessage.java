package com.xl.test.http.rxBus;

/**
 * Created by hushendian on 2017/11/16.
 */

public class RxBusBaseMessage {
    private int code;
    private Object object;

    public RxBusBaseMessage(int code, Object object) {
        this.code = code;
        this.object = object;
    }

    public RxBusBaseMessage() {

    }

    public int getCode() {
        return code;
    }

    public Object getObject() {
        return object;
    }
}
