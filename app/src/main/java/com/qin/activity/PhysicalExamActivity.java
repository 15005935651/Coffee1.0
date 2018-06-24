package com.qin.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import com.coffee.blelibrary.BleController;
import com.coffee.blelibrary.callback.ConnectCallback;
import com.coffee.blelibrary.callback.OnWriteCallback;
import com.qin.R;
import com.qin.view.button.CircularProgressButton;

import java.lang.ref.WeakReference;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class PhysicalExamActivity extends AppCompatActivity {

    @BindView(R.id.physical_toolbar)
    Toolbar physicalToolbar;
    @BindView(R.id.phy_exam_bluetooth_connect)
    CircularProgressButton phyExamBluetoothConnect;
    @BindView(R.id.imgInsideCircle)
    ImageView imgInsideCircle;
    @BindView(R.id.imgOuterCircle)
    ImageView imgOuterCircle;
    @BindView(R.id.bluetooth)
    ImageView bluetooth;
    private BleController mBleController;

    private  boolean isPhy_Examed = false;

    private Animation m_animInsideCircle; //内圆动画
    private Animation m_animOuterCircle;  //外圆动画

    class MyHandler extends Handler {
        WeakReference<PhysicalExamActivity> mActivity;

        MyHandler(PhysicalExamActivity activity) {
            mActivity = new WeakReference<PhysicalExamActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            PhysicalExamActivity theActivity = mActivity.get();
            switch (msg.what) {
                case 0:
                    stopAnimation();
                    bluetooth.setImageResource(R.drawable.finish);
                    phyExamBluetoothConnect.setProgress(0);
                    phyExamBluetoothConnect.setText("体检完成，查看体检报告");
                    isPhy_Examed = true;
                    break;
            }
        }
    }

    MyHandler ttsHandler = new MyHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      //  getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setStatusBarColor(Color.BLACK);
        setContentView(R.layout.activity_physical_exam);
        ButterKnife.bind(this);

        physicalToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isPhy_Examed=false;
                PhysicalExamActivity.this.finish();
            }
        });
        // TODO  第一步：初始化
        mBleController = BleController.getInstance().init(this);

        phyExamBluetoothConnect.setIndeterminateProgressMode(true);


    }


    private void rotateAnimInit() {
        imgInsideCircle.setVisibility(View.VISIBLE);
        imgOuterCircle.setVisibility(View.VISIBLE);

        m_animInsideCircle = AnimationUtils.loadAnimation(this, R.anim.inside_rotate_anim);
        m_animInsideCircle.setInterpolator(new LinearInterpolator()); //匀速动画

        m_animOuterCircle = AnimationUtils.loadAnimation(this, R.anim.outer_rotate_anim);
        m_animOuterCircle.setInterpolator(new LinearInterpolator()); //匀速动画

        //开始动画
        startAnimation();
    }

    //开始动画
    private void startAnimation() {

        imgInsideCircle.startAnimation(m_animInsideCircle);
        imgOuterCircle.startAnimation(m_animOuterCircle);

    }

    private void stopAnimation() {
        imgInsideCircle.setVisibility(View.INVISIBLE);
        imgOuterCircle.setVisibility(View.INVISIBLE);
        imgInsideCircle.clearAnimation();
        imgOuterCircle.clearAnimation();
    }

    @OnClick(R.id.phy_exam_bluetooth_connect)
    public void onViewClicked() {


        if (!isPhy_Examed) {
            rotateAnimInit();
            mBleController.connect(0, "54:6C:0E:B8:2E:01", new ConnectCallback() {
                @Override
                public void onConnSuccess() {
                    phyExamBluetoothConnect.setProgress(0);
                    phyExamBluetoothConnect.setText("连接成功，正在为您体检...");
//                loadView.setVisibility(View.VISIBLE);
                    new Thread() {
                        String[] s = {"Coffee", "Start physical exam", "Cheek heart-jump", "Cheek blood-pressure", "Cheek temperature", "Cheek finish"};

                        public void run() {
                            try {
                                for (int i = 0; i < s.length; i++) {
                                    Thread.currentThread().sleep(2000);
                                    mBleController.writeBuffer(s[i].getBytes(), new OnWriteCallback() {
                                        @Override
                                        public void onSuccess() {
                                        }

                                        @Override
                                        public void onFailed(int state) {

                                        }
                                    });
                                }
                                ttsHandler.sendEmptyMessage(0);
                                Message msg = new Message();
                                msg.obj = "数据";//可以是基本类型，可以是对象，可以是List、map等；
                                ttsHandler.sendMessage(msg);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }.start();
                    //  Toast.makeText(MainActivity.this, "连接成功", Toast.LENGTH_SHORT).show();
                    //  startActivity(new Intent(MainActivity.this,SendAndReciveActivity.class));
                }

                @Override
                public void onConnFailed() {
                    //ToastUtils.showBgResource(getBaseContext(), "连接超时请重试");
                    stopAnimation();
                    phyExamBluetoothConnect.setProgress(-1);
                }
            });
        } else {
            Intent intent = new Intent();
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setClass(getApplication(), PhysicalReportActivity.class);
            startActivity(intent);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isPhy_Examed=false;
        mBleController.closeBleConn();
    }
}
