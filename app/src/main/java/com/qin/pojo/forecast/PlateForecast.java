/**
  * Copyright 2018 bejson.com 
  */
package com.qin.pojo.forecast;


public class PlateForecast {

    private String status;
    private String msg;
    private Result result;
    public void setStatus(String status) {
         this.status = status;
     }
     public String getStatus() {
         return status;
     }

    public void setMsg(String msg) {
         this.msg = msg;
     }
     public String getMsg() {
         return msg;
     }

    public void setResult(Result result) {
         this.result = result;
     }
     public Result getResult() {
         return result;
     }

}