package com.axin.coffee.ball_library;

/*
 * FloatingBallWindow making by Syusuke/琴声悠扬 on 2016/6/1
 * E-Mail: Zyj7810@126.com
 * Package: com.gson8.floatingballwindow.PopupService
 * Description: null
 */

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class PopupService extends Service implements View.OnClickListener {

    /**
     * 控件window
     */
    private FloatingView mFloatingWindow;

    /**
     * 两个状态的View
     */
    private View mFloatView;
    private View mPopupView;


    /**
     * 相关控件
     */
    private TextView mTvShowShotTime;

    private ImageView Ball;

    private ImageView mIvShotGif;
    private ImageView mIvShotVideo;
    private ImageView mIvShotFolder;
    private ImageView mIvShotSettings;

    private TextView ball_soc;

    private Handler mHandler;
    @Override
    public IBinder onBind(Intent intent) {
        return new PopupBinder();
    }

    public class PopupBinder extends Binder {
        public PopupService getService() {
            return PopupService.this;
        }
    }

    private DataThread0 dt0=new DataThread0();


    private int Flag=0;

    private int getFlag(){
        return this.Flag;
    }

    private void setFlag(int Flag){
        this.Flag=Flag;
    }

    @SuppressLint("HandlerLeak")
    @Override
    public void onCreate() {
        super.onCreate();

        initFloatingWindow();

        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 0:
                        ball_soc.setText((String)msg.obj+"bpm");
                        break;
                    case 1:
                        ball_soc.setText((String)msg.obj+"℃");
                        break;
                    case 2:
                        int i = Integer.valueOf((String)msg.obj)-(int)(40+10* Math.random());
                        ball_soc.setText((String)msg.obj+"/"+i);
                        break;
                    case 3:
                        ball_soc.setText((String)msg.obj+"%");
                        break;
                        default:break;
                }
            }
        };
    dt0.start();

    }


    private class DataThread0 extends Thread {
        @Override
        public void run() {

            for(;;) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                }
                // 只能在主线程中修改ui控件
                switch (getFlag()){
                    case 0:
                        final int soc = (int)(70+ Math.random()*5);
                        final String data = String.valueOf(soc);
                        mHandler.sendMessage(mHandler.obtainMessage(0, data));
                        break;
                    case 1:
                        final String soc1 = String.format("%.2f", (37+ Math.random()*0.5));
                        final String data1 = String.valueOf(soc1);
                        mHandler.sendMessage(mHandler.obtainMessage(1, data1));
                        break;
                    case 2:
                        final int soc2 = (int)(110+ Math.random()*5);
                        final String data2 = String.valueOf(soc2);
                        mHandler.sendMessage(mHandler.obtainMessage(2, data2));
                        break;
                    case 3:
                        final int soc3 = (int)(95+ Math.random()*2);
                        final String data3 = String.valueOf(soc3);
                        mHandler.sendMessage(mHandler.obtainMessage(3, data3));
                        break;
                        default:break;
                }
            }
        }
    }


    private void initFloatingWindow() {
        mFloatView = LayoutInflater.from(this).inflate(R.layout.folating_view, null);
        mPopupView = LayoutInflater.from(this).inflate(R.layout.popup_view, null);

        Ball = (ImageView)mFloatView.findViewById(R.id.id_iv);
        ball_soc=(TextView)mFloatView.findViewById(R.id.ball_soc);


        mIvShotGif = (ImageView) mPopupView.findViewById(R.id.id_pop_shot_gif);
        mIvShotVideo = (ImageView) mPopupView.findViewById(R.id.id_pop_shot_video);
        mIvShotFolder = (ImageView) mPopupView.findViewById(R.id.id_pop_shot_folder);
        mIvShotSettings = (ImageView) mPopupView.findViewById(R.id.id_pop_shot_settings);

        mIvShotGif.setOnClickListener(this);
        mIvShotVideo.setOnClickListener(this);
        mIvShotFolder.setOnClickListener(this);
        mIvShotSettings.setOnClickListener(this);


        mFloatingWindow = new FloatingView(this);
        mFloatingWindow.setFloatingView(mFloatView);
        mFloatingWindow.setPopupView(mPopupView);
    }

    public void show() {
        if(null != mFloatingWindow)
        { mFloatingWindow.show();}
    }

    public void dimiss() {
        if(null != mFloatingWindow)
        { mFloatingWindow.dismiss();}
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.id_pop_shot_gif) {
            Ball.setImageResource(R.drawable.ic_heart_svg);
            setFlag(0);

        } else if (i == R.id.id_pop_shot_video) {
            Ball.setImageResource(R.drawable.ic_tempearture);
            setFlag(1);

        } else if (i == R.id.id_pop_shot_folder) {
            Ball.setImageResource(R.drawable.ic_blood_pressure);
            setFlag(2);

        } else if (i == R.id.id_pop_shot_settings) {
            Ball.setImageResource(R.drawable.ic_blood_oxy);
            setFlag(3);

        } else {
        }
        mFloatingWindow.turnMini();
    }


}
