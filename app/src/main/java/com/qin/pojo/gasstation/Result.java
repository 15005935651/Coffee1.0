/**
  * Copyright 2018 bejson.com 
  */
package com.qin.pojo.gasstation;
import java.util.List;


public class Result {

    private List<Data> data;
    private Pageinfo pageinfo;
    public void setData(List<Data> data) {
         this.data = data;
     }
     public List<Data> getData() {
         return data;
     }

    public void setPageinfo(Pageinfo pageinfo) {
         this.pageinfo = pageinfo;
     }
     public Pageinfo getPageinfo() {
         return pageinfo;
     }

}