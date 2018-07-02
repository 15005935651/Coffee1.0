package com.qin.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import com.coffee.blelibrary.BleController;
import com.coffee.blelibrary.callback.ConnectCallback;
import com.coffee.blelibrary.callback.OnWriteCallback;
import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.qin.R;
import com.qin.pojo.HealthScroces.PersonPhySource;
import com.qin.util.ToastUtils;
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

    private boolean isPhy_Examed = false;

    private Animation m_animInsideCircle; //内圆动画
    private Animation m_animOuterCircle;  //外圆动画
    //http://localhost:8080/Coffee1.0/PhyExam?user_name=小红&blood_pressure_high=110
    // &blood_pressure_low=70&temperature=70&blood_oxygen=0.95&hreat_rate=90
    //体检的医疗数据
    private String user_name="赵鑫鑫";
    private double blood_pressure_high=110;
    private double blood_pressure_low=70;
    private double temperature=37.2;
    private double blood_oxygen=0.95;
    private double hreat_rate=80;

    //服务器回传的数据检测异常指数
    private double blood_pressure_high_sro;//收缩压
    private double blood_pressure_low_sro;//舒张压
    private double temperature_sro;//体温
    private double blood_oxygen_sro;//血氧
    private double hreat_rate_sro;//心率



    //发送体检的数据给服务器
    private void sendRort(){

        try {
            new String(user_name.getBytes(),"UTF-8");
        }catch (Exception e){
            e.printStackTrace();
        }


        String url = "http://172.17.167.178:8080/LBS_SSH/PhyExam?user_name="+user_name+
                "&blood_pressure_high="+blood_pressure_high+"&blood_pressure_low="+blood_pressure_low+"&temperature="
                +temperature + "&blood_oxygen="+blood_oxygen+"&hreat_rate="+hreat_rate;
        OkGo.<String>post(url).tag(this).execute(new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                Log.i("phy_report", response.body());
                //String s = response.body();
                Log.i("body",response.body());
                parseHealthSroceData(response.body());
//                bluetooth.setImageResource(R.drawable.finish);
                phyExamBluetoothConnect.setBackgroundColor(getResources().getColor(R.color.colorGreen));
                phyExamBluetoothConnect.setText("体检完成，查看体检报告");
//                Vibrator vibrator = (Vibrator)getApplication().getSystemService(Context.VIBRATOR_SERVICE);
//                vibrator.vibrate(new long [] {300,500},0);
                isPhy_Examed = true;
            }

            @Override
            public void onError(Response<String> response) {
                Log.i("phy_report", "上传数据异常");
                ToastUtils.showBgResource(getApplicationContext(), "服务器异常");
            }
        });
    }

    //解析体检回来的分数指标
    private void parseHealthSroceData(String json){

        Gson gson = new Gson();
        PersonPhySource ps = gson.fromJson(json, PersonPhySource.class);
        user_name=ps.getName();
        blood_pressure_high_sro=ps.getbloodpressurehigh_sco();
        blood_pressure_low_sro=ps.getbloodpressurelow_sco();
        temperature_sro=ps.gettemperature_sco();
        blood_oxygen_sro=ps.getbloodoxygen_sco();
        hreat_rate_sro=ps.gethreatrate_sco();

        Log.i("姓名",user_name);
        Log.i("收缩压异常指数",String.valueOf(blood_pressure_high_sro));
        Log.i("舒张压异常指数",String.valueOf(blood_pressure_low_sro));
        Log.i("体温异常指数",String.valueOf(temperature_sro));
        Log.i("血氧异常指数",String.valueOf(blood_oxygen_sro));
        Log.i("心率异常指数",String.valueOf(hreat_rate_sro));

    }

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
                    phyExamBluetoothConnect.setText("正在上传分析体检数据...");
                    sendRort();
                    break;
            }
        }
    }

    MyHandler ttsHandler = new MyHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //  getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setStatusBarColor(Color.parseColor("#3e3d5d"));
        setContentView(R.layout.activity_physical_exam);
        ButterKnife.bind(this);

        physicalToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isPhy_Examed = false;
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
                    phyExamBluetoothConnect.setText("连接成功，正在为您体检...");
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
            intent.putExtra("收缩压异常指数",String.valueOf(blood_pressure_high_sro));
            intent.putExtra("舒张压异常指数",String.valueOf(blood_pressure_low_sro));
            intent.putExtra("体温异常指数",String.valueOf(temperature_sro));
            intent.putExtra("血氧异常指数",String.valueOf(blood_oxygen_sro));
            intent.putExtra("心率异常指数",String.valueOf(hreat_rate_sro));
            startActivity(intent);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isPhy_Examed = false;
        mBleController.closeBleConn();
    }
}
