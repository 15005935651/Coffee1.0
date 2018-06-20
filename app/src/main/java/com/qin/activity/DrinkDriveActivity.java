package com.qin.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

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
        drinkRadar.showScore(98f);
        drinkDriveToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DrinkDriveActivity.this.finish();
            }
        });
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
}
