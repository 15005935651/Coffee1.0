package com.coffee.blelibrary.callback;

/**
 * Description：
 * Author: Hansion
 * Time: 2016/10/11 12:11
 */
public interface ConnectCallback {
    /**
     * Notify之后的回调
     */
    void onConnSuccess();

    void onConnFailed();

}
