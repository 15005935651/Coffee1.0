package com.qin.activity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.mylhyl.circledialog.CircleDialog;
import com.qin.R;
import com.qin.activity.consume.RadarView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DrinkDriveActivity extends AppCompatActivity {

    @BindView(R.id.drink_drive_toolbar)
    Toolbar drinkDriveToolbar;
    @BindView(R.id.drink_radar)
    RadarView drinkRadar;
    @BindView(R.id.drink_drive_start)
    Button drinkDriveStart;

    private boolean isAll=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drink_drive);
        ButterKnife.bind(this);
        //     drinkRadar.setScanColor(new int[]{Color.RED,Color.BLUE,drinkRadar.getCircleColor()});
        drinkRadar.showScore(30f);
        drinkDriveToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DrinkDriveActivity.this.finish();
            }
        });

        new CircleDialog.Builder(DrinkDriveActivity.this)
                .setTitleIcon(R.drawable.ic_yy3)
                .setTitle("酒驾预警")
                .setTextColor(Color.GRAY)
                .setText("驾驶宝检测到您酒精浓度超标:\n30mg/100ml，属于酒驾范围，为了您和他人的安全，请不要驾驶汽车。\n是否为您打开 e代驾？")
                .setPositive("确认", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Open_App("cn.edaijia.android.client");
                    }
                })
                .show();
    }

    @OnClick(R.id.drink_drive_start)
    public void onViewClicked() {
        if(!isAll) {
            drinkRadar.start();
            drinkDriveStart.setText("点击取消");
            isAll=true;
        }else{
            drinkRadar.showScore(98f);
            drinkDriveStart.setText("点击开始");
            isAll=false;
        }
    }

    private void Open_App(String PackageName){
        PackageInfo packageInfo;//PackageInfo所在包为android.content.pm
        try {
            packageInfo = this.getPackageManager().getPackageInfo(PackageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            packageInfo = null;
            e.printStackTrace();
        }
        if(packageInfo ==null){
            Toast.makeText(this, "您的手机没有安装该应用!", Toast.LENGTH_LONG).show();
        }else{
            PackageManager packageManager =  this.getPackageManager();
            Intent it= packageManager.getLaunchIntentForPackage(PackageName);
            this.startActivity(it);
        }
    }


}
