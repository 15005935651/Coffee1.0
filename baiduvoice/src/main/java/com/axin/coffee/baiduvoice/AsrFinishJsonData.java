package com.axin.coffee.baiduvoice;

/**
 * Created by Administrator on 2018/4/29.
 */
public class AsrFinishJsonData {
    private String error;
    private String sub_error;
    private String desc;
    private OriginResult origin_result;

    public String getError() {
        return error;
    }

    public String getSub_error() {
        return sub_error;
    }

    public String getDesc() {
        return desc;
    }

    public OriginResult getOrigin_result() {
        return origin_result;
    }
}