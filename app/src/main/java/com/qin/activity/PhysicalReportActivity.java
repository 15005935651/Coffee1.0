package com.qin.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.qin.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PhysicalReportActivity extends AppCompatActivity {

    @BindView(R.id.physical_report_toolbar)
    Toolbar physicalReportToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_physical_report);
        ButterKnife.bind(this);
        physicalReportToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PhysicalReportActivity.this.finish();
            }
        });
    }
}
