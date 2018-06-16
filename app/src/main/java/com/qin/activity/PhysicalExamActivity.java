package com.qin.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.qin.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PhysicalExamActivity extends AppCompatActivity {

    @BindView(R.id.physical_toolbar)
    Toolbar physicalToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_physical_exam);
        ButterKnife.bind(this);

        physicalToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PhysicalExamActivity.this.finish();
            }
        });
    }

}
