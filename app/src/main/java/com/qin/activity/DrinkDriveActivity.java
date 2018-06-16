package com.qin.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.qin.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DrinkDriveActivity extends AppCompatActivity {

    @BindView(R.id.drink_drive_toolbar)
    Toolbar drinkDriveToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drink_drive);
        ButterKnife.bind(this);
        drinkDriveToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DrinkDriveActivity.this.finish();
            }
        });
    }
}
