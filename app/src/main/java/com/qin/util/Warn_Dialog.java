package com.qin.util;

import android.graphics.Color;

import com.mylhyl.circledialog.CircleDialog;
import com.qin.R;

public class Warn_Dialog {
    public Warn_Dialog(){

    }

    private void showDialog(String warn_item,String detial_msg){

        new CircleDialog.Builder()
                .setTitleIcon(R.drawable.ic_yy3)
                .setTitle("预警")
                .setTextColor(Color.GRAY)
                .setText("驾驶宝检测到你"+warn_item+"异常："+detial_msg+"\n请您说“安全”两个字确认平安")
                .setPositive("确认", null)
                .show();

    }
}
